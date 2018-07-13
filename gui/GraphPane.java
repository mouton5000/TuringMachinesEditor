package gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
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
import turingmachines.ActionType;
import turingmachines.Direction;
import turingmachines.Tape;
import turingmachines.Transition;
import util.BidirMap;
import util.Pair;
import util.StringEnumerator;
import util.Vector;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by dimitri.watel on 18/06/18.
 */
class GraphPane extends Pane {

    StateOptionRectangle stateOptionRectangle;
    TransitionOptionRectangle transitionOptionRectangle;

    StringEnumerator stringEnumerator;

    private BidirMap<StateGroup, Integer> stateGroupToState;
    private BidirMap<TransitionGroup, Transition> transitionGroupToTransition;

    private StateGroup lastCurrentStateGroup;

    private Group graphGroup;
    private Scale graphScale;

    GraphPane(){

        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        this.graphGroup = new Group();

        this.stateOptionRectangle = new StateOptionRectangle( this);
        this.stateOptionRectangle.setVisible(false);

        this.transitionOptionRectangle = new TransitionOptionRectangle(this);
        this.transitionOptionRectangle.setVisible(false);

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().graphPaneMouseHandler);
        this.setOnMousePressed(TuringMachineDrawer.getInstance().graphPaneMouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().graphPaneMouseHandler);

        graphScale = new Scale();
        this.graphGroup.getTransforms().add(graphScale);
        this.setOnScroll(scrollEvent -> {
            int direction = (int)Math.signum(scrollEvent.getDeltaY());
            if(this.graphGroup.getScaleY() < 0.3 && direction == -1)
                return;

            graphScale.setX(graphScale.getX() + direction * 0.1);
            graphScale.setY(graphScale.getY() + direction * 0.1);
        });

        this.getChildren().addAll(this.graphGroup);
        graphGroup.getChildren().addAll(this.stateOptionRectangle, this.transitionOptionRectangle);


        Rectangle graphClip = new Rectangle();

        this.setClip(graphClip);

