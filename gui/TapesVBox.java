package gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.json.JSONArray;
import org.json.JSONObject;
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
            tapesHeadMenu.setMinWidth(width * TuringMachineDrawer.TAPE_MENU_RATIO);
            tapesHeadMenu.setMaxWidth(width * TuringMachineDrawer.TAPE_MENU_RATIO);
            symbolsMenu.setMinWidth(width * (1 - TuringMachineDrawer.TAPE_MENU_RATIO));
            symbolsMenu.setMaxWidth(width * (1 - TuringMachineDrawer.TAPE_MENU_RATIO));
            tapesHeadClip.setWidth(width * TuringMachineDrawer.TAPE_MENU_RATIO - 30);
            symbolclip.setWidth(width  * (1 - TuringMachineDrawer.TAPE_MENU_RATIO) - 30);
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

    void closeAllOptionRectangle() {
        tapesHeadMenu.closeAllOptionRectangle();
        symbolsMenu.closeAllOptionRectangle();
        tapesPane.closeAllOptionRectangle();
    }

    Timeline getHeadWriteTimeline(Tape tape, Integer head) {
        KeyFrame kHeadMenu = tapesHeadMenu.getHeadWriteKeyFrame(tape, head);
        KeyFrame kTape = tapesPane.getHeadWriteKeyFrame(tape, head);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(kHeadMenu, kTape);
        timeline.setCycleCount(2);
        timeline.setAutoReverse(true);
        return timeline;
    }

    Timeline getMoveHeadTimeline(Tape tape, Integer head, Integer line, Integer column) {
        KeyFrame kTape = tapesPane.getMoveHeadKeyFrame(tape, head, line, column);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(kTape);
        return timeline;
    }

    Timeline getWriteSymbolTimeline(Tape tape, Integer line, Integer column, String symbol) {
        return tapesPane.getWriteSymbolTimeline(tape, line, column, symbol);
    }

    JSONObject getJSON() {
        return new JSONObject()
//                .put("tapeHeadsMenu", tapesHeadMenu.getJSON())
                .put("symbolsMenu", symbolsMenu.getJSON())
                .put("tapes", tapesPane.getJSON());
    }

    void loadJSON(JSONObject jsonTapes) {
        JSONArray jsonSymbols = jsonTapes.getJSONArray("symbolsMenu");
        for(Object object : jsonSymbols)
            drawer.addSymbol((String)object);

        JSONArray jsonTapesAr = jsonTapes.getJSONArray("tapes");
        for(int i = 0; i < jsonTapesAr.length(); i++){
            drawer.addTape();
            Tape tape = drawer.machine.getTape(i);

            JSONObject jsonTape = jsonTapesAr.getJSONObject(i);

            Object leftBoundObj = jsonTape.get("leftBound");
            if(leftBoundObj instanceof Integer)
                tape.setLeftBound((Integer)leftBoundObj);
            else
                tape.setLeftBound(null);

            Object rightBoundObj = jsonTape.get("rightBound");
            if(rightBoundObj instanceof Integer)
                tape.setRightBound((Integer)rightBoundObj);
            else
                tape.setRightBound(null);

            Object bottomBoundObj = jsonTape.get("bottomBound");
            if(bottomBoundObj instanceof Integer)
                tape.setBottomBound((Integer)bottomBoundObj);
            else
                tape.setBottomBound(null);

            Object topBoundObj = jsonTape.get("topBound");
            if(topBoundObj instanceof Integer)
                tape.setTopBound((Integer)topBoundObj);
            else
                tape.setTopBound(null);

            JSONArray jsonHeads = jsonTape.getJSONArray("heads");
            for(int j = 0; j < jsonHeads.length(); j++){
                JSONObject jsonHead = jsonHeads.getJSONObject(j);
                Color color = Color.valueOf(jsonHead.getString("color"));
                int line = jsonHead.getInt("line");
                int column = jsonHead.getInt("column");
                drawer.addHead(tape, line, column, color);
            }

            JSONArray jsonCells = jsonTape.getJSONArray("cells");
            for(int j = 0; j < jsonCells.length(); j++){
                JSONObject jsonCell = jsonCells.getJSONObject(j);
                String symbol = jsonCell.getString("symbol");
                int line = jsonCell.getInt("line");
                int column = jsonCell.getInt("column");
                tape.writeInput(line, column, symbol);
            }
        }
    }

    void clear() {
        tapesHeadMenu.clear();
        symbolsMenu.clear();
        tapesPane.clear();
    }

    void eraseTapes(String tapesCellsDescription) {
        tapesCellsDescription = tapesCellsDescription.trim();
        tapesPane.eraseTapes(tapesCellsDescription);
    }

    String getTapesString() {
        return tapesPane.getTapesString();
    }
}
