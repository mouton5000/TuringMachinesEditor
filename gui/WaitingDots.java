package gui;

import javafx.scene.Group;
import javafx.scene.shape.Circle;

/**
 * Created by dimitri.watel on 06/06/18.
 */
public class WaitingDots extends Group {
    Circle left;
    Circle middle;
    Circle right;

    CoordinatesProperties coords;

    private static final int MARGIN = 3;

    WaitingDots(){
        left = new Circle(1);
        middle = new Circle(1);
        right = new Circle(1);

        coords = new CoordinatesProperties();


        coords.xProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            left.setCenterX(nv - MARGIN);
            middle.setCenterX(nv);
            right.setCenterX(nv + MARGIN);
        });

        coords.yProperty().addListener((obs, oldVal, newVal) -> {
            double nv = newVal.doubleValue();
            left.setCenterY(nv);
            middle.setCenterY(nv);
            right.setCenterY(nv);
        });

        this.getChildren().addAll(left, middle, right);

    }

    void setCenterX(double centerX){
        this.coords.setX(centerX);
    }

    void setCenterY(double centerY){
        this.coords.setY(centerY);
    }
}
