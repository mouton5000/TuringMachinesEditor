package gui;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.Scanner;

public class StateCircle extends Group {

    TuringMachineDrawer drawer;
    Circle outerCircle;
    Circle innerCircle;
    Label label;

    private DoubleProperty centerX;
    private DoubleProperty centerY;

    public StateCircle(TuringMachineDrawer drawer){
        this.drawer = drawer;

        centerX = new SimpleDoubleProperty();
        centerY = new SimpleDoubleProperty();

        outerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        outerCircle.setFill(Color.WHITE);
        outerCircle.setStroke(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        innerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS *
                TuringMachineDrawer.FINAL_STATE_RADIUS_RATIO);
        innerCircle.setVisible(false);

        label = new Label("ABCDE");
        label.setPrefSize(TuringMachineDrawer.STATE_RADIUS, TuringMachineDrawer.STATE_RADIUS);
        label.setAlignment(Pos.CENTER);

        centerX.addListener((obs, oldVal, newVal) -> {
            outerCircle.setCenterX(newVal.doubleValue());
            innerCircle.setCenterX(newVal.doubleValue());
            label.setLayoutX(newVal.doubleValue());
        });

        centerY.addListener((obs, oldVal, newVal) -> {
            outerCircle.setCenterY(newVal.doubleValue());
            innerCircle.setCenterY(newVal.doubleValue());
            label.setLayoutY(newVal.doubleValue());
        });

        label.textProperty().addListener((obs, oldVal, newVal) -> {
            label.setLayoutX(getCenterX() - label.getWidth()/2);
            label.setLayoutY(getCenterY() - label.getHeight()/2);
        });

        this.getChildren().addAll(outerCircle, innerCircle, label);
    }

    public DoubleProperty centerXProperty(){
        return outerCircle.centerXProperty();
    }

    public DoubleProperty centerYProperty(){
        return outerCircle.centerYProperty();
    }

    public double getCenterX() {
        return centerX.doubleValue();
    }

    public void setCenterX(double centerX) {
        this.centerX.setValue(centerX);
    }

    public double getCenterY() {
        return centerY.doubleValue();
    }

    public void setCenterY(double centerY) {
        this.centerY.setValue(centerY);
    }
}
