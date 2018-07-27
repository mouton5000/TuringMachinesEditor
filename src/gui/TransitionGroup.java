/*
 * Copyright (c) 2018 Dimitri Watel
 */

package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;
import org.json.JSONObject;
import turingmachines.Tape;
import turingmachines.Transition;
import util.MouseListener;
import util.Pair;
import util.Vector;

import java.util.List;


/**
 * Widget corresponding to a transition of the machine.
 *
 * The widget is a group containing
 * <ul>
 *     <li>a cubic bezier curve as main line</li>
 *     <li>a invisible and large cubic bezier curve over the main line used as a hitbox</li>
 *     <li>two lines representing the arrow</li>
 *     <li>other circles and lines to let the user edit the curve</li>
 * </ul>
 */
class TransitionGroup extends Group {
    /**
     * Transition associated with the widget
     */
    Transition transition;

    /**
     * Widget associated with the input state of the transition. It is stored here to make the transition
     * transformation faster.
     */
    private final StateGroup input;

    /**
     * Widget associated with the output state of the transition. It is stored here to make the transition
     * transformation faster.
     */
    private final StateGroup output;

    /**
     * Main line of the group.
     */
    private CubicCurve centerLine;

    /**
     * Line used as a hitbox of the group.
     */
    private TransitionArrowInvisibleLine invisibleLine;

    /**
     * Line of the arrow of the transition.
     */
    private Line arrowLine1;

    /**
     * Line of the arrow of the transition.
     */
    private Line arrowLine2;

    /**
     * Circle displayed when the user wants to edit the curve. It is always positioned where the first control key of
     * the main curve is.
     */
    private TransitionArrowControl1KeyCircle control1Key;

    /**
     * Circle displayed when the user wants to edit the curve. It is always positioned where the second control key of
     * the main curve is.
     */
    private TransitionArrowControl2KeyCircle control2Key;

    /**
     * Line displayed when the user wants to edit the curve, linking the center of the input state to the first control
     * key.
     */
    private Line control1Line;

    /**
     * Line displayed when the user wants to edit the curve, linking the center of the ouput state to the second
     * control key.
     */
    private Line control2Line;

    /**
     * Label displayed near the transition and containing the read symbols and the actions of the transition.
     */
    private TransitionDisplay transitionDisplay;

    /**
     * Property containing the abscissa of the middle point of the main line of the transition.
     */
    private DoubleProperty centerX;

    /**
     * Property containing the ordinate of the middle point of the main line of the transition.
     */
    private DoubleProperty centerY;

    /**
     * When the user press the mouse on the hitbox of the transition ({@link #invisibleLine}, the widget gets darker
     * during a fixed period of time. This parameter is true if the user is pressing the transition and if the timer
     * has not ended.
     */
    boolean animating;

    /**
     * When the user press the mouse on the transition, using this timeline, the widget gets darker during a fixed
     * period of time.
     */
    private Timeline timeline;

