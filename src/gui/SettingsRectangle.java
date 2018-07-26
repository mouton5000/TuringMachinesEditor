/*
 * Copyright (c) 2018 Dimitri Watel
 */

package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import util.MouseListener;

/**
 * Widget containing Settings.
 *
 * Each such widget is a rectangle containing icons the user can click to activate/deactivate an option.
 * The widget is, at first, invisible. The user must click or press some specific points of the application to make
 * them appear.
 *
 * When the widget appears, it grows (using or not an animation), and, conversely, when it is closed, it first
 * diminishes.
 *
 * It has six subclasses, each one is a distinct widget in the application.
 * <ul>
 *     <li>{@link CellSettingsRectangle}</li>
 *     <li>{@link StateSettingsRectangle}</li>
 *     <li>{@link TransitionSettingsRectangle}</li>
 *     <li>{@link TapeSettingsRectangle}</li>
 *     <li>{@link HeadSettingsRectangle}</li>
 *     <li>{@link SymbolSettingsRectangle}</li>
 * </ul>
 */
abstract class SettingsRectangle extends Group implements MouseListener {

    /**
     * A small rectangle inside the group, the only button in the rectangle that close the rectangle.
     */
    private MinimizedSettingsRectangle minimizedRectangle;

    /**
     * The outer rectangle of this group.
     */
    private Rectangle maximizedRectangle;

    /**
     * Timeline used to animated the growing and the minimizing of the rectangle.
     */
    Timeline timeline;

    /**
     * True if the rectangle is visible and maximized.
     */
    private boolean maximized;

    /**
     * Build a generic empty settings rectangle.
     */
    SettingsRectangle(){


        minimizedRectangle = new MinimizedSettingsRectangle(this);
        minimizedRectangle.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        // Set the clip so that no inner shape may move outside the rectangle without disappearing.
        Rectangle clipRectangle = new Rectangle();
        this.setClip(clipRectangle);

        maximizedRectangle = new Rectangle();

        maximizedRectangle.xProperty().bindBidirectional(clipRectangle.xProperty());
        maximizedRectangle.yProperty().bindBidirectional(clipRectangle.yProperty());
        maximizedRectangle.widthProperty().bindBidirectional(clipRectangle.widthProperty());
        maximizedRectangle.heightProperty().bindBidirectional(clipRectangle.heightProperty());

        maximizedRectangle.setWidth(TuringMachineDrawer.SETTING_RECTANGLE_MINIMIZED_WIDTH);
        maximizedRectangle.setHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);
        maximizedRectangle.setFill(TuringMachineDrawer.STATE_SETTINGS_RECTANGLE_INNER_COLOR);
        maximizedRectangle.setStroke(TuringMachineDrawer.STATE_SETTINGS_RECTANGLE_OUTER_COLOR);

        timeline = new Timeline();

        maximized = false;

        this.getChildren().addAll(maximizedRectangle, minimizedRectangle);

