package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Created by dimitri.watel on 06/06/18.
 */
abstract class OptionRectangle extends Group{

    final TuringMachineDrawer drawer;

    private MinimizedOptionRectangle minimizedRectangle;
    private Rectangle maximizedRectangle;

    Timeline timeline;

    private boolean maximized;

    OptionRectangle(TuringMachineDrawer drawer, EventHandler<Event> mouseHandler){
        this.drawer = drawer;

        minimizedRectangle = new MinimizedOptionRectangle(this);
        minimizedRectangle.setOnMouseClicked(mouseHandler);

        Rectangle clipRectangle = new Rectangle();
        this.setClip(clipRectangle);

        maximizedRectangle = new Rectangle();

        maximizedRectangle.xProperty().bindBidirectional(clipRectangle.xProperty());
        maximizedRectangle.yProperty().bindBidirectional(clipRectangle.yProperty());
        maximizedRectangle.widthProperty().bindBidirectional(clipRectangle.widthProperty());
        maximizedRectangle.heightProperty().bindBidirectional(clipRectangle.heightProperty());

        maximizedRectangle.setWidth(TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_WIDTH);
        maximizedRectangle.setHeight(TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        maximizedRectangle.setFill(TuringMachineDrawer.STATE_OPTION_RECTANGLE_INNER_COLOR);
        maximizedRectangle.setStroke(TuringMachineDrawer.STATE_OPTION_RECTANGLE_OUTER_COLOR);

        timeline = new Timeline();

        maximized = false;

        this.getChildren().addAll(maximizedRectangle, minimizedRectangle);

        maximizedRectangle.setX(- maximizedRectangle.getWidth() / 2);
        maximizedRectangle.setY(- maximizedRectangle.getHeight() + TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2);
    }

    boolean isMaximized(){
        return maximized;
    }

    void maximize(){
        maximized = true;
        minimizedRectangle.toFront();

        timeline.setOnFinished(actionEvent -> {
            drawer.animating = false;
        });

        animateSize(
                minimizedRectangle.getLayoutX() + getOffsetX(),
                minimizedRectangle.getLayoutY() + getOffsetY(),
                getMaximizedWidth(),
                getMaximizedHeight());
    }

    protected double getMaximizedWidth(){
        return TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH;
    }

    protected double getMaximizedHeight(){
        return TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT;
    }

    protected double getOffsetX(){return -TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2;}

    protected double getOffsetY(){
        return TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - getMaximizedHeight();
    }

    void minimize(boolean animate){
        maximized = false;

        if(animate) {
            timeline.setOnFinished(actionEvent -> {
                drawer.animating = false;
                this.setVisible(false);
            });
            animateSize(minimizedRectangle.getLayoutX() - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_WIDTH / 2,
                    minimizedRectangle.getLayoutY() - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2,
                    TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_WIDTH,
                    TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        }
        else {
            maximizedRectangle.setX(minimizedRectangle.getLayoutX() - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_WIDTH / 2);
            maximizedRectangle.setY(minimizedRectangle.getLayoutY() - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2);
            maximizedRectangle.setWidth(TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_WIDTH);
            maximizedRectangle.setHeight(TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        }
    }

    private void animateSize(double x, double y, double width, double height){
        if(drawer.animating)
            return;
        drawer.animating = true;
        timeline.getKeyFrames().clear();
        KeyValue kx = new KeyValue(maximizedRectangle.xProperty(), x, Interpolator.EASE_BOTH);
        KeyValue ky = new KeyValue(maximizedRectangle.yProperty(), y, Interpolator.EASE_BOTH);
        KeyValue kw = new KeyValue(maximizedRectangle.widthProperty(), width, Interpolator.EASE_BOTH);
        KeyValue kh = new KeyValue(maximizedRectangle.heightProperty(), height, Interpolator.EASE_BOTH);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(TuringMachineDrawer.OPTION_RECTANGLE_TIMELINE_DURATION), kx, ky, kh, kw)
        );
        timeline.play();
    }

    protected abstract Node associatedNode();
}

class MinimizedOptionRectangle extends Group{
    OptionRectangle optionRectangle;
    private Rectangle minimizedRectangle;
    private WaitingDots waitingDots;

    MinimizedOptionRectangle(OptionRectangle optionRectangle) {
        this.optionRectangle = optionRectangle;

        minimizedRectangle = new Rectangle();
        minimizedRectangle.setWidth(TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_WIDTH);
        minimizedRectangle.setHeight(TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        minimizedRectangle.setFill(TuringMachineDrawer.STATE_OPTION_RECTANGLE_INNER_COLOR);
        minimizedRectangle.setStroke(TuringMachineDrawer.STATE_OPTION_RECTANGLE_OUTER_COLOR);

        waitingDots = new WaitingDots();

        this.getChildren().addAll(minimizedRectangle, waitingDots);
        minimizedRectangle.setX( - minimizedRectangle.getWidth() / 2);
        minimizedRectangle.setY( - minimizedRectangle.getHeight() / 2);
    }
}