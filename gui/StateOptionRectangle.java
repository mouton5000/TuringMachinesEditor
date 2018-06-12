package gui;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * Created by dimitri.watel on 06/06/18.
 */
class StateOptionRectangle extends OptionRectangle{

    private StateGroup stateGroup;

    StateOptionRectangle(TuringMachineDrawer drawer, StateGroup stateGroup) {
        super(drawer, drawer.graphPaneMouseHandler);
        this.stateGroup = stateGroup;

        FinalStateOption finalStateOption = new FinalStateOption(this);
        AcceptingStateOption acceptingStateOption = new AcceptingStateOption(this);
        InitialStateOption initialStateOption = new InitialStateOption(this);

        ImageView editStateNameOptionIcon = new EditStateNameOptionIcon(this,"./images/cursor_icon.png");
        editStateNameOptionIcon.setPreserveRatio(true);
        editStateNameOptionIcon.setFitHeight(TuringMachineDrawer.STATE_RADIUS * 2);


        finalStateOption.setOnMouseClicked(drawer.graphPaneMouseHandler);
        acceptingStateOption.setOnMouseClicked(drawer.graphPaneMouseHandler);
        initialStateOption.setOnMouseClicked(drawer.graphPaneMouseHandler);
        editStateNameOptionIcon.setOnMouseClicked(drawer.graphPaneMouseHandler);

        this.getChildren().addAll(finalStateOption, acceptingStateOption,
                initialStateOption, editStateNameOptionIcon);

        finalStateOption.setLayoutX(- TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2
                + TuringMachineDrawer.STATE_RADIUS + TuringMachineDrawer.OPTION_RECTANGLE_MARGIN);
        finalStateOption.setLayoutY(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT
                + TuringMachineDrawer.STATE_RADIUS + TuringMachineDrawer.OPTION_RECTANGLE_MARGIN);

        acceptingStateOption.setLayoutX(- TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2
                + 3 * TuringMachineDrawer.STATE_RADIUS + 2 * TuringMachineDrawer.OPTION_RECTANGLE_MARGIN);
        acceptingStateOption.setLayoutY(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT
                + TuringMachineDrawer.STATE_RADIUS + TuringMachineDrawer.OPTION_RECTANGLE_MARGIN);

        initialStateOption.setLayoutX(
                - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2
                        + (5 + Math.cos(TuringMachineDrawer.ARROW_ANGLE)) * TuringMachineDrawer.STATE_RADIUS
                        + 3 * TuringMachineDrawer.OPTION_RECTANGLE_MARGIN);
        initialStateOption.setLayoutY(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT
                + TuringMachineDrawer.STATE_RADIUS + TuringMachineDrawer.OPTION_RECTANGLE_MARGIN);

        editStateNameOptionIcon.setLayoutX(- editStateNameOptionIcon.getBoundsInLocal().getWidth() / 2);
        editStateNameOptionIcon.setLayoutY(
                TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.STATE_RADIUS - TuringMachineDrawer.OPTION_RECTANGLE_MARGIN
                - editStateNameOptionIcon.getBoundsInLocal().getHeight() / 2);
    }


    @Override
    protected Node associatedNode() {
        return stateGroup;
    }
}

class FinalStateOption extends Group {

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

        this.getChildren().addAll(finalStateOptionOuterCircle, finalStateOptionInnerCircle);
    }
}

class AcceptingStateOption extends Group{
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

        ImageView acceptingStateOptionIcon = new ImageView("./images/Accept-icon.png");

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

    InitialStateOption(StateOptionRectangle optionRectangle){
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

        this.getChildren().addAll(initialStateOptionCircle, initialStateOptionLine1, initialStateOptionLine2);
    }
}

class EditStateNameOptionIcon extends ImageView{
    StateOptionRectangle optionRectangle;
    EditStateNameOptionIcon(StateOptionRectangle optionRectangle, String url){
        super(url);
        this.optionRectangle = optionRectangle;
    }
}