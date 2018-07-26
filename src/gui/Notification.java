/*
 * Copyright (c) 2018 Dimitri Watel
 */

package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * Created by dimitri.watel on 22/06/18.
 */
class Notification extends Group {

    Label label;
    Timeline timeline;
    boolean animating;

    Notification(){

        this.animating = false;

        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(TuringMachineDrawer.NOTIFICATION_WIDTH);
        rectangle.setHeight(TuringMachineDrawer.NOTIFICATION_HEIGHT);
        rectangle.setLayoutX(-rectangle.getWidth() / 2);
        rectangle.setLayoutY(-rectangle.getHeight() / 2);
        rectangle.setFill(Color.BLACK);

        rectangle.setArcWidth(15);
        rectangle.setArcHeight(15);

        label = new Label();
        label.setFont(Font.font(TuringMachineDrawer.NOTIFICATION_FONT_NAME,
                TuringMachineDrawer.NOTIFICATION_FONT_SIZE));

        label.setMinWidth(TuringMachineDrawer.NOTIFICATION_WIDTH - 6);
        label.setMaxWidth(TuringMachineDrawer.NOTIFICATION_WIDTH - 6);
        label.setMinHeight(TuringMachineDrawer.NOTIFICATION_HEIGHT);
        label.setMaxHeight(TuringMachineDrawer.NOTIFICATION_HEIGHT);
        label.setLayoutX(-TuringMachineDrawer.NOTIFICATION_WIDTH / 2 + 3);
        label.setLayoutY(-TuringMachineDrawer.NOTIFICATION_HEIGHT / 2);
        label.setTextFill(Color.WHITE);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setWrapText(true);

        this.setVisible(false);
        this.getChildren().addAll(rectangle, label);


        timeline = new Timeline();
        timeline.setOnFinished(actionEvent -> {
            this.setVisible(false);
            this.animating = false;
        });
    }

    void notifyMsg(String msg){
        if(animating)
            this.timeline.stop();
        label.setText(msg);
        this.setOpacity(1);

        timeline.getKeyFrames().clear();
        KeyValue kOpacity = new KeyValue(this.opacityProperty(),
                0,
                Interpolator.EASE_IN);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(TuringMachineDrawer.NOTIFICATION_DURATION),
                        kOpacity)
        );

        this.setVisible(true);
        this.animating = true;
        timeline.play();

    }
}
