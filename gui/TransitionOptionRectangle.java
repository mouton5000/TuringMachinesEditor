package gui;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import turingmachines.Tape;
import util.Pair;

import java.util.*;

/**
 * Created by dimitri.watel on 06/06/18.
 */
class TransitionOptionRectangle extends OptionRectangle {

    private final ReadIcon readIcon;
    private final ActionsIcon actionsIcon;
    private final ActionDisplay actionsDisplay;
    private boolean readMenuSelected;

    TransitionArrowGroup currentTransitionArrowGroup;
    GraphPane graphPane;

    private HeadOptionsGroup headOptionsGroup;
    private RemoveTransitionIcon removeTransitionIcon;
    private ReadSymbolMenu readSymbolMenu;
    private ActionsMenu actionsMenu;
    private TransitionOptionRectangleSymbolsDisplay transitionOptionRectangleSymbolsDisplay;

    private VBox vbox;

    private Color currentColor;
    Tape currentTape;
    int currentHead;

    TransitionOptionRectangle(TuringMachineDrawer drawer, GraphPane graphPane) {
        super(drawer, drawer.graphPaneMouseHandler);
        this.graphPane = graphPane;
        this.setOnMouseClicked(drawer.graphPaneMouseHandler);

        vbox = new VBox();

        HBox iconsHBox = new HBox();
        iconsHBox.setMinWidth(getMaximizedWidth());
        iconsHBox.setMaxWidth(getMaximizedWidth());
        iconsHBox.setAlignment(Pos.CENTER);

        readIcon = new ReadIcon(drawer, this);
        actionsIcon = new ActionsIcon(drawer, this);
        removeTransitionIcon = new RemoveTransitionIcon(this);

        iconsHBox.setSpacing((getMaximizedWidth()
                - readIcon.getBoundsInLocal().getWidth()
                - actionsIcon.getBoundsInLocal().getWidth()
        - removeTransitionIcon.getBoundsInLocal().getWidth()) / 4);

        iconsHBox.getChildren().addAll(readIcon, actionsIcon, removeTransitionIcon);

        headOptionsGroup = new HeadOptionsGroup(this);
        readSymbolMenu = new ReadSymbolMenu(this);
        transitionOptionRectangleSymbolsDisplay = new TransitionOptionRectangleSymbolsDisplay(this.drawer, this);
        actionsMenu = new ActionsMenu(this);
        actionsDisplay = new ActionDisplay(this.drawer, this);

        vbox.getChildren().addAll(
                iconsHBox, new Separator(),
                headOptionsGroup, new Separator(),
                readSymbolMenu, new Separator(), transitionOptionRectangleSymbolsDisplay);

        vbox.setLayoutX(- getMaximizedWidth() / 2);
        vbox.setLayoutY(TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - getMaximizedHeight());

        readMenuSelected = true;
        readIcon.setSelected(true);
        actionsIcon.setSelected(false);

        this.getChildren().add(vbox);

    }

    @Override
    protected double getMaximizedHeight(){
        return TuringMachineDrawer.TRANSITION_OPTION_RECTANGLE_MAXIMIZED_HEIGHT;
    }

    void setCurrentTransitionArrowGroup(TransitionArrowGroup transitionArrowGroup) {

        if(this.currentTransitionArrowGroup != null){
            this.layoutXProperty().unbind();
            this.layoutYProperty().unbind();
        }

        this.currentTransitionArrowGroup = transitionArrowGroup;

        if(transitionArrowGroup == null)
            return;

        chooseHead(currentTape, currentHead);

        this.layoutXProperty().bind(transitionArrowGroup.centerXProperty());
        this.layoutYProperty().bind(transitionArrowGroup.centerYProperty());

        transitionOptionRectangleSymbolsDisplay.setCurrentTransitionArrowGroup(transitionArrowGroup);
        actionsDisplay.setCurrentTransitionArrowGroup(transitionArrowGroup);
    }

    @Override
    protected Node associatedNode() {
        return graphPane;
    }

    void selectReadMenu(){
        if(readMenuSelected)
            return;
        readMenuSelected = true;

        readIcon.setSelected(true);
        actionsIcon.setSelected(false);

        vbox.getChildren().remove(4);
        vbox.getChildren().add(4, readSymbolMenu);
        vbox.getChildren().remove(6);
        vbox.getChildren().add(6, transitionOptionRectangleSymbolsDisplay);
    }

