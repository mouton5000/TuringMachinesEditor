package gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import turingmachines.*;
import util.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This widget displays the graph of the edited Turing machine.
 */
class GraphPane extends Pane implements MouseListener {

    /**
     * Rectangle displaying the settings of a state. Appear when the user long press on a state.
     */
    StateSettingsRectangle stateSettingsRectangle;

    /**
     * Rectangle displaying the settings of a transition. Appear when the user long press on a transition.
     */
    TransitionSettingsRectangle transitionSettingsRectangle;

    /**
     * String enumerator that gives default names to states.
     */
    private StringEnumerator stringEnumerator;

    /**
     * Bidirectional map linking each state to the associated widget displayed on the pane.
     */
    private BidirMap<StateGroup, Integer> stateGroupToState;

    /**
     * Bidirectional map linking each transition to the associated widget displayed on the pane.
     */
    private BidirMap<TransitionGroup, Transition> transitionGroupToTransition;

    /**
     * Widget corresponding to the last state that was declared as the current state pointer by the state register of
     * the machine while it is executed.
     */
    private StateGroup lastCurrentStateGroup;

    /**
     * Widget containing all the drawn graph and the settings. It is used to make the translation/scale of the graph
     * easier.
     */
    private Group graphGroup;

    /**
     * Scale transformation of the graph. The pivot is always on the center of the pane.
     */
    private Scale graphScale;

    /**
     * Last abscissa where the mouse was dragging the whole pane.
     */
    private Double dragX;

    /**
     * Last ordinate where the mouse was dragging the whole pane.
     */
    private Double dragY;

    /**
     * Current selected node (either a state or a transition of the graph)
     */
    private Node selected;

    /**
     * Construction initializing the pane.
     */
    GraphPane(){
        // White background
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);

        // Build the group containing the graph
        this.graphGroup = new Group();

        // Built the scale transformation of the graph
        graphScale = new Scale();
        this.graphGroup.getTransforms().add(graphScale);

        // TODO Clean this code
        // Scrolling modify the scale by 0.1
        this.setOnScroll(scrollEvent -> {
            int direction = (int)Math.signum(scrollEvent.getDeltaY());
            if(this.graphGroup.getScaleY() < 0.3 && direction == -1)
                return;

            graphScale.setX(graphScale.getX() + direction * 0.1);
            graphScale.setY(graphScale.getY() + direction * 0.1);
        });

        // Add the settings rectangles to the graph group
        this.stateSettingsRectangle = new StateSettingsRectangle( this);
        this.stateSettingsRectangle.setVisible(false);

        this.transitionSettingsRectangle = new TransitionSettingsRectangle(this);
        this.transitionSettingsRectangle.setVisible(false);
        graphGroup.getChildren().addAll(this.stateSettingsRectangle, this.transitionSettingsRectangle);

        this.getChildren().addAll(this.graphGroup);

        // Clip the pane so that nodes do not appear outside the pane.
        Rectangle graphClip = new Rectangle();

        this.setClip(graphClip);