    /**
     * Build a widget associated with the given transition from the given input state to the given output state.
     * @param transition
     * @param input
     * @param output
     */
    TransitionGroup(Transition transition, StateGroup input, StateGroup output) {
        this.transition = transition;
        this.input = input;
        this.output = output;

        this.transitionDisplay = new TransitionDisplay();

        this.animating = false;
        this.timeline = new Timeline();
        this.timeline.setOnFinished(actionEvent -> animating = false);

        this.centerX = new SimpleDoubleProperty();
        this.centerY = new SimpleDoubleProperty();

        invisibleLine = new TransitionArrowInvisibleLine( this);

        control1Key = new TransitionArrowControl1KeyCircle(this, TuringMachineDrawer.TRANSITION_KEY_RADIUS);
        control2Key = new TransitionArrowControl2KeyCircle(this, TuringMachineDrawer.TRANSITION_KEY_RADIUS);
        control1Line = new Line();
        control2Line = new Line();

        control1Key.setFill(TuringMachineDrawer.TRANSITION_KEY_COLOR);
        control2Key.setFill(TuringMachineDrawer.TRANSITION_KEY_COLOR);
        control1Key.setStroke(TuringMachineDrawer.TRANSITION_KEY_STROKE_COLOR);
        control2Key.setStroke(TuringMachineDrawer.TRANSITION_KEY_STROKE_COLOR);
        control1Line.setStroke(TuringMachineDrawer.TRANSITION_KEY_LINE_COLOR);
        control2Line.setStroke(TuringMachineDrawer.TRANSITION_KEY_LINE_COLOR);
        control1Line.setStrokeWidth(TuringMachineDrawer.TRANSITION_KEY_LINE_STROKE_WIDTH);
        control2Line.setStrokeWidth(TuringMachineDrawer.TRANSITION_KEY_LINE_STROKE_WIDTH);

        control1Key.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);
        control2Key.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);

        centerLine = new CubicCurve();
        centerLine.setFill(Color.TRANSPARENT);
        centerLine.setStroke(Color.BLACK);
        arrowLine1 = new Line();
        arrowLine2 = new Line();

        this.getChildren().addAll(centerLine, arrowLine1, arrowLine2, transitionDisplay, invisibleLine, control1Line, control2Line,
                control1Key, control2Key);

        // When the input/output state coordinate is changed, change the drawing of the transition
        // Note that Editing the coordinates of the center line changes the coordinates of the other widgets contained
        // in this group.
        ChangeListener<Number> inputXChangeListener = (obs, oldVal, newVal) -> {
            centerLine.setStartX(centerLine.getStartX() + newVal.doubleValue() - oldVal.doubleValue());
            centerLine.setControlX1(centerLine.getControlX1() + newVal.doubleValue() - oldVal.doubleValue());
        };
        ChangeListener<Number> inputYChangeListener = (obs, oldVal, newVal) -> {
            centerLine.setStartY(centerLine.getStartY() + newVal.doubleValue() - oldVal.doubleValue());
            centerLine.setControlY1(centerLine.getControlY1() + newVal.doubleValue() - oldVal.doubleValue());
        };
        ChangeListener<Number> outputXChangeListener = (obs, oldVal, newVal) -> {
            centerLine.setEndX(centerLine.getEndX() + newVal.doubleValue() - oldVal.doubleValue());
            centerLine.setControlX2(centerLine.getControlX2() + newVal.doubleValue() - oldVal.doubleValue());
        };
        ChangeListener<Number> outputYChangeListener = (obs, oldVal, newVal) -> {
            centerLine.setEndY(centerLine.getEndY() + newVal.doubleValue() - oldVal.doubleValue());
            centerLine.setControlY2(centerLine.getControlY2() + newVal.doubleValue() - oldVal.doubleValue());
        };

        input.layoutXProperty().addListener(inputXChangeListener);
        input.translateXProperty().addListener(inputXChangeListener);
        input.layoutYProperty().addListener(inputYChangeListener);
        input.translateYProperty().addListener(inputYChangeListener);

        output.layoutXProperty().addListener(outputXChangeListener);
        output.translateXProperty().addListener(outputXChangeListener);
        output.layoutYProperty().addListener(outputYChangeListener);
        output.translateYProperty().addListener(outputYChangeListener);

        // When the center line is edited, update the other shapes of the group.
        // StartX, StartY, endX, endY are updated with the control keys
        centerLine.startXProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setStartX(nv);
            control1Line.setStartX(nv);
            centerX.setValue(getCenterX());
            replaceDisplay();
        });
        centerLine.startYProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setStartY(nv);
            control1Line.setStartY(nv);
            centerY.setValue(getCenterY());
            replaceDisplay();
        });
        centerLine.endXProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setEndX(nv);
            control2Line.setStartX(nv);
            centerX.setValue(getCenterX());
            replaceDisplay();
        });
        centerLine.endYProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setEndY(nv);
            control2Line.setStartY(nv);
            centerY.setValue(getCenterY());
            replaceDisplay();
        });
        centerLine.controlX1Property().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            replaceStart();
            invisibleLine.setControlX1(nv);
            control1Key.setCenterX(nv);
            control1Line.setEndX(nv);
            centerX.setValue(getCenterX());
            replaceDisplay();
        });
        centerLine.controlY1Property().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            replaceStart();
            invisibleLine.setControlY1(nv);
            control1Key.setCenterY(nv);
            control1Line.setEndY(nv);
            centerY.setValue(getCenterY());
            replaceDisplay();
        });
        centerLine.controlX2Property().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            replaceEnd();
            invisibleLine.setControlX2(nv);
            control2Key.setCenterX(nv);
            control2Line.setEndX(nv);
            centerX.setValue(getCenterX());
            replaceDisplay();
            setArrow();
        });
        centerLine.controlY2Property().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            replaceEnd();
            invisibleLine.setControlY2(nv);
            control2Key.setCenterY(nv);
            control2Line.setEndY(nv);
            centerY.setValue(getCenterY());
            replaceDisplay();
            setArrow();
        });

        // Initialize the drawing depending on the position of the nodes
        computeCoordinates();
        setUnselected();
    }

    /**
     * @return the property pointing to the abscissa of the middle point of the main line of the transition.
     */
    DoubleProperty centerXProperty() {
        return centerX;
    }

    /**
     * @return the property pointing to the ordinate of the middle point of the main line of the transition.
     */
    DoubleProperty centerYProperty() {
        return centerY;
    }

    /**
     * Compute the main line coordinates considering where the input and output states are.
     */
    private void computeCoordinates() {
        if (input != output)
            computeCoordinatesNotSame();
        else
            computeCoordinatesSame();
    }

    /**
     * Compute the main line coordinates considering where the input and output states are in the case where those
     * states are not the sames.
     *
     * This default drawing is a straight line from the input state to the output state.
     */
    private void computeCoordinatesNotSame(){
        Vector v1 = new Vector(input.getLayoutX() + input.getTranslateX(),
                input.getLayoutY() + input.getTranslateY());
        Vector v2 = new Vector(output.getLayoutX() + output.getTranslateX(),
                output.getLayoutY() + output.getTranslateY());
        Vector w = v2.diff(v1);

        double dist = w.mag();
        w.multIP(1/dist); // Normalize (could have call normalize but we need dist)
        w.multIP(TuringMachineDrawer.STATE_RADIUS);

        // Set v1 and v2 pointing to the intersection of the circles corresponding to the input and output state
        // and the straight line linking those two states.
        v1.addIP(w);
        v2.diffIP(w);

        dist -= 2 * TuringMachineDrawer.STATE_RADIUS; // Dist is now the distance of the current (v2 - v1)
        w.multIP(dist * TuringMachineDrawer.TRANSITION_KEY_DISTANCE_RATIO / TuringMachineDrawer.STATE_RADIUS);
        // |w| is the distance between the border of the states and the keys of the curve

        // Draw the control keys of the center line.
        // All other shapes and properties (including StartX, StartY, endX, endY) will be
        // updated using the property listeners initialized in the constructor.
        centerLine.setControlX1(v1.x + w.x);
        centerLine.setControlY1(v1.y + w.y);
        centerLine.setControlX2(v2.x - w.x);
        centerLine.setControlY2(v2.y - w.y);
    }


    /**
     * Compute the main line coordinates considering where the input and output states are in the case where those
     * states are the same state.
     *
     * This default drawing is a curve below the state (going from and to that state).
     */
    private void computeCoordinatesSame(){
        Vector v1 = new Vector(input.getLayoutX() + input.getTranslateX(),
                input.getLayoutY() + input.getTranslateY());

        Vector w = new Vector(0, TuringMachineDrawer.STATE_RADIUS);
        w.rotateIP(TuringMachineDrawer.TRANSITION_SAME_STATE_DEFAULT_CONTROL_ANGLE);
        v1.addIP(w);
        // v1 is on the intersection of the circle of the state with a line doing an angle of value
        // TRANSITION_SAME_STATE_DEFAULT_CONTROL_ANGLE with the vertical line.

        w.multIP(TuringMachineDrawer.TRANSITION_SAME_STATE_DEFAULT_CONTROL_DISTANCE_RATIO);
        // |w| is the distance between the border of the states and the keys of the curve

        // Draw the first control key of the center line.
        // All other shapes and properties (including StartX, StartY, endX, endY) will be
        // updated using the property listeners initialized in the constructor.
        centerLine.setControlX1(v1.x + w.x);
        centerLine.setControlY1(v1.y + w.y);

        // Symetric case for the second part of the center line.

        v1.set(input.getLayoutX(), input.getLayoutY());
        w.set(0, TuringMachineDrawer.STATE_RADIUS);
        w.rotateIP(-TuringMachineDrawer.TRANSITION_SAME_STATE_DEFAULT_CONTROL_ANGLE);
        v1.addIP(w);
        w.multIP(TuringMachineDrawer.TRANSITION_SAME_STATE_DEFAULT_CONTROL_DISTANCE_RATIO);

        // Draw the second part of the center line.
        centerLine.setControlX2(v1.x + w.x);
        centerLine.setControlY2(v1.y + w.y);
    }

    private void replaceStart() {
        Vector v1 = new Vector(input.getLayoutX() + input.getTranslateX(),
                input.getLayoutY() + input.getTranslateY());
        Vector w = new Vector(centerLine.getControlX1(), centerLine.getControlY1());

        w.diffIP(v1);
        w.normalizeIP();
        w.multIP(TuringMachineDrawer.STATE_RADIUS);

        w.addIP(v1);
        centerLine.setStartX(w.x);
        centerLine.setStartY(w.y);
    }

    private void replaceEnd() {
        Vector v1 = new Vector(output.getLayoutX() + output.getTranslateX(),
                output.getLayoutY() + output.getTranslateY());
        Vector w = new Vector(centerLine.getControlX2(), centerLine.getControlY2());

        w.diffIP(v1);
        w.normalizeIP();
        w.multIP(TuringMachineDrawer.STATE_RADIUS);

        w.addIP(v1);
        centerLine.setEndX(w.x);
        centerLine.setEndY(w.y);
    }

    /**
     * Replace and rotate the string associated with the transition displaying the read symbols and the actions of that
     * transition.
     *
     * The orientation of the string follows the straight line linking the input and output states of the transition.
     * Place that string near the middle point of the main line ("above" it if we consider the horizontal line as the
     * line linking the states of the transition).
     */
    private void replaceDisplay(){
        Vector vc =
                new Vector(centerX.getValue() - transitionDisplay.getMaxWidth() / 2,
                        centerY.getValue() - transitionDisplay.getMaxHeight() / 2
                );

        double angle = getAngle();
        Vector n = new Vector(1, 0);
        n.rotateIP(angle + Math.PI / 2);

        n.multIP(TuringMachineDrawer.TRANSITION_DISPLAY_MARGIN);

        vc.addIP(n);

        transitionDisplay.setLayoutX(vc.x);
        transitionDisplay.setLayoutY(vc.y);

        if(angle > Math.PI / 2 || angle < -Math.PI / 2){
            angle -= Math.PI;
        }
        transitionDisplay.setRotate(angle * 360 / (2 * Math.PI));
    }

    /**
     * Draw the transition as selected (used when the user click on the transition)
     */
    void setSelected(){
        invisibleLine.setVisible(false);
        control1Line.setVisible(true);
        control1Key.setVisible(true);
        control2Line.setVisible(true);
        control2Key.setVisible(true);
        input.toBack();
        output.toBack();
        this.toFront();
    }

    /**
     * Draw the transition as not selected (used when the user unselect the transition)
     */
    void setUnselected(){
        invisibleLine.setVisible(true);
        control1Line.setVisible(false);
        control1Key.setVisible(false);
        control2Line.setVisible(false);
        control2Key.setVisible(false);
        input.toFront();
        output.toFront();
        this.toBack();
    }

    /**
     * Set the position of the first control key of the main line of the transition to the given coordinates.
     * @param x
     * @param y
     */
    void setControl1(double x, double y) {
        this.centerLine.setControlX1(x);
        this.centerLine.setControlY1(y);
    }

    /**
     * Set the position of the second control key of the main line of the transition to the given coordinates.
     * @param x
     * @param y
     */
    void setControl2(double x, double y) {
        this.centerLine.setControlX2(x);
        this.centerLine.setControlY2(y);
    }

    /**
     * Draw the arrow of the transition.
     */
    private void setArrow(){
        Vector v1 = new Vector(centerLine.getControlX2(), centerLine.getControlY2());
        Vector v2 = new Vector(centerLine.getEndX(), centerLine.getEndY());

        v1.diffIP(v2);
        v1.normalizeIP();
        v1.multIP(TuringMachineDrawer.TRANSITION_SIZE);

        v1.rotateIP(TuringMachineDrawer.TRANSITION_ANGLE);
        arrowLine1.setStartX(v1.x + v2.x);
        arrowLine1.setStartY(v1.y + v2.y);
        arrowLine1.setEndX(v2.x);
        arrowLine1.setEndY(v2.y);

        v1.rotateIP(-2 * TuringMachineDrawer.TRANSITION_ANGLE);
        arrowLine2.setStartX(v1.x + v2.x);
        arrowLine2.setStartY(v1.y + v2.y);
        arrowLine2.setEndX(v2.x);
        arrowLine2.setEndY(v2.y);
    }

    /**
     * @return the abcissa of the middle point of the main line of the transition.
     */
    double getCenterX() {
        return tPointX(0.5);
    }

    /**
     * @return the ordinate of the middle point of the main line of the transition.
     */
    double getCenterY() {
        return tPointY(0.5);
    }

    /**
     * @return the angle between an horizontal line and the line linking the input and the output state.
     */
    private double getAngle() {
        Vector v1 = new Vector(centerLine.getStartX(), centerLine.getStartY());
        Vector v2 = new Vector(centerLine.getEndX(), centerLine.getEndY());
        v2.diffIP(v1);
        v1.set(1, 0);

        return v2.angle(v1);
    }

    /**
     * @param t a double between 0 and 1
     * @return the abcissa of the point of the main line (a bezier curve) at position t. If t = 0, this is the starting
     * coordinate and, if t = 1, the ending coordinate. For any other value of t, the point evolve linearly on the
     * line.
     */
    private double tPointX(double t){
        double x1, x2, x3, x4, x12, x23, x34, x1223, x2334;
        x1 = centerLine.getStartX();
        x2 = centerLine.getControlX1();
        x3 = centerLine.getControlX2();
        x4 = centerLine.getEndX();


        x12 = x1 + (x2 - x1) * t;
        x23 = x2 + (x3 - x2) * t;
        x34 = x3 + (x4 - x3) * t;

        x1223 = x12 + (x23 - x12) * t;
        x2334 = x23 + (x34 - x23) * t;

        return x1223 + (x2334 - x1223) * t;
    }

    /**
     * @param t a double between 0 and 1
     * @return the ordinate of the point of the main line (a bezier curve) at position t. If t = 0, this is the
     * starting coordinate and, if t = 1, the ending coordinate. For any other value of t, the point evolve linearly
     * on the line.
     */
    private double tPointY(double t){
        double y1, y2, y3, y4, y12, y23, y34, y1223, y2334;
        y1 = centerLine.getStartY();
        y2 = centerLine.getControlY1();
        y3 = centerLine.getControlY2();
        y4 = centerLine.getEndY();

        y12 = y1 + (y2 - y1) * t;
        y23 = y2 + (y3 - y2) * t;
        y34 = y3 + (y4 - y3) * t;

        y1223 = y12 + (y23 - y12) * t;
        y2334 = y23 + (y34 - y23) * t;

        return y1223 + (y2334 - y1223) * t;
    }

    /**
     * Add the given tape to the string displayed next to the transition
     * @param tape
     */
    void addTape(Tape tape) {
        transitionDisplay.addTape(tape);
    }

    /**
     * Remove the given tape from the string displayed next to the transition.
     * @param tape
     */
    void removeTape(Tape tape) {
        transitionDisplay.removeTape(tape);
    }


    /**
     * Add a new head to the string displayed next to the transition. The head is added to the given tape and has the
     * given color.
     *
     * @param tape
     * @param color
     */
    void addHead(Tape tape, Color color) {
        transitionDisplay.addHead(tape, color);
    }

    /**
     * Change the color of the given head (identified by the given tape and the index of the head in the list of
     * heads of the tape) to the given color in the string displayed next to the transition.
     * @param tape
     * @param head
     * @param color
     */
    void editHeadColor(Tape tape, int head, Color color) {
        transitionDisplay.editHeadColor(tape, head, color);
    }

    /**
     * Remove the given head (identified by the given tape and the index of the head in the list of
     * heads of the tape) from the string displayed next to the transition.
     * @param tape
     * @param head
     */
    void removeHead(Tape tape, int head) {
        transitionDisplay.removeHead(tape, head);
    }

    /**
     * Change the name of the symbol (identified by its previous name) by the given name in the string displayed next
     * to the transition.
     * @param previousSymbol
     * @param symbol new name of the symbol
     */
    void editSymbol(String previousSymbol, String symbol){ transitionDisplay.editSymbol(previousSymbol, symbol); }

    /**
     * Remove the given symbol (identified y its name) from the string displayed next to the transition.
     * @param symbol
     */
    void removeSymbol(String symbol){ transitionDisplay.removeSymbol(symbol); }

    /**
     * Add a read symbol to the string displayed next to the transition. The read symbol
     * consists in the triplet containing the given tape, the given head (identified with the index of the head in
     * the list of heads of the tape) and the given symbol.
     * @param tape
     * @param head
     * @param symbol
     */
    void addReadSymbol(Tape tape, int head, String symbol) {
        transitionDisplay.addReadSymbol(tape, head, symbol);
    }

    /**
     * Remove a read symbol from the string displayed next to the transition. The read symbol
     * consists in the triplet containing the given tape, the given head (identified with the index of the head in
     * the list of heads of the tape) and the given symbol.
     * @param tape
     * @param head
     * @param symbol
     */
    void removeReadSymbol(Tape tape, int head, String symbol) {
        transitionDisplay.removeReadSymbol(tape, head, symbol);
    }

    /**
     * Add an action to the the string displayed next to the transition. The action deals with the given tape and
     * the given head (identified with the index of the head in the list of heads of the tape). The type of action
     * (moving a head or writing on the tape) is the given type. The value is either the direction of the moving or
     * the symbol that should be written.
     *
     * @param tape
     * @param head
     * @param actionSymbol
     */
    void addAction(Tape tape, int head, String actionSymbol) {
        transitionDisplay.addAction(tape, head, actionSymbol);
    }

    /**
     * Remove an action at the given index in the list of actions of the transition from the the string displayed next
     * to the transition.
     * @param index
     */
    void removeAction(int index){
        transitionDisplay.removeAction(index);
    }

    /**
     * Start an animation darkening the transition.
     */
    void startTimeline(){
        this.animating = true;
        timeline.getKeyFrames().clear();
        KeyValue kOpacity = new KeyValue(this.invisibleLine.opacityProperty(),
                TuringMachineDrawer.TRANSITION_PRESS_OPACITY,
                Interpolator.EASE_BOTH);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(TuringMachineDrawer.SETTINGS_PRESS_DURATION),
                        kOpacity)
        );

        timeline.play();
    }

    /**
     * Stop an animation darkening the transition.
     * @see #startTimeline()
     */
    void stopTimeline(){
        timeline.stop();
        this.invisibleLine.setOpacity(0);
        this.animating = false;
    }

    /**
     * @param tape
     * @param head
     * @return a property associated with the string containing all the read symbols associated with the given tape
     * and the given head (identified with the index of the head in the list of heads of the tape).
     */
    ObservableValue<String> getSymbolDisplayTextProperty(Tape tape, int head) {
        return transitionDisplay.getSymbolDisplayTextProperty(tape, head);
    }

    /**
     * @return The list of strings displaying the actions of the transition. Each string is associated with a color
     * corresponding the the head associated with the given action.
     */
    List<Pair<String, Color>> getActionsDisplay() {
        return transitionDisplay.getActionsDisplay();
    }

    /**
     * @return an animation key used to animate the firing of the transition.
     */
    KeyFrame getFiredKeyValue() {
        KeyFrame keyFrame;

        KeyValue kCenterColor = new KeyValue(this.centerLine.strokeProperty(),
                TuringMachineDrawer.TRANSITION_FIRED_COLOR,
                Interpolator.EASE_BOTH);
        KeyValue kCenterStrokeWidth = new KeyValue(this.centerLine.strokeWidthProperty(),
                TuringMachineDrawer.TRANSITION_FIRED_STROKE_WIDTH,
                Interpolator.EASE_BOTH);
        KeyValue kArrow1Color = new KeyValue(this.arrowLine1.strokeProperty(),
                TuringMachineDrawer.TRANSITION_FIRED_COLOR,
                Interpolator.EASE_BOTH);
        KeyValue kArrow1StrokeWidth = new KeyValue(this.arrowLine1.strokeWidthProperty(),
                TuringMachineDrawer.TRANSITION_FIRED_STROKE_WIDTH,
                Interpolator.EASE_BOTH);
        KeyValue kArrow2Color = new KeyValue(this.arrowLine2.strokeProperty(),
                TuringMachineDrawer.TRANSITION_FIRED_COLOR,
                Interpolator.EASE_BOTH);
        KeyValue kArrow2StrokeWidth = new KeyValue(this.arrowLine2.strokeWidthProperty(),
                TuringMachineDrawer.TRANSITION_FIRED_STROKE_WIDTH,
                Interpolator.EASE_BOTH);

        keyFrame = new KeyFrame(Duration.millis(TuringMachineDrawer.ANIMATION_DURATION / 2),
                kCenterColor, kCenterStrokeWidth, kArrow1Color, kArrow1StrokeWidth,
                kArrow2Color, kArrow2StrokeWidth);

        return keyFrame;
    }

    /**
     * @return a JSON description of the transition
     */
    JSONObject getJSON() {
        return new JSONObject()
                .put("input", input.state)
                .put("output", output.state)
                .put("control1X", centerLine.getControlX1())
                .put("control1Y", centerLine.getControlY1())
                .put("control2X", centerLine.getControlX2())
                .put("control2Y", centerLine.getControlY2())
                .put("display", transitionDisplay.getJSON());
    }
}