    void selectActionsMenu(){
        if(!readMenuSelected)
            return;
        readMenuSelected = false;
        readIcon.setSelected(false);
        actionsIcon.setSelected(true);

        vbox.getChildren().remove(4);
        vbox.getChildren().add(4, actionsMenu);
        vbox.getChildren().remove(6);
        vbox.getChildren().add(6, actionsDisplay);
    }

    void addTape(Tape tape){
        headOptionsGroup.addTape(tape);
        transitionOptionRectangleSymbolsDisplay.addTape(tape);
    }

    void removeTape(Tape tape){
        headOptionsGroup.removeTape(tape);
        transitionOptionRectangleSymbolsDisplay.removeTape(tape);
    }

    void addHead(Tape tape, Color color) {
        headOptionsGroup.addHead(tape, color);
        transitionOptionRectangleSymbolsDisplay.addHead(tape, color);
        if(currentColor == null){
            chooseHead(tape, tape.getNbHeads() - 1);
        }
    }

    void removeHead(Tape tape, int head){
        Color color = headOptionsGroup.removeHead(tape, head);
        transitionOptionRectangleSymbolsDisplay.removeHead(tape, head);
        if(currentColor == color){
            Pair<Tape, Integer> pair = headOptionsGroup.getArbitraryHead();
            if(pair != null)
                chooseHead(pair.first, pair.second);
            else
                chooseHead(null, 0);
        }
    }

    void editHeadColor(Tape tape, int head, Color color) {
        headOptionsGroup.editHeadColor(tape, head, color);
        if(tape == currentTape && head == currentHead)
            chooseHead(tape, head);
        transitionOptionRectangleSymbolsDisplay.editHeadColor(tape, head, color);
        actionsDisplay.editHeadColor(tape, head, color);
    }

    void chooseHead(Tape tape, int head){

        currentTape = tape;
        currentHead = head;

        if(tape == null){
            readSymbolMenu.setVisible(false);
            actionsMenu.setVisible(false);
            currentColor = null;
            return;
        }

        headOptionsGroup.chooseHead(tape, head);
        Color color = headOptionsGroup.getColor(tape, head);

        if(currentColor == null){
            readSymbolMenu.setVisible(true);
            actionsMenu.setVisible(true);
        }
        currentColor = color;
        readSymbolMenu.changeColor(tape, head, color);
        actionsMenu.changeColor(color);
    }

    void addSymbol(String symbol) {
        readSymbolMenu.addSymbol(symbol);
        actionsMenu.addSymbol(symbol);
    }

    void editSymbol(int index, String previousSymbol, String symbol){
        readSymbolMenu.editSymbol(index, symbol);
        actionsMenu.editSymbol(index, symbol);
        actionsDisplay.editSymbol(previousSymbol, symbol);
    }

    void removeSymbol(int index, String symbol) {
        readSymbolMenu.removeSymbol(index);
        actionsMenu.removeSymbol(index);
        actionsDisplay.removeSymbol(symbol);
    }

    void addReadSymbol(Tape tape, int head, String symbol) {
        if(currentTape == tape && currentHead == head)
            readSymbolMenu.addReadSymbol(symbol);
    }

    void removeReadSymbol(Tape tape, int head, String symbol) {
        if(currentTape == tape && currentHead == head)
            readSymbolMenu.removeReadSymbol(symbol);
    }

    void addAction(Tape tape, int head, String actionSymbol) {
        actionsDisplay.addAction(tape, head, actionSymbol);
    }

    void removeAction(int index){
        actionsDisplay.removeAction(index);
    }

    @Override
    public void clear() {
        currentTape = null;
        currentHead = 0;
        currentTransitionArrowGroup = null;
    }
}

class HeadOptionsGroup extends HBox{
    TransitionOptionRectangle optionRectangle;
    private double offsetX;
    private Map<Tape, TransitionOptionRectangleTapeHBox> tapes;

    private TransitionOptionRectangleTapeHBox selected;

