package gui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * Created by dimitri.watel on 11/06/18.
 */
class CellOptionRectangle extends OptionRectangle{

    final TapePane tapePane;
    final CellOptionRectangleSymbolsOptionsGroup symbolsGroup;
    final CellOptionRectangleHeadOptionsGroup headsGroup;
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

    void addHead(Color color){
        headsGroup.addHead(color);
    }

    @Override
    protected Node associatedNode() {
        return tapePane;
    }

    void setLineAndColumn(int line, int column){
        this.currentLine = line;
        this.currentColumn = column;
    }
}

class CellOptionRectangleSymbolsOptionsGroup extends HBox {

    CellOptionRectangleSymbolsOptionsGroup(CellOptionRectangle optionRectangle) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SPACING);

        ImageView symbolsIcon = new ImageView("./images/edit-icon.png");
        this.getChildren().add(symbolsIcon);
//        symbolsIcon.setTranslateX(- symbolsIcon.getBoundsInLocal().getWidth() / 2);
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

        for(String symbol: optionRectangle.tapePane.drawer.machine.getSymbols()){
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

    public CellOptionRectangleHeadOptionsGroup(CellOptionRectangle optionRectangle) {
        this.optionRectangle = optionRectangle;
        this.setSpacing(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_SPACING);

        ImageView addHeadIcon = new AddHeadOptionIcon(optionRectangle, "./images/add_head.png");
        addHeadIcon.setOnMouseClicked(optionRectangle.tapePane.drawer.tapesMouseHandler);

        this.getChildren().add(addHeadIcon);
        addHeadIcon.setTranslateY(- addHeadIcon.getBoundsInLocal().getHeight() / 2
                + TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_SIZE / 2);

    }

    public void addHead(Color color) {
        ChooseHeadOptionRectangle headRectangle = new ChooseHeadOptionRectangle(
                optionRectangle,
                0, 0,
                TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_SIZE,
                TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_HEAD_SIZE);
        headRectangle.setFill(Color.WHITE);
        headRectangle.setStroke(color);
        headRectangle.setStrokeWidth(TuringMachineDrawer.TAPE_CELL_HEAD_STROKE_WIDTH);
        headRectangle.setOnMouseClicked(optionRectangle.tapePane.drawer.tapesMouseHandler);
        this.getChildren().add(headRectangle);
    }
}

class ChooseHeadOptionRectangle extends Rectangle{
    CellOptionRectangle optionRectangle;

    public ChooseHeadOptionRectangle(CellOptionRectangle optionRectangle,
                                     double v, double v1, double v2, double v3) {
        super(v, v1, v2, v3);
        this.optionRectangle = optionRectangle;
    }
}

class AddHeadOptionIcon extends ImageView{
    CellOptionRectangle optionRectangle;
    public AddHeadOptionIcon(CellOptionRectangle optionRectangle, String url) {
        super(url);
        this.optionRectangle = optionRectangle;
    }
}