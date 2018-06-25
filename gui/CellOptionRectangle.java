package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import turingmachines.Tape;

/**
 * Created by dimitri.watel on 11/06/18.
 */
class CellOptionRectangle extends OptionRectangle{

    final TapePane tapePane;

    private final CellOptionRectangleSymbolsOptionsGroup symbolsGroup;
    private final CellOptionRectangleHeadOptionsGroup headsGroup;

    Tape currentTape;
    int currentLine;
    int currentColumn;

    CellOptionRectangle(TuringMachineDrawer drawer, TapePane tapePane) {
        super(drawer, drawer.tapesMouseHandler);
        this.tapePane = tapePane;
        this.currentTape = tapePane.tapeBorderPane.tape;

        symbolsGroup = new CellOptionRectangleSymbolsOptionsGroup(this);
        headsGroup = new CellOptionRectangleHeadOptionsGroup(this);

        Line separator = new Line(-TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2,
                TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2,
                TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2,
                TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                        - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.getChildren().addAll(symbolsGroup, headsGroup, separator);


        symbolsGroup.setLayoutX(-TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2
                + TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING);
        symbolsGroup.setLayoutY(
                - TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE / 2
                - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 4
        );

        headsGroup.setLayoutX(-TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2
                + TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING);
        headsGroup.setLayoutY(
                - TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE / 2
                - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT * 3 / 4
                + TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
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
    protected Node associatedNode() {
        return tapePane;
    }

    @Override
    public void clear() {
        this.setLineAndColumn(0, 0);
    }
}

class CellOptionRectangleSymbolsOptionsGroup extends HBox {

    private double offsetX;
    private ImageView symbolsIcon;
    private CellOptionRectangle optionRectangle;

    CellOptionRectangleSymbolsOptionsGroup(CellOptionRectangle optionRectangle) {
        this.optionRectangle = optionRectangle;

        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setAlignment(Pos.CENTER);
        this.setSpacing(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING);
        this.setOnMousePressed(optionRectangle.tapePane.drawer.tapesMouseHandler);
        this.setOnMouseDragged(optionRectangle.tapePane.drawer.tapesMouseHandler);

        symbolsIcon = new ImageView("./images/edit-icon.png");
        this.getChildren().add(symbolsIcon);
        symbolsIcon.setTranslateY(- symbolsIcon.getBoundsInLocal().getHeight() / 2
        + TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE / 2);

        this.addSymbol(TuringMachineDrawer.BLANK_SYMBOL);
        for(String symbol : this.optionRectangle.drawer.machine.getSymbols())
            this.addSymbol(symbol);
    }

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        int nbSymbols = this.getChildren().size() - 1;
        if(dx < offsetX - symbolsIcon.getBoundsInLocal().getWidth()
                - (nbSymbols - 1) * (TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING +
        TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE))
            dx = offsetX - symbolsIcon.getBoundsInLocal().getWidth()
                    - (nbSymbols - 1) * (TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING +
                    TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    void addSymbol(String symbol){
        CellOptionRectangleChooseSymbolOptionLabel label = new CellOptionRectangleChooseSymbolOptionLabel(optionRectangle, symbol);
        label.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
                TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_FONT_SIZE));
        label.setOnMouseClicked(optionRectangle.tapePane.drawer.tapesMouseHandler);

        label.setMinWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
        label.setMaxWidth(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
        label.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);
        label.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE);

        this.getChildren().add(label);
    }

    void editSymbol(int index, String symbol){
        ((CellOptionRectangleChooseSymbolOptionLabel) this.getChildren().get(index + 2)).setText(symbol);
    }

    void removeSymbol(int index) {
        this.getChildren().remove(index + 2);
    }
}

class CellOptionRectangleChooseSymbolOptionLabel extends Label {

    CellOptionRectangle optionRectangle;

    CellOptionRectangleChooseSymbolOptionLabel(CellOptionRectangle optionRectangle, String s) {
        super(s);
        this.optionRectangle = optionRectangle;
    }
}

class CellOptionRectangleHeadOptionsGroup extends HBox{

    private CellOptionRectangle optionRectangle;
    private double offsetX;

    private AddHeadOptionIcon addHeadIcon;

    CellOptionRectangleHeadOptionsGroup(CellOptionRectangle optionRectangle) {
        this.optionRectangle = optionRectangle;
        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setSpacing(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING);

        addHeadIcon = new AddHeadOptionIcon(this.optionRectangle);
        addHeadIcon.setTranslateY(- addHeadIcon.getBoundsInLocal().getHeight() / 2
                + TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE / 2);
        this.setOnMousePressed(optionRectangle.tapePane.drawer.tapesMouseHandler);
        this.setOnMouseDragged(optionRectangle.tapePane.drawer.tapesMouseHandler);
        this.getChildren().add(addHeadIcon);
    }

    void addHead(Color color) {

        CellOptionRectangleChooseHead headRectangle = new CellOptionRectangleChooseHead(
                optionRectangle,
                0, 0,
                TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE,
                TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE);
        headRectangle.setFill(Color.WHITE);
        headRectangle.setStroke(color);
        headRectangle.setStrokeWidth(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_STROKE_WIDTH);
        headRectangle.setTranslateX(-offsetX);
        this.getChildren().add(headRectangle);
    }

    void editHeadColor(int head, Color color) {
        ((CellOptionRectangleChooseHead) this.getChildren().get(head + 1)).setStroke(color);
    }

    void removeHead(int head) {
        this.getChildren().remove(head + 1);
    }

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        int nbHeads = this.getChildren().size() - 1;
        if(dx < offsetX - (nbHeads - 1) * (TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING))
            dx = offsetX - (nbHeads - 1) * (TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING);

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }
}

class CellOptionRectangleChooseHead extends Rectangle{
    CellOptionRectangle optionRectangle;

    CellOptionRectangleChooseHead(CellOptionRectangle optionRectangle,
                                  double v, double v1, double v2, double v3) {
        super(v, v1, v2, v3);
        this.optionRectangle = optionRectangle;
        this.setOnMousePressed(optionRectangle.tapePane.drawer.tapesMouseHandler);
        this.setOnMouseClicked(optionRectangle.tapePane.drawer.tapesMouseHandler);
    }
}

class AddHeadOptionIcon extends Group {
    CellOptionRectangle optionRectangle;

    AddHeadOptionIcon(CellOptionRectangle optionRectangle) {
        super();

        this.optionRectangle = optionRectangle;
        ImageView addHeadIcon = new ImageView("./images/add_head.png");

        ColorPicker changeColorPicker = new ColorPicker();

        changeColorPicker.setMinWidth(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE);
        changeColorPicker.setMaxWidth(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE);
        changeColorPicker.setMinHeight(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE);
        changeColorPicker.setMaxHeight(TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE);

        changeColorPicker.setOpacity(0);

        changeColorPicker.setOnAction(actionEvent ->{
            Color color = changeColorPicker.getValue();
            Tape tape = optionRectangle.currentTape;
            Integer line = optionRectangle.currentLine;
            Integer column = optionRectangle.currentColumn;
            optionRectangle.drawer.addHead(tape, line, column, color);
        });

        this.getChildren().addAll(addHeadIcon, changeColorPicker);
    }



}