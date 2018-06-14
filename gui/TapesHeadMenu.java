package gui;

import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TapesHeadMenu extends HBox {

    private final TuringMachineDrawer drawer;

    TapesHeadMenu(TuringMachineDrawer drawer){

        this.drawer = drawer;

        this.setMinHeight(TuringMachineDrawer.TAPES_HEAD_MENU_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.TAPES_HEAD_MENU_HEIGHT);
        this.setSpacing(TuringMachineDrawer.TAPES_HEAD_MENU_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);

        TapeHeadMenu tapeHeadMenu = new TapeHeadMenu(this.drawer);

        Separator separator = new Separator();

        this.getChildren().addAll(tapeHeadMenu, separator);


    }

    void addHead(int tape, Color color){
        TapeHeadMenu tapeHeadMenu = (TapeHeadMenu) this.getChildren().get(tape);
        tapeHeadMenu.addHead(color);
    }


    public void editHeadColor(int tape, int head, Color color) {
        TapeHeadMenu tapeHeadMenu = (TapeHeadMenu) this.getChildren().get(tape);
        tapeHeadMenu.editHeadColor(head, color);
    }
}

class TapeHeadMenu extends HBox {
    TuringMachineDrawer drawer;
    TapeHeadMenu(TuringMachineDrawer drawer) {
        this.drawer = drawer;
        this.setAlignment(Pos.CENTER);
        this.setSpacing(TuringMachineDrawer.TAPES_HEAD_MENU_SPACING);
    }

    void addHead(Color color){
        HeadMenuSelect headRectangle = new HeadMenuSelect(
                drawer,
                0, 0,
                TuringMachineDrawer.TAPES_HEAD_MENU_HEAD_SIZE,
                TuringMachineDrawer.TAPES_HEAD_MENU_HEAD_SIZE);
        headRectangle.setFill(Color.WHITE);
        headRectangle.setStroke(color);
        headRectangle.setStrokeWidth(TuringMachineDrawer.TAPE_HEAD_MENU_HEAD_STROKE_WIDTH);
        this.getChildren().add(headRectangle);
    }

    public void editHeadColor(int head, Color color) {
        ((HeadMenuSelect)this.getChildren().get(head)).setStroke(color);
    }
}

class HeadMenuSelect extends Rectangle {

    TuringMachineDrawer drawer;

    public HeadMenuSelect( TuringMachineDrawer drawer, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.drawer = drawer;
        this.setOnMouseClicked(drawer.tapesMouseHandler);
        this.setOnMousePressed(drawer.tapesMouseHandler);
    }
}