    HeadOptionsGroup(TransitionOptionRectangle optionRectangle) {
        this.optionRectangle = optionRectangle;
        this.tapes = new HashMap<>();

        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setSpacing(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setTranslateX(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING);

        this.setOnMousePressed(optionRectangle.drawer.graphPaneMouseHandler);
        this.setOnMouseDragged(optionRectangle.drawer.graphPaneMouseHandler);
    }

    void addTape(Tape tape){
        TransitionOptionRectangleTapeHBox transitionOptionRectangleTapeHBox =
                new TransitionOptionRectangleTapeHBox(optionRectangle, tape);
        tapes.put(tape, transitionOptionRectangleTapeHBox);
        if(this.getChildren().size() != 0) {
            Separator separator = new Separator(Orientation.VERTICAL);
            separator.setTranslateX(-offsetX);
            this.getChildren().add(separator);
        }

        transitionOptionRectangleTapeHBox.setTranslateX(-offsetX);
        this.getChildren().add(transitionOptionRectangleTapeHBox);
        transitionOptionRectangleTapeHBox.setTranslateX(-offsetX);
    }

    void removeTape(Tape tape){
        TransitionOptionRectangleTapeHBox transitionOptionRectangleTapeHBox = tapes.remove(tape);
        int index = this.getChildren().indexOf(transitionOptionRectangleTapeHBox);
        this.getChildren().remove(index);
        int size = this.getChildren().size();
        if(size == 0)
            return;

        if(index != 0)
            this.getChildren().remove(index - 1);
        else
            this.getChildren().remove(index);
    }

    void addHead(Tape tape, Color color){
        TransitionOptionRectangleTapeHBox transitionOptionRectangleTapeHBox = tapes.get(tape);
        transitionOptionRectangleTapeHBox.addHead(color);
    }

    Color removeHead(Tape tape, int head){
        TransitionOptionRectangleTapeHBox transitionOptionRectangleTapeHBox = tapes.get(tape);
        return transitionOptionRectangleTapeHBox.removeHead(head);
    }

    void editHeadColor(Tape tape, int head, Color color) {
        TransitionOptionRectangleTapeHBox transitionOptionRectangleTapeHBox = tapes.get(tape);
        transitionOptionRectangleTapeHBox.editHeadColor(head, color);
    }

    Pair<Tape, Integer> getArbitraryHead() {
        if(tapes.isEmpty())
            return null;
        for(Tape tape : tapes.keySet()){
            if(tape.getNbHeads() == 0)
                continue;
            return new Pair<>(tape, 0);
        }
        return null;
    }

    void chooseHead(Tape tape, int head) {

        if(selected != null)
            selected.unchoose();

        TransitionOptionRectangleTapeHBox transitionOptionRectangleTapeHBox = tapes.get(tape);
        selected = transitionOptionRectangleTapeHBox;
        transitionOptionRectangleTapeHBox.choose(head);
    }

    Color getColor(Tape tape, int head) {
        return tapes.get(tape).getColor(head);
    }

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        double maxTranslate = 0;
        for(TransitionOptionRectangleTapeHBox transitionOptionRectangleTapeHBox : tapes.values())
            maxTranslate += transitionOptionRectangleTapeHBox.getChildren().size() *
                    (TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING +
                            TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE) +
                    2 * TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING;

        maxTranslate -= TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE +
                2 * TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING;
        if(dx < offsetX - maxTranslate)
            dx = offsetX - maxTranslate;

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }
}

class RemoveTransitionIcon extends ImageView{
    TransitionOptionRectangle optionRectangle;

    RemoveTransitionIcon(TransitionOptionRectangle optionRectangle) {
        super("./images/remove_transition.png");
        this.optionRectangle = optionRectangle;

        this.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);
    }
}

class ReadIcon extends Group{

    TuringMachineDrawer drawer;
    TransitionOptionRectangle optionRectangle;

    private Rectangle backgroundColor;

    ReadIcon(TuringMachineDrawer drawer, TransitionOptionRectangle optionRectangle) {

        ImageView readIcon = new ImageView("./images/read_tape_icon.png");
        this.drawer = drawer;
        this.optionRectangle = optionRectangle;

        readIcon.setLayoutX(-readIcon.getBoundsInLocal().getWidth() / 2);
        readIcon.setLayoutY(-readIcon.getBoundsInLocal().getHeight() / 2);

        backgroundColor = new Rectangle(
                - this.optionRectangle.getMaximizedWidth() / 6,
                - readIcon.getBoundsInLocal().getHeight() / 2,
                this.optionRectangle.getMaximizedWidth() / 3,
                readIcon.getBoundsInLocal().getHeight());
        backgroundColor.setFill(Color.GREEN);

        this.setOnMouseClicked(drawer.graphPaneMouseHandler);

        this.getChildren().addAll(backgroundColor, readIcon);
    }

