package gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * Created by dimitri.watel on 06/06/18.
 */
class StateOptionRectangle extends OptionRectangle{

    StateGroup currentState;
    private GraphPane graphPane;
    private ChangeListener<Number> changeListener;

    StateOptionRectangle(TuringMachineDrawer drawer, GraphPane pane) {
        super(drawer, drawer.graphPaneMouseHandler);
        this.graphPane = pane;
        this.setOnMouseClicked(drawer.graphPaneMouseHandler);

        changeListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                StateOptionRectangle.this.setLayoutY(newVal.doubleValue()
                        - TuringMachineDrawer.STATE_RADIUS
                        * TuringMachineDrawer.STATE_OPTION_RECTANGLE_DISTANCE_RATIO);
            }
        };

        FinalStateOption finalStateOption = new FinalStateOption(drawer, this);
        AcceptingStateOption acceptingStateOption = new AcceptingStateOption(drawer, this);
        InitialStateOption initialStateOption = new InitialStateOption(drawer, this);

        ImageView editStateNameOptionIcon = new EditStateNameOptionIcon(drawer, this,"./images/cursor_icon.png");
        editStateNameOptionIcon.setPreserveRatio(true);
        editStateNameOptionIcon.setFitHeight(TuringMachineDrawer.STATE_RADIUS * 2);

        this.getChildren().addAll(finalStateOption, acceptingStateOption,
                initialStateOption, editStateNameOptionIcon);

        finalStateOption.setLayoutX(- TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2
                + TuringMachineDrawer.STATE_RADIUS + TuringMachineDrawer.OPTION_RECTANGLE_MARGIN);
        finalStateOption.setLayoutY(TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT
                + TuringMachineDrawer.STATE_RADIUS + TuringMachineDrawer.OPTION_RECTANGLE_MARGIN);

        acceptingStateOption.setLayoutX(- TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2
                + 3 * TuringMachineDrawer.STATE_RADIUS + 2 * TuringMachineDrawer.OPTION_RECTANGLE_MARGIN);
        acceptingStateOption.setLayoutY(TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT
                + TuringMachineDrawer.STATE_RADIUS + TuringMachineDrawer.OPTION_RECTANGLE_MARGIN);

        initialStateOption.setLayoutX(
                - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2
                        + (5 + Math.cos(TuringMachineDrawer.ARROW_ANGLE)) * TuringMachineDrawer.STATE_RADIUS
                        + 3 * TuringMachineDrawer.OPTION_RECTANGLE_MARGIN);
        initialStateOption.setLayoutY(TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT
                + TuringMachineDrawer.STATE_RADIUS + TuringMachineDrawer.OPTION_RECTANGLE_MARGIN);

        editStateNameOptionIcon.setLayoutX(- editStateNameOptionIcon.getBoundsInLocal().getWidth() / 2);
        editStateNameOptionIcon.setLayoutY(
                TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.STATE_RADIUS - TuringMachineDrawer.OPTION_RECTANGLE_MARGIN
                - editStateNameOptionIcon.getBoundsInLocal().getHeight() / 2);
    }

    void setCurrentState(StateGroup state) {

        if (state == null && this.currentState != null){
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
}

class FinalStateOption extends Group {

    StateOptionRectangle optionRectangle;

    FinalStateOption(TuringMachineDrawer drawer, StateOptionRectangle optionRectangle) {
        this.optionRectangle = optionRectangle;

        Circle finalStateOptionOuterCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        finalStateOptionOuterCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        finalStateOptionOuterCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        Circle finalStateOptionInnerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS *
                TuringMachineDrawer.FINAL_STATE_RADIUS_RATIO);
        finalStateOptionInnerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        finalStateOptionInnerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        this.setOnMouseClicked(drawer.graphPaneMouseHandler);

        this.getChildren().addAll(finalStateOptionOuterCircle, finalStateOptionInnerCircle);
    }
}

class AcceptingStateOption extends Group{
    StateOptionRectangle optionRectangle;

    AcceptingStateOption(TuringMachineDrawer drawer, StateOptionRectangle optionRectangle){
        this.optionRectangle = optionRectangle;
        Circle acceptingStateOptionOuterCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        acceptingStateOptionOuterCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        acceptingStateOptionOuterCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        Circle acceptingStateOptionInnerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS *
                TuringMachineDrawer.FINAL_STATE_RADIUS_RATIO);
        acceptingStateOptionInnerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        acceptingStateOptionInnerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        ImageView acceptingStateOptionIcon = new ImageView("./images/Accept-icon.png");

        this.setOnMouseClicked(drawer.graphPaneMouseHandler);

        this.getChildren().addAll(acceptingStateOptionOuterCircle, acceptingStateOptionInnerCircle,
                acceptingStateOptionIcon);

        acceptingStateOptionIcon.setLayoutX(
                TuringMachineDrawer.STATE_RADIUS * Math.sqrt(2) / 2
                        - acceptingStateOptionIcon.getBoundsInLocal().getWidth() / 2);

        acceptingStateOptionIcon.setLayoutY(
                TuringMachineDrawer.STATE_RADIUS * Math.sqrt(2) / 2
                        - acceptingStateOptionIcon.getBoundsInLocal().getHeight() / 2);
    }
}

class InitialStateOption extends Group{
    StateOptionRectangle optionRectangle;

    InitialStateOption(TuringMachineDrawer drawer, StateOptionRectangle optionRectangle){
        this.optionRectangle = optionRectangle;

        Circle initialStateOptionCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        initialStateOptionCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        initialStateOptionCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        Line initialStateOptionLine1 = new Line();
        Line initialStateOptionLine2 = new Line();

        initialStateOptionLine1.setStartX(-TuringMachineDrawer.STATE_RADIUS);
        initialStateOptionLine2.setStartX(-TuringMachineDrawer.STATE_RADIUS);
        initialStateOptionLine1.setEndX(-(1 + Math.cos(TuringMachineDrawer.ARROW_ANGLE)) *
                TuringMachineDrawer.STATE_RADIUS);
        initialStateOptionLine2.setEndX(-(1 + Math.cos(TuringMachineDrawer.ARROW_ANGLE)) *
                TuringMachineDrawer.STATE_RADIUS);
        initialStateOptionLine1.setEndY(-Math.sin(TuringMachineDrawer.ARROW_ANGLE) *
                TuringMachineDrawer.STATE_RADIUS);
        initialStateOptionLine2.setEndY(Math.sin(TuringMachineDrawer.ARROW_ANGLE) *
                TuringMachineDrawer.STATE_RADIUS);

        this.setOnMouseClicked(drawer.graphPaneMouseHandler);

        this.getChildren().addAll(initialStateOptionCircle, initialStateOptionLine1, initialStateOptionLine2);
    }
}

class EditStateNameOptionIcon extends ImageView{
    StateOptionRectangle optionRectangle;
    EditStateNameOptionIcon(TuringMachineDrawer drawer, StateOptionRectangle optionRectangle, String url){
        super(url);
        this.optionRectangle = optionRectangle;

        this.setOnMouseClicked(drawer.graphPaneMouseHandler);
    }
}