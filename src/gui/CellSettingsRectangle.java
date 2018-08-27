/*
 * Copyright (c) 2018 Dimitri Watel
 */

package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import turingmachines.Tape;
import util.MouseListener;
import util.Pair;
import util.Ressources;

import java.util.Iterator;

/**
 * This class is a widget containing the settings for the cells of the tape.
 *
 * Each tape has one unique such widget for all its cells: it displays the settings of one
 * cell at a time.
 *
 * The widget is divided into two parts:
 * - a part containing the heads of the tape and allowing to add a new head to the tape or to move an existing head
 * into the current cell
 * - a part containing the symbols of the machine and allowing to change the input symbol written in the current cell
 */
class CellSettingsRectangle extends SettingsRectangle {

    /**
     * Pane associated with the tape for which this widget displays the settings
     */
    final TapePane tapePane;

    /**
     * Upper part of the widget containing the settings related to the heads of the tape
     */
    private final CellSettingsRectangleHeadOptionsGroup headsGroup;

    /**
     * Bottom part of the widget containing the settings related to the input word of the tape
     */
    private final CellSettingsRectangleSymbolsOptionsGroup symbolsGroup;

    /**
     * Line of the cell for which this widget is currently displaying the settings
     */
    int currentLine;

    /**
     * Column of the cell for which this widget is currently displaying the settings
     */
    int currentColumn;

    /**
     * Build a widget associated with the tape of the given TapePane.
     * @param tapePane
     */
    CellSettingsRectangle(TapePane tapePane) {
        super();
        this.tapePane = tapePane;

        // Main child of the widget
        VBox vbox = new VBox();
        headsGroup = new CellSettingsRectangleHeadOptionsGroup(this);
        symbolsGroup = new CellSettingsRectangleSymbolsOptionsGroup(this);
        vbox.getChildren().addAll(headsGroup, new Separator(), symbolsGroup);
        this.getChildren().add(vbox);

        vbox.setLayoutX(- getMaximizedWidth() / 2);
        vbox.setLayoutY(TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT / 2
                - getMaximizedHeight());

    }

    /**
     * Add the given symbol to the list of symbols of the widget
     * @param symbol
     */
    void addSymbol(String symbol) {symbolsGroup.addSymbol(symbol);}

    /**
     * Edit the symbol (identified by the given index in the list of symbols of the machine) and replace it by the
     * given symbol.
     * @param index
     * @param symbol
     */
    void editSymbol(int index, String symbol){
        symbolsGroup.editSymbol(index, symbol);
    }

    /**
     * Remove the symbol (identified by the given index in the list of symbols of the machine) from the widget.
     * @param index
     */
    void removeSymbol(int index) {
        symbolsGroup.removeSymbol(index);
    }

    /**
     * Add a new head to the list of heads of the widget with the given color.
     * @param color
     */
    void addHead(Color color){
        headsGroup.addHead(color);
    }

    /**
     * Change the color of the given head (identified by its index in the list of heads of the tape associated with
     * this widget) to the given color.
     * @param head
     * @param color
     */
    void editHeadColor(int head, Color color) {
        headsGroup.editHeadColor(head, color);
    }

    /**
     * Remove the given head (identified by its index in the list of heads of the tape).
     * @param head
     */
    void removeHead(int head) {headsGroup.removeHead(head);}

    /**
     * Set the cell this widget is now associated with. The cell is the one at the given line and column.
     * @param line
     * @param column
     */
    void setLineAndColumn(int line, int column){
        this.currentLine = line;
        this.currentColumn = column;
    }

    @Override
    void clear() {
        this.setLineAndColumn(0, 0);
    }
}

/**
 * Group displaying the symbols of the machine and used to change the input word of the tape.
 * This group accepts mouse events for dragging.
 */
class CellSettingsRectangleSymbolsOptionsGroup extends HBox implements MouseListener {

    /**
     * Widget containing this group.
     */
    private CellSettingsRectangle settingsRectangle;

    /**
     * Informative icon on the left of the group.
     */
    private ImageView symbolsIcon;

    /**
     * Current horizontal offset of the widget.
     */
    private double offsetX;

    /**
     * Value of the last drag distance of the mouse if the user is dragging this group or null otherwise.
     */
    private Double dragX;

