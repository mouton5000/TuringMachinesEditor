package gui;

import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.ImageView;
import turingmachines.Tape;

/**
 * Created by dimitri.watel on 19/06/18.
 */
class HeadOptionRectangle extends OptionRectangle{

    TapeHeadMenu tapeHeadMenu;
    Tape tape;
    int currentHead;

    HeadOptionRectangle(TapeHeadMenu tapeHeadMenu, Tape tape) {
        super(TuringMachineDrawer.getInstance().tapesMouseHandler);
        this.tapeHeadMenu = tapeHeadMenu;
        this.tape = tape;


        ImageView changeColorIcon = new ImageView("./images/edit_head_color.png");
        ColorPicker changeColorPicker = new ColorPicker();
        changeColorPicker.setOpacity(0);

        RemoveHeadIcon removeHeadIcon = new RemoveHeadIcon( this, "./images/remove_head.png");


        changeColorPicker.setMinWidth(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE);
        changeColorPicker.setMaxWidth(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE);
        changeColorPicker.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE);
        changeColorPicker.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE);

        changeColorIcon.setLayoutX(
                TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING
                        + TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE / 2
                        - changeColorIcon.getBoundsInLocal().getWidth() / 2
        );

        changeColorIcon.setLayoutY(
                - changeColorIcon.getBoundsInLocal().getHeight() / 2
        );


        changeColorPicker.setLayoutX(
                TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING
        );

        changeColorPicker.setLayoutY(
                - TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE / 2
        );


        removeHeadIcon.setLayoutX(
                TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE * 3 / 2
                        + TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING * 2
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
    protected double getMaximizedHeight() {
        return TuringMachineDrawer.TAPES_MENU_HEIGHT - 2;
    }
    @Override
    protected double getMaximizedWidth() {
        return TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE * 2
                + TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING * 3;
    }

    @Override
    protected double getOffsetY() {
        return - TuringMachineDrawer.TAPES_MENU_HEIGHT / 2 + 1;
    }

    @Override
    protected double getOffsetX() {
        return -TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_WIDTH / 2;
    }

    @Override
    protected Node associatedNode() {
        return null;
    }

    public void setHead(int head) {
        this.currentHead = head;
    }

    @Override
    public void clear() {
        currentHead = 0;
    }
}

class RemoveHeadIcon extends ImageView{

    HeadOptionRectangle optionRectangle;

    public RemoveHeadIcon(HeadOptionRectangle headOptionRectangle, String s) {
        super(s);
        this.optionRectangle = headOptionRectangle;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().tapesMouseHandler);
    }
}