package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Created by dimitri.watel on 06/06/18.
 */
abstract class OptionRectangle extends Group{

    private final TuringMachineDrawer drawer;

    private MinimizedOptionRectangle minimizedRectangle;
    private Rectangle maximizedRectangle;

    protected Timeline timeline;

    private boolean maximized;

    OptionRectangle(TuringMachineDrawer drawer){
        this.drawer = drawer;

        minimizedRectangle = new MinimizedOptionRectangle(this);
        minimizedRectangle.setOnMouseClicked(drawer.graphPaneMouseHandler);

        Rectangle clipRectangle = new Rectangle();
        this.setClip(clipRectangle);

        maximizedRectangle = new Rectangle();

        maximizedRectangle.xProperty().bindBidirectional(clipRectangle.xProperty());
        maximizedRectangle.yProperty().bindBidirectional(clipRectangle.yProperty());
        maximizedRectangle.widthProperty().bindBidirectional(clipRectangle.widthProperty());
        maximizedRectangle.heightProperty().bindBidirectional(clipRectangle.heightProperty());

        maximizedRectangle.setWidth(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_WIDTH);
        maximizedRectangle.setHeight(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        maximizedRectangle.setFill(TuringMachineDrawer.STATE_OPTION_RECTANGLE_INNER_COLOR);
        maximizedRectangle.setStroke(TuringMachineDrawer.STATE_OPTION_RECTANGLE_OUTER_COLOR);

        timeline = new Timeline();
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawer.animating = false;
            }
        });

        maximized = false;

        this.getChildren().addAll(maximizedRectangle, minimizedRectangle);

        maximizedRectangle.setX(- maximizedRectangle.getWidth() / 2);
        maximizedRectangle.setY(- maximizedRectangle.getHeight() / 2);
    }

    boolean isMaximized(){
        return maximized;
    }

    void maximize(){
        maximized = true;
        animateSize(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH,
                TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT);
    }

    void minimize(boolean animate){
        maximized = false;

        double width = TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_WIDTH;
        double height = TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT;
        if(animate)
            animateSize(width, height);
        else {
            maximizedRectangle.setX(maximizedRectangle.getX() + maximizedRectangle.getWidth() / 2 - width / 2);
            maximizedRectangle.setY(maximizedRectangle.getY() + maximizedRectangle.getHeight() - height);
            maximizedRectangle.setWidth(width);
            maximizedRectangle.setHeight(height);
        }
    }

    private void animateSize(double width, double height){
        if(drawer.animating)
            return;
        drawer.animating = true;
        timeline.getKeyFrames().clear();
        KeyValue kx = new KeyValue(maximizedRectangle.xProperty(),
                maximizedRectangle.getX() + maximizedRectangle.getWidth() / 2 - width / 2,
                Interpolator.EASE_BOTH);
        KeyValue ky = new KeyValue(maximizedRectangle.yProperty(),
                maximizedRectangle.getY() + maximizedRectangle.getHeight() - height,
                Interpolator.EASE_BOTH);
        KeyValue kw = new KeyValue(maximizedRectangle.widthProperty(), width, Interpolator.EASE_BOTH);
        KeyValue kh = new KeyValue(maximizedRectangle.heightProperty(), height, Interpolator.EASE_BOTH);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(600), kx, ky, kh, kw)
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
        minimizedRectangle.setWidth(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_WIDTH);
        minimizedRectangle.setHeight(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        minimizedRectangle.setFill(TuringMachineDrawer.STATE_OPTION_RECTANGLE_INNER_COLOR);
        minimizedRectangle.setStroke(TuringMachineDrawer.STATE_OPTION_RECTANGLE_OUTER_COLOR);

        waitingDots = new WaitingDots();

        this.getChildren().addAll(minimizedRectangle, waitingDots);
        minimizedRectangle.setX( - minimizedRectangle.getWidth() / 2);
        minimizedRectangle.setY( - minimizedRectangle.getHeight() / 2);
    }
}