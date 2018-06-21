package gui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.awt.*;

class SymbolsMenu extends HBox {

    private final TuringMachineDrawer drawer;
    SymbolOptionRectangle symbolOptionRectangle;
    AddSymbolIcon addSymbolIcon;

    SymbolsMenu(TuringMachineDrawer drawer){
        this.drawer = drawer;
        this.symbolOptionRectangle = new SymbolOptionRectangle(drawer);
        this.addSymbolIcon = new AddSymbolIcon(drawer);

        this.setMinHeight(TuringMachineDrawer.TAPES_MENU_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.TAPES_MENU_HEIGHT);
        this.setSpacing(TuringMachineDrawer.TAPES_MENU_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);

        symbolOptionRectangle.managedProperty().bind(symbolOptionRectangle.visibleProperty());
        this.setOnMouseClicked(drawer.tapesMouseHandler);

        this.getChildren().addAll(addSymbolIcon);
    }

    void addSymbol(String symbol){
        SymbolLabel label = new SymbolLabel(drawer, symbol);
        this.getChildren().add(label);
    }

    void editSymbol(int index, String newSymbol){
        this.getSymbolLabel(index).setText(newSymbol);
    }

    void removeSymbol(int index){
        this.getChildren().remove(getSymbolLabel(index));
    }

    private SymbolLabel getSymbolLabel(int index){
        int current = 0;
        for(Node child : this.getChildren()){
            if(!(child instanceof SymbolLabel))
                continue;

            if(current == index)
                return (SymbolLabel) child;
            else
                current++;
        }
        return null;
    }

    void openSymbolOptionRectantle(int index){
        symbolOptionRectangle.setSymbolIndex(index);
        symbolOptionRectangle.setVisible(true);
        this.getChildren().remove(symbolOptionRectangle);
        this.getChildren().add(index + 2, symbolOptionRectangle);
        symbolOptionRectangle.maximize();
    }

    void closeSymbolOptionRectangle(){
        symbolOptionRectangle.minimize(true);
    }
}

class AddSymbolIcon extends ImageView {

    TuringMachineDrawer drawer;

    AddSymbolIcon(TuringMachineDrawer drawer) {
        super("./images/add_symbol.png");
        this.drawer = drawer;
        this.setOnMouseClicked(drawer.tapesMouseHandler);
    }
}

class SymbolLabel extends Label {

    TuringMachineDrawer drawer;

    SymbolLabel(TuringMachineDrawer drawer, String symbol) {
        this.drawer = drawer;

        this.setText(symbol);
        this.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
                TuringMachineDrawer.TAPE_MENU_SYMBOL_FONT_SIZE));
        this.setOnMouseClicked(drawer.tapesMouseHandler);

        this.setMinWidth(TuringMachineDrawer.TAPE_MENU_SYMBOL_SIZE);
        this.setMaxWidth(TuringMachineDrawer.TAPE_MENU_SYMBOL_SIZE);
        this.setMinHeight(TuringMachineDrawer.TAPE_MENU_SYMBOL_SIZE);
        this.setMaxHeight(TuringMachineDrawer.TAPE_MENU_SYMBOL_SIZE);
        this.setAlignment(Pos.CENTER);

    }
}
