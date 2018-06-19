package gui;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dimitri.watel on 06/06/18.
 */
class TransitionOptionRectangle extends OptionRectangle {

    private final ReadIcon readIcon;
    private final ActionsIcon actionsIcon;
    private boolean readMenuSelected;

    TransitionArrowGroup currentTransitionArrowGroup;
    GraphPane graphPane;

    private HeadOptionsGroup headOptionsGroup;
    private ReadSymbolMenu readSymbolMenu;
    private ActionsMenu actionsMenu;

    private VBox vbox;

    private Color currentColor;

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

        iconsHBox.setSpacing((getMaximizedWidth()
                - readIcon.getBoundsInLocal().getWidth()
                - actionsIcon.getBoundsInLocal().getWidth()) / 4);

        iconsHBox.getChildren().addAll(readIcon, actionsIcon);

        headOptionsGroup = new HeadOptionsGroup(this);
        readSymbolMenu = new ReadSymbolMenu(this);
        actionsMenu = new ActionsMenu(this);

        headOptionsGroup.setTranslateX(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING);
        readSymbolMenu.setTranslateX(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING);
        actionsMenu.setTranslateX(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING);

        vbox.getChildren().addAll(iconsHBox, new Separator(), headOptionsGroup, new Separator(), readSymbolMenu);

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

        if(transitionArrowGroup == null && this.currentTransitionArrowGroup != null){
            this.layoutXProperty().unbind();
            this.layoutYProperty().unbind();
        }

        this.currentTransitionArrowGroup = transitionArrowGroup;

        if(transitionArrowGroup == null)
            return;

        this.layoutXProperty().bind(transitionArrowGroup.centerXProperty());
        this.layoutYProperty().bind(transitionArrowGroup.centerYProperty());
    }

    @Override
    protected Node associatedNode() {
        return graphPane;
    }

    void selectReadMenu(){
        if(readMenuSelected)
            return;
        readMenuSelected = true;
        vbox.getChildren().add(readSymbolMenu);
        readIcon.setSelected(true);
        vbox.getChildren().remove(actionsMenu);
        actionsIcon.setSelected(false);
    }

    void selectActionsMenu(){
        if(!readMenuSelected)
            return;
        readMenuSelected = false;
        readIcon.setSelected(false);
        vbox.getChildren().remove(readSymbolMenu);
        actionsIcon.setSelected(true);
        vbox.getChildren().add(actionsMenu);
    }

    void addTape(Tape tape){
        headOptionsGroup.addTape(tape);
    }

    void removeTape(Tape tape){
        headOptionsGroup.removeTape(tape);
    }

    void addHead(Tape tape, Color color) {
        headOptionsGroup.addHead(tape, color);
    }

    void removeHead(Tape tape, int head){
        Color color = headOptionsGroup.removeHead(tape, head);
        if(currentColor == color){
            color = headOptionsGroup.getHeadColor();
            chooseHead(color);
        }
    }

    void editHeadColor(Tape tape, int head, Color color) {
        headOptionsGroup.editHeadColor(tape, head, color);
    }

    void chooseHead(Color color){
        System.out.println(color);
        if(currentColor == null && color != null){
            readSymbolMenu.setVisible(true);
            actionsMenu.setVisible(true);
        }
        else if(color == null){
            readSymbolMenu.setVisible(false);
            actionsMenu.setVisible(false);
        }
        currentColor = color;
        if(color != null){
            readSymbolMenu.changeColor(color);
            actionsMenu.changeColor(color);
        }
    }

    void addSymbol(String symbol) {
        readSymbolMenu.addSymbol(symbol);
        actionsMenu.addSymbol(symbol);
    }

    void removeSymbol(String symbol) {
        readSymbolMenu.removeSymbol(symbol);
        actionsMenu.removeSymbol(symbol);
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
                - this.optionRectangle.getMaximizedWidth() / 4,
                - readIcon.getBoundsInLocal().getHeight() / 2,
                this.optionRectangle.getMaximizedWidth() / 2,
                readIcon.getBoundsInLocal().getHeight());
        backgroundColor.setFill(Color.GREEN);

        this.setOnMouseClicked(drawer.graphPaneMouseHandler);

        this.getChildren().addAll(backgroundColor, readIcon);
    }

    void setSelected(boolean selected){
        this.backgroundColor.setOpacity(selected?1:0);
    }
}

class HeadOptionsGroup extends HBox{
    TransitionOptionRectangle optionRectangle;
    private double offsetX;
    private Map<Tape, TransitionOptionRectangleTapeHBox> tapes;

    HeadOptionsGroup(TransitionOptionRectangle optionRectangle) {
        this.optionRectangle = optionRectangle;
        this.tapes = new HashMap<>();

        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setSpacing(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);

        this.setOnMousePressed(optionRectangle.drawer.graphPaneMouseHandler);
        this.setOnMouseDragged(optionRectangle.drawer.graphPaneMouseHandler);
    }