        maximizedRectangle.setX(- maximizedRectangle.getWidth() / 2);
        maximizedRectangle.setY(- maximizedRectangle.getHeight() + TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT / 2);
    }

    /**
     * @return true if the settings rectangle is maximized.
     */
    boolean isMaximized(){
        return maximized;
    }

    /**
     * Make the rectangle appear, using an animation that grows the rectangle.
     */
    void maximize(){
        if(maximized)
            return;
        
        maximized = true;
        minimizedRectangle.toFront();

        timeline.setOnFinished(actionEvent -> TuringMachineDrawer.getInstance().animating = false);

        // Animate to the given size. The size uses the methods #getMaximizedWidth and #getMaximizedHeight so that
        // subclasses may have not the default size.
        animateSize(
                minimizedRectangle.getLayoutX() + getOffsetX(),
                minimizedRectangle.getLayoutY() + getOffsetY(),
                getMaximizedWidth(),
                getMaximizedHeight());
    }

    /**
     * @return the width of the maximized rectangle.
     */
    double getMaximizedWidth(){
        return TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH;
    }

    /**
     * @return the height of the maximized rectangle.
     */
    double getMaximizedHeight(){
        return TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT;
    }

    /**
     * @return the difference between the abscissa of the minimized rectangle and the maximized rectangle.
     */
    double getOffsetX(){return -TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH / 2;}

    /**
     * @return the difference between the ordinate of the minimized rectangle and the maximized rectangle.
     */
    double getOffsetY(){
        return TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT / 2
                - getMaximizedHeight();
    }

    /**
     * Minimize and vanish the rectangle. Use an animation if animate is true.
     * @param animate
     */
    void minimize(boolean animate){
        if(!maximized)
            return;

        maximized = false;

        if(animate) {
            timeline.setOnFinished(actionEvent -> {
                TuringMachineDrawer.getInstance().animating = false;
                this.setVisible(false);
            });
            animateSize(minimizedRectangle.getLayoutX() - TuringMachineDrawer.SETTING_RECTANGLE_MINIMIZED_WIDTH / 2,
                    minimizedRectangle.getLayoutY() - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT / 2,
                    TuringMachineDrawer.SETTING_RECTANGLE_MINIMIZED_WIDTH,
                    TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);
        }
        else {
            maximizedRectangle.setX(minimizedRectangle.getLayoutX() - TuringMachineDrawer.SETTING_RECTANGLE_MINIMIZED_WIDTH / 2);
            maximizedRectangle.setY(minimizedRectangle.getLayoutY() - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT / 2);
            maximizedRectangle.setWidth(TuringMachineDrawer.SETTING_RECTANGLE_MINIMIZED_WIDTH);
            maximizedRectangle.setHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);
            this.setVisible(false);
        }
    }

    /**
     * Animate the rectangle from its current size and position to the given size and position.
     * @param x
     * @param y
     * @param width
     * @param height
     */
    private void animateSize(double x, double y, double width, double height){
        if(TuringMachineDrawer.getInstance().animating)
            return;
        TuringMachineDrawer.getInstance().animating = true;
        timeline.getKeyFrames().clear();
        KeyValue kx = new KeyValue(maximizedRectangle.xProperty(), x, Interpolator.EASE_BOTH);
        KeyValue ky = new KeyValue(maximizedRectangle.yProperty(), y, Interpolator.EASE_BOTH);
        KeyValue kw = new KeyValue(maximizedRectangle.widthProperty(), width, Interpolator.EASE_BOTH);
        KeyValue kh = new KeyValue(maximizedRectangle.heightProperty(), height, Interpolator.EASE_BOTH);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(TuringMachineDrawer.SETTINGS_RECTANGLE_TIMELINE_DURATION), kx, ky, kh, kw)
        );
        timeline.play();
    }

    /**
     * Each settings rectangle is associated with another widget on the screen. This method clears
     * everything related to this widget.
     */
    abstract void clear();

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return !TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return !TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

/**
 * Small rectangle containing three dots inside a SettingsRectangle. It is the only button inside the rectangle that
 * close it.
 */
class MinimizedSettingsRectangle extends Group implements MouseListener{
    private SettingsRectangle settingsRectangle;

    MinimizedSettingsRectangle(SettingsRectangle settingsRectangle) {
        this.settingsRectangle = settingsRectangle;

        Rectangle minimizedRectangle = new Rectangle();
        minimizedRectangle.setWidth(TuringMachineDrawer.SETTING_RECTANGLE_MINIMIZED_WIDTH);
        minimizedRectangle.setHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);
        minimizedRectangle.setFill(TuringMachineDrawer.STATE_SETTINGS_RECTANGLE_INNER_COLOR);
        minimizedRectangle.setStroke(TuringMachineDrawer.STATE_SETTINGS_RECTANGLE_OUTER_COLOR);

        WaitingDots waitingDots = new WaitingDots();

        this.getChildren().addAll(minimizedRectangle, waitingDots);
        minimizedRectangle.setX( - minimizedRectangle.getWidth() / 2);
        minimizedRectangle.setY( - minimizedRectangle.getHeight() / 2);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        this.settingsRectangle.minimize(true);
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