    /**
     * Build a new CellSettingsRectangleSymbolsOptionsGroup for the given widget.
     * @param settingsRectangle
     */
    CellSettingsRectangleSymbolsOptionsGroup(CellSettingsRectangle settingsRectangle) {
        this.settingsRectangle = settingsRectangle;

        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING);
        this.setMinWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH);
        this.setMinHeight((TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT - TuringMachineDrawer
                .SETTINGS_RECTANGLE_MINIMIZED_HEIGHT)  / 2);
        this.setMaxHeight((TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT - TuringMachineDrawer
                .SETTINGS_RECTANGLE_MINIMIZED_HEIGHT)  / 2);

        // Initially translate the group to the right
        this.setTranslateX(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING);

        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);

        symbolsIcon = new ImageView(Ressources.getRessource("edit-icon.png"));
        this.getChildren().add(symbolsIcon);
        symbolsIcon.setTranslateY(- symbolsIcon.getBoundsInLocal().getHeight() / 2
        + TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE / 2);

        // Add all the current symbols of the machine to the list.
        this.addSymbol(TuringMachineDrawer.BLANK_SYMBOL);
        Iterator<String> it = TuringMachineDrawer.getInstance().machine.getSymbols();
        while(it.hasNext())
            this.addSymbol(it.next());
    }

    /**
     * Translate the group horizontally of a distance dx.
     * @param dx
     */
    void translate(double dx){

        // Cannot infinitely translate to the right. Stop when the offset if 0.
        if(dx > offsetX)
            dx = offsetX;

        // Cannot infinitely translate to the left. Stop when all the symbols are hidden.
        int nbSymbols = this.getChildren().size() - 1;
        if(dx < offsetX - symbolsIcon.getBoundsInLocal().getWidth()
                - (nbSymbols - 1) * (TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING +
        TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE))
            dx = offsetX - symbolsIcon.getBoundsInLocal().getWidth()
                    - (nbSymbols - 1) * (TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING +
                    TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);

        // If the group is already maximally translated, do nothing.
        if(dx == 0)
            return;

        // Move the offset and translate the children.
        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    /**
     * Add the given symbol to the list of symbols of this group.
     * @param symbol
     */
    void addSymbol(String symbol){
        CellSettingsRectangleChooseSymbolOptionLabel label = new CellSettingsRectangleChooseSymbolOptionLabel(settingsRectangle, symbol);
        label.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
                TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_FONT_SIZE));
        label.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        label.setMinWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);
        label.setMaxWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);
        label.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);
        label.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);

        this.getChildren().add(label);
    }

    /**
     * Edit the symbol (identified by the given index in the list of symbols of the machine) and replace it by the
     * given symbol.
     * @param index
     * @param symbol
     */
    void editSymbol(int index, String symbol){
        ((CellSettingsRectangleChooseSymbolOptionLabel) this.getChildren().get(index + 2)).setText(symbol);
    }

    /**
     * Remove the symbol (identified by the given index in the list of symbols of the machine) from the group.
     * @param index
     */
    void removeSymbol(int index) {
        this.getChildren().remove(index + 2);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        // Cannot drag if the current mode does not consist in editing the machine.
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        double x = mouseEvent.getX();
        if(dragX == null)
            // Set the last known position to x
            dragX = x;
        else {
            // Drag from the last known position to the mouse position.
            this.translate(x - dragX);
            dragX = x;
        }
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        // Start dragging and set the last known position to x
        dragX = mouseEvent.getX();
        return true;
    }
}

/**
 * Label displaying a symbol of the machine. This label accepts mouse events in order to change the input symbol in
 * the cell associated with the widget containing this label
 */
class CellSettingsRectangleChooseSymbolOptionLabel extends Label implements MouseListener{

    /**
     * Widget containing this label.
     */
    private CellSettingsRectangle settingsRectangle;

    /**
     * Build a new label in the given widget associated with the given symbol
     * @param settingsRectangle
     * @param symbol
     */
    CellSettingsRectangleChooseSymbolOptionLabel(CellSettingsRectangle settingsRectangle, String symbol) {
        super(symbol);
        this.settingsRectangle = settingsRectangle;
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        // Do nothing if the current mode does not consist in editing the machine.
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        // Get the cell the widget is currently associated with and change the input symbol written in that cell.
        int line = this.settingsRectangle.currentLine;
        int column = this.settingsRectangle.currentColumn;

        String symbol = this.getText();
        symbol = symbol.equals(TuringMachineDrawer.BLANK_SYMBOL)?null:symbol;
        TuringMachineDrawer.getInstance().setInputSymbol(this.settingsRectangle.tapePane.tapeBorderPane.tape,
                line, column, symbol);

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

/**
 * Group displaying the heads of the machine and used to add and move the heads to the tape associated with the
 * widget containing that group.
 * This group accepts mouse events for dragging.
 */
class CellSettingsRectangleHeadOptionsGroup extends HBox implements MouseListener{

    /**
     * Widget containing this group.
     */
    private CellSettingsRectangle settingsRectangle;

    /**
     * Icon used to add a head
     */
    private AddHeadOptionIcon addHeadIcon;

    /**
     * Current horizontal offset of the widget.
     */
    private double offsetX;

    /**
     * Value of the last drag distance of the mouse if the user is dragging this group or null otherwise.
     */
    private Double dragX;

    /**
     * Add a new group to the given widget
     * @param settingsRectangle
     */
    CellSettingsRectangleHeadOptionsGroup(CellSettingsRectangle settingsRectangle) {
        this.settingsRectangle = settingsRectangle;

        this.setAlignment(Pos.CENTER_LEFT);
        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setSpacing(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING);
        this.setMinWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH);
        this.setMinHeight((TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT - TuringMachineDrawer
                .SETTINGS_RECTANGLE_MINIMIZED_HEIGHT)  / 2);
        this.setMaxHeight((TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT - TuringMachineDrawer
                .SETTINGS_RECTANGLE_MINIMIZED_HEIGHT)  / 2);
        this.setTranslateX(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING);

        addHeadIcon = new AddHeadOptionIcon(this.settingsRectangle);
        addHeadIcon.setTranslateY(- addHeadIcon.getBoundsInLocal().getHeight() / 2
                + TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE / 2);
        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);
        this.getChildren().add(addHeadIcon);
    }