    void addTape(Tape tape){
        TransitionOptionRectangleTapeHBox transitionOptionRectangleTapeHBox = new TransitionOptionRectangleTapeHBox(optionRectangle);
        tapes.put(tape, transitionOptionRectangleTapeHBox);
        if(this.getChildren().size() != 0)
            this.getChildren().add(new Separator(Orientation.VERTICAL));
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

    Color getHeadColor() {
        if(tapes.isEmpty())
            return null;
        for(TransitionOptionRectangleTapeHBox transitionOptionRectangleTapeHBox : tapes.values()){
            if(transitionOptionRectangleTapeHBox.getChildren().size() == 0)
                continue;
            return (Color) ((TransitionOptionRectangleChooseHead)
                    transitionOptionRectangleTapeHBox.getChildren().remove(0)).getStroke();
        }
        return null;
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

class TransitionOptionRectangleTapeHBox extends HBox{

    TransitionOptionRectangle optionRectangle;

    TransitionOptionRectangleTapeHBox(TransitionOptionRectangle optionRectangle) {
        this.optionRectangle = optionRectangle;

        this.setSpacing(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
    }

    void addHead(Color color) {
        TransitionOptionRectangleChooseHead headRectangle = new TransitionOptionRectangleChooseHead(
                optionRectangle,
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
}

class TransitionOptionRectangleChooseHead extends Rectangle{
    TransitionOptionRectangle optionRectangle;

    TransitionOptionRectangleChooseHead(TransitionOptionRectangle optionRectangle,
                                  double v, double v1, double v2, double v3) {
        super(v, v1, v2, v3);
        this.optionRectangle = optionRectangle;
        this.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);
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

        this.setOnMousePressed(optionRectangle.drawer.graphPaneMouseHandler);
        this.setOnMouseDragged(optionRectangle.drawer.graphPaneMouseHandler);

        {
            TransitionOptionRectangleChooseSymbolOptionLabel label =
                    new TransitionOptionRectangleChooseSymbolOptionLabel(optionRectangle, "\u2205");
            label.setFont(Font.font(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_FONT_NAME,
                    TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_FONT_SIZE));
            label.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);

            label.setMinWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
            label.setMaxWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
            label.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
            label.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);

            this.getChildren().add(label);
        }
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
        TransitionOptionRectangleChooseSymbolOptionLabel label =
                new TransitionOptionRectangleChooseSymbolOptionLabel(optionRectangle, symbol);
        label.setFont(Font.font(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_FONT_NAME,
                TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_FONT_SIZE));
        label.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);

        label.setMinWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
        label.setMaxWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
        label.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        label.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);

        this.getChildren().add(label);
    }

    void changeColor(Color color) {
        for(Node child : this.getChildren()){
            TransitionOptionRectangleChooseSymbolOptionLabel label =
                    (TransitionOptionRectangleChooseSymbolOptionLabel) child;
            label.setTextFill(color);
        }
    }

    void removeSymbol(String symbol) {
        Iterator<Node> it = this.getChildren().iterator();
        while(it.hasNext()){
            TransitionOptionRectangleChooseSymbolOptionLabel label =
                    (TransitionOptionRectangleChooseSymbolOptionLabel) it.next();
            if(label.getText().equals(symbol))
                it.remove();
        }
    }
}

class TransitionOptionRectangleChooseSymbolOptionLabel extends Label{
    TransitionOptionRectangle optionRectangle;

    TransitionOptionRectangleChooseSymbolOptionLabel(TransitionOptionRectangle optionRectangle, String s) {
        super(s);
        this.optionRectangle = optionRectangle;

        this.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);
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
                - this.optionRectangle.getMaximizedWidth() / 4,
                - actionsIcon.getBoundsInLocal().getHeight() / 2,
                this.optionRectangle.getMaximizedWidth() / 2,
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
    private double offsetX;

    ActionsMenu(TransitionOptionRectangle optionRectangle) {
        this.optionRectangle = optionRectangle;
        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING);
        this.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);

        this.setOnMousePressed(optionRectangle.drawer.graphPaneMouseHandler);
        this.setOnMouseDragged(optionRectangle.drawer.graphPaneMouseHandler);

        String directions[] = {"\u21D0", "\u21D2", "\u21D3", "\u21D1"} ;
        for(String direction : directions) {
            TransitionOptionRectangleChooseActionOptionLabel label =
                    new TransitionOptionRectangleChooseActionOptionLabel(optionRectangle, direction);
            label.setFont(Font.font(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_FONT_NAME,
                    TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_FONT_SIZE));
            label.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);

            label.setMinWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
            label.setMaxWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
            label.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
            label.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);

            this.getChildren().add(label);
        }
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
        TransitionOptionRectangleChooseActionOptionLabel label =
                new TransitionOptionRectangleChooseActionOptionLabel(optionRectangle, symbol);
        label.setFont(Font.font(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_FONT_NAME,
                TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_FONT_SIZE));
        label.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);

        label.setMinWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
        label.setMaxWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
        label.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);
        label.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT);

        this.getChildren().add(label);
    }

    void changeColor(Color color) {
        for(Node child : this.getChildren()){
            TransitionOptionRectangleChooseActionOptionLabel label =
                    (TransitionOptionRectangleChooseActionOptionLabel) child;
            label.setTextFill(color);
        }
    }

    void removeSymbol(String symbol) {
        Iterator<Node> it = this.getChildren().iterator();
        while(it.hasNext()){
            TransitionOptionRectangleChooseActionOptionLabel label =
                    (TransitionOptionRectangleChooseActionOptionLabel) it.next();
            if(label.getText().equals(symbol))
                it.remove();
        }
    }
}

class TransitionOptionRectangleChooseActionOptionLabel extends Label{
    TransitionOptionRectangle optionRectangle;

    TransitionOptionRectangleChooseActionOptionLabel(TransitionOptionRectangle optionRectangle, String s) {
        super(s);
        this.optionRectangle = optionRectangle;

        this.setOnMouseClicked(optionRectangle.drawer.graphPaneMouseHandler);
    }
}
