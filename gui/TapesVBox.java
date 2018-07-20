/*
 * Copyright (c) 2018 Dimitri Watel
 */

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

import java.util.*;


/**
 * Widget containing the tapes of the edited machine and some menus to manage them.
 */
class TapesVBox extends VBox {

    /**
     * Widget displaying the menu where the user can manage the tapes and the heads of the machine.
     */
    private TapesHeadMenu tapesHeadMenu;

    /**
     * Widget displaying the menu where the user can manage the symbols of the machine.
     */
    private SymbolsMenu symbolsMenu;

    /**
     * Widget displaying the tapes of the machine.
     */
    private TapeBorderPanesHBox tapesPane;

    /**
     * Initialize the widget
     */
    TapesVBox() {
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox hbox = new HBox();

        tapesHeadMenu = new TapesHeadMenu();
        tapesHeadMenu.setTranslateX(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
        symbolsMenu = new SymbolsMenu();

        // Clip for the head menu so that its children do not appear outside (particularly, in the symbols menu).
        Rectangle tapesHeadClip = new Rectangle(0, 0, 0, TuringMachineDrawer.TAPES_MENU_HEIGHT);
        tapesHeadMenu.setClip(tapesHeadClip);

        // Clip for the head menu so that its children do not appear outside (particularly, in the heads menu).
        Rectangle symbolclip = new Rectangle(0, 0, 0, TuringMachineDrawer.TAPES_MENU_HEIGHT);
        symbolsMenu.setClip(symbolclip);

        hbox.getChildren().addAll(tapesHeadMenu, new Separator(Orientation.VERTICAL), symbolsMenu);

        tapesPane = new TapeBorderPanesHBox();

        this.getChildren().addAll(hbox, new Separator(), tapesPane);

        // Bind the width and height properties of the children with the width and height of this widget.
        // This widget change its height and width when the user resize the window or resize the widget by moving
        // the separation between the tapes and the graph panes.
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

    /**
     * Add a new symbol to this widget. It consists in adding it in the symbols menu and in the rectangle containing
     * the cells settings.
     * @param symbol
     */
    void addSymbol(String symbol){
        this.symbolsMenu.addSymbol(symbol);
        this.tapesPane.addSymbol(symbol);
    }

    /**
     * Change the name of a symbol (identified by its index in the list of symbols of the machine and its previous
     * name).
     * It consists in changing it in the symbols menu and in the rectangle containing the cells settings.
     * @param index
     * @param previousSymbol
     * @param symbol new name of the symbol.
     */
    void editSymbol(int index, String previousSymbol, String symbol){
        this.symbolsMenu.editSymbol(index, symbol);
        this.tapesPane.editSymbol(index, previousSymbol, symbol);
    }

    /**
     * Remove a symbol from this widget. It consists in removing it from the symbols menu and from the rectangle
     * containing
     * the cells settings.
     * @param symbol
     */
    void removeSymbol(int index, String symbol) {
        this.symbolsMenu.removeSymbol(index);
        this.tapesPane.removeSymbol(index, symbol);
    }

    /**
     * Add a new tape to this widget and center the view on its (0, 0) coordinate. The tape is also added to the
     * tapes and heads menu.
     * @param tape
     */
    void addTape(Tape tape) {
        tapesHeadMenu.addTape(tape);
        tapesPane.addTape(tape);
        this.centerOn(tape);
    }

    /**
     * Remove the tape from this widget and from the tapes and heads menu.
     * @param tape
     */
    void removeTape(Tape tape) {
        this.tapesHeadMenu.removeTape(tape);
        this.tapesPane.removeTape(tape);
    }

    /**
     * Add a new head to this widget on the given tape at the given coordinates. The color of the head is the given
     * coordinates. The head is also added to the tapes and heads menu and the cells settings. It is added at the end
     * of the
     * lists
     * of heads
     * of the tape.
     * @param tape
     * @param color
     * @param line
     * @param column
     */
    void addHead(Tape tape, Color color, int line, int column) {
        tapesHeadMenu.addHead(tape, color);
        tapesPane.addHead(tape, line, column, color);
    }

    /**
     * Move the given head (identified by its tape and its index in the list of heads of the tape) to the given
     * coordinates of the tape.
     * @param tape
     * @param line
     * @param column
     * @param head
     */
    void moveHead(Tape tape, int line, int column, int head) {
        tapesPane.moveHead(tape, line, column, head);
    }

    /**
     * Remove the given head (identified by its tape and its index in the list of heads of the tape) from the widget.
     * The head is also removed from the tapes and heads menu and the cells settings.
     * @param tape
     * @param head
     */
    void removeHead(Tape tape, int head){
        tapesHeadMenu.removeHead(tape, head);
        tapesPane.removeHead(tape, head);
    }

    /**
     * Change the color of the given head (identified by its tape and its index in the list of heads of the tape) to
     * the given color. The color is also changed in the tapes and heads menu and in the cells settings.
     * @param tape
     * @param head
     * @param color
     */
    void editHeadColor(Tape tape, Integer head, Color color) {
        tapesHeadMenu.editHeadColor(tape, head, color);
        tapesPane.editHeadColor(tape, head, color);
    }

    /**
     * Write the given symbol in the cell of the given tape at the given coordinates. This changes the input symbol
     * of the machine and not the currently written word during an execution.
     * @param tape
     * @param line
     * @param column
     * @param symbol
     */
    void setInputSymbol(Tape tape, int line, int column, String symbol){
        tapesPane.setInputSymbol(tape, line, column, symbol);
    }

    /**
     * Change the left bound of the given tape to the given bound.
     * @param tape
     * @param left
     */
    void setTapeLeftBound(Tape tape, Integer left){
        tapesPane.setTapeLeftBound(tape, left);
    }

    void setTapeRightBound(Tape tape, Integer right){
        tapesPane.setTapeRightBound(tape, right);
    }

    void setTapeBottomBound(Tape tape, Integer bottom){
        tapesPane.setTapeBottomBound(tape, bottom);
    }

    void setTapeTopBound(Tape tape, Integer top){
        tapesPane.setTapeTopBound(tape, top);
    }

    void centerOn(Tape tape, Integer head) {
        tapesPane.centerOn(tape, head);
    }

    void centerOn(Tape tape){
        this.tapesHeadMenu.centerOn(tape);
        this.tapesPane.centerOn(tape);
    }

    void closeAllSettingsRectangle() {
        tapesHeadMenu.closeAllSettingsRectangle();
        symbolsMenu.closeAllSettingsRectangle();
        tapesPane.closeAllSettingsRectangle();
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
            TuringMachineDrawer.getInstance().addSymbol((String)object);

        JSONArray jsonTapesAr = jsonTapes.getJSONArray("tapes");
        for(int i = 0; i < jsonTapesAr.length(); i++){
            TuringMachineDrawer.getInstance().addTape();
            Tape tape = TuringMachineDrawer.getInstance().machine.getTape(i);

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
                TuringMachineDrawer.getInstance().addHead(tape, line, column, color);
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

        int index = tapesCellsDescription.indexOf('\n');
        String symbolsDescripion = tapesCellsDescription.substring(0, index);
        symbolsDescripion = symbolsDescripion.trim();
        Set<String> symbolsDescriptionAr = new HashSet<>(Arrays.asList(symbolsDescripion.split(" ")));
        Iterator<String> currentSymbols = TuringMachineDrawer.getInstance().machine.getSymbols();

        int i = 0;
        while(currentSymbols.hasNext()){
            String symbol = currentSymbols.next();
            if(symbolsDescriptionAr.contains(symbol)) {
                symbolsDescriptionAr.remove(symbol);
                i++;
            }
            else
                TuringMachineDrawer.getInstance().removeSymbol(i, false);
        }


        for(String symbol : symbolsDescriptionAr)
            TuringMachineDrawer.getInstance().addSymbol(symbol);

        tapesPane.eraseTapes(tapesCellsDescription.substring(index + 1));
    }

    String getTapesString() {
        StringBuilder sb = new StringBuilder();

        LinkedList<String> symbols = new LinkedList<>();
        Iterator<String> it = TuringMachineDrawer.getInstance().machine.getSymbols();
        while(it.hasNext())
            symbols.add(it.next());

        sb.append(String.join(" ", symbols));
        sb.append('\n');
        sb.append(tapesPane.getTapesString());

        return sb.toString();
    }

}
