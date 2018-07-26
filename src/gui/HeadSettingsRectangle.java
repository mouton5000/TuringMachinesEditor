/*
 * Copyright (c) 2018 Dimitri Watel
 */

package gui;

import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import turingmachines.Tape;
import util.MouseListener;
import util.Ressources;

/**
 * Created by dimitri.watel on 19/06/18.
 */
class HeadSettingsRectangle extends SettingsRectangle {

    TapeHeadMenu tapeHeadMenu;
    Tape tape;
    int currentHead;

    HeadSettingsRectangle(TapeHeadMenu tapeHeadMenu, Tape tape) {
        super();
        this.tapeHeadMenu = tapeHeadMenu;
        this.tape = tape;


        ImageView changeColorIcon = new ImageView(Ressources.getRessource("edit_head_color.png"));
        ColorPicker changeColorPicker = new ColorPicker();
        changeColorPicker.setOpacity(0);

        RemoveHeadIcon removeHeadIcon = new RemoveHeadIcon( this, Ressources.getRessource("remove_head.png"));


        changeColorPicker.setMinWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE);
        changeColorPicker.setMaxWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE);
        changeColorPicker.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE);
        changeColorPicker.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE);

        changeColorIcon.setLayoutX(
                TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING
                        + TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE / 2
                        - changeColorIcon.getBoundsInLocal().getWidth() / 2
        );

        changeColorIcon.setLayoutY(
                - changeColorIcon.getBoundsInLocal().getHeight() / 2
        );


        changeColorPicker.setLayoutX(
                TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING
        );

        changeColorPicker.setLayoutY(
                - TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE / 2
        );


        removeHeadIcon.setLayoutX(
                TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE * 3 / 2
                        + TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING * 2
                        - removeHeadIcon.getBoundsInLocal().getWidth() / 2
        );
        removeHeadIcon.setLayoutY(
                - removeHeadIcon.getBoundsInLocal().getHeight() / 2
        );

        changeColorPicker.setOnAction(actionEvent ->
        {
            TuringMachineDrawer.getInstance().editHeadColor(this.tape, this.currentHead, changeColorPicker.getValue());
        });

        this.getChildren().addAll(changeColorIcon, changeColorPicker, removeHeadIcon);
    }

    @Override
    double getMaximizedHeight() {
        return TuringMachineDrawer.TAPES_MENU_HEIGHT - 2;
    }
    @Override
    double getMaximizedWidth() {
        return TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE * 2
                + TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING * 3;
    }

    @Override
    double getOffsetY() {
        return - TuringMachineDrawer.TAPES_MENU_HEIGHT / 2 + 1;
    }

    @Override
    double getOffsetX() {
        return -TuringMachineDrawer.SETTING_RECTANGLE_MINIMIZED_WIDTH / 2;
    }

    void setHead(int head) {
        this.currentHead = head;
    }

    @Override
    void clear() {
        currentHead = 0;
    }
}

class RemoveHeadIcon extends ImageView implements MouseListener {

    HeadSettingsRectangle settingsRectangle;

    RemoveHeadIcon(HeadSettingsRectangle headSettingsRectangle, String s) {
        super(s);
        this.settingsRectangle = headSettingsRectangle;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode|| TuringMachineDrawer.getInstance().manualMode)
            return false;

        Tape tape = this.settingsRectangle.tape;
        int head = this.settingsRectangle.currentHead;
        TuringMachineDrawer.getInstance().removeHead(tape, head);
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