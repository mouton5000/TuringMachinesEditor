package gui;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import util.MouseHandler;
import util.Ressources;
import util.widget.VirtualKeyboard;

import java.util.Optional;

/**
 * Created by dimitri.watel on 06/06/18.
 */
class StateOptionRectangle extends OptionRectangle implements MouseHandler {

    StateGroup currentState;
    private GraphPane graphPane;
    private ChangeListener<Number> changeListener;

    StateOptionRectangle(GraphPane pane) {
        super(TuringMachineDrawer.getInstance().graphPaneMouseHandler);
        this.graphPane = pane;
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().graphPaneMouseHandler);

        changeListener = (observableValue, oldVal, newVal) ->
                StateOptionRectangle.this.setLayoutY(
                        currentState.getLayoutY()
                - TuringMachineDrawer.STATE_RADIUS
                * TuringMachineDrawer.STATE_OPTION_RECTANGLE_DISTANCE_RATIO);

        VBox vbox = new VBox();
        vbox.setSpacing(0);
        vbox.setMinWidth(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH);
        vbox.setMaxWidth(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH);
        vbox.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT);
        vbox.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT);
        vbox.setLayoutX(
                - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2
        );
        vbox.setLayoutY(
                TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                        - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT
        );

        HBox hBoxTop = new HBox();
        hBoxTop.setMinWidth(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH);
        hBoxTop.setMaxWidth(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH);
        hBoxTop.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        hBoxTop.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        hBoxTop.setAlignment(Pos.TOP_CENTER);
        hBoxTop.setTranslateY(TuringMachineDrawer.STATE_OPTION_RECTANGLE_SPACING / 2);
        hBoxTop.setSpacing(TuringMachineDrawer.STATE_OPTION_RECTANGLE_SPACING);


        HBox hBoxDown = new HBox();
        hBoxDown.setMinWidth(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH);
        hBoxDown.setMaxWidth(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH);
        hBoxDown.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        hBoxDown.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        hBoxDown.setAlignment(Pos.CENTER);
        hBoxDown.setSpacing(TuringMachineDrawer.STATE_OPTION_RECTANGLE_SPACING);

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

    void setCurrentState(StateGroup state) {

        if (state == null){
            if(this.currentState != null)
                this.currentState.layoutYProperty().removeListener(changeListener);
            this.layoutXProperty().unbind();
            this.translateXProperty().unbind();
            this.translateYProperty().unbind();
        }


        this.currentState = state;

        if(state == null)
            return;

        this.layoutXProperty().bind(state.layoutXProperty());
        state.layoutYProperty().addListener(changeListener);
        this.translateXProperty().bind(state.translateXProperty());
        this.translateYProperty().bind(state.translateYProperty());

    }

    @Override
    protected Node associatedNode() {
        return graphPane;
    }

    @Override
    public void clear() {
        currentState = null;
    }

}

class FinalStateOption extends Group implements MouseHandler{

    StateOptionRectangle optionRectangle;

    FinalStateOption(StateOptionRectangle optionRectangle) {
        this.optionRectangle = optionRectangle;

        Circle finalStateOptionOuterCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        finalStateOptionOuterCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        finalStateOptionOuterCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        Circle finalStateOptionInnerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS *
                TuringMachineDrawer.FINAL_STATE_RADIUS_RATIO);
        finalStateOptionInnerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        finalStateOptionInnerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().graphPaneMouseHandler);

        this.getChildren().addAll(finalStateOptionOuterCircle, finalStateOptionInnerCircle);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TuringMachineDrawer.getInstance().graphPane.toggleFinal(this.optionRectangle.currentState);
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

class AcceptingStateOption extends Group implements MouseHandler{
    StateOptionRectangle optionRectangle;

    AcceptingStateOption(StateOptionRectangle optionRectangle){
        this.optionRectangle = optionRectangle;
        Circle acceptingStateOptionOuterCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        acceptingStateOptionOuterCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        acceptingStateOptionOuterCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        Circle acceptingStateOptionInnerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS *
                TuringMachineDrawer.FINAL_STATE_RADIUS_RATIO);
        acceptingStateOptionInnerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        acceptingStateOptionInnerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        ImageView acceptingStateOptionIcon = new ImageView(Ressources.getRessource("Accept-icon.png"));

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().graphPaneMouseHandler);

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

        TuringMachineDrawer.getInstance().graphPane.toggleAccepting(this.optionRectangle.currentState);
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

class InitialStateOption extends Group implements MouseHandler{
    StateOptionRectangle optionRectangle;

    InitialStateOption(StateOptionRectangle optionRectangle){
        this.optionRectangle = optionRectangle;

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

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().graphPaneMouseHandler);

        this.getChildren().addAll(initialStateOptionCircle, initialStateOptionLine1, initialStateOptionLine2);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TuringMachineDrawer.getInstance().graphPane.toggleInitial(this.optionRectangle.currentState);
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

class EditStateNameOptionIcon extends ImageView implements MouseHandler{
    StateOptionRectangle optionRectangle;
    EditStateNameOptionIcon(StateOptionRectangle optionRectangle){
        super(Ressources.getRessource("cursor_icon.png"));
        this.optionRectangle = optionRectangle;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().graphPaneMouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        StateGroup stateGroup = this.optionRectangle.currentState;

        VirtualKeyboard virtualKeyboard = new VirtualKeyboard(stateGroup.getName());
        virtualKeyboard.setX(mouseEvent.getScreenX() - virtualKeyboard.getWidth() / 2);
        virtualKeyboard.setY(mouseEvent.getScreenY());

        Optional<String> result = virtualKeyboard.showAndWait();
        if(result.isPresent()) {
            stateGroup.setName(result.get());
            TuringMachineDrawer.getInstance().setEnableToSave();
        }
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

class RemoveStateOptionIcon extends ImageView implements MouseHandler{
    StateOptionRectangle optionRectangle;
    RemoveStateOptionIcon(StateOptionRectangle optionRectangle){
        super(Ressources.getRessource("remove_state.png"));
        this.optionRectangle = optionRectangle;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().graphPaneMouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TuringMachineDrawer.getInstance().graphPane.removeState(this.optionRectangle.currentState);
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