/**
 * This widget represents the hitbox of a transition on the GUI. It is a path acting like a large bezier curve.
 */
class TransitionArrowInvisibleLine extends Path implements MouseListener {
    TransitionGroup transitionGroup;

    MoveTo moveTo;
    CubicCurveTo cubicCurveTo1;

    TransitionArrowInvisibleLine(TransitionGroup transitionGroup) {
        super();
        this.transitionGroup = transitionGroup;

        this.setFillRule(FillRule.EVEN_ODD);
        this.setStrokeWidth(TuringMachineDrawer.TRANSITION_HITBOX_WIDTH);
        this.setStroke(Color.BLACK);
        this.setOpacity(0);

        moveTo = new MoveTo();
        cubicCurveTo1 = new CubicCurveTo();
        this.getElements().addAll(moveTo, cubicCurveTo1);


        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

    }

    void setStartX(double startX) {
        moveTo.setX(startX);
    }

    void setStartY(double startY) {
        moveTo.setY(startY);
    }

    void setEndX(double endX){
        cubicCurveTo1.setX(endX);
    }

    void setEndY(double endY){
        cubicCurveTo1.setY(endY);
    }

    void setControlX1(double controlX1){
        cubicCurveTo1.setControlX1(controlX1);
    }

    void setControlY1(double controlY1){
        cubicCurveTo1.setControlY1(controlY1);
    }

