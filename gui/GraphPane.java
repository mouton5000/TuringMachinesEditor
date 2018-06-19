package gui;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import turingmachines.Tape;
import turingmachines.Transition;
import turingmachines.TuringMachine;
import util.BidirMap;
import util.Subscriber;

import java.util.HashMap;
import java.util.Map;

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
    private Map<Group, Transition> arrowGroupToTransition;

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
        arrowGroupToTransition = new HashMap<Group, Transition>();

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

    void addTransitionFromMachine(Transition transition){
        Integer input = transition.getInput();
        Integer output = transition.getOutput();

        StateGroup start = stateGroupToState.getK(input);
        StateGroup end = stateGroupToState.getK(output);

        TransitionArrowGroup arrow = new TransitionArrowGroup(this.drawer, start, end);
        if(nextControl1X != null)
            arrow.setControl1(nextControl1X, nextControl1Y);
        if(nextControl2X != null)
            arrow.setControl2(nextControl2X, nextControl2Y);

        arrowGroupToTransition.put(arrow, transition);
        this.getChildren().add(arrow);
        arrow.toBack();
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


    void addSymbol(String symbol) {
        this.transitionOptionRectangle.addSymbol(symbol);
    }

    void removeSymbol(String symbol) {
        this.transitionOptionRectangle.removeSymbol(symbol);
    }

    void addTape(Tape tape) {
        this.transitionOptionRectangle.addTape(tape);
    }

    void removeTape(Tape tape) {
        this.transitionOptionRectangle.removeTape(tape);
    }

    void addHead(Tape tape, Color color) {
        this.transitionOptionRectangle.addHead(tape, color);
    }

    void editHeadColor(Tape tape, Integer head, Color color) {
        this.transitionOptionRectangle.editHeadColor(tape, head, color);
    }

    void removeHead(Tape tape, int head) {
        this.transitionOptionRectangle.removeHead(tape, head);
    }
}
