package gui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import turingmachines.Tape;


/**
 * Created by dimitri.watel on 19/06/18.
 */
class TapesVBox extends VBox {

    TuringMachineDrawer drawer;
    TapesHeadMenu tapesHeadMenu;
    SymbolsMenu symbolsMenu;
    TapeBorderPanesHBox tapesPane;

    TapesVBox(TuringMachineDrawer drawer) {
        this.drawer = drawer;
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox hbox = new HBox();

        tapesHeadMenu = new TapesHeadMenu(this.drawer);
        tapesHeadMenu.setTranslateX(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
        symbolsMenu = new SymbolsMenu(this.drawer);

        Rectangle tapesHeadClip = new Rectangle(0, 0, 0, TuringMachineDrawer.TAPES_MENU_HEIGHT);
        tapesHeadMenu.setClip(tapesHeadClip);

        Rectangle symbolclip = new Rectangle(0, 0, 0, TuringMachineDrawer.TAPES_MENU_HEIGHT);
        symbolsMenu.setClip(symbolclip);

        hbox.getChildren().addAll(tapesHeadMenu, new Separator(Orientation.VERTICAL), symbolsMenu);

        tapesPane = new TapeBorderPanesHBox(this.drawer);

        this.getChildren().addAll(hbox, new Separator(), tapesPane);

        this.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.getWidth();
            double height = newVal.getHeight();
            tapesHeadMenu.setMinWidth(width * 3.0 / 4);
            tapesHeadMenu.setMaxWidth(width * 3.0 / 4);
            symbolsMenu.setMinWidth(width / 4);
            symbolsMenu.setMaxWidth(width / 4);
            tapesHeadClip.setWidth(width * (3.0 / 4) - 100);
            symbolclip.setWidth(width / 4 - 100);
            tapesPane.setMinHeight(height - TuringMachineDrawer.TAPES_MENU_HEIGHT);
            tapesPane.setMaxHeight(height - TuringMachineDrawer.TAPES_MENU_HEIGHT);
            tapesPane.setMinWidth(width);
            tapesPane.setMaxWidth(width);
        });
    }

    void addSymbol(String symbol){
        this.symbolsMenu.addSymbol(symbol);
        this.tapesPane.addSymbol(symbol);
    }

    void editSymbol(int index, String previousSymbol, String symbol){
        this.symbolsMenu.editSymbol(index, symbol);
        this.tapesPane.editSymbol(index, previousSymbol, symbol);
    }

    void removeSymbol(int index, String symbol) {
        this.symbolsMenu.removeSymbol(index);
        this.tapesPane.removeSymbol(index, symbol);
    }

    void addTape(Tape tape) {
        tapesHeadMenu.addTape(tape);
        tapesPane.addTape(tape);
        this.centerOn(tape);
    }

    void removeTape(Tape tape) {
        this.tapesHeadMenu.removeTape(tape);
        this.tapesPane.removeTape(tape);
    }

    void addHead(Tape tape, Color color, int line, int column) {
        tapesHeadMenu.addHead(tape, color);
        tapesPane.addHead(tape, line, column, color);
    }

    void removeHead(Tape tape, int head){
        tapesHeadMenu.removeHead(tape, head);
        tapesPane.removeHead(tape, head);
    }

    void editHeadColor(Tape tape, Integer head, Color color) {
        tapesHeadMenu.editHeadColor(tape, head, color);
        tapesPane.editHeadColor(tape, head, color);
    }

    void centerOn(Tape tape, Integer head) {
        tapesPane.centerOn(tape, head);
    }

    void centerOn(Tape tape){
        this.tapesHeadMenu.centerOn(tape);
        this.tapesPane.centerOn(tape);
    }
}
