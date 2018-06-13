package gui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

/**
 * Created by dimitri.watel on 11/06/18.
 */
class CellOptionRectangle extends OptionRectangle{

    final TapePane tapePane;
    final CellOptionRectangleSymbolsGroup symbolsGroup;
    int currentLine;
    int currentColumn;

    CellOptionRectangle(TuringMachineDrawer drawer, TapePane tapePane) {
        super(drawer, drawer.tapesMouseHandler);
        this.tapePane = tapePane;
        symbolsGroup = new CellOptionRectangleSymbolsGroup(this);

        Line separator = new Line(-TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2,
                TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2,
                TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2,
                TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                        - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.getChildren().addAll(symbolsGroup, separator);


        symbolsGroup.setLayoutX(-TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2
                + TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SPACING);
        symbolsGroup.setLayoutY(
                - TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE / 2
                - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 4
        );

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

class CellOptionRectangleSymbolsGroup extends HBox {
    private CellOptionRectangle optionRectangle;

    CellOptionRectangleSymbolsGroup(CellOptionRectangle optionRectangle) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SPACING);
        this.optionRectangle = optionRectangle;

        ImageView symbolsIcon = new ImageView("./images/edit-icon.png");
        this.getChildren().add(symbolsIcon);
//        symbolsIcon.setTranslateX(- symbolsIcon.getBoundsInLocal().getWidth() / 2);
        symbolsIcon.setTranslateY(- symbolsIcon.getBoundsInLocal().getHeight() / 2
        + TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE / 2);

        {
            CellOptionRectangleSymbolLabel label = new CellOptionRectangleSymbolLabel(optionRectangle, "\u2205");
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
            CellOptionRectangleSymbolLabel label = new CellOptionRectangleSymbolLabel(optionRectangle, symbol);
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

class CellOptionRectangleSymbolLabel extends Label {

    CellOptionRectangle optionRectangle;

    CellOptionRectangleSymbolLabel(CellOptionRectangle optionRectangle, String s) {
        super(s);
        this.optionRectangle = optionRectangle;
    }
}