package gui;

import javafx.scene.Group;
import javafx.scene.shape.Circle;

/**
 * Created by dimitri.watel on 06/06/18.
 */
class WaitingDots extends Group {

    private static final int MARGIN = 3;

    WaitingDots(){
        Circle left = new Circle(1);
        Circle middle = new Circle(1);
        Circle right = new Circle(1);

        this.getChildren().addAll(left, middle, right);

        left.setCenterX(- MARGIN);
        right.setCenterX(MARGIN);

    }
}
