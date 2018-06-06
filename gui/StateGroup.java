package gui;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class StateGroup extends Group {

    private TuringMachineDrawer drawer;

    private StateCircle stateCircle;
    StateOptionRectangle optionRectangle;

    public StateGroup(TuringMachineDrawer drawer, String name, double x, double y){
        this.drawer = drawer;

        stateCircle = new StateCircle(this, name);
        stateCircle.setOnMouseClicked(drawer.graphPaneMouseHandler);
        stateCircle.setOnMouseDragged(drawer.graphPaneMouseHandler);

        optionRectangle = new StateOptionRectangle(this.drawer, this);

        this.getChildren().addAll(stateCircle, optionRectangle);

        optionRectangle.setLayoutY(- TuringMachineDrawer.STATE_RADIUS
                * TuringMachineDrawer.STATE_OPTION_RECTANGLE_DISTANCE_RATIO);

        drawer.moveStateGroup(this, x, y);
        setUnselected();

    }

    void setSelected(){
        stateCircle.setSelected();
        optionRectangle.setVisible(true);
    }

    void setUnselected(){
        stateCircle.setUnselected();
        if(!optionRectangle.isMaximized())
            optionRectangle.setVisible(false);
    }
}

class StateCircle extends Group{
    private Circle outerCircle;
    private Circle innerCircle;
    private Label label;
    StateGroup stateGroup;

    StateCircle(StateGroup stateGroup, String name){
        this.stateGroup = stateGroup;

        outerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        outerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);

        innerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS *
                TuringMachineDrawer.FINAL_STATE_RADIUS_RATIO);
        innerCircle.setStroke(Color.BLACK);
        innerCircle.setVisible(false);

        label = new Label(name);
        label.setMinWidth(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setMaxWidth(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setMinHeight(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setMaxHeight(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setAlignment(Pos.CENTER);

        this.getChildren().addAll(outerCircle, innerCircle, label);
        label.setLayoutX(- label.getMinWidth() / 2);
        label.setLayoutY(- label.getMinHeight() / 2);
    }

    void setSelected(){
        outerCircle.setFill(TuringMachineDrawer.SELECTED_STATE_COLOR);
    }

    void setUnselected(){
        outerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
    }
}