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

    StateOptionRectangle stateOptionRectangle;
    TransitionOptionRectangle transitionOptionRectangle;

    private double graphOffsetX;
    private double graphOffsetY;

    private BidirMap<StateGroup, Integer> stateGroupToState;
    private BidirMap<TransitionGroup, Transition> transitionGroupToTransition;

    private StringEnumerator stringEnumerator;

    private Double nextX;
    private Double nextY;

    private Double nextControl1X;
    private Double nextControl1Y;
    private Double nextControl2X;
    private Double nextControl2Y;

    private StateGroup lastCurrentStateGroup;

    GraphPane(){

        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        this.stateOptionRectangle = new StateOptionRectangle( this);
        this.stateOptionRectangle.setVisible(false);

        this.transitionOptionRectangle = new TransitionOptionRectangle(this);
        this.transitionOptionRectangle.setVisible(false);

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().graphPaneMouseHandler);
        this.setOnMousePressed(TuringMachineDrawer.getInstance().graphPaneMouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().graphPaneMouseHandler);


        this.getChildren().addAll(this.stateOptionRectangle, this.transitionOptionRectangle);

        Rectangle graphClip = new Rectangle();

        this.setClip(graphClip);

        this.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            graphClip.setWidth(newValue.getWidth());
            graphClip.setHeight(newValue.getHeight());
        });

        stateGroupToState = new BidirMap<>();
        transitionGroupToTransition = new BidirMap<>();

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
        if(!TuringMachineDrawer.getInstance().editGraphMode)
            return -1;
        String name = stringEnumerator.next();
        return this.addState(x, y, name);
    }

    int addState(double x, double y, String name){
        if(!TuringMachineDrawer.getInstance().editGraphMode)
            return -1;
        nextX = x;
        nextY = y;
        return TuringMachineDrawer.getInstance().machine.addState(name);
    }

    void addStateFromMachine(Integer state){
        String name = TuringMachineDrawer.getInstance().machine.getStateName(state);
        StateGroup circle = new StateGroup(name);
        this.moveStateGroup(circle, nextX, nextY);

        stateGroupToState.put(circle, state);
        this.getChildren().add(circle);

        TuringMachineDrawer.getInstance().setEnableToSave();

    }

    void removeState(StateGroup stateGroup) {
        removeState(stateGroup, true);
    }

    void removeState(StateGroup stateGroup, boolean doConfirm){
        if(!TuringMachineDrawer.getInstance().editGraphMode)
            return;
        Integer state = stateGroupToState.getV(stateGroup);

        if(doConfirm){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Supprimer l'état?");
            alert.setHeaderText("");
            alert.setContentText("Confirmer la suppression.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    TuringMachineDrawer.getInstance().machine.removeState(state);
            });
        }
        else
            TuringMachineDrawer.getInstance().machine.removeState(state);
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
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    void moveStateGroup(StateGroup stateGroup, double x, double y){
        int xg = gridClosest(x);
        int yg = gridClosest(y);
        stateGroup.setLayoutX(xg + graphOffsetX);
        stateGroup.setLayoutY(yg + graphOffsetY);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    Integer getState(StateGroup stateGroup) {
        return stateGroupToState.getV(stateGroup);
    }

    Transition addTransition(StateGroup start, StateGroup end){
        if(!TuringMachineDrawer.getInstance().editGraphMode)
            return null;
        return addTransition(start, end, null, null, null, null);
    }


    Transition addTransition(StateGroup start, StateGroup end,
                             Double control1X, Double control1Y,
                             Double control2X, Double control2Y
    ){
        if(!TuringMachineDrawer.getInstance().editGraphMode)
            return null;
        Integer input = stateGroupToState.getV(start);
        Integer output = stateGroupToState.getV(end);

        this.nextControl1X = control1X;
        this.nextControl1Y = control1Y;
        this.nextControl2X = control2X;
        this.nextControl2Y = control2Y;

        return TuringMachineDrawer.getInstance().machine.addTransition(input, output);
    }

    private void addTransitionFromMachine(Transition transition){
        Integer input = transition.getInput();
        Integer output = transition.getOutput();

        StateGroup start = stateGroupToState.getK(input);
        StateGroup end = stateGroupToState.getK(output);

        TransitionGroup transitionGroup = new TransitionGroup(start, end);
        if(nextControl1X != null)
            transitionGroup.setControl1(nextControl1X, nextControl1Y);
        if(nextControl2X != null)
            transitionGroup.setControl2(nextControl2X, nextControl2Y);

        transitionGroupToTransition.put(transitionGroup, transition);
        this.getChildren().add(transitionGroup);
        transitionGroup.toBack();

        Iterator<Tape> it = TuringMachineDrawer.getInstance().machine.getTapes();

        while(it.hasNext()){
            Tape tape = it.next();
            transitionGroup.addTape(tape);

            for(int head = 0; head < tape.getNbHeads(); head++)
                transitionGroup.addHead(tape, TuringMachineDrawer.getInstance().getColorOfHead(tape, head));
        }
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    void removeTransition(TransitionGroup transitionGroup){
        removeTransition(transitionGroup, true);
    }

    void removeTransition(TransitionGroup transitionGroup, boolean doConfirm){
        if(!TuringMachineDrawer.getInstance().editGraphMode)
            return;

        Transition transition = transitionGroupToTransition.getV(transitionGroup);

        if(doConfirm){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Supprimer la transition?");
            alert.setHeaderText("");
            alert.setContentText("Confirmer la suppression.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    TuringMachineDrawer.getInstance().machine.removeTransition(transition);
            });
        }
        else
            TuringMachineDrawer.getInstance().machine.removeTransition(transition);
    }

    void removeTransitionFromMachine(Transition transition){
        TransitionGroup transitionGroup = transitionGroupToTransition.removeV(transition);
        this.closeStateOptionRectangle();
        this.closeTransitionOptionRectangle();
        this.getChildren().remove(transitionGroup);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    Transition getTransition(TransitionGroup transitionGroup) {
        return transitionGroupToTransition.getV(transitionGroup);
    }

    void toggleFinal(StateGroup stateGroup){
        Integer state = stateGroupToState.getV(stateGroup);
        if(stateGroup.isAccepting())
            TuringMachineDrawer.getInstance().machine.unsetAcceptingState(state);
        else {
            if(stateGroup.isFinal())
                TuringMachineDrawer.getInstance().machine.unsetFinalState(state);
            else
                TuringMachineDrawer.getInstance().machine.setFinalState(state);
        }
    }

    void toggleAccepting(StateGroup stateGroup){
        Integer state = stateGroupToState.getV(stateGroup);
        if(stateGroup.isAccepting())
            TuringMachineDrawer.getInstance().machine.unsetFinalState(state);
        else
            TuringMachineDrawer.getInstance().machine.setAcceptingState(state);
    }

    void toggleInitial(StateGroup stateGroup){
        Integer state = stateGroupToState.getV(stateGroup);
        if(stateGroup.isInitial())
            TuringMachineDrawer.getInstance().machine.unsetInitialState(state);
        else
            TuringMachineDrawer.getInstance().machine.setInitialState(state);
    }

    private void setFinalStateFromMachine(Integer state, boolean isFinal){
        StateGroup stateGroup = stateGroupToState.getK(state);
        stateGroup.setFinal(isFinal);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    private void setAcceptingStateFromMachine(Integer state, boolean isAccepting){
        StateGroup stateGroup = stateGroupToState.getK(state);
        stateGroup.setAccepting(isAccepting);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    private void setInitialStateFromMachine(Integer state, boolean isInitial){
        StateGroup stateGroup = stateGroupToState.getK(state);
        stateGroup.setInitial(isInitial);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    void addReadSymbol(TransitionGroup transitionGroup, Tape tape, int head, String symbol){
        symbol = (symbol.equals(TuringMachineDrawer.BLANK_SYMBOL))?
                null:symbol;
        Transition transition = transitionGroupToTransition.getV(transitionGroup);
        transition.addReadSymbols(tape, head, symbol);
    }

    void removeReadSymbol(TransitionGroup transitionGroup, Tape tape, int head, String symbol){
        symbol = (symbol.equals(TuringMachineDrawer.BLANK_SYMBOL))?
                null:symbol;
        Transition transition = transitionGroupToTransition.getV(transitionGroup);
        transition.removeReadSymbols(tape, head, symbol);
    }

    private void addReadSymbolFromMachine(Transition transition, Tape tape, int head, String symbol){
        TransitionGroup arrow = transitionGroupToTransition.getK(transition);
        if(symbol == null)
            symbol = TuringMachineDrawer.BLANK_SYMBOL;
        arrow.addReadSymbol(tape, head, symbol);

        if(transitionOptionRectangle.currentTransitionGroup == arrow)
            transitionOptionRectangle.addReadSymbol(tape, head, symbol);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    private void removeReadSymbolFromMachine(Transition transition, Tape tape, int head, String symbol){
        TransitionGroup arrow = transitionGroupToTransition.getK(transition);
        if(symbol == null)
            symbol = TuringMachineDrawer.BLANK_SYMBOL;
        arrow.removeReadSymbol(tape, head, symbol);
        if(transitionOptionRectangle.currentTransitionGroup == arrow)
            transitionOptionRectangle.removeReadSymbol(tape, head, symbol);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    void addAction(TransitionGroup transitionGroup, Tape tape, int head, String actionSymbol) {
        Transition transition = transitionGroupToTransition.getV(transitionGroup);

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

    void removeAction(TransitionGroup transitionGroup){
        Transition transition = transitionGroupToTransition.getV(transitionGroup);
        transition.removeAction(transition.getNbActions() - 1);
    }

    void addActionFromMachine(Transition transition, Tape tape, int head, ActionType type, Object value){
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

    void removeActionFromMachine(Transition transition, int index){
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
