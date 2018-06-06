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
    private StateOptionRectangle optionRectangle;

    private CoordinatesProperties coords;

    public StateGroup(TuringMachineDrawer drawer, String name, double x, double y){
        this.drawer = drawer;
        coords = new CoordinatesProperties();

        stateCircle = new StateCircle(this, name);
        stateCircle.setOnMouseClicked(drawer.graphPaneMouseHandler);
        stateCircle.setOnMouseDragged(drawer.graphPaneMouseHandler);

        optionRectangle = new StateOptionRectangle(this.drawer, this);
        optionRectangle.setOnMouseClicked(drawer.graphPaneMouseHandler);

        this.getChildren().addAll(stateCircle, optionRectangle);

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

    DoubleProperty centerXProperty(){
        return coords.xProperty();
    }

    DoubleProperty centerYProperty(){
        return coords.yProperty();
    }

    double getCenterX() {
        return coords.getX();
    }

    void setCenterX(double x) {
        coords.setX(x);
        stateCircle.setCenterX(x);
        optionRectangle.setCenterX(x);
    }

    double getCenterY() {
        return coords.getY();
    }

    void setCenterY(double y) {
        coords.setY(y);
        stateCircle.setCenterY(y);
        optionRectangle.setCenterY(y -
                TuringMachineDrawer.STATE_RADIUS * TuringMachineDrawer.STATE_OPTION_RECTANGLE_DISTANCE_RATIO);
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
    }

    void setCenterX(double x){
        outerCircle.setCenterX(x);
        innerCircle.setCenterX(x);
        label.setLayoutX(x - label.getMinWidth() / 2);
    }

    void setCenterY(double y){
        outerCircle.setCenterY(y);
        innerCircle.setCenterY(y);
        label.setLayoutY(y - label.getMinHeight() / 2);
    }

    void setSelected(){
        outerCircle.setFill(TuringMachineDrawer.SELECTED_STATE_COLOR);
    }

    void setUnselected(){
        outerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
    }
}