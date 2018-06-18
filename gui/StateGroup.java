package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

class StateGroup extends Group{

    TuringMachineDrawer drawer;
    boolean animating;

    private Circle outerCircle;
    private Circle innerCircle;
    private ImageView acceptIcon;
    private Line initLine1;
    private Line initLine2;
    private Label label;


    private Timeline timeline;

    StateGroup(TuringMachineDrawer drawer, String name){
        this.drawer = drawer;
        this.timeline = new Timeline();
        this.timeline.setOnFinished(actionEvent -> animating = false);

        this.setOnMousePressed(drawer.graphPaneMouseHandler);
        this.setOnMouseClicked(drawer.graphPaneMouseHandler);
        this.setOnMouseDragged(drawer.graphPaneMouseHandler);

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

        setUnselected();
    }

    void setSelected(){
        outerCircle.setFill(TuringMachineDrawer.SELECTED_STATE_COLOR);
        innerCircle.setFill(TuringMachineDrawer.SELECTED_STATE_COLOR);
    }

    void setUnselected(){
        outerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        innerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
    }

    boolean isFinal(){
        return innerCircle.isVisible() && !acceptIcon.isVisible();
    }

    boolean isAccepting(){
        return acceptIcon.isVisible();
    }

    boolean isInitial(){
        return initLine1.isVisible();
    }

    void setFinal(boolean isFinal){
        innerCircle.setVisible(isFinal);
    }

    void setAccepting(boolean isAccepting){
        acceptIcon.setVisible(isAccepting);
    }

    void setInitial(boolean isInitial){
        initLine1.setVisible(isInitial);
        initLine2.setVisible(isInitial);
    }


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
                new KeyFrame(Duration.millis(TuringMachineDrawer.STATE_PRESS_DURATION),
                        kOuterFill, kInnerfill)
        );

        this.outerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        this.innerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        timeline.play();
    }

    void stopTimeline(){
        timeline.stop();
        this.outerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        this.innerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        this.animating = false;
    }
}