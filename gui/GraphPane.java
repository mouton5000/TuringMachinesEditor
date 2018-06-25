package gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import turingmachines.*;
import util.BidirMap;
import util.Pair;
import util.StringEnumerator;
import util.Subscriber;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by dimitri.watel on 18/06/18.
 */
class GraphPane extends Pane {

    TuringMachineDrawer drawer;
    StateOptionRectangle stateOptionRectangle;
    TransitionOptionRectangle transitionOptionRectangle;

    private double graphOffsetX;
    private double graphOffsetY;

    private BidirMap<StateGroup, Integer> stateGroupToState;
    private BidirMap<TransitionArrowGroup, Transition> arrowGroupToTransition;

    private StringEnumerator stringEnumerator;

    private Double nextX;
    private Double nextY;

    private Double nextControl1X;
    private Double nextControl1Y;
    private Double nextControl2X;
    private Double nextControl2Y;

    private StateGroup lastCurrentStateGroup;

    GraphPane(TuringMachineDrawer drawer){
        this.drawer = drawer;

        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        this.stateOptionRectangle = new StateOptionRectangle(drawer, this);
        this.stateOptionRectangle.setVisible(false);

        this.transitionOptionRectangle = new TransitionOptionRectangle(drawer, this);
        this.transitionOptionRectangle.setVisible(false);

        this.setOnMouseClicked(drawer.graphPaneMouseHandler);
        this.setOnMousePressed(drawer.graphPaneMouseHandler);
        this.setOnMouseDragged(drawer.graphPaneMouseHandler);


        this.getChildren().addAll(this.stateOptionRectangle, this.transitionOptionRectangle);

        Rectangle graphClip = new Rectangle();

        this.setClip(graphClip);

        this.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            graphClip.setWidth(newValue.getWidth());
            graphClip.setHeight(newValue.getHeight());
        });

        stateGroupToState = new BidirMap<>();
        arrowGroupToTransition = new BidirMap<>();

        clear();

        Subscriber s = new Subscriber() {
            @Override
            public void read(String msg, Object... parameters) {
                switch (msg){
                    case TuringMachine.SUBSCRIBER_MSG_ADD_STATE:{
                        Integer state = (Integer) parameters[1];
                        addStateFromMachine(state);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_REMOVE_STATE:{
                        Integer state = (Integer) parameters[1];
                        removeStateFromMachine(state);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_ADD_TRANSITION:{
                        Transition transition = (Transition) parameters[1];
                        addTransitionFromMachine(transition);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_REMOVE_TRANSITION:{
                        Transition transition = (Transition) parameters[1];
                        removeTransitionFromMachine(transition);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_ADD_READ_SYMBOL:{
                        Transition transition = (Transition) parameters[1];
                        Tape tape = (Tape)parameters[2];
                        Integer head = (Integer)parameters[3];
                        String symbol = (String)parameters[4];
                        addReadSymbolFromMachine(transition, tape, head, symbol);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_REMOVE_READ_SYMBOL:{
                        Transition transition = (Transition) parameters[1];
                        Tape tape = (Tape)parameters[2];
                        Integer head = (Integer)parameters[3];
                        String symbol = (String)parameters[4];
                        removeReadSymbolFromMachine(transition, tape, head, symbol);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_ADD_ACTION:{
                        Transition transition = (Transition) parameters[1];
                        Tape tape = (Tape)parameters[2];
                        Integer head = (Integer)parameters[3];
                        ActionType type = (ActionType) parameters[4];
                        Object value = parameters[5];
                        addActionFromMachine(transition, tape, head, type, value);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_REMOVE_ACTION:{
                        Transition transition = (Transition) parameters[1];
                        Integer index = (Integer) parameters[2];
                        removeActionFromMachine(transition, index);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_SET_FINAL_STATE:{
                        Integer state = (Integer) parameters[1];
                        setFinalStateFromMachine(state, true);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_UNSET_FINAL_STATE:{
                        Integer state = (Integer) parameters[1];
                        setFinalStateFromMachine(state, false);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_SET_ACCEPTING_STATE:{
                        Integer state = (Integer) parameters[1];
                        setAcceptingStateFromMachine(state, true);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_UNSET_ACCEPTING_STATE:{
                        Integer state = (Integer) parameters[1];
                        setAcceptingStateFromMachine(state, false);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_SET_INITIAL_STATE:{
                        Integer state = (Integer) parameters[1];
                        setInitialStateFromMachine(state, true);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_UNSET_INITIAL_STATE:{
                        Integer state = (Integer) parameters[1];
                        setInitialStateFromMachine(state, false);
                    }
                    break;
                }
            }
        };

        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_REMOVE_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_TRANSITION);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_REMOVE_TRANSITION);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_READ_SYMBOL);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_REMOVE_READ_SYMBOL);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_ACTION);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_REMOVE_ACTION);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_SET_FINAL_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_UNSET_FINAL_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_SET_ACCEPTING_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_UNSET_ACCEPTING_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_SET_INITIAL_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_UNSET_INITIAL_STATE);
    }

    void clear() {
        graphOffsetX = 0;
        graphOffsetY = 0;
        stringEnumerator = new StringEnumerator();
        lastCurrentStateGroup = null;

        closeStateOptionRectangle();
        closeTransitionOptionRectangle();
        stateOptionRectangle.clear();
        transitionOptionRectangle.clear();
    }


    private int gridClosest(double value){
        return ((int)value / TuringMachineDrawer.GRAPH_GRID_WIDTH) * TuringMachineDrawer.GRAPH_GRID_WIDTH;
    }

    int addState(double x, double y){
        String name = stringEnumerator.next();
        return this.addState(x, y, name);
    }

    int addState(double x, double y, String name){
        nextX = x;
        nextY = y;
        return drawer.machine.addState(name);
    }

    void addStateFromMachine(Integer state){
        String name = drawer.machine.getStateName(state);
        StateGroup circle = new StateGroup(this.drawer, name);
        this.moveStateGroup(circle, nextX, nextY);

        stateGroupToState.put(circle, state);
        this.getChildren().add(circle);

    }

    void removeState(StateGroup stateGroup) {
        removeState(stateGroup, true);
    }

    void removeState(StateGroup stateGroup, boolean doConfirm){
        Integer state = stateGroupToState.getV(stateGroup);

        if(doConfirm){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Supprimer l'état?");
            alert.setHeaderText("");
            alert.setContentText("Confirmer la suppression.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    this.drawer.machine.removeState(state);
            });
        }
        else
            this.drawer.machine.removeState(state);
    }

    void removeStateFromMachine(Integer state){
        StateGroup stateGroup = stateGroupToState.removeV(state);
        this.closeStateOptionRectangle();
        this.closeTransitionOptionRectangle();
        this.getChildren().remove(stateGroup);

        Set<Map.Entry<StateGroup, Integer>> entries = new HashSet<>(stateGroupToState.entrySet());
        for(Map.Entry<StateGroup, Integer> entry : entries){
            if(entry.getValue() >= state)
                stateGroupToState.put(entry.getKey(), entry.getValue() - 1);
        }
    }

    void moveStateGroup(StateGroup stateGroup, double x, double y){
        int xg = gridClosest(x);
        int yg = gridClosest(y);
        stateGroup.setLayoutX(xg + graphOffsetX);
        stateGroup.setLayoutY(yg + graphOffsetY);
    }

    Integer getState(StateGroup stateGroup) {
        return stateGroupToState.getV(stateGroup);
    }

    Transition addTransition(StateGroup start, StateGroup end){
        return addTransition(start, end, null, null, null, null);
    }


    Transition addTransition(StateGroup start, StateGroup end,
                             Double control1X, Double control1Y,
                             Double control2X, Double control2Y
    ){
        Integer input = stateGroupToState.getV(start);
        Integer output = stateGroupToState.getV(end);

        this.nextControl1X = control1X;
        this.nextControl1Y = control1Y;
        this.nextControl2X = control2X;
        this.nextControl2Y = control2Y;

        return drawer.machine.addTransition(input, output);
    }

    private void addTransitionFromMachine(Transition transition){
        Integer input = transition.getInput();
        Integer output = transition.getOutput();

        StateGroup start = stateGroupToState.getK(input);
        StateGroup end = stateGroupToState.getK(output);

        TransitionArrowGroup transitionArrowGroup = new TransitionArrowGroup(this.drawer, start, end);
        if(nextControl1X != null)
            transitionArrowGroup.setControl1(nextControl1X, nextControl1Y);
        if(nextControl2X != null)
            transitionArrowGroup.setControl2(nextControl2X, nextControl2Y);

        arrowGroupToTransition.put(transitionArrowGroup, transition);
        this.getChildren().add(transitionArrowGroup);
        transitionArrowGroup.toBack();

        Iterator<Tape> it = drawer.machine.getTapes();

        while(it.hasNext()){
            Tape tape = it.next();
            transitionArrowGroup.addTape(tape);

            for(int head = 0; head < tape.getNbHeads(); head++)
                transitionArrowGroup.addHead(tape, drawer.getColorOfHead(tape, head));
        }
    }

    void removeTransition(TransitionArrowGroup transitionArrowGroup){
        removeTransition(transitionArrowGroup, true);
    }

    void removeTransition(TransitionArrowGroup transitionArrowGroup, boolean doConfirm){
        Transition transition = arrowGroupToTransition.getV(transitionArrowGroup);

        if(doConfirm){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Supprimer la transition?");
            alert.setHeaderText("");
            alert.setContentText("Confirmer la suppression.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    this.drawer.machine.removeTransition(transition);
            });
        }
        else
            this.drawer.machine.removeTransition(transition);
    }

    void removeTransitionFromMachine(Transition transition){
        TransitionArrowGroup transitionArrowGroup = arrowGroupToTransition.removeV(transition);
        this.closeStateOptionRectangle();
        this.closeTransitionOptionRectangle();
        this.getChildren().remove(transitionArrowGroup);

    }

    void toggleFinal(StateGroup stateGroup){
        Integer state = stateGroupToState.getV(stateGroup);
        if(stateGroup.isAccepting())
            drawer.machine.unsetAcceptingState(state);
        else {
            if(stateGroup.isFinal())
                drawer.machine.unsetFinalState(state);
            else
                drawer.machine.setFinalState(state);
        }
    }

    void toggleAccepting(StateGroup stateGroup){
        Integer state = stateGroupToState.getV(stateGroup);
        if(stateGroup.isAccepting())
            drawer.machine.unsetFinalState(state);
        else
            drawer.machine.setAcceptingState(state);
    }

    void toggleInitial(StateGroup stateGroup){
        Integer state = stateGroupToState.getV(stateGroup);
        if(stateGroup.isInitial())
            drawer.machine.unsetInitialState(state);
        else
            drawer.machine.setInitialState(state);
    }

    private void setFinalStateFromMachine(Integer state, boolean isFinal){
        StateGroup stateGroup = stateGroupToState.getK(state);
        stateGroup.setFinal(isFinal);
    }

    private void setAcceptingStateFromMachine(Integer state, boolean isAccepting){
        StateGroup stateGroup = stateGroupToState.getK(state);
        stateGroup.setAccepting(isAccepting);
    }

    private void setInitialStateFromMachine(Integer state, boolean isInitial){
        StateGroup stateGroup = stateGroupToState.getK(state);
        stateGroup.setInitial(isInitial);
    }

    void addReadSymbol(TransitionArrowGroup transitionArrowGroup, Tape tape, int head, String symbol){
        symbol = (symbol.equals(TuringMachineDrawer.BLANK_SYMBOL))?
                null:symbol;
        Transition transition = arrowGroupToTransition.getV(transitionArrowGroup);
        transition.addReadSymbols(tape, head, symbol);
    }

    void removeReadSymbol(TransitionArrowGroup transitionArrowGroup, Tape tape, int head, String symbol){
        symbol = (symbol.equals(TuringMachineDrawer.BLANK_SYMBOL))?
                null:symbol;
        Transition transition = arrowGroupToTransition.getV(transitionArrowGroup);
        transition.removeReadSymbols(tape, head, symbol);
    }

    private void addReadSymbolFromMachine(Transition transition, Tape tape, int head, String symbol){
        TransitionArrowGroup arrow = arrowGroupToTransition.getK(transition);
        if(symbol == null)
            symbol = TuringMachineDrawer.BLANK_SYMBOL;
        arrow.addReadSymbol(tape, head, symbol);

        if(transitionOptionRectangle.currentTransitionArrowGroup == arrow)
            transitionOptionRectangle.addReadSymbol(tape, head, symbol);
    }

    private void removeReadSymbolFromMachine(Transition transition, Tape tape, int head, String symbol){
        TransitionArrowGroup arrow = arrowGroupToTransition.getK(transition);
        if(symbol == null)
            symbol = TuringMachineDrawer.BLANK_SYMBOL;
        arrow.removeReadSymbol(tape, head, symbol);
        if(transitionOptionRectangle.currentTransitionArrowGroup == arrow)
            transitionOptionRectangle.removeReadSymbol(tape, head, symbol);
    }

    void addAction(TransitionArrowGroup transitionArrowGroup, Tape tape, int head, String actionSymbol) {
        Transition transition = arrowGroupToTransition.getV(transitionArrowGroup);

        Action action;
        switch (actionSymbol){
            case TuringMachineDrawer.LEFT_SYMBOL:
                action = new MoveAction(tape, head, Direction.LEFT);
                break;
            case TuringMachineDrawer.RIGHT_SYMBOL:
                action = new MoveAction(tape, head, Direction.RIGHT);
                break;
            case TuringMachineDrawer.DOWN_SYMBOL:
                action = new MoveAction(tape, head, Direction.DOWN);
                break;
            case TuringMachineDrawer.UP_SYMBOL:
                action = new MoveAction(tape, head, Direction.UP);
                break;
            case TuringMachineDrawer.BLANK_SYMBOL:
                actionSymbol = null;
            default:
                action = new WriteAction(tape, head, actionSymbol);
                break;
        }

        transition.addAction(action);
    }

    void removeAction(TransitionArrowGroup transitionArrowGroup){
        Transition transition = arrowGroupToTransition.getV(transitionArrowGroup);
        transition.removeAction(transition.getNbActions() - 1);
    }

    void addActionFromMachine(Transition transition, Tape tape, int head, ActionType type, Object value){
        TransitionArrowGroup transitionArrowGroup = arrowGroupToTransition.getK(transition);

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

        transitionArrowGroup.addAction(tape, head, actionSymbol);

        if(transitionOptionRectangle.currentTransitionArrowGroup == transitionArrowGroup)
            transitionOptionRectangle.addAction(tape, head, actionSymbol);
    }

    void removeActionFromMachine(Transition transition, int index){
        TransitionArrowGroup transitionArrowGroup = arrowGroupToTransition.getK(transition);
        transitionArrowGroup.removeAction(index);

        if(transitionOptionRectangle.currentTransitionArrowGroup == transitionArrowGroup)
            transitionOptionRectangle.removeAction(index);
    }

    void addSymbol(String symbol) {
        this.transitionOptionRectangle.addSymbol(symbol);
    }

    void editSymbol(int index, String previousSymbol, String symbol) {
        for(TransitionArrowGroup transitionArrowGroup : arrowGroupToTransition.keySet())
            transitionArrowGroup.editSymbol(previousSymbol, symbol);
        this.transitionOptionRectangle.editSymbol(index, previousSymbol, symbol);
    }

    void removeSymbol(int index, String symbol) {
        for(TransitionArrowGroup transitionArrowGroup : arrowGroupToTransition.keySet())
            transitionArrowGroup.removeSymbol(symbol);
        this.transitionOptionRectangle.removeSymbol(index, symbol);
    }

    void addTape(Tape tape) {
        for(TransitionArrowGroup transitionArrowGroup : arrowGroupToTransition.keySet())
            transitionArrowGroup.addTape(tape);
        this.transitionOptionRectangle.addTape(tape);
    }

    void removeTape(Tape tape) {
        for(TransitionArrowGroup transitionArrowGroup : arrowGroupToTransition.keySet())
            transitionArrowGroup.removeTape(tape);
        this.transitionOptionRectangle.removeTape(tape);
    }

    void addHead(Tape tape, Color color) {
        for(TransitionArrowGroup transitionArrowGroup : arrowGroupToTransition.keySet())
            transitionArrowGroup.addHead(tape, color);
        this.transitionOptionRectangle.addHead(tape, color);
    }

    void editHeadColor(Tape tape, int head, Color color) {
        for(TransitionArrowGroup transitionArrowGroup : arrowGroupToTransition.keySet())
            transitionArrowGroup.editHeadColor(tape, head, color);
        this.transitionOptionRectangle.editHeadColor(tape, head, color);
    }

    void removeHead(Tape tape, int head) {
        for(TransitionArrowGroup transitionArrowGroup : arrowGroupToTransition.keySet())
            transitionArrowGroup.removeHead(tape, head);
        this.transitionOptionRectangle.removeHead(tape, head);
    }

    Set<String> getReadSymbols(TransitionArrowGroup transitionArrowGroup, Tape tape, int head) {
        Transition transition = arrowGroupToTransition.getV(transitionArrowGroup);

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
        for(Node child : getChildren()) {
            if(child instanceof StateGroup) {
                child.setLayoutX(child.getLayoutX() + dx);
                child.setLayoutY(child.getLayoutY() + dy);
            }
        }

        graphOffsetX = (graphOffsetX + dx) % TuringMachineDrawer.GRAPH_GRID_WIDTH;
        graphOffsetY = (graphOffsetY + dy) % TuringMachineDrawer.GRAPH_GRID_WIDTH;
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

    void openTransitionOptionRectangle(TransitionArrowGroup transitionArrowGroup){
        transitionOptionRectangle.setCurrentTransitionArrowGroup(null);
        transitionOptionRectangle.setLayoutX(transitionArrowGroup.getCenterX());
        transitionOptionRectangle.setLayoutY(transitionArrowGroup.getCenterY());
        transitionOptionRectangle.setCurrentTransitionArrowGroup(transitionArrowGroup);
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
            keyFrame = new KeyFrame(Duration.millis(TuringMachineDrawer.CURRENT_STATE_ANIMATION_DURATION), klast, knew);
        }
        else
            keyFrame = new KeyFrame(Duration.millis(TuringMachineDrawer.CURRENT_STATE_ANIMATION_DURATION), knew);

        timeline.getKeyFrames().add(keyFrame);

        lastCurrentStateGroup = stateGroup;
        return timeline;
    }

    Timeline getRemoveCurrentStateTimeline() {

        if(lastCurrentStateGroup != null){
            Timeline timeline = new Timeline();

            KeyFrame keyFrame;
            KeyValue klast = lastCurrentStateGroup.getNoCurrentStateKeyValue();
            keyFrame = new KeyFrame(Duration.millis(TuringMachineDrawer.CURRENT_STATE_ANIMATION_DURATION), klast);
            timeline.getKeyFrames().add(keyFrame);
            lastCurrentStateGroup = null;

            return timeline;
        }
        else
            return null;
    }

    Timeline getFiredTransitionTimeline(Transition transition) {
        TransitionArrowGroup transitionArrowGroup = arrowGroupToTransition.getK(transition);

        Timeline timeline = new Timeline();

        KeyFrame keyFrame = transitionArrowGroup.getFiredKeyValue();

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
        for(Map.Entry<TransitionArrowGroup, Transition> entry: arrowGroupToTransition.entrySet())
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

            int state = this.addState(x, y, name);
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
            StateGroup inputStateGroup = stateGroupToState.getK(inputState);
            StateGroup outputStateGroup = stateGroupToState.getK(outputState);

            double control1X = jsonTransition.getDouble("control1X");
            double control1Y = jsonTransition.getDouble("control1Y");
            double control2X = jsonTransition.getDouble("control2X");
            double control2Y = jsonTransition.getDouble("control2Y");

            Transition transition = this.addTransition(inputStateGroup, outputStateGroup,
                    control1X, control1Y,
                    control2X, control2Y);
            TransitionArrowGroup transitionArrowGroup = arrowGroupToTransition.getK(transition);

            JSONObject jsonDisplay = jsonTransition.getJSONObject("display");
            JSONArray jsonReadSymbols = jsonDisplay.getJSONArray("readSymbols");
            for(int tapeId = 0; tapeId < jsonReadSymbols.length(); tapeId++){
                JSONArray jsonReadSymbolsOfTape = jsonReadSymbols.getJSONArray(tapeId);
                Tape tape = drawer.machine.getTape(tapeId);
                for(int head = 0; head < jsonReadSymbolsOfTape.length(); head++){
                    JSONArray jsonReadSymbolsOfHead = jsonReadSymbolsOfTape.getJSONArray(head);
                    for(Object symbol : jsonReadSymbolsOfHead)
                        this.addReadSymbol(transitionArrowGroup, tape, head, (String)symbol);
                }
            }

            JSONArray jsonActions = jsonDisplay.getJSONArray("actions");
            for(int j = 0; j < jsonActions.length(); j++){
                JSONObject jsonAction = jsonActions.getJSONObject(j);

                Color color = Color.valueOf(jsonAction.getString("color"));
                Pair<Tape, Integer> pair = drawer.getHead(color);
                Tape tape = pair.first;
                Integer head = pair.second;

                String actionSymbol = jsonAction.getString("actionSymbol");
                this.addAction(transitionArrowGroup, tape, head, actionSymbol);
            }

        }

    }
}
