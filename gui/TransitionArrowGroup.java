package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import util.Vector;

public class TransitionArrowGroup extends Group {
    final TuringMachineDrawer drawer;
    private final StateGroup input;
    private final StateGroup output;

    private CubicCurve centerLine;
    private TransitionArrowInvisibleLine invisibleLine;
    private Line arrowLine1;
    private Line arrowLine2;

    private TransitionArrowControl1KeyCircle control1Key;
    private TransitionArrowControl2KeyCircle control2Key;
    private Line control1Line;
    private Line control2Line;

    private DoubleProperty centerX;
    private DoubleProperty centerY;

    boolean animating;
    private Timeline timeline;

    TransitionArrowGroup(TuringMachineDrawer drawer, StateGroup input, StateGroup output) {
        this.drawer = drawer;
        this.input = input;
        this.output = output;

        this.animating = false;
        this.timeline = new Timeline();
        this.timeline.setOnFinished(actionEvent -> animating = false);

        this.centerX = new SimpleDoubleProperty();
        this.centerY = new SimpleDoubleProperty();

        invisibleLine = new TransitionArrowInvisibleLine(this.drawer, this);
        invisibleLine.setStrokeWidth(TuringMachineDrawer.ARROW_HITBOX_WIDTH);
        invisibleLine.setStroke(Color.BLACK);
        invisibleLine.setOpacity(0);
        invisibleLine.setStrokeType(StrokeType.CENTERED);

        control1Key = new TransitionArrowControl1KeyCircle(this, TuringMachineDrawer.ARROW_KEY_RADIUS);
        control2Key = new TransitionArrowControl2KeyCircle(this, TuringMachineDrawer.ARROW_KEY_RADIUS);
        control1Line = new Line();
        control2Line = new Line();

        control1Key.setFill(TuringMachineDrawer.ARROW_KEY_COLOR);
        control2Key.setFill(TuringMachineDrawer.ARROW_KEY_COLOR);
        control1Key.setStroke(TuringMachineDrawer.ARROW_KEY_STROKE_COLOR);
        control2Key.setStroke(TuringMachineDrawer.ARROW_KEY_STROKE_COLOR);
        control1Line.setStroke(TuringMachineDrawer.ARROW_KEY_LINE_COLOR);
        control2Line.setStroke(TuringMachineDrawer.ARROW_KEY_LINE_COLOR);
        control1Line.setStrokeWidth(TuringMachineDrawer.ARROW_KEY_LINE_STROKE_WIDTH);
        control2Line.setStrokeWidth(TuringMachineDrawer.ARROW_KEY_LINE_STROKE_WIDTH);

        control1Key.setOnMouseDragged(drawer.graphPaneMouseHandler);
        control2Key.setOnMouseDragged(drawer.graphPaneMouseHandler);

        centerLine = new CubicCurve();
        centerLine.setFill(Color.TRANSPARENT);
        centerLine.setStroke(Color.BLACK);
        arrowLine1 = new Line();
        arrowLine2 = new Line();

        this.getChildren().addAll(centerLine, arrowLine1, arrowLine2, invisibleLine, control1Line, control2Line,
                control1Key, control2Key);

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


        centerLine.startXProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setStartX(nv);
            control1Line.setStartX(nv);
            centerX.setValue(getCenterX());
        });
        centerLine.startYProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setStartY(nv);
            control1Line.setStartY(nv);
            centerY.setValue(getCenterY());
        });
        centerLine.endXProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setEndX(nv);
            control2Line.setStartX(nv);
            centerX.setValue(getCenterX());
        });
        centerLine.endYProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setEndY(nv);
            control2Line.setStartY(nv);
            centerY.setValue(getCenterY());
        });
        centerLine.controlX1Property().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setControlX1(nv);
            control1Key.setCenterX(nv);
            control1Line.setEndX(nv);
            centerX.setValue(getCenterX());
        });
        centerLine.controlY1Property().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setControlY1(nv);
            control1Key.setCenterY(nv);
            control1Line.setEndY(nv);
            centerY.setValue(getCenterY());
        });
        centerLine.controlX2Property().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setControlX2(nv);
            control2Key.setCenterX(nv);
            control2Line.setEndX(nv);
            centerX.setValue(getCenterX());
            setArrow();
        });
        centerLine.controlY2Property().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setControlY2(nv);
            control2Key.setCenterY(nv);
            control2Line.setEndY(nv);
            centerY.setValue(getCenterY());
            setArrow();
        });


        computeCoordinates();
        setSelected(false);
    }

    public DoubleProperty centerXProperty() {
        return centerX;
    }

    public DoubleProperty centerYProperty() {
        return centerY;
    }

    private void computeCoordinates() {
        if (input != output)
            computeCoordinatesNotSame();
        else
            computeCoordinatesSame();
    }

    private void computeCoordinatesNotSame(){
        Vector v1 = new Vector(input.getLayoutX() + input.getTranslateX(),
                input.getLayoutY() + input.getTranslateY());
        Vector v2 = new Vector(output.getLayoutX() + output.getTranslateX(),
                output.getLayoutY() + output.getTranslateY());
        Vector w = v2.diff(v1);
        double dist = w.mag();

        w.multIP(1/dist); // Normalize (could have call normalize but we need dist)
        w.multIP(TuringMachineDrawer.STATE_RADIUS);
        v1.addIP(w);
        v2.diffIP(w);

        dist -= 2 * TuringMachineDrawer.STATE_RADIUS;
        w.multIP(dist * TuringMachineDrawer.ARROW_KEY_DISTANCE_RATIO / TuringMachineDrawer.STATE_RADIUS);

        centerLine.setStartX(v1.x);
        centerLine.setStartY(v1.y);
        centerLine.setEndX(v2.x);
        centerLine.setEndY(v2.y);
        centerLine.setControlX1(v1.x + w.x);
        centerLine.setControlY1(v1.y + w.y);
        centerLine.setControlX2(v2.x - w.x);
        centerLine.setControlY2(v2.y - w.y);
    }

    private void computeCoordinatesSame(){
        Vector v1 = new Vector(input.getLayoutX() + input.getTranslateX(),
                input.getLayoutY() + input.getTranslateY());

        Vector w = new Vector(0, TuringMachineDrawer.STATE_RADIUS);
        w.rotateIP(TuringMachineDrawer.ARROW_SAME_STATE_DEFAULT_CONTROL_ANGLE);
        v1.addIP(w);
        w.multIP(TuringMachineDrawer.ARROW_SAME_STATE_DEFAULT_CONTROL_DISTANCE_RATIO);

        centerLine.setStartX(v1.x);
        centerLine.setStartY(v1.y);
        centerLine.setControlX1(v1.x + w.x);
        centerLine.setControlY1(v1.y + w.y);

        v1.set(input.getLayoutX(), input.getLayoutY());
        w.set(0, TuringMachineDrawer.STATE_RADIUS);
        w.rotateIP(-TuringMachineDrawer.ARROW_SAME_STATE_DEFAULT_CONTROL_ANGLE);
        v1.addIP(w);
        w.multIP(TuringMachineDrawer.ARROW_SAME_STATE_DEFAULT_CONTROL_DISTANCE_RATIO);

        centerLine.setEndX(v1.x);
        centerLine.setEndY(v1.y);
        centerLine.setControlX2(v1.x + w.x);
        centerLine.setControlY2(v1.y + w.y);
    }

    void setSelected(boolean visible){

        if(visible){
            invisibleLine.setVisible(false);
            control1Line.setVisible(true);
            control1Key.setVisible(true);
            control2Line.setVisible(true);
            control2Key.setVisible(true);
            input.toBack();
            output.toBack();
            this.toFront();
        }
        else{
            invisibleLine.setVisible(true);
            control1Line.setVisible(false);
            control1Key.setVisible(false);
            control2Line.setVisible(false);
            control2Key.setVisible(false);
            input.toFront();
            output.toFront();
            this.toBack();
        }
    }

    void setControl1(double x, double y) {
        this.centerLine.setControlX1(x);
        this.centerLine.setControlY1(y);

        Vector v1 = new Vector(input.getLayoutX() + input.getTranslateX(),
                input.getLayoutY() + input.getTranslateY());
        Vector v2 = new Vector(x, y);
        v2.diffIP(v1);
        v2.normalizeIP();
        v2.multIP(TuringMachineDrawer.STATE_RADIUS);
        v2.addIP(v1);

        this.centerLine.setStartX(v2.x);
        this.centerLine.setStartY(v2.y);
    }

    void setControl2(double x, double y) {
        this.centerLine.setControlX2(x);
        this.centerLine.setControlY2(y);

        Vector v1 = new Vector(output.getLayoutX() + output.getTranslateX(),
                output.getLayoutY() + output.getTranslateY());
        Vector v2 = new Vector(x, y);
        v2.diffIP(v1);
        v2.normalizeIP();
        v2.multIP(TuringMachineDrawer.STATE_RADIUS);
        v2.addIP(v1);

        this.centerLine.setEndX(v2.x);
        this.centerLine.setEndY(v2.y);
    }

    private void setArrow(){
        Vector v1 = new Vector(centerLine.getControlX2(), centerLine.getControlY2());
        Vector v2 = new Vector(centerLine.getEndX(), centerLine.getEndY());

        v1.diffIP(v2);
        v1.normalizeIP();
        v1.multIP(TuringMachineDrawer.ARROW_SIZE);

        v1.rotateIP(TuringMachineDrawer.ARROW_ANGLE);
        arrowLine1.setStartX(v1.x + v2.x);
        arrowLine1.setStartY(v1.y + v2.y);
        arrowLine1.setEndX(v2.x);
        arrowLine1.setEndY(v2.y);

        v1.rotateIP(-2 * TuringMachineDrawer.ARROW_ANGLE);
        arrowLine2.setStartX(v1.x + v2.x);
        arrowLine2.setStartY(v1.y + v2.y);
        arrowLine2.setEndX(v2.x);
        arrowLine2.setEndY(v2.y);
    }

    double getCenterX() {
        return tPointX(0.5);
    }

    double getCenterY() {
        return tPointY(0.5);
    }

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

    void startTimeline(){
        this.animating = true;
        timeline.getKeyFrames().clear();
        KeyValue kOpacity = new KeyValue(this.invisibleLine.opacityProperty(),
                TuringMachineDrawer.ARROW_PRESS_OPACITY,
                Interpolator.EASE_BOTH);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(TuringMachineDrawer.ARROW_PRESS_DURATION),
                        kOpacity)
        );

        timeline.play();
    }

    void stopTimeline(){
        timeline.stop();
        this.invisibleLine.setOpacity(0);
        this.animating = false;
    }
}

class TransitionArrowInvisibleLine extends CubicCurve{
    TransitionArrowGroup transitionArrowGroup;

    public TransitionArrowInvisibleLine(TuringMachineDrawer drawer, TransitionArrowGroup transitionArrowGroup) {
        super();
        this.transitionArrowGroup = transitionArrowGroup;

        this.setOnMousePressed(drawer.graphPaneMouseHandler);
        this.setOnMouseClicked(drawer.graphPaneMouseHandler);
    }
}

class TransitionArrowControl1KeyCircle extends Circle{

    TransitionArrowGroup transitionArrowGroup;

    public TransitionArrowControl1KeyCircle(TransitionArrowGroup transitionArrowGroup, double v) {
        super(v);
        this.transitionArrowGroup = transitionArrowGroup;
    }
}
class TransitionArrowControl2KeyCircle extends Circle{

    TransitionArrowGroup transitionArrowGroup;

    public TransitionArrowControl2KeyCircle(TransitionArrowGroup transitionArrowGroup, double v) {
        super(v);
        this.transitionArrowGroup = transitionArrowGroup;
    }
}