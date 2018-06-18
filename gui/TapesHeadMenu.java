package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import turingmachines.Tape;

import java.util.HashMap;
import java.util.Map;

class TapesHeadMenu extends HBox {

    private final TuringMachineDrawer drawer;
    private Map<Tape, TapeHeadMenu> tapeToMenu;

    TapesHeadMenu(TuringMachineDrawer drawer){

        this.drawer = drawer;
        this.tapeToMenu = new HashMap<>();

        this.setMinHeight(TuringMachineDrawer.TAPES_HEAD_MENU_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.TAPES_HEAD_MENU_HEIGHT);
        this.setSpacing(TuringMachineDrawer.TAPES_HEAD_MENU_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);

        AddTapeIcon addTapeIcon = new AddTapeIcon(drawer);

        this.getChildren().addAll(addTapeIcon, new Separator(Orientation.VERTICAL));

    }

    void addTape(Tape tape){
        TapeHeadMenu tapeHeadMenu = new TapeHeadMenu(this.drawer, tape);
        tapeToMenu.put(tape, tapeHeadMenu);
        this.getChildren().addAll(tapeHeadMenu, new Separator(Orientation.VERTICAL));

    }

    void removeTape(Tape tape){
        TapeHeadMenu tapeHeadMenu = tapeToMenu.get(tape);
        int index = this.getChildren().indexOf(tapeHeadMenu);
        this.getChildren().remove(index + 1);
        this.getChildren().remove(index);
    }

    void addHead(Tape tape, Color color){
        TapeHeadMenu tapeHeadMenu = tapeToMenu.get(tape);
        tapeHeadMenu.addHead(color);
    }


    void editHeadColor(Tape tape, int head, Color color) {
        TapeHeadMenu tapeHeadMenu = tapeToMenu.get(tape);
        tapeHeadMenu.editHeadColor(head, color);
    }
}

class TapeHeadMenu extends HBox {
    TuringMachineDrawer drawer;

    TapeHeadMenu(TuringMachineDrawer drawer, Tape tape) {
        this.drawer = drawer;
        this.setAlignment(Pos.CENTER);
        this.setSpacing(TuringMachineDrawer.TAPES_HEAD_MENU_SPACING);

        this.getChildren().add(new RemoveTapeIcon(drawer, tape));
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

    void editHeadColor(int head, Color color) {
        ((HeadMenuSelect)this.getChildren().get(head + 1)).setStroke(color);
    }
}

class HeadMenuSelect extends Rectangle {

    TuringMachineDrawer drawer;
    private Timeline timeline;
    boolean animating;

    HeadMenuSelect( TuringMachineDrawer drawer, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.drawer = drawer;
        this.timeline = new Timeline();
        this.timeline.setOnFinished(actionEvent -> animating = false);
        this.animating = false;

        this.setOnMouseClicked(drawer.tapesMouseHandler);
        this.setOnMousePressed(drawer.tapesMouseHandler);
        this.setOnMouseDragged(drawer.tapesMouseHandler);
    }

    void startTimeline(){
        animating = true;
        timeline.getKeyFrames().clear();
        KeyValue kfill = new KeyValue(this.fillProperty(),
                TuringMachineDrawer.EDIT_PRESS_COLOR,
                Interpolator.EASE_BOTH);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(TuringMachineDrawer.EDIT_PRESS_DURATION), kfill)
        );
        timeline.play();
    }

    void stopTimeline(){
        timeline.stop();
        this.setFill(TuringMachineDrawer.TAPE_HEAD_MENU_DEFAULT_FILL_COLOR);
        animating = false;
    }
}

class AddTapeIcon extends ImageView{

    TuringMachineDrawer drawer;

    AddTapeIcon(TuringMachineDrawer drawer){
        super("./images/add_tape.png");
        this.drawer = drawer;
        this.setOnMouseClicked(drawer.tapesMouseHandler);
    }
}

class RemoveTapeIcon extends ImageView{

    TuringMachineDrawer drawer;
    Tape tape;

    RemoveTapeIcon(TuringMachineDrawer drawer, Tape tape){
        super("./images/remove_tape.png");
        this.drawer = drawer;
        this.tape = tape;

        this.setOnMouseClicked(drawer.tapesMouseHandler);
    }
}