        // Modifying the size of the pane modify the clip and the pivot of the scale.
        this.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            graphScale.setPivotX(newValue.getWidth()/2);
            graphScale.setPivotY(newValue.getHeight()/2);
            graphClip.setWidth(newValue.getWidth());
            graphClip.setHeight(newValue.getHeight());
        });

        stateGroupToState = new BidirMap<>();
        transitionGroupToTransition = new BidirMap<>();

        // Init some parameters to default values
        clear();
    }

    /**
     * Init or reinit some of the parameters of the pane.
     */
    void clear() {
        // Default translation and scale
        graphGroup.setTranslateX(0);
        graphGroup.setTranslateY(0);
        graphScale.setX(1);
        graphScale.setY(1);

        lastCurrentStateGroup = null;
        stringEnumerator = new StringEnumerator();

        closeStateSettingsRectangle();
        closeTransitionSettingsRectangle();
        stateSettingsRectangle.clear();
        transitionSettingsRectangle.clear();
    }

    /**
     * @param value
     * @return the closest value on an imaginary discrete grid of size {@link TuringMachineDrawer#GRAPH_GRID_WIDTH}.
     */
    private int gridClosest(double value){
        return ((int)value / TuringMachineDrawer.GRAPH_GRID_WIDTH) * TuringMachineDrawer.GRAPH_GRID_WIDTH;
    }

    /**
     * @return a String that is not currently used by the other states of the machine.
     */
    String nextStateName(){
        Set<String> names = new HashSet<>();
        for(int i = 0; i < this.stateGroupToState.size(); i++){
            names.add(TuringMachineDrawer.getInstance().machine.getStateName(i));
        }
        String s =  stringEnumerator.next();
        while(names.contains(s)){
            s = stringEnumerator.next();
        }
        return s;
    }

    /**
     * Draw a new state on the pane at the given position and associated with the given state of the machine.
     * @param x
     * @param y
     * @param state
     */
    void addState(double x, double y, Integer state){
        String name = TuringMachineDrawer.getInstance().machine.getStateName(state);
        StateGroup circle = new StateGroup(name);

        Vector p = new Vector(x - graphGroup.getTranslateX(), y - graphGroup.getTranslateY());
        Vector c = new Vector(this.getWidth() / 2, this.getHeight() / 2);
        Vector n = p.diff(c).mult(1 / graphScale.getY()).add(c);
        x = n.x;
        y = n.y;
        this.moveStateGroup(circle, x, y);

        stateGroupToState.put(circle, state);
        graphGroup.getChildren().add(circle);
    }

    /**
     * Edit the label written in the widget associated with the given state
     * @param state
     * @param name
     */
    void editStateName(Integer state, String name){
        StateGroup stateGroup = stateGroupToState.getK(state);
        stateGroup.setName(name);
    }

    /**
     * Request the machine to remove the state associated with the given widget.
     * @param stateGroup
     */
    void removeState(StateGroup stateGroup) {
        TuringMachineDrawer.getInstance().removeState(stateGroupToState.getV(stateGroup));
    }

    /**
     * Remove the widget associated with the given state from the pane.
     * @param state
     */
    void removeState(Integer state){
        StateGroup stateGroup = stateGroupToState.removeV(state);
        this.closeStateSettingsRectangle();
        this.closeTransitionSettingsRectangle();
        graphGroup.getChildren().remove(stateGroup);

        Set<Map.Entry<StateGroup, Integer>> entries = new HashSet<>(stateGroupToState.entrySet());
        for(Map.Entry<StateGroup, Integer> entry : entries){
            if(entry.getValue() >= state)
                stateGroupToState.put(entry.getKey(), entry.getValue() - 1);
        }
    }

    /**
     * Move the given widget to the given position.
     * @param stateGroup
     * @param x
     * @param y
     */
    void moveStateGroup(StateGroup stateGroup, double x, double y){
        int xg = gridClosest(x);
        int yg = gridClosest(y);
        stateGroup.setLayoutX(xg);
        stateGroup.setLayoutY(yg);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    /**
     * Return the state associated with the given widget.
     * @param stateGroup
     * @return
     */
    Integer getState(StateGroup stateGroup) {
        return stateGroupToState.getV(stateGroup);
    }

    /**
     * Request the machine to add a transition from the state associated with the start widget to the state
     * associated with the end widget.
     * @param start
     * @param end
     */
    void addTransition(StateGroup start, StateGroup end){
        if(!TuringMachineDrawer.getInstance().editGraphMode)
            return;
        Integer input = stateGroupToState.getV(start);
        Integer output = stateGroupToState.getV(end);
        TuringMachineDrawer.getInstance().addTransition(input, output);
    }

    /**
     * Draw the given transtition on the pane as a Bezier curve which keys as described by the given coordinates.
     * @param transition
     * @param control1X
     * @param control1Y
     * @param control2X
     * @param control2Y
     */
    void addTransition(Transition transition,
                                          Double control1X, Double control1Y,
                                          Double control2X, Double control2Y){
        Integer input = transition.getInput();
        Integer output = transition.getOutput();

        StateGroup start = stateGroupToState.getK(input);
        StateGroup end = stateGroupToState.getK(output);

        TransitionGroup transitionGroup = new TransitionGroup(start, end);
        if(control1X != null)
            transitionGroup.setControl1(control1X, control1Y);
        if(control2X != null)
            transitionGroup.setControl2(control2X, control2Y);

        transitionGroupToTransition.put(transitionGroup, transition);
        graphGroup.getChildren().add(transitionGroup);
        transitionGroup.toBack();

        // Add all the tapes and the heads to the String displayed next to the transition.
        Iterator<Tape> it = TuringMachineDrawer.getInstance().machine.getTapes();

        while(it.hasNext()){
            Tape tape = it.next();
            transitionGroup.addTape(tape);

            for(int head = 0; head < tape.getNbHeads(); head++)
                transitionGroup.addHead(tape, TuringMachineDrawer.getInstance().getColorOfHead(tape, head));
        }
    }

    /**
     * Request the machine to remove the transition associated with the given widget.
     * @param transitionGroup
     */
    void removeTransition(TransitionGroup transitionGroup){
        Transition transition = transitionGroupToTransition.getV(transitionGroup);
        TuringMachineDrawer.getInstance().removeTransition(transition);
    }

    /**
     * Remove the widget associated with the given transition.
     * @param transition
     */
    void removeTransition(Transition transition){
        TransitionGroup transitionGroup = transitionGroupToTransition.removeV(transition);
        this.closeStateSettingsRectangle();
        this.closeTransitionSettingsRectangle();
        graphGroup.getChildren().remove(transitionGroup);
    }

    /**
     *
     * @param transitionGroup
     * @return the transition associated with the given widget.
     */
    Transition getTransition(TransitionGroup transitionGroup) {
        return transitionGroupToTransition.getV(transitionGroup);
    }

    /**
     * Request the machine to toggle the "final" property of the state associated with the given widget.
     * @param stateGroup
     */
    void toggleFinal(StateGroup stateGroup){
        Integer state = stateGroupToState.getV(stateGroup);
        if(stateGroup.isAccepting())
            TuringMachineDrawer.getInstance().setAcceptingState(state, false);
        else
            TuringMachineDrawer.getInstance().setFinalState(state, !stateGroup.isFinal());
    }

    /**
     * Request the machine to toggle the "accepting" property of the state associated with the given widget.
     * @param stateGroup
     */
    void toggleAccepting(StateGroup stateGroup){
        Integer state = stateGroupToState.getV(stateGroup);
        if(stateGroup.isAccepting())
            TuringMachineDrawer.getInstance().setFinalState(state, false);
        else
            TuringMachineDrawer.getInstance().setAcceptingState(state, true);
    }

    /**
     * Request the machine to toggle the "initial" property of the state associated with the given widget.
     * @param stateGroup
     */
    void toggleInitial(StateGroup stateGroup){
        Integer state = stateGroupToState.getV(stateGroup);
        TuringMachineDrawer.getInstance().setInitialState(state, !stateGroup.isInitial());
    }

    /**
     * Draw the widget associated with the given state as a final state if isFinal is true and as a not final state
     * otherwise.
     * @param state
     * @param isFinal
     */
    void setFinalState(Integer state, boolean isFinal){
        StateGroup stateGroup = stateGroupToState.getK(state);
        stateGroup.setFinal(isFinal);
    }

    /**
     * Draw the widget associated with the given state as an accepting state if isAccepting is true and as a not
     * accepting state otherwise.
     * @param state
     * @param isAccepting
     */
    void setAcceptingState(Integer state, boolean isAccepting){
        StateGroup stateGroup = stateGroupToState.getK(state);
        stateGroup.setAccepting(isAccepting);
    }

    /**
     * Draw the widget associated with the given state as an initial state if isInitial is true and as a not initial
     * state otherwise.
     *
     * @param state
     * @param isInitial
     */
    void setInitialState(Integer state, boolean isInitial){
        StateGroup stateGroup = stateGroupToState.getK(state);
        stateGroup.setInitial(isInitial);
    }

    /**
     * Request the machine to add a read symbol to the transition associated with the given widget. The read symbol
     * consists in the triplet containing the given tape, the given head (identified with the index of the head in
     * the list of heads of the tape) and the given symbol.
     * @param transitionGroup
     * @param tape
     * @param head
     * @param symbol
     */
    void addReadSymbol(TransitionGroup transitionGroup, Tape tape, int head, String symbol){
        Transition transition = transitionGroupToTransition.getV(transitionGroup);
        TuringMachineDrawer.getInstance().addReadSymbol(transition, tape, head, symbol);
    }

    /**
     * Add a read symbol to the widget associated with the given transition. The read symbol
     * consists in the triplet containing the given tape, the given head (identified with the index of the head in
     * the list of heads of the tape) and the given symbol.
     * @param transition
     * @param tape
     * @param head
     * @param symbol
     */
    void addReadSymbol(Transition transition, Tape tape, int head, String symbol){
        TransitionGroup transitionGroup = transitionGroupToTransition.getK(transition);
        if(symbol == null)
            symbol = TuringMachineDrawer.BLANK_SYMBOL;
        transitionGroup.addReadSymbol(tape, head, symbol);

        if(transitionSettingsRectangle.currentTransitionGroup == transitionGroup)
            transitionSettingsRectangle.addReadSymbol(tape, head, symbol);
    }

    /**
     * Request the machine to remove a read symbol from the transition associated with the given widget. The read symbol
     * consists in the triplet containing the given tape, the given head (identified with the index of the head in
     * the list of heads of the tape) and the given symbol.
     * @param transitionGroup
     * @param tape
     * @param head
     * @param symbol
     */
    void removeReadSymbol(TransitionGroup transitionGroup, Tape tape, int head, String symbol){
        Transition transition = transitionGroupToTransition.getV(transitionGroup);
        TuringMachineDrawer.getInstance().removeReadSymbol(transition, tape, head, symbol);
    }


    /**
     * Remove a read symbol from the widget associated with the given transition. The read symbol
     * consists in the triplet containing the given tape, the given head (identified with the index of the head in
     * the list of heads of the tape) and the given symbol.
     * @param transition
     * @param tape
     * @param head
     * @param symbol
     */
    void removeReadSymbol(Transition transition, Tape tape, int head, String symbol){
        TransitionGroup transitionGroup = transitionGroupToTransition.getK(transition);
        if(symbol == null)
            symbol = TuringMachineDrawer.BLANK_SYMBOL;
        transitionGroup.removeReadSymbol(tape, head, symbol);
        if(transitionSettingsRectangle.currentTransitionGroup == transitionGroup)
            transitionSettingsRectangle.removeReadSymbol(tape, head, symbol);
    }

    /**
     * Request the machine to add an action to the transition associated with the given widget. The action deals with
     * the given tape and the given head (identified with the index of the head in the list of heads of the tape).
     * The type of action depends on the given action symbol. If the symbol is an arrow, the action consists in
     * moving the head in the given direction. Otherwise it consists in writing the symbol on the head position in
     * the tape.
     *
     * @param transitionGroup
     * @param tape
     * @param head
     * @param actionSymbol
     */
    void addAction(TransitionGroup transitionGroup, Tape tape, int head, String actionSymbol) {
        Transition transition = transitionGroupToTransition.getV(transitionGroup);
        TuringMachineDrawer.getInstance().addAction(transition, tape, head, actionSymbol);
    }
    /**
     * Add an action to the widget associated with the given transition. The action deals with the given tape and
     * the given head (identified with the index of the head in the list of heads of the tape). The type of action
     * (moving a head or writing on the tape) is the given type. The value is either the direction of the moving or
     * the symbol that should be written.
     *
     * @param transition
     * @param tape
     * @param head
     * @param type
     * @param value
     */
    void addAction(Transition transition, Tape tape, int head, ActionType type, Object value){
        TransitionGroup transitionGroup = transitionGroupToTransition.getK(transition);

        String actionSymbol = null;
        switch (type){

            case MOVE:
                Direction direction = (Direction)value;
                switch (direction){
                    case LEFT:
                        actionSymbol = TuringMachineDrawer.LEFT_SYMBOL;
                        break;
                    case RIGHT:
                        actionSymbol = TuringMachineDrawer.RIGHT_SYMBOL;
                        break;
                    case DOWN:
                        actionSymbol = TuringMachineDrawer.DOWN_SYMBOL;
                        break;
                    case UP:
                        actionSymbol = TuringMachineDrawer.UP_SYMBOL;
                        break;
                }
                break;
            case WRITE:
                if(value == null)
                    actionSymbol = TuringMachineDrawer.BLANK_SYMBOL;
                else
                    actionSymbol = (String)value;

                break;
        }

        transitionGroup.addAction(tape, head, actionSymbol);

        if(transitionSettingsRectangle.currentTransitionGroup == transitionGroup)
            transitionSettingsRectangle.addAction(tape, head, actionSymbol);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    /**
     * Request the machine to remove the last action of the list of actions of the transition associated with the
     * given widget.
     * @param transitionGroup
     */
    void removeAction(TransitionGroup transitionGroup){
        Transition transition = transitionGroupToTransition.getV(transitionGroup);
        TuringMachineDrawer.getInstance().removeAction(transition);
    }

    /**
     * Remove the action at the given index in the list of actions of the widget associated with the given transition.
     * @param transition
     * @param index
     */
    void removeAction(Transition transition, int index){
        TransitionGroup transitionGroup = transitionGroupToTransition.getK(transition);
        transitionGroup.removeAction(index);

        if(transitionSettingsRectangle.currentTransitionGroup == transitionGroup)
            transitionSettingsRectangle.removeAction(index);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    /**
     * Add the given symbol to the widget (which consists in adding it to the rectangle containing the transition
     * settings.
     * @param symbol
     */
    void addSymbol(String symbol) {
        this.transitionSettingsRectangle.addSymbol(symbol);
    }

    /**
     * Change the name of the symbol (identified by its index in the list of symbols and by its previous name) by the
     * given name
     * @param index
     * @param previousSymbol
     * @param symbol new name of the symbol
     */
    void editSymbol(int index, String previousSymbol, String symbol) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.editSymbol(previousSymbol, symbol);
        this.transitionSettingsRectangle.editSymbol(index, previousSymbol, symbol);
    }

    /**
     * Remove the given symbol from the pane (identified by its index in the list of symbols and by its name)
     * @param index
     * @param symbol
     */
    void removeSymbol(int index, String symbol) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.removeSymbol(symbol);
        this.transitionSettingsRectangle.removeSymbol(index, symbol);
    }

    /**
     * Add the given tape to the pane (which consists in adding it to the strings displayed next to each transition and
     * to the settings of the transitions).
     * @param tape
     */
    void addTape(Tape tape) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.addTape(tape);
        this.transitionSettingsRectangle.addTape(tape);
    }

    /**
     * Remove the given tape to the pane (which consists in removing it from the strings displayed next to each
     * transition and from the settings of the transitions).
     * @param tape
     */
    void removeTape(Tape tape) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.removeTape(tape);
        this.transitionSettingsRectangle.removeTape(tape);
    }

    /**
     * Add a new head to the pane (which consists in adding it to the strings displayed next to each transition and
     * to the settings of the transitions). The head is added to the given tape and has the given color.
     *
     * @param tape
     * @param color
     */
    void addHead(Tape tape, Color color) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.addHead(tape, color);
        this.transitionSettingsRectangle.addHead(tape, color);
    }

    /**
     * Change the color of the given head (identified by the given tape and the index of hte head in the list of
     * heads of the tape) to the given color in the pane (which consists in changing it in the strings displayed next
     * to each transition and in the settings of the transitions)
     * @param tape
     * @param head
     * @param color
     */
    void editHeadColor(Tape tape, int head, Color color) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.editHeadColor(tape, head, color);
        this.transitionSettingsRectangle.editHeadColor(tape, head, color);
    }

    /**
     * Remove the given head (identified by the given tape and the index of hte head in the list of
     * heads of the tape) from the pane (which consists in removing it from the strings displayed next
     * to each transition and from the settings of the transitions)
     * @param tape
     * @param head
     * @param color
     */
    void removeHead(Tape tape, int head) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.removeHead(tape, head);
        this.transitionSettingsRectangle.removeHead(tape, head);
    }

    /**
     * @param transitionGroup
     * @param tape
     * @param head
     * @return the set of symbols that are in a read symbol (a triplet of tape, head and symbol) containing the given
     * tape and the given head (identified with the index of the head in the list of heads of the given tape).
     */
    Set<String> getReadSymbols(TransitionGroup transitionGroup, Tape tape, int head) {
        Transition transition = transitionGroupToTransition.getV(transitionGroup);

        if(transition == null)
            return new HashSet<>();

        // Request the machine directly
        Iterator<String> it =  transition.getReadSymbols(tape, head);
        Set<String> set = new HashSet<>();
        while(it.hasNext()){
            String symbol = it.next();
            symbol = (symbol == null)?TuringMachineDrawer.BLANK_SYMBOL:symbol;
            set.add(symbol);
        }
        return set;
    }

    /**
     * Translate the graph by a vector (dx, dy)
     * @param dx
     * @param dy
     */
    void translate(double dx, double dy) {
        graphGroup.setTranslateX(graphGroup.getTranslateX() + dx);
        graphGroup.setTranslateY(graphGroup.getTranslateY() + dy);
    }

    /**
     * Place the rectangle containing the settings of the state groups above the given widget and animate it to
     * maximize it.
     * @param stateGroup
     */
    void openStateSettingsRectangle(StateGroup stateGroup){
        stateSettingsRectangle.setCurrentState(null);
        stateSettingsRectangle.setLayoutX(stateGroup.getLayoutX());
        stateSettingsRectangle.setLayoutY(stateGroup.getLayoutY() - TuringMachineDrawer.STATE_RADIUS
                * TuringMachineDrawer.STATE_SETTINGS_RECTANGLE_DISTANCE_RATIO);
        stateSettingsRectangle.setTranslateX(stateGroup.getTranslateX());
        stateSettingsRectangle.setTranslateY(stateGroup.getTranslateY());
        stateSettingsRectangle.setCurrentState(stateGroup);
        stateSettingsRectangle.toFront();
        stateSettingsRectangle.setVisible(true);
        stateSettingsRectangle.maximize();
    }

    /**
     * Animate the rectangle containing the settings of the state groups to minimize it and close it.
     */
    void closeStateSettingsRectangle(){
        closeStateSettingsRectangle(true);
    }

    /**
     * Minimize and close the rectangle containing the settings of the state groups. Animate this operation if the
     * animate flag is true.
     * @param animate
     */
    void closeStateSettingsRectangle(boolean animate){
        stateSettingsRectangle.minimize(animate);
    }

    /**
     * Place the rectangle containing the settings of the transition groups above the given widget and animate it to
     * maximize it.
     * @param transitionGroup
     */
    void openTransitionSettingsRectangle(TransitionGroup transitionGroup){
        transitionSettingsRectangle.setCurrentTransitionGroup(null);
        transitionSettingsRectangle.setLayoutX(transitionGroup.getCenterX());
        transitionSettingsRectangle.setLayoutY(transitionGroup.getCenterY());
        transitionSettingsRectangle.setCurrentTransitionGroup(transitionGroup);
        transitionSettingsRectangle.toFront();
        transitionSettingsRectangle.setVisible(true);
        transitionSettingsRectangle.maximize();
    }


    /**
     * Animate the rectangle containing the settings of the transition groups to minimize it and close it.
     */
    void closeTransitionSettingsRectangle() { closeTransitionSettingsRectangle(true); }

    /**
     * Minimize and close the rectangle containing the settings of the transition groups. Animate this operation if the
     * animate flag is true.
     */
    void closeTransitionSettingsRectangle(boolean animate){
        transitionSettingsRectangle.minimize(animate);
    }

    /**
     * Minimize and close the rectangles containing the settings of the state groups and of the transition groups.
     * Do not animate this operation.
     */
    void closeAllSettingsRectangle() {
        closeStateSettingsRectangle(false);
        closeTransitionSettingsRectangle(false);
    }

    /**
     * @param state
     * @return the timeline animating the change of the current state pointed by the state register during the
     * machine execution. The next current state is the given state.
     * @see #lastCurrentStateGroup
     */
    Timeline getChangeCurrentStateTimeline(Integer state) {
        StateGroup stateGroup = stateGroupToState.getK(state);

        Timeline timeline = new Timeline();

        KeyFrame keyFrame;

        KeyValue knew = stateGroup.getCurrentStateKeyValue();

        // If there was already a state pointed by the state register, animate the fact that it is not anymore
        // pointed by.
        if(lastCurrentStateGroup != null){
            KeyValue klast = lastCurrentStateGroup.getNoCurrentStateKeyValue();
            keyFrame = new KeyFrame(Duration.millis(TuringMachineDrawer.ANIMATION_DURATION), klast, knew);
        }
        else
            keyFrame = new KeyFrame(Duration.millis(TuringMachineDrawer.ANIMATION_DURATION), knew);

        timeline.getKeyFrames().add(keyFrame);

        lastCurrentStateGroup = stateGroup;
        return timeline;
    }

    /**
     * @return the timeline animating the fact that the machine is not executed anymore and that the state register
     * is not pointing at any state.
     */
    Timeline getRemoveCurrentStateTimeline() {

        if(lastCurrentStateGroup != null){
            Timeline timeline = new Timeline();

            KeyFrame keyFrame;
            KeyValue klast = lastCurrentStateGroup.getNoCurrentStateKeyValue();
            keyFrame = new KeyFrame(Duration.millis(TuringMachineDrawer.ANIMATION_DURATION), klast);
            timeline.getKeyFrames().add(keyFrame);
            lastCurrentStateGroup = null;

            return timeline;
        }
        else
            return null;
    }

    /**
     * @param transition
     * @return the timeline animating a firing transition.
     */
    Timeline getFiredTransitionTimeline(Transition transition) {
        TransitionGroup transitionGroup = transitionGroupToTransition.getK(transition);

        Timeline timeline = new Timeline();

        KeyFrame keyFrame = transitionGroup.getFiredKeyValue();

        timeline.setCycleCount(2);
        timeline.setAutoReverse(true);
        timeline.getKeyFrames().add(keyFrame);

        return timeline;
    }

    /**
     * @return a JSON description of the graph
     */
    JSONObject getJSON() {
        JSONArray jsonStates = new JSONArray();
        for(int state = 0; state < stateGroupToState.size(); state++)
            jsonStates.put(stateGroupToState.getK(state).getJSON());

        JSONArray jsonTransition = new JSONArray();
        for(Map.Entry<TransitionGroup, Transition> entry: transitionGroupToTransition.entrySet())
            jsonTransition.put(entry.getKey().getJSON());

        return new JSONObject().put("states", jsonStates).put("transitions", jsonTransition);
    }

    /**
     * Load a JSON description of the graph and apply it to the pane.
     * @param jsonGraph
     */
    void loadJSON(JSONObject jsonGraph){
        JSONArray jsonStates = jsonGraph.getJSONArray("states");
        for(int i = 0; i < jsonStates.length(); i++){
            JSONObject jsonState = jsonStates.getJSONObject(i);
            double x = jsonState.getDouble("x");
            double y = jsonState.getDouble("y");
            String name = jsonState.getString("name");

            int state = TuringMachineDrawer.getInstance().addState(x, y, name);
            StateGroup stateGroup = stateGroupToState.getK(state);

            if(jsonState.getBoolean("isInitial"))
                this.toggleInitial(stateGroup);

            if(jsonState.getBoolean("isFinal"))
                this.toggleFinal(stateGroup);

            if(jsonState.getBoolean("isAccepting"))
                this.toggleAccepting(stateGroup);

        }

        JSONArray jsonTransitions = jsonGraph.getJSONArray("transitions");
        for(int i = 0; i < jsonTransitions.length(); i++) {
            JSONObject jsonTransition = jsonTransitions.getJSONObject(i);

            int inputState = jsonTransition.getInt("input");
            int outputState = jsonTransition.getInt("output");

            double control1X = jsonTransition.getDouble("control1X");
            double control1Y = jsonTransition.getDouble("control1Y");
            double control2X = jsonTransition.getDouble("control2X");
            double control2Y = jsonTransition.getDouble("control2Y");

            Transition transition = TuringMachineDrawer.getInstance().addTransition(inputState, outputState,
                    control1X, control1Y,
                    control2X, control2Y);
            TransitionGroup transitionGroup = transitionGroupToTransition.getK(transition);

            JSONObject jsonDisplay = jsonTransition.getJSONObject("display");
            JSONArray jsonReadSymbols = jsonDisplay.getJSONArray("readSymbols");
            for(int tapeId = 0; tapeId < jsonReadSymbols.length(); tapeId++){
                JSONArray jsonReadSymbolsOfTape = jsonReadSymbols.getJSONArray(tapeId);
                Tape tape = TuringMachineDrawer.getInstance().machine.getTape(tapeId);
                for(int head = 0; head < jsonReadSymbolsOfTape.length(); head++){
                    JSONArray jsonReadSymbolsOfHead = jsonReadSymbolsOfTape.getJSONArray(head);
                    for(Object symbol : jsonReadSymbolsOfHead)
                        this.addReadSymbol(transitionGroup, tape, head, (String)symbol);
                }
            }

            JSONArray jsonActions = jsonDisplay.getJSONArray("actions");
            for(int j = 0; j < jsonActions.length(); j++){
                JSONObject jsonAction = jsonActions.getJSONObject(j);

                Color color = Color.valueOf(jsonAction.getString("color"));
                Pair<Tape, Integer> pair = TuringMachineDrawer.getInstance().getHead(color);
                Tape tape = pair.first;
                Integer head = pair.second;

                String actionSymbol = jsonAction.getString("actionSymbol");
                this.addAction(transitionGroup, tape, head, actionSymbol);
            }

        }

    }

    /**
     * Select the given node (either a state or a transition)
     * @param node
     * @see #unselect()
     */
    void select(Node node) {
        unselect();
        selected = node;

        if(node instanceof TransitionGroup)
            ((TransitionGroup) node).setSelected(true);
        else if(node instanceof StateGroup)
            ((StateGroup) node).setSelected();
    }

    /**
     * Unselect the selected node.
     * @see #select(Node)
     */
    void unselect() {
        if(selected == null)
            return;

        if(selected instanceof TransitionGroup) {
            TransitionGroup transitionGroup = (TransitionGroup) selected;
            transitionGroup.setSelected(false);
            selected = null;
        }
        else if(selected instanceof StateGroup){
            StateGroup stateGroup = (StateGroup) selected;
            stateGroup.setUnselected();
            selected = null;
        }
        else
            selected = null;
    }

    /**
     * @return the current selected node or null if no node is selected.
     * @see #select(Node)
     */
    Node getSelected(){
        return selected;
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        // Close any settings recntalge is such a rectangle is opened
        if(this.stateSettingsRectangle.isMaximized()){
            this.closeStateSettingsRectangle();
            return true;
        }
        else if(this.transitionSettingsRectangle.isMaximized()){
            this.closeTransitionSettingsRectangle();
            return true;
        }

        // Unselect any selected node if such a node exists and add a new state on the mouse position otherwise.
        if(selected != null)
            unselect();
        else {
            TuringMachineDrawer.getInstance().addState(mouseEvent.getX(), mouseEvent.getY());
        }
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        // Drag from the stored coordinates to the given position and register those coordinates for any possible futur
        // dragging.
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        if(dragX == null){
            dragX = x;
            dragY = y;
        }
        else {
            this.translate(x - dragX, y - dragY);
            dragX = x;
            dragY = y;
        }
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        // When pressed register the current coordinates in case the user want to drag the graph.
        dragX = mouseEvent.getX();
        dragY = mouseEvent.getY();
        return true;
    }
}
