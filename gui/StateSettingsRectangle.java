/*
 * Copyright (c) 2018 Dimitri Watel
 */

package gui;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import util.MouseListener;
import util.Ressources;
import util.widget.VirtualKeyboard;

import java.util.Optional;

/**
 * Widget containing the settings of a stateGroup of the machine.
 * It allows the user to
 * <ul>
 *     <li>set the stateGroup final, accepting and/or initial,</li>
 *     <li>change the name of the stateGroup,</li>
 *     <li>delete the node.</li>
 * </ul>
 */
class StateSettingsRectangle extends SettingsRectangle implements MouseListener {

    /**
     * State the settings rectangle is currently associated with.
     */
    StateGroup currentState;

    /**
     * Listener to the layoutY of the stateGroup this settings rectangle is currently associated with
     */
    private ChangeListener<Number> changeListener;

    StateSettingsRectangle() {
        super();
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        changeListener = (observableValue, oldVal, newVal) ->
                StateSettingsRectangle.this.setLayoutY(
                        currentState.getLayoutY()
                - TuringMachineDrawer.STATE_RADIUS
                * TuringMachineDrawer.STATE_SETTINGS_RECTANGLE_DISTANCE_RATIO);

        VBox vbox = new VBox();
        vbox.setSpacing(0);
        vbox.setMinWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH);
        vbox.setMaxWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH);
        vbox.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT);
        vbox.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT);
        vbox.setLayoutX(
                - TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH / 2
        );
        vbox.setLayoutY(
                TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT / 2
                        - TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT
        );

        HBox hBoxTop = new HBox();
        hBoxTop.setMinWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH);
        hBoxTop.setMaxWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH);
        hBoxTop.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        hBoxTop.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        hBoxTop.setAlignment(Pos.TOP_CENTER);
        hBoxTop.setTranslateY(TuringMachineDrawer.STATE_SETTINGS_RECTANGLE_SPACING / 2);
        hBoxTop.setSpacing(TuringMachineDrawer.STATE_SETTINGS_RECTANGLE_SPACING);


        HBox hBoxDown = new HBox();
        hBoxDown.setMinWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH);
        hBoxDown.setMaxWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH);
        hBoxDown.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        hBoxDown.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        hBoxDown.setAlignment(Pos.CENTER);
        hBoxDown.setSpacing(TuringMachineDrawer.STATE_SETTINGS_RECTANGLE_SPACING);

        FinalStateOption finalStateOption = new FinalStateOption(this);
        AcceptingStateOption acceptingStateOption = new AcceptingStateOption( this);
        InitialStateOption initialStateOption = new InitialStateOption( this);

        EditStateNameOptionIcon editStateNameOptionIcon = new EditStateNameOptionIcon( this);
        editStateNameOptionIcon.setPreserveRatio(true);
        editStateNameOptionIcon.setFitHeight(TuringMachineDrawer.STATE_RADIUS * 2);

        RemoveStateOptionIcon removeStateOptionIcon = new RemoveStateOptionIcon( this);

        hBoxTop.getChildren().addAll(finalStateOption, acceptingStateOption, initialStateOption);
        hBoxDown.getChildren().addAll(editStateNameOptionIcon, removeStateOptionIcon);
        vbox.getChildren().addAll(hBoxTop, hBoxDown);
        this.getChildren().add(vbox);

    }

    /**
     * Change the stateGroup this rectangle currently associated with.
     * If stateGroup is null, this rectangle is not associated with anymore node.
     * @param stateGroup
     */
    void setCurrentState(StateGroup stateGroup) {

        if (stateGroup == null){
            // Remove all the position listeners.
            if(this.currentState != null)
                this.currentState.layoutYProperty().removeListener(changeListener);
            this.layoutXProperty().unbind();
            this.translateXProperty().unbind();
            this.translateYProperty().unbind();
        }


        this.currentState = stateGroup;

        if(stateGroup == null)
            return;

        // Set the position listeners. the layoutY is different as the rectangle is not exactly on the stateGroup it is
        // associated with.
        this.layoutXProperty().bind(stateGroup.layoutXProperty());
        stateGroup.layoutYProperty().addListener(changeListener);
        this.translateXProperty().bind(stateGroup.translateXProperty());
        this.translateYProperty().bind(stateGroup.translateYProperty());

    }

    @Override
    public void clear() {
        currentState = null;
    }

}

/**
 * Icon the user can click on to make the stateGroup (associated with the settings rectangle) final
 */
class FinalStateOption extends Group implements MouseListener {

    private StateSettingsRectangle settingsRectangle;

