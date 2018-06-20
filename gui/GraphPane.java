package gui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import turingmachines.*;
import util.BidirMap;
import util.Subscriber;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by dimitri.watel on 18/06/18.
 */
public class GraphPane extends Pane {

    TuringMachineDrawer drawer;
    StateOptionRectangle stateOptionRectangle;
    TransitionOptionRectangle transitionOptionRectangle;

    private double graphOffsetX;
    private double graphOffsetY;

    private BidirMap<StateGroup, Integer> stateGroupToState;
    private BidirMap<TransitionArrowGroup, Transition> arrowGroupToTransition;

    private char currentDefaultStateChar;

    private Double nextX;
    private Double nextY;

    private Double nextControl1X;
    private Double nextControl1Y;
    private Double nextControl2X;
    private Double nextControl2Y;

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

        graphOffsetX = 0;
        graphOffsetY = 0;

        currentDefaultStateChar = 'A';

        Subscriber s = new Subscriber() {
            @Override
            public void read(String msg, Object... parameters) {
                switch (msg){
                    case TuringMachine.SUBSCRIBER_MSG_ADD_STATE:{
                        Integer state = (Integer) parameters[1];
                        addStateFromMachine(state);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_ADD_TRANSITION:{
                        Transition transition = (Transition) parameters[1];
                        addTransitionFromMachine(transition);
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
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_TRANSITION);
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


    private int gridClosest(double value){
        return ((int)value / TuringMachineDrawer.GRAPH_GRID_WIDTH) * TuringMachineDrawer.GRAPH_GRID_WIDTH;
    }

    void addState(double x, double y){
        String name = Character.toString(currentDefaultStateChar);
        this.addState(x, y, name);
    }

    void addState(double x, double y, String name){
        nextX = x;
        nextY = y;
        drawer.machine.addState(name);
    }

    void addStateFromMachine(Integer state){
        currentDefaultStateChar++;
        String name = drawer.machine.getStateName(state);
        StateGroup circle = new StateGroup(this.drawer, name);
        this.moveStateGroup(circle, nextX, nextY);

        stateGroupToState.put(circle, state);
        this.getChildren().add(circle);

    }
    void moveStateGroup(StateGroup stateGroup, double x, double y){
        int xg = gridClosest(x);
        int yg = gridClosest(y);
        stateGroup.setLayoutX(xg + graphOffsetX);
        stateGroup.setLayoutY(yg + graphOffsetY);
    }

    void addTransition(StateGroup start, StateGroup end){
        addTransition(start, end, null, null, null, null);
    }


    void addTransition(StateGroup start, StateGroup end,
                       Double nextControl1X, Double nextControl1Y,
                       Double nextControl2X, Double nextControl2Y
    ){
        Integer input = stateGroupToState.getV(start);
        Integer output = stateGroupToState.getV(end);

        this.nextControl1X = nextControl1X;
        this.nextControl1Y = nextControl1Y;
        this.nextControl2X = nextControl2X;
        this.nextControl2Y = nextControl2Y;

        drawer.machine.addTransition(input, output);
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
        Transition transition = arrowGroupToTransition.getV(transitionArrowGroup);
        transition.addReadSymbols(tape, head, symbol);
    }

    void removeReadSymbol(TransitionArrowGroup transitionArrowGroup, Tape tape, int head, String symbol){
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

    void removeSymbol(String symbol) {
        this.transitionOptionRectangle.removeSymbol(symbol);
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
        stateOptionRectangle.minimize(true);
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

    void closeTransitionOptionRectangle(){
        transitionOptionRectangle.minimize(true);
    }

}
