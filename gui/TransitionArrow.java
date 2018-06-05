package gui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import util.Vector;

public class TransitionArrow extends Group {
    private final TuringMachineDrawer drawer;
    private final Circle input;
    private final Circle output;

    private CubicCurve centerLine;
    private TransitionArrowInvisibleLine invisibleLine;
    private Line arrowLine1;
    private Line arrowLine2;

    private TransitionArrowControl1KeyCircle control1Key;
    private TransitionArrowControl2KeyCircle control2Key;
    private Line control1Line;
    private Line control2Line;

    TransitionArrow(TuringMachineDrawer drawer, Circle input, Circle output) {
        this.drawer = drawer;
        this.input = input;
        this.output = output;

        invisibleLine = new TransitionArrowInvisibleLine(this);
        invisibleLine.setStrokeWidth(TuringMachineDrawer.ARROW_HITBOX_WIDTH);
        invisibleLine.setStroke(Color.BLACK);
        invisibleLine.setStrokeWidth(TuringMachineDrawer.STATE_RADIUS);
        invisibleLine.setOpacity(0);

        invisibleLine.setOnMouseClicked(drawer.graphPaneMouseHandler);

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

        setKeysVisible(false);

        centerLine = new CubicCurve();
        centerLine.setFill(Color.TRANSPARENT);
        centerLine.setStroke(Color.BLACK);
        arrowLine1 = new Line();
        arrowLine2 = new Line();

        this.getChildren().addAll(centerLine, arrowLine1, arrowLine2);

        input.centerXProperty().addListener((obs, oldVal, newVal) -> {
            computeCoordinates();
        });
        input.centerYProperty().addListener((obs, oldVal, newVal) -> {
            computeCoordinates();
        });
        output.centerXProperty().addListener((obs, oldVal, newVal) -> {
            computeCoordinates();
        });
        output.centerYProperty().addListener((obs, oldVal, newVal) -> {
            computeCoordinates();
        });
        centerLine.startXProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setStartX(nv);
            control1Line.setStartX(nv);
        });
        centerLine.startYProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setStartY(nv);
            control1Line.setStartY(nv);
        });
        centerLine.endXProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setEndX(nv);
            control2Line.setStartX(nv);
        });
        centerLine.endYProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setEndY(nv);
            control2Line.setStartY(nv);
        });
        centerLine.controlX1Property().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setControlX1(nv);
            control1Key.setCenterX(nv);
            control1Line.setEndX(nv);
        });
        centerLine.controlY1Property().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setControlY1(nv);
            control1Key.setCenterY(nv);
            control1Line.setEndY(nv);
        });
        centerLine.controlX2Property().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setControlX2(nv);
            control2Key.setCenterX(nv);
            control2Line.setEndX(nv);
            setArrow();
        });
        centerLine.controlY2Property().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            invisibleLine.setControlY2(nv);
            control2Key.setCenterY(nv);
            control2Line.setEndY(nv);
            setArrow();
        });


        computeCoordinates();
    }

    private void computeCoordinates(){
        Vector v1 = new Vector(input.getCenterX(), input.getCenterY());
        Vector v2 = new Vector(output.getCenterX(), output.getCenterY());
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

    void setKeysVisible(boolean visible){

        if(visible){
            this.getChildren().addAll(
                    control1Line, control2Line, control1Key, control2Key);
            this.getChildren().removeAll(invisibleLine);
        }
        else{
            this.getChildren().removeAll(
                    control1Line, control2Line, control1Key, control2Key);
            this.getChildren().add(invisibleLine);
        }
    }

    void setControl1(double x, double y) {
        this.centerLine.setControlX1(x);
        this.centerLine.setControlY1(y);

        Vector v1 = new Vector(input.getCenterX(), input.getCenterY());
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

        Vector v1 = new Vector(output.getCenterX(), output.getCenterY());
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
        v1.multIP(TuringMachineDrawer.STATE_RADIUS);

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
}

class TransitionArrowInvisibleLine extends CubicCurve{
    TransitionArrow transitionArrow;

    public TransitionArrowInvisibleLine(TransitionArrow transitionArrow) {
        super();
        this.transitionArrow = transitionArrow;
    }
}

class TransitionArrowControl1KeyCircle extends Circle{

    TransitionArrow transitionArrow;

    public TransitionArrowControl1KeyCircle(TransitionArrow transitionArrow, double v) {
        super(v);
        this.transitionArrow = transitionArrow;
    }
}
class TransitionArrowControl2KeyCircle extends Circle{

    TransitionArrow transitionArrow;

    public TransitionArrowControl2KeyCircle(TransitionArrow transitionArrow, double v) {
        super(v);
        this.transitionArrow = transitionArrow;
    }
}