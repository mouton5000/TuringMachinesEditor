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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import turingmachines.Tape;
import util.MouseListener;
import util.Pair;
import util.Ressources;

import java.util.Iterator;

/**
 * Created by dimitri.watel on 11/06/18.
 */
class CellSettingsRectangle extends SettingsRectangle {

    final TapePane tapePane;

    private final CellSettingsRectangleSymbolsOptionsGroup symbolsGroup;
    private final CellSettingsRectangleHeadOptionsGroup headsGroup;

    Tape currentTape;
    int currentLine;
    int currentColumn;

    CellSettingsRectangle(TapePane tapePane) {
        super();
        this.tapePane = tapePane;
        this.currentTape = tapePane.tapeBorderPane.tape;

        symbolsGroup = new CellSettingsRectangleSymbolsOptionsGroup(this);
        headsGroup = new CellSettingsRectangleHeadOptionsGroup(this);

        Line separator = new Line(-TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH / 2,
                TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2,
                TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH / 2,
                TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT / 2
                        - TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.getChildren().addAll(symbolsGroup, headsGroup, separator);


        symbolsGroup.setLayoutX(-TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH / 2
                + TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING);
        symbolsGroup.setLayoutY(
                - TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE / 2
                - TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 4
        );

        headsGroup.setLayoutX(-TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_WIDTH / 2
                + TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING);
        headsGroup.setLayoutY(
                - TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE / 2
                - TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT * 3 / 4
                + TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT / 2
        );

    }

    void addSymbol(String symbol) {symbolsGroup.addSymbol(symbol);}

    void editSymbol(int index, String symbol){
        symbolsGroup.editSymbol(index, symbol);
    }

    void removeSymbol(int index) {
        symbolsGroup.removeSymbol(index);
    }

    void addHead(Color color){
        headsGroup.addHead(color);
    }

    void setLineAndColumn(int line, int column){
        this.currentLine = line;
        this.currentColumn = column;
    }

    void editHeadColor(int head, Color color) {
        headsGroup.editHeadColor(head, color);
    }

    void removeHead(int head) {headsGroup.removeHead(head);}

    @Override
    void clear() {
        this.setLineAndColumn(0, 0);
    }
}

class CellSettingsRectangleSymbolsOptionsGroup extends HBox implements MouseListener {

    private double offsetX;
    private ImageView symbolsIcon;
    private CellSettingsRectangle settingsRectangle;
    private Double dragX;

    CellSettingsRectangleSymbolsOptionsGroup(CellSettingsRectangle settingsRectangle) {
        this.settingsRectangle = settingsRectangle;

        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setAlignment(Pos.CENTER);
        this.setSpacing(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING);
        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);

        symbolsIcon = new ImageView(Ressources.getRessource("edit-icon.png"));
        this.getChildren().add(symbolsIcon);
        symbolsIcon.setTranslateY(- symbolsIcon.getBoundsInLocal().getHeight() / 2
        + TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE / 2);

        this.addSymbol(TuringMachineDrawer.BLANK_SYMBOL);
        Iterator<String> it = TuringMachineDrawer.getInstance().machine.getSymbols();
        while(it.hasNext())
            this.addSymbol(it.next());
    }

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        int nbSymbols = this.getChildren().size() - 1;
        if(dx < offsetX - symbolsIcon.getBoundsInLocal().getWidth()
                - (nbSymbols - 1) * (TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING +
        TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE))
            dx = offsetX - symbolsIcon.getBoundsInLocal().getWidth()
                    - (nbSymbols - 1) * (TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING +
                    TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

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

    void editSymbol(int index, String symbol){
        ((CellSettingsRectangleChooseSymbolOptionLabel) this.getChildren().get(index + 2)).setText(symbol);
    }

    void removeSymbol(int index) {
        this.getChildren().remove(index + 2);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

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

class CellSettingsRectangleChooseSymbolOptionLabel extends Label implements MouseListener{

    CellSettingsRectangle settingsRectangle;

    CellSettingsRectangleChooseSymbolOptionLabel(CellSettingsRectangle settingsRectangle, String s) {
        super(s);
        this.settingsRectangle = settingsRectangle;
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        CellSettingsRectangle settingsRectangle = this.settingsRectangle;
        int line = settingsRectangle.currentLine;
        int column = settingsRectangle.currentColumn;

        String symbol = this.getText();
        symbol = symbol.equals(TuringMachineDrawer.BLANK_SYMBOL)?null:symbol;
        TuringMachineDrawer.getInstance().setInputSymbol(settingsRectangle.tapePane.tapeBorderPane.tape, line, column, symbol);

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

class CellSettingsRectangleHeadOptionsGroup extends HBox implements MouseListener{

    private CellSettingsRectangle settingsRectangle;
    private double offsetX;
    private Double dragX;

    private AddHeadOptionIcon addHeadIcon;

    CellSettingsRectangleHeadOptionsGroup(CellSettingsRectangle settingsRectangle) {
        this.settingsRectangle = settingsRectangle;
        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setSpacing(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING);

        addHeadIcon = new AddHeadOptionIcon(this.settingsRectangle);
        addHeadIcon.setTranslateY(- addHeadIcon.getBoundsInLocal().getHeight() / 2
                + TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE / 2);
        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);
        this.getChildren().add(addHeadIcon);
    }

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

    void editHeadColor(int head, Color color) {
        ((CellSettingsRectangleChooseHead) this.getChildren().get(head + 1)).setStroke(color);
    }

    void removeHead(int head) {
        this.getChildren().remove(head + 1);
    }

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

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

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

class CellSettingsRectangleChooseHead extends Rectangle implements MouseListener{
    CellSettingsRectangle settingsRectangle;

    CellSettingsRectangleChooseHead(CellSettingsRectangle settingsRectangle,
                                  double v, double v1, double v2, double v3) {
        super(v, v1, v2, v3);
        this.settingsRectangle = settingsRectangle;
        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

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

class AddHeadOptionIcon extends Group {
    CellSettingsRectangle settingsRectangle;

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

        changeColorPicker.setOnAction(actionEvent ->{
            Color color = changeColorPicker.getValue();
            Tape tape = settingsRectangle.currentTape;
            Integer line = settingsRectangle.currentLine;
            Integer column = settingsRectangle.currentColumn;
            TuringMachineDrawer.getInstance().addHead(tape, line, column, color);
        });

        this.getChildren().addAll(addHeadIcon, changeColorPicker);
    }



}