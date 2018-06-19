package gui;

import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import turingmachines.Tape;

/**
 * Created by dimitri.watel on 19/06/18.
 */
class TapesVBox extends VBox {

    TuringMachineDrawer drawer;
    TapesHeadMenu tapesHeadMenu;
    TapeBorderPanesHBox tapesPane;

    TapesVBox(TuringMachineDrawer drawer) {
        this.drawer = drawer;
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        tapesHeadMenu = new TapesHeadMenu(this.drawer);
        tapesHeadMenu.setTranslateX(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);


        tapesPane = new TapeBorderPanesHBox(this.drawer);
        this.getChildren().addAll(tapesHeadMenu, new Separator(), tapesPane);
        this.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.getWidth();
            double height = newVal.getHeight();
            tapesHeadMenu.setMinWidth(width);
            tapesHeadMenu.setMaxWidth(width);
            tapesPane.setMinHeight(height - TuringMachineDrawer.TAPES_HEAD_MENU_HEIGHT);
            tapesPane.setMaxHeight(height - TuringMachineDrawer.TAPES_HEAD_MENU_HEIGHT);
            tapesPane.setMinWidth(width);
            tapesPane.setMaxWidth(width);
        });
    }

    void addSymbol(String symbol){
        this.tapesPane.addSymbol(symbol);
    }

    void removeSymbol(String symbol) { this.tapesPane.removeSymbol(symbol); }

    void addTape(Tape tape) {
        tapesHeadMenu.addTape(tape);
        tapesPane.addTape(tape);
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

    void translateTo(Tape tape, Integer head) {
        tapesPane.translateTo(tape, head);
    }
}