    void setSelected(boolean selected){
        this.backgroundColor.setOpacity(selected?1:0);
    }
}

class TransitionOptionRectangleTapeHBox extends HBox{

    TransitionOptionRectangle optionRectangle;
    TransitionOptionRectangleChooseHead selected;
    Tape tape;

    TransitionOptionRectangleTapeHBox(TransitionOptionRectangle optionRectangle, Tape tape) {
        this.optionRectangle = optionRectangle;
        this.tape = tape;

        this.setSpacing(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
    }

    void addHead(Color color) {
        TransitionOptionRectangleChooseHead headRectangle = new TransitionOptionRectangleChooseHead(
                optionRectangle, this,
                0, 0,
                TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE,
                TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE);
        headRectangle.setFill(Color.WHITE);
        headRectangle.setStroke(color);
        headRectangle.setStrokeWidth(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_STROKE_WIDTH);
        this.getChildren().add(headRectangle);
    }

    Color removeHead(int head) {
        return (Color) ((TransitionOptionRectangleChooseHead)this.getChildren().remove(head)).getStroke();
    }

    void editHeadColor(int head, Color color) {
        ((TransitionOptionRectangleChooseHead) this.getChildren().get(head)).setStroke(color);
    }

    void choose(int head) {
        selected = (TransitionOptionRectangleChooseHead) this.getChildren().get(head);
        selected.setFill(TuringMachineDrawer.TRANSITION_OPTION_RECTANGLE_SELECTED_FILL_COLOR);
    }

    void unchoose() {
        if(selected == null)
            return;

        selected.setFill(TuringMachineDrawer.TRANSITION_OPTION_RECTANGLE_UNSELECTED_FILL_COLOR);
        selected = null;
    }

    Color getColor(int head) {
        return (Color) ((TransitionOptionRectangleChooseHead)this.getChildren().get(head)).getStroke();
    }
}

class TransitionOptionRectangleChooseHead extends Rectangle{
    TransitionOptionRectangle optionRectangle;
    TransitionOptionRectangleTapeHBox transitionOptionRectangleTapeHBox;

    TransitionOptionRectangleChooseHead(TransitionOptionRectangle optionRectangle,
                                        TransitionOptionRectangleTapeHBox transitionOptionRectangleTapeHBox,
                                        double v, double v1, double v2, double v3) {
        super(v, v1, v2, v3);
        this.optionRectangle = optionRectangle;
        this.transitionOptionRectangleTapeHBox = transitionOptionRectangleTapeHBox;
        this.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);
    }

    int getHead() {
        return this.getParent().getChildrenUnmodifiable().indexOf(this);
    }
}

class ReadSymbolMenu extends HBox{
    TransitionOptionRectangle optionRectangle;
    private double offsetX;

    ReadSymbolMenu(TransitionOptionRectangle optionRectangle) {
        this.optionRectangle = optionRectangle;
        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING);
        this.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        this.setTranslateX(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING);

        this.setOnMousePressed(optionRectangle.drawer.graphPaneMouseHandler);
        this.setOnMouseDragged(optionRectangle.drawer.graphPaneMouseHandler);

        ChooseSymbolOptionLabel label =
                new ChooseSymbolOptionLabel(optionRectangle, TuringMachineDrawer.BLANK_SYMBOL);
        this.getChildren().add(label);
    }

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        int nbSymbols = this.getChildren().size() - 1;
        if(dx < offsetX - (nbSymbols - 1) * (TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING +
                TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE))
            dx = offsetX - (nbSymbols - 1) * (TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING +
                    TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    void addSymbol(String symbol){
        ChooseSymbolOptionLabel label =
                new ChooseSymbolOptionLabel(optionRectangle, symbol);
        label.setTranslateX(-offsetX);
        this.getChildren().add(label);
    }

    void editSymbol(int index, String symbol) {
        ((ChooseSymbolOptionLabel)this.getChildren().get(index + 1)).setText(symbol);
    }

    void removeSymbol(int index) {
        this.getChildren().remove(index + 1);
    }

    void changeColor(Tape tape, int head, Color color) {

        Set<String> selectedSymbols = new HashSet<>();

        if(optionRectangle.currentTransitionArrowGroup != null) {
            selectedSymbols = optionRectangle.drawer.graphPane.getReadSymbols(
                    optionRectangle.currentTransitionArrowGroup, tape, head);
        }

        for(Node child : this.getChildren()){
            ChooseSymbolOptionLabel label =
                    (ChooseSymbolOptionLabel) child;
            label.setTextFill(color);
            if(selectedSymbols.contains(label.getText()))
                label.setSelected();
            else
                label.setUnselected();
        }
    }

    void addReadSymbol(String symbol) {
        for(Node child : this.getChildren()){
            ChooseSymbolOptionLabel label =
                    (ChooseSymbolOptionLabel) child;
            if(label.getText().equals(symbol)) {
                label.setSelected();
                return;
            }
        }
    }

    void removeReadSymbol(String symbol) {
        for(Node child : this.getChildren()){
            ChooseSymbolOptionLabel label =
                    (ChooseSymbolOptionLabel) child;
            if(label.getText().equals(symbol)) {
                label.setUnselected();
                return;
            }
        }
    }
}

