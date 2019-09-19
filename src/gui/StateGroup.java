/*
 * Copyright (c) 2018 Dimitri Watel
 */

package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.json.JSONObject;
import turingmachines.TuringMachine;
import util.MouseListener;
import util.Ressources;

/**
 * Widget corresponding to a state of the machine.
 *
 * The widget is a group containing the circle representing the state itself, the label displaying the name of the
 * state and other shapes or images to display the state as final, accepting and/or initial.
 */
class StateGroup extends Group implements MouseListener {

    /**
     * The state of the machine this state is associated with.
     */
    int state;

    /**
     * Main circle of the state
     */
    private Circle outerCircle;

    /**
     * Inner circle drawn when the state is final or accepting.
     */
    private Circle innerCircle;

    /**
     * Small icon drawn when the state is accepting.
     */
    private ImageView acceptIcon;

    /**
     * Line drawn when the state is initial.
     */
    private Line initLine1;

    /**
     * Line drawn when the state is initial.
     */
    private Line initLine2;

    /**
     * Label containing the name of the state
     */
    private Label label;

    /**
     * When the user press the mouse on the state, the widget gets darker during a fixed period of time. This parameter
     * is true if the user is pressing the state and if the timer has not ended.
     */
    private boolean animating;

    /**
     * When the user press the mouse on the state, using this timeline, the widget gets darker during a fixed period of
     * time.
     */
    private Timeline timeline;

    /**
     * True if and only if the state is deterministic.
     */
    private boolean deterministic;

    /**
     * True if and only if the state is selected.
     */
    private boolean selected;

    /**
     * Build a new widget corresponding to a state with the given name.
     * @param state
     */
    StateGroup(Integer state){
        this.state = state;
        String name = TuringMachineDrawer.getInstance().machine.getStateName(state);

        this.timeline = new Timeline();
        this.timeline.setOnFinished(actionEvent -> animating = false);

        this.deterministic = true;

        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);

        outerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        outerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        outerCircle.setFill(getUnselectedFillColor());

        innerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS *
                TuringMachineDrawer.FINAL_STATE_RADIUS_RATIO);
        innerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        innerCircle.setFill(getUnselectedFillColor());
        innerCircle.setVisible(false);
        
        acceptIcon = new ImageView(Ressources.getRessource("Accept-icon.png"));
        acceptIcon.setLayoutX(
                TuringMachineDrawer.STATE_RADIUS * Math.sqrt(2) / 2
                        - acceptIcon.getBoundsInLocal().getWidth() / 2);
        acceptIcon.setLayoutY(
                TuringMachineDrawer.STATE_RADIUS * Math.sqrt(2) / 2
                        - acceptIcon.getBoundsInLocal().getHeight() / 2);
        acceptIcon.setVisible(false);

        initLine1 = new Line();
        initLine2 = new Line();
        initLine1.setStartX(-TuringMachineDrawer.STATE_RADIUS);
        initLine2.setStartX(-TuringMachineDrawer.STATE_RADIUS);
        initLine1.setEndX(-(1 + Math.cos(TuringMachineDrawer.TRANSITION_ANGLE)) *
                TuringMachineDrawer.STATE_RADIUS);
        initLine2.setEndX(-(1 + Math.cos(TuringMachineDrawer.TRANSITION_ANGLE)) *
                TuringMachineDrawer.STATE_RADIUS);
        initLine1.setEndY(-Math.sin(TuringMachineDrawer.TRANSITION_ANGLE) *
                TuringMachineDrawer.STATE_RADIUS);
        initLine2.setEndY(Math.sin(TuringMachineDrawer.TRANSITION_ANGLE) *
                TuringMachineDrawer.STATE_RADIUS);
        initLine1.setVisible(false);
        initLine2.setVisible(false);

        label = new Label(name);
        label.setFont(Font.font(TuringMachineDrawer.STATE_NAME_FONT_NAME,
                TuringMachineDrawer.STATE_NAME_FONT_SIZE));
        label.setMinWidth(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setMaxWidth(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setMinHeight(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setMaxHeight(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setAlignment(Pos.CENTER);

        this.getChildren().addAll(outerCircle, innerCircle, acceptIcon, initLine1, initLine2, label);
        label.setLayoutX(- label.getMinWidth() / 2);
        label.setLayoutY(- label.getMinHeight() / 2);

        setUnselected();
    }

    /**
     * Reset the color of the node, depending if it is selected or not.
     */
    private void resetFillColor(){
        if(selected)
            setSelectedFillColor();
        else
            setUnSelectedFillColor();
    }

    /**
     * Draw the state as selected (used when the user click on the state)
     * @see #setUnselected()
     */
    void setSelected(){
        selected = true;
        resetFillColor();
    }

    private void setSelectedFillColor(){
        Color color = this.getSelectedFillColor();
        outerCircle.setFill(color);
        innerCircle.setFill(color);
    }

    private Color getSelectedFillColor(){
        return TuringMachineDrawer.SELECTED_STATE_COLOR;
    }

    /**
     * Draw the state as not selected (used when the user unselect the state)
     * @see #setSelected()
     */
    void setUnselected(){
        selected = false;
        resetFillColor();
    }

    private void setUnSelectedFillColor(){
        Color color = getUnselectedFillColor();
        outerCircle.setFill(color);
        innerCircle.setFill(color);
    }

    private Color getUnselectedFillColor(){
        return deterministic ? TuringMachineDrawer.UNSELECTED_DETERMINISTIC_STATE_COLOR :
                TuringMachineDrawer.UNSELECTED_NONDETERMINISTIC_STATE_COLOR;
    }

    /**
     * Draw the widget as final if and only if isFinal is true
     * @param isFinal
     */
    void setFinal(boolean isFinal){
        innerCircle.setVisible(isFinal);
    }

    /**
     * Draw the widget as accepting if and only if isAccepting is true
     * @param isAccepting
     */
    void setAccepting(boolean isAccepting){
        acceptIcon.setVisible(isAccepting);
    }

    public void setDeterministic(boolean deterministic) {
        this.deterministic = deterministic;
        resetFillColor();
    }

    /**
     * Draw the widget as initial if and only if isInitial is true
     * @param isInitial
     */
    void setInitial(boolean isInitial){
        initLine1.setVisible(isInitial);
        initLine2.setVisible(isInitial);
    }

    /**
     * Change the name displayed in the state.
     * @param name
     */
    void setName(String name) {
        this.label.setText(name);
    }

    /**
     * Start an animation darkening the state.
     */
    void startTimeline(){
        this.animating = true;
        timeline.getKeyFrames().clear();
        KeyValue kOuterFill = new KeyValue(this.outerCircle.fillProperty(),
                TuringMachineDrawer.STATE_PRESS_COLOR,
                Interpolator.EASE_BOTH);
        KeyValue kInnerfill = new KeyValue(this.outerCircle.fillProperty(),
                TuringMachineDrawer.STATE_PRESS_COLOR,
                Interpolator.EASE_BOTH);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(TuringMachineDrawer.SETTINGS_PRESS_DURATION),
                        kOuterFill, kInnerfill)
        );

        this.setUnselected();
        timeline.play();
    }

    /**
     * Stop the animation darkening the state.
     * @see #startTimeline()
     */
    void stopTimeline(){
        timeline.stop();
        this.setUnselected();
        this.animating = false;
    }

    /**n
     * @return an animation key used to animate the coloring of the node when it is declared as the current node
     * (pointed by the state register when the machine is executed).
     */
    KeyValue getCurrentStateKeyValue() {
        return new KeyValue(this.outerCircle.fillProperty(),
                TuringMachineDrawer.STATE_CURRENT_COLOR,
                Interpolator.EASE_BOTH);
    }

    /**
     * @return an animation key used to animate the coloring of the node when it is declared as not the current node
     * (pointed by the state register when the machine is executed).
     */
    KeyValue getNotCurrentStateKeyValue() {
        return new KeyValue(this.outerCircle.fillProperty(),
                this.getUnselectedFillColor(),
                Interpolator.EASE_BOTH);
    }

    /**
     * @return a JSON representation of the widget.
     */
    JSONObject getJSON() {
        TuringMachine turingMachine = TuringMachineDrawer.getInstance().machine;
        return new JSONObject()
                .put("x", this.getLayoutX())
                .put("y", this.getLayoutY())
                .put("name", turingMachine.getStateName(this.state))
                .put("isFinal", turingMachine.isFinal(this.state))
                .put("isAccepting", turingMachine.isAccepting(this.state))
                .put("isInitial", turingMachine.isInitial(this.state));
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode)
            return false;

        GraphPane graphPane = TuringMachineDrawer.getInstance().graphPane;

        // Close any settings rectangle if such a rectangle is opened
        if(graphPane.stateSettingsRectangle.isMaximized()){
            graphPane.closeStateSettingsRectangle();
            return true;
        }
        else if(graphPane.transitionSettingsRectangle.isMaximized()){
            graphPane.closeTransitionSettingsRectangle();
            return true;
        }

        // If the "manual firing mode" is selected, declare the state as the current state.
        if(TuringMachineDrawer.getInstance().manualMode)
            TuringMachineDrawer.getInstance().manualSelectCurrentState(this);
        else {

            // Otherwise, check if the user pressed the state during enough time.
            boolean pressFinished = !this.animating;
            this.stopTimeline();

            // In that case open the settings rectangle associated with that state
            if (pressFinished) {
                graphPane.unselect();
                graphPane.openStateSettingsRectangle(this);
            } else {
                // Otherwise select/unselect the node if no other node is selected and add a new transition if
                // another node was selected.
                Node selected = graphPane.getSelected();
                if (selected == null)
                    graphPane.select(this);
                else if (selected instanceof StateGroup) {
                    graphPane.addTransition((StateGroup) selected, this);
                    graphPane.unselect();
                } else
                    graphPane.unselect();
            }
        }

        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        // Stop the animation darkening the node and drags the node
        this.stopTimeline();
        GraphPane graphPane = TuringMachineDrawer.getInstance().graphPane;
        graphPane.select(this);

        // Translate the node
        // getX and getY return the coordinates of the mouse in the node reference.
        graphPane.moveStateGroup(this,this.getLayoutX() + mouseEvent.getX(),
                this.getLayoutY() + mouseEvent.getY());
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        // Start the animation darkening the node
        if(!TuringMachineDrawer.getInstance().graphPane.stateSettingsRectangle.isMaximized()
                && !TuringMachineDrawer.getInstance().graphPane.transitionSettingsRectangle.isMaximized())
            this.startTimeline();

        return false;
    }
}