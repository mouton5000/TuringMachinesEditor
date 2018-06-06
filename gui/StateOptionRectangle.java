package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.shape.Circle;

/**
 * Created by dimitri.watel on 06/06/18.
 */
class StateOptionRectangle extends OptionRectangle{

    private final EventHandler<ActionEvent> optionsAppear;
    StateGroup stateGroup;
    private Circle finalCircleOptionOuterCircle;
    private Circle finalCircleOptionInnerCircle;

    StateOptionRectangle(TuringMachineDrawer drawer, StateGroup stateGroup) {
        super(drawer);
        this.stateGroup = stateGroup;
        finalCircleOptionOuterCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        finalCircleOptionOuterCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        finalCircleOptionOuterCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        finalCircleOptionInnerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS *
                TuringMachineDrawer.FINAL_STATE_RADIUS_RATIO);
        finalCircleOptionInnerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        finalCircleOptionInnerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        finalCircleOptionOuterCircle.setVisible(false);
        finalCircleOptionInnerCircle.setVisible(false);

        optionsAppear = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                finalCircleOptionInnerCircle.setVisible(true);
                finalCircleOptionOuterCircle.setVisible(true);
            }
        };

        this.getChildren().addAll(finalCircleOptionOuterCircle, finalCircleOptionInnerCircle);
    }

    @Override
    void setCenterX(double centerX) {
        super.setCenterX(centerX);
        finalCircleOptionOuterCircle.setCenterX(centerX);
        finalCircleOptionInnerCircle.setCenterX(centerX);
    }

    @Override
    void setCenterY(double centerY) {
        super.setCenterY(centerY);
        finalCircleOptionOuterCircle.setCenterY(centerY);
        finalCircleOptionInnerCircle.setCenterY(centerY);
    }

    @Override
    void maximize() {
        timeline.setOnFinished(optionsAppear);
        super.maximize();

    }

    @Override
    void minimize(boolean animate) {
        timeline.setOnFinished(null);
        finalCircleOptionInnerCircle.setVisible(false);
        finalCircleOptionOuterCircle.setVisible(false);
        super.minimize(animate);
    }
}
