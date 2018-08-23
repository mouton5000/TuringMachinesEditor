/*
 * Copyright (c) 2018 Dimitri Watel
 */

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
import javafx.scene.input.MouseEvent;
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
import util.MouseListener;
import util.Ressources;

import java.util.HashMap;
import java.util.Map;

class TapesHeadMenu extends HBox implements MouseListener {

    private Map<Tape, TapeHeadMenu> tapeToMenu;

    private static final Background CENTERED_BACKGROUND = new Background(
            new BackgroundFill(TuringMachineDrawer.TAPE_HEAD_MENU_CENTERED_FILL_COLOR,
                    CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background NOT_CENTERED_BACKGROUND = new Background(
            new BackgroundFill(TuringMachineDrawer.TAPE_HEAD_MENU_NOT_CENTERED_FILL_COLOR,
                    CornerRadii.EMPTY, Insets.EMPTY));

    TapeHeadMenu centered;
    private double offsetX;
    private Double dragX;

    TapesHeadMenu() {

        this.tapeToMenu = new HashMap<>();
        this.offsetX = 0;

        this.setMinHeight(TuringMachineDrawer.TAPES_MENU_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.TAPES_MENU_HEIGHT);
        this.setSpacing(TuringMachineDrawer.TAPES_MENU_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);

        AddTapeIcon addTapeIcon = new AddTapeIcon();

        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);

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

    void closeAllSettingsRectangle() {
        for(TapeHeadMenu tapeHeadMenu: tapeToMenu.values())
            tapeHeadMenu.closeHeadSettingsRectangle(false);
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

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        if(dragX == null)
            dragX = x;
        else {
            this.translate(x - dragX);
            dragX = x;
        }
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        dragX = mouseEvent.getX();
        return true;
    }
}

class TapeHeadMenu extends HBox implements MouseListener {
    HeadSettingsRectangle headSettingsRectangle;
    Tape tape;

    TapeHeadMenu(Tape tape) {
        this.tape = tape;

        this.setAlignment(Pos.CENTER);
        this.setSpacing(TuringMachineDrawer.TAPES_MENU_SPACING);

        this.getChildren().add(new RemoveTapeIcon(this, tape));

        this.headSettingsRectangle = new HeadSettingsRectangle( this, tape);
        headSettingsRectangle.managedProperty().bind(headSettingsRectangle.visibleProperty());

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    private HeadMenuSelect getHeadMenuSelect(int head){
        for(Node child : this.getChildren()){
            if(child instanceof HeadMenuSelect) {
                if (head == 0)
                    return (HeadMenuSelect) child;
                else
                    head--;
            }
        }
        return null;
    }

    void addHead(Color color){
        HeadMenuSelect headRectangle = new HeadMenuSelect( this);
        headRectangle.setStroke(color);
        this.getChildren().add(headRectangle);
    }

    void editHeadColor(int head, Color color) {
        getHeadMenuSelect(head).setStroke(color);
    }

    void removeHead(int head) {
        this.closeHeadSettingsRectangle(true);
        this.getChildren().remove(getHeadMenuSelect(head));
    }

    void openHeadSettingsRectangle(int head) {
        headSettingsRectangle.setHead(head);
        headSettingsRectangle.setVisible(true);
        this.getChildren().remove(headSettingsRectangle);
        this.getChildren().add(head + 2, headSettingsRectangle);
        headSettingsRectangle.maximize();
    }

    void closeHeadSettingsRectangle() { closeHeadSettingsRectangle(true); }
    void closeHeadSettingsRectangle(boolean animate) {
        headSettingsRectangle.minimize(animate);
    }

    KeyFrame getHeadWriteKeyFrame(Integer head) {
        return getHeadMenuSelect(head).getHeadWriteKeyFrame();
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
        closeHeadSettingsRectangle();
        headSettingsRectangle.clear();
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(this.headSettingsRectangle.isMaximized())
            this.closeHeadSettingsRectangle();
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

class HeadMenuSelect extends Rectangle implements MouseListener{

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

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);
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
                new KeyFrame(Duration.millis(TuringMachineDrawer.SETTINGS_PRESS_DURATION), kfill)
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

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode){
            TuringMachineDrawer.getInstance().centerOn(this.tapeHeadMenu.tape, this.getHead());
            return false;
        }
        else {
            if (this.tapeHeadMenu.headSettingsRectangle.isMaximized())
                this.tapeHeadMenu.closeHeadSettingsRectangle();
            else {
                boolean pressFinished = !this.animating;
                this.stopTimeline();

                if (!pressFinished)
                    TuringMachineDrawer.getInstance().centerOn(this.tapeHeadMenu.tape, this.getHead());
                else
                    this.tapeHeadMenu.openHeadSettingsRectangle(this.getHead());
            }
            return true;
        }
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        this.stopTimeline();
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(!this.tapeHeadMenu.headSettingsRectangle.isMaximized())
            this.startTimeline();

        return false;
    }
}

class AddTapeIcon extends ImageView implements MouseListener{

    AddTapeIcon(){
        super(Ressources.getRessource("add_tape.png"));
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TuringMachineDrawer.getInstance().addTape();
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

class RemoveTapeIcon extends ImageView implements MouseListener{

    TapeHeadMenu tapeHeadMenu;
    Tape tape;

    RemoveTapeIcon( TapeHeadMenu tapeHeadMenu, Tape tape){
        super(Ressources.getRessource("remove_tape.png"));
        this.tapeHeadMenu = tapeHeadMenu;
        this.tape = tape;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(this.tapeHeadMenu.headSettingsRectangle.isMaximized())
            this.tapeHeadMenu.closeHeadSettingsRectangle();
        else
            TuringMachineDrawer.getInstance().removeTape(this.tape);
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