class ChooseSymbolOptionLabel extends Label{


    private static final Background SELECTED_BACKGROUND = new Background(
            new BackgroundFill(TuringMachineDrawer.TRANSITION_OPTION_RECTANGLE_SELECTED_FILL_COLOR,
                    CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background UNSELECTED_BACKGROUND = new Background(
            new BackgroundFill(TuringMachineDrawer.TRANSITION_OPTION_RECTANGLE_UNSELECTED_FILL_COLOR,
                    CornerRadii.EMPTY, Insets.EMPTY));

    TransitionOptionRectangle optionRectangle;

    boolean selected = false;

    ChooseSymbolOptionLabel(TransitionOptionRectangle optionRectangle, String s) {
        super(s);
        this.optionRectangle = optionRectangle;

        this.setBackground(UNSELECTED_BACKGROUND);
        this.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
                TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_FONT_SIZE));
        this.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);

        this.setMinWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
        this.setMaxWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
        this.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);

        this.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);
        this.setAlignment(Pos.CENTER);
    }

    void setSelected(){
        selected = true;
        this.setBackground(SELECTED_BACKGROUND);
    }

    void setUnselected(){
        selected = false;
        this.setBackground(UNSELECTED_BACKGROUND);
    }
}

class TransitionOptionRectangleSymbolsDisplay extends HBox {
    TuringMachineDrawer drawer;
    TransitionOptionRectangle optionRectangle;

    private Map<Tape, TapeSymbolsDisplay> tapes;
    private double offsetX;

    TransitionOptionRectangleSymbolsDisplay(TuringMachineDrawer drawer, TransitionOptionRectangle optionRectangle) {
        this.drawer = drawer;
        this.optionRectangle = optionRectangle;
        this.tapes = new HashMap<>();

        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING);
        this.setTranslateX(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING);


        this.setOnMousePressed(optionRectangle.drawer.graphPaneMouseHandler);
        this.setOnMouseDragged(optionRectangle.drawer.graphPaneMouseHandler);
    }

    void addTape(Tape tape){
        TapeSymbolsDisplay transitionSymbolsLabels =
                new TapeSymbolsDisplay(tape);
        transitionSymbolsLabels.managedProperty().bind(transitionSymbolsLabels.visibleProperty());
        tapes.put(tape, transitionSymbolsLabels);
        transitionSymbolsLabels.setTranslateX(-offsetX);
        this.getChildren().add(transitionSymbolsLabels);
    }

    void removeTape(Tape tape){
        TapeSymbolsDisplay transitionSymbolsLabels = tapes.remove(tape);
        this.getChildren().remove(transitionSymbolsLabels);
    }

    void addHead(Tape tape, Color color){
        TapeSymbolsDisplay transitionSymbolsLabels = tapes.get(tape);
        transitionSymbolsLabels.addHead(color);
        if(optionRectangle.currentTransitionArrowGroup != null)
            transitionSymbolsLabels.setCurrentTransitionArrowGroup(optionRectangle.currentTransitionArrowGroup);
    }

    void editHeadColor(Tape tape, int head, Color color){
        TapeSymbolsDisplay transitionSymbolsLabels = tapes.get(tape);
        transitionSymbolsLabels.editHeadColor(head, color);
    }

    void removeHead(Tape tape, int head){
        TapeSymbolsDisplay transitionSymbolsLabels = tapes.get(tape);
        transitionSymbolsLabels.removeHead(head);
    }

    void setCurrentTransitionArrowGroup(TransitionArrowGroup transitionArrowGroup) {
        for(TapeSymbolsDisplay tapeSymbolsDisplay: tapes.values())
            tapeSymbolsDisplay.setCurrentTransitionArrowGroup(transitionArrowGroup);
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
}

