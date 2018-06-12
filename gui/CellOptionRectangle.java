package gui;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dimitri.watel on 11/06/18.
 */
class CellOptionRectangle extends OptionRectangle{

    final TapePane tapePane;
    final SymbolsGroup symbolsGroup;

    CellOptionRectangle(TuringMachineDrawer drawer, TapePane tapePane) {
        super(drawer, drawer.tapesMouseHandler);
        this.tapePane = tapePane;
        symbolsGroup = new SymbolsGroup(this);

        this.getChildren().add(symbolsGroup);
        symbolsGroup.setLayoutX(-TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2);
        symbolsGroup.setLayoutY(-TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2);

    }

    @Override
    protected Node associatedNode() {
        return tapePane;
    }
}

class SymbolsGroup extends HBox {
    private CellOptionRectangle optionRectangle;

    public SymbolsGroup(CellOptionRectangle optionRectangle) {
        this.setSpacing(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SPACING);
        this.optionRectangle = optionRectangle;
        for(String symbol: optionRectangle.tapePane.drawer.machine.getSymbols()){
            Label label = new Label(symbol);
            label.setFont(Font.font(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_FONT_NAME,
                    TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_FONT_SIZE));
            label.setOnMouseClicked(optionRectangle.tapePane.drawer.tapesMouseHandler);

            label.setMinWidth(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE * 2);
            label.setMaxWidth(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE * 2);
            label.setMinHeight(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE * 2);
            label.setMaxHeight(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_SIZE * 2);
            label.setAlignment(Pos.CENTER);

            this.getChildren().add(label);
        }
    }
}