    void setControlX2(double controlX2){
        cubicCurveTo1.setControlX2(controlX2);
    }

    void setControlY2(double controlY2){
        cubicCurveTo1.setControlY2(controlY2);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode)
            return false;

        GraphPane graphPane = TuringMachineDrawer.getInstance().graphPane;

        if(graphPane.stateSettingsRectangle.isMaximized()){
            graphPane.closeStateSettingsRectangle();
            return true;
        }
        else if(graphPane.transitionSettingsRectangle.isMaximized()){
            graphPane.closeTransitionSettingsRectangle();
            return true;
        }

        if(TuringMachineDrawer.getInstance().manualMode)
            TuringMachineDrawer.getInstance().manualFireTransition(transitionGroup);
        else {
            boolean pressFinished = !transitionGroup.animating;
            transitionGroup.stopTimeline();

            if (pressFinished) {
                graphPane.unselect();
                graphPane.openTransitionSettingsRectangle(transitionGroup);
            } else
                graphPane.select(transitionGroup);
        }

        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        transitionGroup.stopTimeline();
        TuringMachineDrawer.getInstance().graphPane.select(transitionGroup);
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(!TuringMachineDrawer.getInstance().graphPane.stateSettingsRectangle.isMaximized()
                && !TuringMachineDrawer.getInstance().graphPane.transitionSettingsRectangle.isMaximized())
            transitionGroup.startTimeline();

