package gui;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

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

    public void toggleFinal() {
        this.stateCircle.toggleFinal();
    }

    public void toggleAccepting() {
        this.stateCircle.toggleAccepting();
    }

    public void toggleInitial() {
        this.stateCircle.toggleInitial();
    }
}

class StateCircle extends Group{
    private Circle outerCircle;
    private Circle innerCircle;
    private ImageView acceptIcon;
    private Line initLine1;
    private Line initLine2;
    private Label label;
    StateGroup stateGroup;

    StateCircle(StateGroup stateGroup, String name){
        this.stateGroup = stateGroup;

        outerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        outerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        outerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        innerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS *
                TuringMachineDrawer.FINAL_STATE_RADIUS_RATIO);
        innerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        innerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        innerCircle.setVisible(false);

        acceptIcon = new ImageView("./images/Accept-icon.png");
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
        initLine1.setEndX(-(1 + Math.cos(TuringMachineDrawer.ARROW_ANGLE)) *
                TuringMachineDrawer.STATE_RADIUS);
        initLine2.setEndX(-(1 + Math.cos(TuringMachineDrawer.ARROW_ANGLE)) *
                TuringMachineDrawer.STATE_RADIUS);
        initLine1.setEndY(-Math.sin(TuringMachineDrawer.ARROW_ANGLE) *
                TuringMachineDrawer.STATE_RADIUS);
        initLine2.setEndY(Math.sin(TuringMachineDrawer.ARROW_ANGLE) *
                TuringMachineDrawer.STATE_RADIUS);
        initLine1.setVisible(false);
        initLine2.setVisible(false);

        label = new Label(name);
        label.setMinWidth(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setMaxWidth(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setMinHeight(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setMaxHeight(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setAlignment(Pos.CENTER);

        this.getChildren().addAll(outerCircle, innerCircle, acceptIcon, initLine1, initLine2, label);
        label.setLayoutX(- label.getMinWidth() / 2);
        label.setLayoutY(- label.getMinHeight() / 2);
    }

    void setSelected(){
        outerCircle.setFill(TuringMachineDrawer.SELECTED_STATE_COLOR);
        innerCircle.setFill(TuringMachineDrawer.SELECTED_STATE_COLOR);
    }

    void setUnselected(){
        outerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        innerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
    }

    private boolean isFinal(){
        return innerCircle.isVisible() && !acceptIcon.isVisible();
    }

    private boolean isAccepted(){
        return acceptIcon.isVisible();
    }

    private boolean isInitial(){
        return initLine1.isVisible();
    }

    void toggleFinal(){
        if(isFinal())
            innerCircle.setVisible(false);
        else if(isAccepted())
            acceptIcon.setVisible(false);
        else
            innerCircle.setVisible(true);
    }

    void toggleAccepting(){
        if(isFinal())
            acceptIcon.setVisible(true);
        else if(isAccepted()) {
            innerCircle.setVisible(false);
            acceptIcon.setVisible(false);
        }
        else{
            innerCircle.setVisible(true);
            acceptIcon.setVisible(true);
        }
    }

    void toggleInitial(){
        if(isInitial()){
            initLine1.setVisible(false);
            initLine2.setVisible(false);
        }
        else{
            initLine1.setVisible(true);
            initLine2.setVisible(true);
        }
    }
}