class TapeSymbolsDisplay extends HBox {

    Tape tape;

    TapeSymbolsDisplay(Tape tape) {
        this.tape = tape;
        this.setSpacing(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING);
    }

    void addHead(Color color){
        HeadSymbolsLabelDisplay transitionSymbolLabel =
                new HeadSymbolsLabelDisplay(color);
        this.getChildren().add(transitionSymbolLabel);
    }


    void editHeadColor(int head, Color color) {
        HeadSymbolsLabelDisplay transitionSymbolLabel =
                (HeadSymbolsLabelDisplay) this.getChildren().get(head);
        transitionSymbolLabel.setTextFill(color);
    }

    void removeHead(int head){
        this.getChildren().remove(head);
    }

    void setCurrentTransitionArrowGroup(TransitionArrowGroup transitionArrowGroup) {
        int head = 0;
        for(Node child : this.getChildren()){
            HeadSymbolsLabelDisplay headSymbolsLabelDisplay = (HeadSymbolsLabelDisplay) child;
            ObservableValue<String> property = transitionArrowGroup.getSymbolDisplayTextProperty(tape, head);
            headSymbolsLabelDisplay.textProperty().bind(property);
            head++;
        }
    }
}

class HeadSymbolsLabelDisplay extends Label {

    HeadSymbolsLabelDisplay(Color color) {
        this.setTextFill(color);

        this.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
                TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_FONT_SIZE));

        this.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setAlignment(Pos.CENTER);
    }
}

class ActionsIcon extends Group{

    TuringMachineDrawer drawer;
    TransitionOptionRectangle optionRectangle;
    private Rectangle backgroundColor;

    ActionsIcon(TuringMachineDrawer drawer, TransitionOptionRectangle optionRectangle) {

        ImageView actionsIcon = new ImageView("./images/action_tape_icon.png");
        this.drawer = drawer;
        this.optionRectangle = optionRectangle;

        actionsIcon.setLayoutX(-actionsIcon.getBoundsInLocal().getWidth() / 2);
        actionsIcon.setLayoutY(-actionsIcon.getBoundsInLocal().getHeight() / 2);

        backgroundColor = new Rectangle(
                - this.optionRectangle.getMaximizedWidth() / 6,
                - actionsIcon.getBoundsInLocal().getHeight() / 2,
                this.optionRectangle.getMaximizedWidth() / 3,
                actionsIcon.getBoundsInLocal().getHeight());
        backgroundColor.setFill(Color.GREEN);

        this.setOnMouseClicked(drawer.graphPaneMouseHandler);

        this.getChildren().addAll(backgroundColor, actionsIcon);
    }

    void setSelected(boolean selected){
        this.backgroundColor.setOpacity(selected?1:0);
    }
}

class ActionsMenu extends HBox {
    TransitionOptionRectangle optionRectangle;
    RemoveActionIcon removeActionIcon;
    private double offsetX;

    ActionsMenu(TransitionOptionRectangle optionRectangle) {
        this.optionRectangle = optionRectangle;
        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING);
        this.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        this.setTranslateX(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING);

        this.setOnMousePressed(optionRectangle.drawer.graphPaneMouseHandler);
        this.setOnMouseDragged(optionRectangle.drawer.graphPaneMouseHandler);

        removeActionIcon = new RemoveActionIcon(optionRectangle);
        this.getChildren().add(removeActionIcon);