        return false;
    }
}

/**
 * This class is a circle positionned on the first control key of the main line of the widget associated with a
 * transition.
 */
class TransitionArrowControl1KeyCircle extends Circle implements MouseListener {

    TransitionGroup transitionGroup;

    TransitionArrowControl1KeyCircle(TransitionGroup transitionGroup, double v) {
        super(v);
        this.transitionGroup = transitionGroup;
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(!(TuringMachineDrawer.getInstance().graphPane.getSelected() instanceof TransitionGroup))
            return false;

        transitionGroup.setControl1(mouseEvent.getX(), mouseEvent.getY());
        TuringMachineDrawer.getInstance().setEnableToSave();
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}


/**
 * This class is a circle positioned on the second control key of the main line of the widget associated with a
 * transition.
 */
class TransitionArrowControl2KeyCircle extends Circle implements MouseListener {

    TransitionGroup transitionGroup;

    TransitionArrowControl2KeyCircle(TransitionGroup transitionGroup, double v) {
        super(v);
        this.transitionGroup = transitionGroup;
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(!(TuringMachineDrawer.getInstance().graphPane.getSelected() instanceof TransitionGroup))
            return false;

        transitionGroup.setControl2(mouseEvent.getX(), mouseEvent.getY());
        TuringMachineDrawer.getInstance().setEnableToSave();
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}