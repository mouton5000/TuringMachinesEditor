package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
        symbolsGroup = new CellOptionRectangleSymbolsOptionsGroup(this);
        headsGroup = new CellOptionRectangleHeadOptionsGroup(this);

        Line separator = new Line(-TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2,
                TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2,
                TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2,
                TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                        - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.getChildren().addAll(symbolsGroup, headsGroup, separator);


        symbolsGroup.setLayoutX(-TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2
                + TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SPACING);
        symbolsGroup.setLayoutY(
                - TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE / 2
                - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 4
        );

        headsGroup.setLayoutX(-TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2
                + TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SPACING);
        headsGroup.setLayoutY(
                - TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_SIZE / 2
                - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT * 3 / 4
                + TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
        );

    }

    void addSymbol(String symbol) {symbolsGroup.addSymbol(symbol);}

    void addHead(Color color){
        headsGroup.addHead(color);
    }

    void setLineAndColumn(Tape tape, int line, int column){
        this.currentTape = tape;
        this.currentLine = line;
        this.currentColumn = column;
    }

    void editHeadColor(int head, Color color) {
        headsGroup.editHeadColor(head, color);
    }

    @Override
    protected Node associatedNode() {
        return tapePane;
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
        this.setSpacing(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SPACING);
        this.setOnMousePressed(optionRectangle.tapePane.drawer.tapesMouseHandler);
        this.setOnMouseDragged(optionRectangle.tapePane.drawer.tapesMouseHandler);

        symbolsIcon = new ImageView("./images/edit-icon.png");
        this.getChildren().add(symbolsIcon);
        symbolsIcon.setTranslateY(- symbolsIcon.getBoundsInLocal().getHeight() / 2
        + TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE / 2);

        {
            ChooseSymbolOptionLabel label = new ChooseSymbolOptionLabel(optionRectangle, "\u2205");
            label.setFont(Font.font(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_FONT_NAME,
                    TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_FONT_SIZE));
            label.setOnMouseClicked(optionRectangle.tapePane.drawer.tapesMouseHandler);

            label.setMinWidth(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE);
            label.setMaxWidth(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE);
            label.setMinHeight(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE);
            label.setMaxHeight(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE);

            this.getChildren().add(label);
        }

        for(String symbol: optionRectangle.tapePane.drawer.machine.getSymbols())
            addSymbol(symbol);
    }

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        int nbSymbols = this.getChildren().size() - 1;
        if(dx < offsetX - symbolsIcon.getBoundsInLocal().getWidth()
                - (nbSymbols - 1) * (TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SPACING +
        TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE))
            dx = offsetX - symbolsIcon.getBoundsInLocal().getWidth()
                    - (nbSymbols - 1) * (TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SPACING +
                    TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE);

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    void addSymbol(String symbol){
        ChooseSymbolOptionLabel label = new ChooseSymbolOptionLabel(optionRectangle, symbol);
        label.setFont(Font.font(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_FONT_NAME,
                TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_FONT_SIZE));
        label.setOnMouseClicked(optionRectangle.tapePane.drawer.tapesMouseHandler);

        label.setMinWidth(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE);
        label.setMaxWidth(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE);
        label.setMinHeight(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE);
        label.setMaxHeight(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE);

        this.getChildren().add(label);
    }
}

class ChooseSymbolOptionLabel extends Label {

    CellOptionRectangle optionRectangle;

    ChooseSymbolOptionLabel(CellOptionRectangle optionRectangle, String s) {
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
        this.setSpacing(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_SPACING);

        addHeadIcon = new AddHeadOptionIcon(this.optionRectangle);
        addHeadIcon.setTranslateY(- addHeadIcon.getBoundsInLocal().getHeight() / 2
                + TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_SIZE / 2);
        this.setOnMousePressed(optionRectangle.tapePane.drawer.tapesMouseHandler);
        this.setOnMouseDragged(optionRectangle.tapePane.drawer.tapesMouseHandler);
        this.getChildren().add(addHeadIcon);
    }

    void addHead(Color color) {

        ChooseHeadOptionRectangle headRectangle = new ChooseHeadOptionRectangle(
                optionRectangle,
                0, 0,
                TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_SIZE,
                TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_SIZE);
        headRectangle.setFill(Color.WHITE);
        headRectangle.setStroke(color);
        headRectangle.setStrokeWidth(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_STROKE_WIDTH);
        headRectangle.setTranslateX(-offsetX);
        this.getChildren().add(headRectangle);
    }

    void editHeadColor(int head, Color color) {
        ((ChooseHeadOptionRectangle) this.getChildren().get(head + 1)).setStroke(color);
    }

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        int nbHeads = this.getChildren().size() - 1;
        if(dx < offsetX - addHeadIcon.getBoundsInLocal().getWidth()
                - (nbHeads - 1) * (TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_SPACING +
                TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_SIZE))
            dx = offsetX - addHeadIcon.getBoundsInLocal().getWidth()
                    - (nbHeads - 1) * (TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_SPACING +
                    TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_SIZE);

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }


}

class ChooseHeadOptionRectangle extends Rectangle{
    CellOptionRectangle optionRectangle;

    ChooseHeadOptionRectangle(CellOptionRectangle optionRectangle,
                                     double v, double v1, double v2, double v3) {
        super(v, v1, v2, v3);
        this.optionRectangle = optionRectangle;
        this.setOnMousePressed(optionRectangle.tapePane.drawer.tapesMouseHandler);
        this.setOnMouseClicked(optionRectangle.tapePane.drawer.tapesMouseHandler);
    }
}

class AddHeadOptionIcon extends ImageView {
    CellOptionRectangle optionRectangle;

    AddHeadOptionIcon(CellOptionRectangle optionRectangle) {
        super("./images/add_head.png");
        this.optionRectangle = optionRectangle;
        this.setOnMouseClicked(optionRectangle.tapePane.drawer.tapesMouseHandler);
    }
}