    /**
     * Translate the group horizontally of a distance dx.
     * @param dx
     */
    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        int nbHeads = this.getChildren().size() - 1;
        if(dx < offsetX - (nbHeads - 1) * (TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING))
            dx = offsetX - (nbHeads - 1) * (TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING);

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    /**
     * Add a new head to the list of heads of this group with the given color.
     * @param color
     */
    void addHead(Color color) {

        CellSettingsRectangleChooseHead headRectangle = new CellSettingsRectangleChooseHead(
                settingsRectangle,
                0, 0,
                TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE,
                TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE);
        headRectangle.setFill(Color.WHITE);
        headRectangle.setStroke(color);
        headRectangle.setStrokeWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_STROKE_WIDTH);
        headRectangle.setTranslateX(-offsetX);
        this.getChildren().add(headRectangle);
    }

    /**
     * Change the color of the given head (identified by its index in the list of heads of the tape associated with
     * this widget) to the given color.
     * @param head
     * @param color
     */
    void editHeadColor(int head, Color color) {
        ((CellSettingsRectangleChooseHead) this.getChildren().get(head + 1)).setStroke(color);
    }

    /**
     * Remove the given head (identified by its index in the list of heads of the tape).
     * @param head
     */
    void removeHead(int head) {
        this.getChildren().remove(head + 1);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        // Cannot drag if the current mode does not consist in editing the machine.
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        double x = mouseEvent.getX();
        if(dragX == null)
            // Set the last known position to x
            dragX = x;
        else {
            // Drag from the last known position to the mouse position.
            this.translate(x - dragX);
            dragX = x;
        }
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        // Start dragging and set the last known position to x
        dragX = mouseEvent.getX();
        return true;
    }
}

/**
 * Rectangle displaying a head of the tape associated with the widget containing this group. This rectangle accepts
 * mouse events in order to move that head to the cell currently associated with the widget.
 */
class CellSettingsRectangleChooseHead extends Rectangle implements MouseListener{

    /**
     * Widget containing this group
     */
    private CellSettingsRectangle settingsRectangle;

    /**
     * Add a new rectangle to the given widget
     * @param settingsRectangle
     * @param x
     * @param y
     * @param w
     * @param h
     */
    CellSettingsRectangleChooseHead(CellSettingsRectangle settingsRectangle,
                                  double x, double y, double w, double h) {
        super(x, y, w, h);
        this.settingsRectangle = settingsRectangle;
        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        // Do nothing if the current mode does not consist in editing the machine.
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        // Get the cell the widget is currently associated with and move the head associated with this rectangle to
        // the given cell.
        CellSettingsRectangle settingsRectangle = this.settingsRectangle;
        Color color = (Color) this.getStroke();

        int line = settingsRectangle.currentLine;
        int column = settingsRectangle.currentColumn;
        Pair<Tape, Integer> pair = TuringMachineDrawer.getInstance().getHead(color);
        TuringMachineDrawer.getInstance().moveHead(pair.first, line, column, pair.second);

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

/**
 * Icon the user can click on to add a new head to the tape associated with the widget containing this icon.
 *
 * When the user click on this icon, a ColorPicker is displayed in order to choose the color of the added head. (As
 * the ColorPicker has its own event handler, there is no need to define a handler for this icon.)
 */
class AddHeadOptionIcon extends Group {

    /**
     * Widget containing this icon
     */
    private CellSettingsRectangle settingsRectangle;

    /**
     * Build a new icon for the given widget
     * @param settingsRectangle
     */
    AddHeadOptionIcon(CellSettingsRectangle settingsRectangle) {
        super();

        this.settingsRectangle = settingsRectangle;
        ImageView addHeadIcon = new ImageView(Ressources.getRessource("add_head.png"));

        ColorPicker changeColorPicker = new ColorPicker();

        changeColorPicker.setMinWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE);
        changeColorPicker.setMaxWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE);
        changeColorPicker.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE);
        changeColorPicker.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE);

        changeColorPicker.setOpacity(0);

        // The color picker has its own handler. Defining a click handler for the whole group would be useless.
        changeColorPicker.setOnAction(actionEvent ->{
            Color color = changeColorPicker.getValue();
            Tape tape = settingsRectangle.tapePane.tapeBorderPane.tape;
            Integer line = settingsRectangle.currentLine;
            Integer column = settingsRectangle.currentColumn;
            TuringMachineDrawer.getInstance().addHead(tape, line, column, color);
        });

        this.getChildren().addAll(addHeadIcon, changeColorPicker);
    }



}