        String defaultActions[] = {TuringMachineDrawer.LEFT_SYMBOL,
                TuringMachineDrawer.RIGHT_SYMBOL,
                TuringMachineDrawer.DOWN_SYMBOL,
                TuringMachineDrawer.UP_SYMBOL,
                TuringMachineDrawer.BLANK_SYMBOL
        } ;
        for(String direction : defaultActions) {
            ChooseActionOptionLabel label =
                    new ChooseActionOptionLabel(optionRectangle, direction);

            this.getChildren().add(label);
        }
    }

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        int nbSymbols = this.getChildren().size() - 1;
        if(dx < offsetX - (nbSymbols - 1) * (TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING +
                TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE)
                - TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING
                - removeActionIcon.getBoundsInLocal().getWidth())
            dx = offsetX - (nbSymbols - 1) * (TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING +
                    TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE)
                    - TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING
                    - removeActionIcon.getBoundsInLocal().getWidth();

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    void addSymbol(String symbol){
        ChooseActionOptionLabel label =
                new ChooseActionOptionLabel(optionRectangle, symbol);


        label.setTranslateX(-offsetX);
        this.getChildren().add(label);
    }

    void editSymbol(int index, String symbol) {
        ((ChooseActionOptionLabel)this.getChildren().get(index + 6)).setText(symbol);
    }

    void removeSymbol(int index) {
        this.getChildren().remove(index +6);
    }

    void changeColor(Color color) {
        for(Node child : this.getChildren()){
            if(!(child instanceof ChooseActionOptionLabel))
                continue;
            ChooseActionOptionLabel label =
                    (ChooseActionOptionLabel) child;
            label.setTextFill(color);
        }
    }
}

class ChooseActionOptionLabel extends Label{
    TransitionOptionRectangle optionRectangle;

    ChooseActionOptionLabel(TransitionOptionRectangle optionRectangle, String s) {
        super(s);
        this.optionRectangle = optionRectangle;

        this.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
                TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_FONT_SIZE));
        this.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);

        this.setMinWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
        this.setMaxWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
        this.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);

        this.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);
    }
}

class RemoveActionIcon extends ImageView{

    TransitionOptionRectangle optionRectangle;

    RemoveActionIcon(TransitionOptionRectangle optionRectangle) {
        super("./images/remove_action.png");
        this.optionRectangle = optionRectangle;

        this.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);
    }
}

class ActionDisplay extends HBox{

    TuringMachineDrawer drawer;
    TransitionOptionRectangle optionRectangle;
    private double offsetX;

    ActionDisplay(TuringMachineDrawer drawer, TransitionOptionRectangle optionRectangle) {
        this.drawer = drawer;
        this.optionRectangle = optionRectangle;
        this.offsetX = 0;

        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING);

        this.setOnMousePressed(optionRectangle.drawer.graphPaneMouseHandler);
        this.setOnMouseDragged(optionRectangle.drawer.graphPaneMouseHandler);
    }

    void addAction(Tape tape, int head, String actionSymbol) {
        addAction(drawer.getColorOfHead(tape, head), actionSymbol);
    }

    private void addAction(Color color, String actionSymbol){
        Label label = new Label(actionSymbol);
        label.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
                TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_FONT_SIZE));
        label.setTextFill(color);

        label.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);

        label.setMinWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
        label.setMaxWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
        label.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        label.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        label.setAlignment(Pos.CENTER);

        label.setTranslateX(-offsetX);

        this.getChildren().add(label);
    }

    void removeAction(int index){
        this.getChildren().remove(index);
    }

    void setCurrentTransitionArrowGroup(TransitionArrowGroup transitionArrowGroup) {
        this.getChildren().clear();
        for(Pair<String, Color> pair: transitionArrowGroup.getActionsDisplay()){
            this.addAction(pair.second, pair.first);
        }
    }

    void editSymbol(String previousSymbol, String symbol){
        for(Node child : this.getChildren()) {
            Label label = (Label) child;
            if(label.getText().equals(previousSymbol))
                label.setText(symbol);
        }
    }

    void removeSymbol(String symbol){
        Iterator<Node> it = this.getChildren().iterator();
        while(it.hasNext()){
            Label label = (Label)it.next();
            if(label.getText().equals(symbol))
                it.remove();
        }
    }

    public void editHeadColor(Tape tape, int head, Color color) {
        Color previousColor = drawer.getColorOfHead(tape, head);

        for(Node child: this.getChildren()){
            Label label = (Label) child;
            if(label.getTextFill().equals(previousColor))
                label.setTextFill(color);
        }
    }

    void translate(double dx) {

        if(dx > offsetX)
            dx = offsetX;

        int nbSymbols = this.getChildren().size() - 1;
        if(dx < offsetX - (nbSymbols - 1) * (TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING +
                TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE))
            dx = offsetX - (nbSymbols - 1) * (TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING +
                    TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }
}