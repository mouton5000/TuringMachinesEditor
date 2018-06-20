package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
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

    private static final Background CENTERED_BACKGROUND = new Background(
            new BackgroundFill(TuringMachineDrawer.TAPE_HEAD_MENU_CENTERED_FILL_COLOR,
                    CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background NOT_CENTERED_BACKGROUND = new Background(
            new BackgroundFill(TuringMachineDrawer.TAPE_HEAD_MENU_NOT_CENTERED_FILL_COLOR,
                    CornerRadii.EMPTY, Insets.EMPTY));

    TapeHeadMenu centered;

    TapesHeadMenu(TuringMachineDrawer drawer) {

        this.drawer = drawer;
        this.tapeToMenu = new HashMap<>();

        this.setMinHeight(TuringMachineDrawer.TAPES_HEAD_MENU_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.TAPES_HEAD_MENU_HEIGHT);
        this.setSpacing(TuringMachineDrawer.TAPES_HEAD_MENU_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);

        AddTapeIcon addTapeIcon = new AddTapeIcon(drawer);

        this.getChildren().addAll(addTapeIcon, new Separator(Orientation.VERTICAL));

    }

    void addTape(Tape tape) {
        TapeHeadMenu tapeHeadMenu = new TapeHeadMenu(this.drawer, tape);
        tapeToMenu.put(tape, tapeHeadMenu);
        this.getChildren().addAll(tapeHeadMenu, new Separator(Orientation.VERTICAL));

    }

    void removeTape(Tape tape) {
        TapeHeadMenu tapeHeadMenu = tapeToMenu.get(tape);
        int index = this.getChildren().indexOf(tapeHeadMenu);
        this.getChildren().remove(index + 1);
        this.getChildren().remove(index);
    }

    void addHead(Tape tape, Color color) {
        TapeHeadMenu tapeHeadMenu = tapeToMenu.get(tape);
        tapeHeadMenu.addHead(color);
    }

    void removeHead(Tape tape, int head) {
        TapeHeadMenu tapeHeadMenu = tapeToMenu.get(tape);
        tapeHeadMenu.removeHead(head);
    }


    void editHeadColor(Tape tape, int head, Color color) {
        TapeHeadMenu tapeHeadMenu = tapeToMenu.get(tape);
        tapeHeadMenu.editHeadColor(head, color);
    }

    void centerOn(Tape tape) {
        TapeHeadMenu tapeHeadMenu = tapeToMenu.get(tape);
        if(centered != null)
            centered.setBackground(TapesHeadMenu.NOT_CENTERED_BACKGROUND);
        tapeHeadMenu.setBackground(TapesHeadMenu.CENTERED_BACKGROUND);
        centered = tapeHeadMenu;
    }
}

class TapeHeadMenu extends HBox {
    TuringMachineDrawer drawer;
    HeadOptionRectangle headOptionRectangle;
    Tape tape;

    TapeHeadMenu(TuringMachineDrawer drawer, Tape tape) {
        this.drawer = drawer;
        this.tape = tape;

        this.setAlignment(Pos.CENTER);
        this.setSpacing(TuringMachineDrawer.TAPES_HEAD_MENU_SPACING);

        this.getChildren().add(new RemoveTapeIcon(drawer, this, tape));

        this.headOptionRectangle = new HeadOptionRectangle(drawer, this, tape);
        headOptionRectangle.managedProperty().bind(headOptionRectangle.visibleProperty());

        this.setOnMouseClicked(drawer.tapesMouseHandler);
    }

    void addHead(Color color){
        HeadMenuSelect headRectangle = new HeadMenuSelect(
                drawer,
                this,
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

    void removeHead(int head) {
        this.getChildren().remove(head + 1);
    }

    void openHeadOptionRectangle(int head) {
        headOptionRectangle.setHead(head);
        headOptionRectangle.setVisible(true);
        this.getChildren().remove(headOptionRectangle);
        this.getChildren().add(head + 2, headOptionRectangle);
        headOptionRectangle.maximize();
    }

    void closeHeadOptionRectangle(){
        headOptionRectangle.minimize(true);
    }

}

class HeadMenuSelect extends Rectangle {

    TuringMachineDrawer drawer;
    TapeHeadMenu tapeHeadMenu;
    private Timeline timeline;
    boolean animating;

    HeadMenuSelect( TuringMachineDrawer drawer, TapeHeadMenu tapeHeadMenu, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.drawer = drawer;
        this.tapeHeadMenu = tapeHeadMenu;

        this.timeline = new Timeline();
        this.timeline.setOnFinished(actionEvent -> animating = false);
        this.animating = false;

        this.setOnMouseClicked(drawer.tapesMouseHandler);
        this.setOnMousePressed(drawer.tapesMouseHandler);
        this.setOnMouseDragged(drawer.tapesMouseHandler);
    }

    int getHead(){
        int head = 0;
        for(Node child : this.getParent().getChildrenUnmodifiable()) {
            if (child == this)
                return head;
            if(child instanceof HeadMenuSelect)
                head++;
        }
        return -1;
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
    TapeHeadMenu tapeHeadMenu;
    Tape tape;

    RemoveTapeIcon(TuringMachineDrawer drawer, TapeHeadMenu tapeHeadMenu, Tape tape){
        super("./images/remove_tape.png");
        this.drawer = drawer;
        this.tapeHeadMenu = tapeHeadMenu;
        this.tape = tape;

        this.setOnMouseClicked(drawer.tapesMouseHandler);
    }
}