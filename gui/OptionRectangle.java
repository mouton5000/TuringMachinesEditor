package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Created by dimitri.watel on 06/06/18.
 */
abstract class OptionRectangle extends Group{

    private final TuringMachineDrawer drawer;

    private Rectangle outerRectangle;
    private WaitingDots waitingDots;

    protected Timeline timeline;

    private boolean maximized;


    OptionRectangle(TuringMachineDrawer drawer){
        this.drawer = drawer;

        outerRectangle = new Rectangle();
        outerRectangle.setWidth(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_WIDTH);
        outerRectangle.setHeight(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        outerRectangle.setFill(TuringMachineDrawer.STATE_OPTION_RECTANGLE_INNER_COLOR);
        outerRectangle.setStroke(TuringMachineDrawer.STATE_OPTION_RECTANGLE_OUTER_COLOR);

        waitingDots = new WaitingDots();

        timeline = new Timeline();

        maximized = false;

        this.getChildren().addAll(outerRectangle, waitingDots);
    }

    void setCenterX(double centerX){
        outerRectangle.setX(centerX - outerRectangle.getWidth() / 2);
        waitingDots.setCenterX(centerX);
    }

    void setCenterY(double centerY){
        outerRectangle.setY(centerY - outerRectangle.getHeight() / 2);
        waitingDots.setCenterY(centerY);
    }

    boolean isMaximized(){
        return maximized;
    }

    void maximize(){
        maximized = true;
        waitingDots.setVisible(false);
        animateSize(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH,
                TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT);
    }

    void minimize(boolean animate){
        maximized = false;
        waitingDots.setVisible(true);

        double width = TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_WIDTH;
        double height = TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT;
        if(animate)
            animateSize(width, height);
        else {
            outerRectangle.setX(outerRectangle.getX() + outerRectangle.getWidth() / 2 - width / 2);
            outerRectangle.setY(outerRectangle.getY() + outerRectangle.getHeight() / 2 - height / 2);
            outerRectangle.setWidth(width);
            outerRectangle.setHeight(height);
        }
    }

    private void animateSize(double width, double height){

        timeline.getKeyFrames().clear();
        KeyValue kx = new KeyValue(outerRectangle.xProperty(),
                outerRectangle.getX() + outerRectangle.getWidth() / 2 - width / 2,
                Interpolator.EASE_BOTH);
        KeyValue ky = new KeyValue(outerRectangle.yProperty(),
                outerRectangle.getY() + outerRectangle.getHeight() / 2 - height / 2,
                Interpolator.EASE_BOTH);
        KeyValue kw = new KeyValue(outerRectangle.widthProperty(), width, Interpolator.EASE_BOTH);
        KeyValue kh = new KeyValue(outerRectangle.heightProperty(), height, Interpolator.EASE_BOTH);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(600), kx, ky, kh, kw)
        );
        timeline.play();
    }
}