        this.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            graphScale.setPivotX(newValue.getWidth()/2);
            graphScale.setPivotY(newValue.getHeight()/2);
            graphClip.setWidth(newValue.getWidth());
            graphClip.setHeight(newValue.getHeight());
        });

        stateGroupToState = new BidirMap<>();
        transitionGroupToTransition = new BidirMap<>();

        clear();
    }

    void clear() {
        graphGroup.setTranslateX(0);
        graphGroup.setTranslateY(0);
        graphScale.setX(1);
        graphScale.setY(1);
        lastCurrentStateGroup = null;
        stringEnumerator = new StringEnumerator();

        closeStateOptionRectangle();
        closeTransitionOptionRectangle();
        stateOptionRectangle.clear();
        transitionOptionRectangle.clear();
    }


    private int gridClosest(double value){
        return ((int)value / TuringMachineDrawer.GRAPH_GRID_WIDTH) * TuringMachineDrawer.GRAPH_GRID_WIDTH;
    }

    String nextStateName(){
        return stringEnumerator.next();
    }

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

    void removeState(StateGroup stateGroup) {
        TuringMachineDrawer.getInstance().removeState(stateGroupToState.getV(stateGroup));
    }

    void removeState(Integer state){
        StateGroup stateGroup = stateGroupToState.removeV(state);
        this.closeStateOptionRectangle();
        this.closeTransitionOptionRectangle();
        graphGroup.getChildren().remove(stateGroup);

        Set<Map.Entry<StateGroup, Integer>> entries = new HashSet<>(stateGroupToState.entrySet());
        for(Map.Entry<StateGroup, Integer> entry : entries){
            if(entry.getValue() >= state)
                stateGroupToState.put(entry.getKey(), entry.getValue() - 1);
        }
    }

    void moveStateGroup(StateGroup stateGroup, double x, double y){


        int xg = gridClosest(x);
        int yg = gridClosest(y);
        stateGroup.setLayoutX(xg);
        stateGroup.setLayoutY(yg);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    Integer getState(StateGroup stateGroup) {
        return stateGroupToState.getV(stateGroup);
    }

    Transition addTransition(StateGroup start, StateGroup end){
        if(!TuringMachineDrawer.getInstance().editGraphMode)
            return null;
        Integer input = stateGroupToState.getV(start);
        Integer output = stateGroupToState.getV(end);
        return TuringMachineDrawer.getInstance().addTransition(input, output);
    }

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

        Iterator<Tape> it = TuringMachineDrawer.getInstance().machine.getTapes();

        while(it.hasNext()){
            Tape tape = it.next();
            transitionGroup.addTape(tape);

            for(int head = 0; head < tape.getNbHeads(); head++)
                transitionGroup.addHead(tape, TuringMachineDrawer.getInstance().getColorOfHead(tape, head));
        }
    }

    void removeTransition(TransitionGroup transitionGroup){
        Transition transition = transitionGroupToTransition.getV(transitionGroup);
        TuringMachineDrawer.getInstance().removeTransition(transition);
    }

    void removeTransition(Transition transition){
        TransitionGroup transitionGroup = transitionGroupToTransition.removeV(transition);
        this.closeStateOptionRectangle();
        this.closeTransitionOptionRectangle();
        graphGroup.getChildren().remove(transitionGroup);
    }

    Transition getTransition(TransitionGroup transitionGroup) {
        return transitionGroupToTransition.getV(transitionGroup);
    }

    void toggleFinal(StateGroup stateGroup){
        Integer state = stateGroupToState.getV(stateGroup);
        if(stateGroup.isAccepting())
            TuringMachineDrawer.getInstance().setAcceptingState(state, false);
        else
            TuringMachineDrawer.getInstance().setFinalState(state, !stateGroup.isFinal());
    }

    void toggleAccepting(StateGroup stateGroup){
        Integer state = stateGroupToState.getV(stateGroup);
        if(stateGroup.isAccepting())
            TuringMachineDrawer.getInstance().setFinalState(state, false);
        else
            TuringMachineDrawer.getInstance().setAcceptingState(state, true);
    }

    void toggleInitial(StateGroup stateGroup){
        Integer state = stateGroupToState.getV(stateGroup);
        TuringMachineDrawer.getInstance().setInitialState(state, !stateGroup.isInitial());
    }

    void setFinalState(Integer state, boolean isFinal){
        StateGroup stateGroup = stateGroupToState.getK(state);
        stateGroup.setFinal(isFinal);
    }

    void setAcceptingState(Integer state, boolean isAccepting){
        StateGroup stateGroup = stateGroupToState.getK(state);
        stateGroup.setAccepting(isAccepting);
    }

    void setInitialState(Integer state, boolean isInitial){
        StateGroup stateGroup = stateGroupToState.getK(state);
        stateGroup.setInitial(isInitial);
    }

    void addReadSymbol(TransitionGroup transitionGroup, Tape tape, int head, String symbol){
        Transition transition = transitionGroupToTransition.getV(transitionGroup);
        TuringMachineDrawer.getInstance().addReadSymbol(transition, tape, head, symbol);
    }

    void addReadSymbol(Transition transition, Tape tape, int head, String symbol){
        TransitionGroup arrow = transitionGroupToTransition.getK(transition);
        if(symbol == null)
            symbol = TuringMachineDrawer.BLANK_SYMBOL;
        arrow.addReadSymbol(tape, head, symbol);

        if(transitionOptionRectangle.currentTransitionGroup == arrow)
            transitionOptionRectangle.addReadSymbol(tape, head, symbol);
    }

    void removeReadSymbol(TransitionGroup transitionGroup, Tape tape, int head, String symbol){
        Transition transition = transitionGroupToTransition.getV(transitionGroup);
        TuringMachineDrawer.getInstance().removeReadSymbol(transition, tape, head, symbol);
    }

    void removeReadSymbol(Transition transition, Tape tape, int head, String symbol){
        TransitionGroup arrow = transitionGroupToTransition.getK(transition);
        if(symbol == null)
            symbol = TuringMachineDrawer.BLANK_SYMBOL;
        arrow.removeReadSymbol(tape, head, symbol);
        if(transitionOptionRectangle.currentTransitionGroup == arrow)
            transitionOptionRectangle.removeReadSymbol(tape, head, symbol);
    }

    void addAction(TransitionGroup transitionGroup, Tape tape, int head, String actionSymbol) {
        Transition transition = transitionGroupToTransition.getV(transitionGroup);
        TuringMachineDrawer.getInstance().addAction(transition, tape, head, actionSymbol);
    }

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

        if(transitionOptionRectangle.currentTransitionGroup == transitionGroup)
            transitionOptionRectangle.addAction(tape, head, actionSymbol);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    void removeAction(TransitionGroup transitionGroup){
        Transition transition = transitionGroupToTransition.getV(transitionGroup);
        TuringMachineDrawer.getInstance().removeAction(transition);
    }

    void removeAction(Transition transition, int index){
        TransitionGroup transitionGroup = transitionGroupToTransition.getK(transition);
        transitionGroup.removeAction(index);

        if(transitionOptionRectangle.currentTransitionGroup == transitionGroup)
            transitionOptionRectangle.removeAction(index);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    void addSymbol(String symbol) {
        this.transitionOptionRectangle.addSymbol(symbol);
    }

    void editSymbol(int index, String previousSymbol, String symbol) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.editSymbol(previousSymbol, symbol);
        this.transitionOptionRectangle.editSymbol(index, previousSymbol, symbol);
    }

    void removeSymbol(int index, String symbol) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.removeSymbol(symbol);
        this.transitionOptionRectangle.removeSymbol(index, symbol);
    }

    void addTape(Tape tape) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.addTape(tape);
        this.transitionOptionRectangle.addTape(tape);
    }

    void removeTape(Tape tape) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.removeTape(tape);
        this.transitionOptionRectangle.removeTape(tape);
    }

    void addHead(Tape tape, Color color) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.addHead(tape, color);
        this.transitionOptionRectangle.addHead(tape, color);
    }

    void editHeadColor(Tape tape, int head, Color color) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.editHeadColor(tape, head, color);
        this.transitionOptionRectangle.editHeadColor(tape, head, color);
    }

    void removeHead(Tape tape, int head) {
        for(TransitionGroup transitionGroup : transitionGroupToTransition.keySet())
            transitionGroup.removeHead(tape, head);
        this.transitionOptionRectangle.removeHead(tape, head);
    }

    Set<String> getReadSymbols(TransitionGroup transitionGroup, Tape tape, int head) {
        Transition transition = transitionGroupToTransition.getV(transitionGroup);

        if(transition == null)
            return new HashSet<>();

        Iterator<String> it =  transition.getReadSymbols(tape, head);
        Set<String> set = new HashSet<>();
        while(it.hasNext()){
            String symbol = it.next();
            symbol = (symbol == null)?TuringMachineDrawer.BLANK_SYMBOL:symbol;
            set.add(symbol);
        }
        return set;
    }

    void translate(double dx, double dy) {
        graphGroup.setTranslateX(graphGroup.getTranslateX() + dx);
        graphGroup.setTranslateY(graphGroup.getTranslateY() + dy);
    }


    void openStateOptionRectangle(StateGroup stateGroup){
        stateOptionRectangle.setCurrentState(null);
        stateOptionRectangle.setLayoutX(stateGroup.getLayoutX());
        stateOptionRectangle.setLayoutY(stateGroup.getLayoutY() - TuringMachineDrawer.STATE_RADIUS
                * TuringMachineDrawer.STATE_OPTION_RECTANGLE_DISTANCE_RATIO);
        stateOptionRectangle.setTranslateX(stateGroup.getTranslateX());
        stateOptionRectangle.setTranslateY(stateGroup.getTranslateY());
        stateOptionRectangle.setCurrentState(stateGroup);
        stateOptionRectangle.toFront();
        stateOptionRectangle.setVisible(true);
        stateOptionRectangle.maximize();
    }

    void closeStateOptionRectangle(){
        closeStateOptionRectangle(true);
    }
    void closeStateOptionRectangle(boolean animate){
        stateOptionRectangle.minimize(animate);
    }

    void openTransitionOptionRectangle(TransitionGroup transitionGroup){
        transitionOptionRectangle.setCurrentTransitionGroup(null);
        transitionOptionRectangle.setLayoutX(transitionGroup.getCenterX());
        transitionOptionRectangle.setLayoutY(transitionGroup.getCenterY());
        transitionOptionRectangle.setCurrentTransitionGroup(transitionGroup);
        transitionOptionRectangle.toFront();
        transitionOptionRectangle.setVisible(true);
        transitionOptionRectangle.maximize();
    }

    void closeTransitionOptionRectangle() { closeTransitionOptionRectangle(true); }
    void closeTransitionOptionRectangle(boolean animate){
        transitionOptionRectangle.minimize(animate);
    }

    void closeAllOptionRectangle() {
        closeStateOptionRectangle(false);
        closeTransitionOptionRectangle(false);
    }

    Timeline getChangeCurrentStateTimeline(Integer state) {
        StateGroup stateGroup = stateGroupToState.getK(state);

        Timeline timeline = new Timeline();

        KeyFrame keyFrame;

        KeyValue knew = stateGroup.getCurrentStateKeyValue();

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

    Timeline getFiredTransitionTimeline(Transition transition) {
        TransitionGroup transitionGroup = transitionGroupToTransition.getK(transition);

        Timeline timeline = new Timeline();

        KeyFrame keyFrame = transitionGroup.getFiredKeyValue();

        timeline.setCycleCount(2);
        timeline.setAutoReverse(true);
        timeline.getKeyFrames().add(keyFrame);

        return timeline;
    }

    JSONObject getJSON() {
        JSONArray jsonStates = new JSONArray();
        for(int state = 0; state < stateGroupToState.size(); state++)
            jsonStates.put(stateGroupToState.getK(state).getJSON());

        JSONArray jsonTransition = new JSONArray();
        for(Map.Entry<TransitionGroup, Transition> entry: transitionGroupToTransition.entrySet())
            jsonTransition.put(entry.getKey().getJSON());

        return new JSONObject().put("states", jsonStates).put("transitions", jsonTransition);
    }

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
}
