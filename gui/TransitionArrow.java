package gui;

import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Line;
import util.Vector;

public class TransitionArrow extends Group {
    private Circle input;
    private Circle output;
    private CubicCurve centerLine;
    private CubicCurve invisibleLine;
    private Line arrowLine1;
    private Line arrowLine2;

    public TransitionArrow(Circle input, Circle output) {
        this.input = input;
        this.output = output;

        invisibleLine = new CubicCurve();
//        invisibleLine.setStrokeWidth(TuringMachineDrawer.ARROW_HITBOX_WIDTH);
//        invisibleLine.setOpacity(0);
        centerLine = new CubicCurve();
        arrowLine1 = new Line();
        arrowLine2 = new Line();
        this.getChildren().addAll(invisibleLine, centerLine, arrowLine1, arrowLine2);

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
//        centerLine.startXProperty().addListener((obs, oldVal, newVal) -> {
//            invisibleLine.setStartX(newVal.doubleValue());
//        });
//        centerLine.startYProperty().addListener((obs, oldVal, newVal) -> {
//            invisibleLine.setStartY(newVal.doubleValue());
//        });
//        centerLine.endXProperty().addListener((obs, oldVal, newVal) -> {
//            invisibleLine.setEndX(newVal.doubleValue());
//        });
//        centerLine.endYProperty().addListener((obs, oldVal, newVal) -> {
//            invisibleLine.setEndY(newVal.doubleValue());
//        });
//        centerLine.controlX1Property().addListener((obs, oldVal, newVal) -> {
//            invisibleLine.setControlX1(newVal.doubleValue());
//        });
//        centerLine.controlY1Property().addListener((obs, oldVal, newVal) -> {
//            invisibleLine.setControlY1(newVal.doubleValue());
//        });
//        centerLine.controlX2Property().addListener((obs, oldVal, newVal) -> {
//            invisibleLine.setControlX2(newVal.doubleValue());
//        });
//        centerLine.controlY2Property().addListener((obs, oldVal, newVal) -> {
//            invisibleLine.setControlY2(newVal.doubleValue());
//        });

        computeCoordinates();
    }

    private void computeCoordinates(){
        Vector v1 = new Vector(input.getCenterX(), input.getCenterY());
        Vector v2 = new Vector(output.getCenterX(), output.getCenterY());
        Vector w = v2.diff(v1);
        w.normalizeIP();
        w.multIP(TuringMachineDrawer.STATE_RADIUS);
        v1.addIP(w);
        v2.diffIP(w);
        centerLine.setStartX(v1.x);
        centerLine.setStartY(v1.y);
        centerLine.setEndX(v2.x);
        centerLine.setEndY(v2.y);
        centerLine.setControlX1(v1.x + w.x);
        centerLine.setControlY1(v1.y + w.y + 40);
        centerLine.setControlX2(v2.x - w.x);
        centerLine.setControlY2(v2.y - w.y + 40);

        CubicCurveTo cc;
        

        v1.copyIP(v2);
        w.rotateIP(Math.PI - TuringMachineDrawer.ARROW_ANGLE);
        v1.addIP(w);
        arrowLine1.setStartX(v1.x);
        arrowLine1.setStartY(v1.y);
        arrowLine1.setEndX(v2.x);
        arrowLine1.setEndY(v2.y);

        v1.copyIP(v2);
        w.rotateIP(2 * TuringMachineDrawer.ARROW_ANGLE);
        v1.addIP(w);
        arrowLine2.setStartX(v1.x);
        arrowLine2.setStartY(v1.y);
        arrowLine2.setEndX(v2.x);
        arrowLine2.setEndY(v2.y);
    }
}
