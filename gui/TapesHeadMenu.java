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
import org.json.JSONArray;
import org.json.JSONObject;
import turingmachines.Tape;
import util.Ressources;

import java.util.HashMap;
import java.util.Map;

class TapesHeadMenu extends HBox {

    private Map<Tape, TapeHeadMenu> tapeToMenu;

    private static final Background CENTERED_BACKGROUND = new Background(
            new BackgroundFill(TuringMachineDrawer.TAPE_HEAD_MENU_CENTERED_FILL_COLOR,
                    CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background NOT_CENTERED_BACKGROUND = new Background(
            new BackgroundFill(TuringMachineDrawer.TAPE_HEAD_MENU_NOT_CENTERED_FILL_COLOR,
                    CornerRadii.EMPTY, Insets.EMPTY));

    TapeHeadMenu centered;
    private double offsetX;

    TapesHeadMenu() {

        this.tapeToMenu = new HashMap<>();
        this.offsetX = 0;

        this.setMinHeight(TuringMachineDrawer.TAPES_MENU_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.TAPES_MENU_HEIGHT);
        this.setSpacing(TuringMachineDrawer.TAPES_MENU_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);

        AddTapeIcon addTapeIcon = new AddTapeIcon();

        this.setOnMousePressed(TuringMachineDrawer.getInstance().tapesMouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().tapesMouseHandler);

        this.getChildren().addAll(addTapeIcon, new Separator(Orientation.VERTICAL));

    }

    void addTape(Tape tape) {
        TapeHeadMenu tapeHeadMenu = new TapeHeadMenu(tape);
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

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    void closeAllOptionRectangle() {
        for(TapeHeadMenu tapeHeadMenu: tapeToMenu.values())
            tapeHeadMenu.closeHeadOptionRectangle(false);
    }

    KeyFrame getHeadWriteKeyFrame(Tape tape, Integer head) {
        return tapeToMenu.get(tape).getHeadWriteKeyFrame(head);
    }

    JSONArray getJSON() {
        JSONArray jsonArray = new JSONArray();
        for(Node child: this.getChildren()) {
            if(!(child instanceof TapeHeadMenu))
                continue;
            jsonArray.put(((TapeHeadMenu)child).getJSON());
        }
        return jsonArray;
    }

    void clear() {
        for(TapeHeadMenu tapeHeadMenu: tapeToMenu.values())
            tapeHeadMenu.clear();
    }
}

class TapeHeadMenu extends HBox {
    HeadOptionRectangle headOptionRectangle;
    Tape tape;

    TapeHeadMenu(Tape tape) {
        this.tape = tape;

        this.setAlignment(Pos.CENTER);
        this.setSpacing(TuringMachineDrawer.TAPES_MENU_SPACING);

        this.getChildren().add(new RemoveTapeIcon(this, tape));

        this.headOptionRectangle = new HeadOptionRectangle( this, tape);
        headOptionRectangle.managedProperty().bind(headOptionRectangle.visibleProperty());

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().tapesMouseHandler);
    }

    void addHead(Color color){
        HeadMenuSelect headRectangle = new HeadMenuSelect( this);
        headRectangle.setStroke(color);
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

    void closeHeadOptionRectangle() { closeHeadOptionRectangle(true); }
    void closeHeadOptionRectangle(boolean animate) { headOptionRectangle.minimize(animate); }

    KeyFrame getHeadWriteKeyFrame(Integer head) {
        HeadMenuSelect headRectangle = (HeadMenuSelect) this.getChildren().get(head + 1);
        return headRectangle.getHeadWriteKeyFrame();
    }

    JSONArray getJSON() {
        JSONArray jsonArray = new JSONArray();
        for(Node child: this.getChildren()) {
            if(!(child instanceof HeadMenuSelect))
                continue;
            jsonArray.put(((HeadMenuSelect)child).getJSON());
        }
        return jsonArray;
    }

    void clear() {
        closeHeadOptionRectangle();
        headOptionRectangle.clear();
    }
}

class HeadMenuSelect extends Rectangle {

    TapeHeadMenu tapeHeadMenu;
    private Timeline timeline;
    boolean animating;

    HeadMenuSelect( TapeHeadMenu tapeHeadMenu) {
        super(0, 0, TuringMachineDrawer.TAPES_HEAD_MENU_HEAD_SIZE, TuringMachineDrawer.TAPES_HEAD_MENU_HEAD_SIZE);

        this.tapeHeadMenu = tapeHeadMenu;

        this.setFill(Color.WHITE);
        this.setStrokeWidth(TuringMachineDrawer.TAPE_HEAD_MENU_HEAD_STROKE_WIDTH);

        this.timeline = new Timeline();
        this.timeline.setOnFinished(actionEvent -> animating = false);
        this.animating = false;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().tapesMouseHandler);
        this.setOnMousePressed(TuringMachineDrawer.getInstance().tapesMouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().tapesMouseHandler);
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
        this.setFill(TuringMachineDrawer.TAPE_MENU_DEFAULT_FILL_COLOR);
        animating = false;
    }

    KeyFrame getHeadWriteKeyFrame() {
        KeyValue kStrokeWidth = new KeyValue(this.strokeWidthProperty(),
                TuringMachineDrawer.HEAD_WRITE_STROKE_WIDTH,
                Interpolator.EASE_BOTH);

        return new KeyFrame(Duration.millis(TuringMachineDrawer.ANIMATION_DURATION / 2), kStrokeWidth);
    }

    JSONObject getJSON() {
        return new JSONObject()
                .put("color", this.getStroke());
    }
}

class AddTapeIcon extends ImageView{

    AddTapeIcon(){
        super(Ressources.getRessource("add_tape.png"));
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().tapesMouseHandler);
    }
}

class RemoveTapeIcon extends ImageView{

    TapeHeadMenu tapeHeadMenu;
    Tape tape;

    RemoveTapeIcon( TapeHeadMenu tapeHeadMenu, Tape tape){
        super(Ressources.getRessource("remove_tape.png"));
        this.tapeHeadMenu = tapeHeadMenu;
        this.tape = tape;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().tapesMouseHandler);
    }
}