    FinalStateOption(StateSettingsRectangle settingsRectangle) {
        this.settingsRectangle = settingsRectangle;

        Circle finalStateOptionOuterCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        finalStateOptionOuterCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        finalStateOptionOuterCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        Circle finalStateOptionInnerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS *
                TuringMachineDrawer.FINAL_STATE_RADIUS_RATIO);
        finalStateOptionInnerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        finalStateOptionInnerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        this.getChildren().addAll(finalStateOptionOuterCircle, finalStateOptionInnerCircle);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TuringMachineDrawer.getInstance().graphPane.toggleFinal(this.settingsRectangle.currentState);
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

/**
 * Icon the user can click on to make the stateGroup (associated with the settings rectangle) accepting
 */
class AcceptingStateOption extends Group implements MouseListener {
    private StateSettingsRectangle settingsRectangle;

    AcceptingStateOption(StateSettingsRectangle settingsRectangle){
        this.settingsRectangle = settingsRectangle;
        Circle acceptingStateOptionOuterCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        acceptingStateOptionOuterCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        acceptingStateOptionOuterCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        Circle acceptingStateOptionInnerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS *
                TuringMachineDrawer.FINAL_STATE_RADIUS_RATIO);
        acceptingStateOptionInnerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        acceptingStateOptionInnerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        ImageView acceptingStateOptionIcon = new ImageView(Ressources.getRessource("Accept-icon.png"));

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        this.getChildren().addAll(acceptingStateOptionOuterCircle, acceptingStateOptionInnerCircle,
                acceptingStateOptionIcon);

        acceptingStateOptionIcon.setLayoutX(
                TuringMachineDrawer.STATE_RADIUS * Math.sqrt(2) / 2
                        - acceptingStateOptionIcon.getBoundsInLocal().getWidth() / 2);

        acceptingStateOptionIcon.setLayoutY(
                TuringMachineDrawer.STATE_RADIUS * Math.sqrt(2) / 2
                        - acceptingStateOptionIcon.getBoundsInLocal().getHeight() / 2);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TuringMachineDrawer.getInstance().graphPane.toggleAccepting(this.settingsRectangle.currentState);
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

/**
 * Icon the user can click on to make the stateGroup (associated with the settings rectangle) initial.
 */
class InitialStateOption extends Group implements MouseListener {
    private StateSettingsRectangle settingsRectangle;

    InitialStateOption(StateSettingsRectangle settingsRectangle){
        this.settingsRectangle = settingsRectangle;

        Circle initialStateOptionCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        initialStateOptionCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        initialStateOptionCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        Line initialStateOptionLine1 = new Line();
        Line initialStateOptionLine2 = new Line();

        initialStateOptionLine1.setStartX(-TuringMachineDrawer.STATE_RADIUS);
        initialStateOptionLine2.setStartX(-TuringMachineDrawer.STATE_RADIUS);
        initialStateOptionLine1.setEndX(-(1 + Math.cos(TuringMachineDrawer.TRANSITION_ANGLE)) *
                TuringMachineDrawer.STATE_RADIUS);
        initialStateOptionLine2.setEndX(-(1 + Math.cos(TuringMachineDrawer.TRANSITION_ANGLE)) *
                TuringMachineDrawer.STATE_RADIUS);
        initialStateOptionLine1.setEndY(-Math.sin(TuringMachineDrawer.TRANSITION_ANGLE) *
                TuringMachineDrawer.STATE_RADIUS);
        initialStateOptionLine2.setEndY(Math.sin(TuringMachineDrawer.TRANSITION_ANGLE) *
                TuringMachineDrawer.STATE_RADIUS);

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        this.getChildren().addAll(initialStateOptionCircle, initialStateOptionLine1, initialStateOptionLine2);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TuringMachineDrawer.getInstance().graphPane.toggleInitial(this.settingsRectangle.currentState);
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

/**
 * Icon the user can click on to change the name of the stateGroup (associated with the settings rectangle)
 */
class EditStateNameOptionIcon extends ImageView implements MouseListener {
    StateSettingsRectangle settingsRectangle;
    EditStateNameOptionIcon(StateSettingsRectangle settingsRectangle){
        super(Ressources.getRessource("cursor_icon.png"));
        this.settingsRectangle = settingsRectangle;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        StateGroup stateGroup = this.settingsRectangle.currentState;

        VirtualKeyboard virtualKeyboard = new VirtualKeyboard(TuringMachineDrawer.getInstance().machine
                .getStateName(stateGroup.state));
        virtualKeyboard.setX(mouseEvent.getScreenX() - virtualKeyboard.getWidth() / 2);
        virtualKeyboard.setY(mouseEvent.getScreenY());

        Optional<String> result = virtualKeyboard.showAndWait();
        result.ifPresent(s -> TuringMachineDrawer.getInstance().editStateName(stateGroup.state, s));
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

/**
 * Icon the user can click on to remove the stateGroup (associated with the settings rectangle)
 */
class RemoveStateOptionIcon extends ImageView implements MouseListener {
    StateSettingsRectangle settingsRectangle;
    RemoveStateOptionIcon(StateSettingsRectangle settingsRectangle){
        super(Ressources.getRessource("remove_state.png"));
        this.settingsRectangle = settingsRectangle;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TuringMachineDrawer.getInstance().graphPane.removeState(this.settingsRectangle.currentState);
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}