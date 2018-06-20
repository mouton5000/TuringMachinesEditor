package gui;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import turingmachines.Tape;
import util.Pair;

import java.util.*;

class TransitionDisplay extends HBox {
    TuringMachineDrawer drawer;

    TransitionSymbolsDisplay transitionSymbolsDisplay;
    TransitionActionsDisplay transitionActionsDisplay;

    TransitionDisplay(TuringMachineDrawer drawer) {
        this.drawer = drawer;


        transitionSymbolsDisplay = new TransitionSymbolsDisplay(drawer);
        transitionActionsDisplay = new TransitionActionsDisplay(drawer);

        this.setAlignment(Pos.CENTER);
        this.setMinWidth(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_WIDTH);
        this.setMaxWidth(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_WIDTH);
        this.setMinHeight(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_HEIGHT);
        this.setSpacing(TuringMachineDrawer.TRANSITION_DISPLAY_SPACING);

        this.getChildren().addAll(transitionSymbolsDisplay, new Separator(Orientation.VERTICAL),
                transitionActionsDisplay);
    }

    void addTape(Tape tape){
        transitionSymbolsDisplay.addTape(tape);
    }

    void removeTape(Tape tape){
        transitionSymbolsDisplay.removeTape(tape);
    }

    void addHead(Tape tape, Color color){
        transitionSymbolsDisplay.addHead(tape, color);
    }

    void editHeadColor(Tape tape, int head, Color color){
        transitionSymbolsDisplay.editHeadColor(tape, head, color);
        transitionActionsDisplay.editHeadColor(tape, head, color);
    }

    void removeHead(Tape tape, int head){
        transitionSymbolsDisplay.removeHead(tape, head);
    }

    void addReadSymbol(Tape tape, int head, String symbol){
        transitionSymbolsDisplay.addReadSymbol(tape, head, symbol);
    }

    void removeReadSymbol(Tape tape, int head, String symbol){
        transitionSymbolsDisplay.removeReadSymbol(tape, head, symbol);
    }

    void addAction(Tape tape, int head, String actionSymbol) {
        transitionActionsDisplay.addAction(tape, head, actionSymbol);
    }

    void removeAction(int index){
        transitionActionsDisplay.removeAction(index);
    }

    ObservableValue<String> getSymbolDisplayTextProperty(Tape tape, int head) {
        return transitionSymbolsDisplay.getSymbolDisplayTextProperty(tape, head);
    }

    List<Pair<String, Color>> getActionsDisplay() {
        return transitionActionsDisplay.getActionsDisplay();
    }
}

class TransitionSymbolsDisplay extends HBox {
    TuringMachineDrawer drawer;

    Map<Tape, TapeDisplaySymbolsLabels> tapes;
    Label starLabel;

    TransitionSymbolsDisplay(TuringMachineDrawer drawer) {
        this.drawer = drawer;
        this.tapes = new HashMap<>();
        this.setSpacing(TuringMachineDrawer.TRANSITION_DISPLAY_SPACING);

        starLabel = new Label("*");
        starLabel.managedProperty().bind(starLabel.visibleProperty());

        starLabel.setFont(Font.font(TuringMachineDrawer.TRANSITION_SYMBOL_FONT_NAME,
                TuringMachineDrawer.TRANSITION_SYMBOL_FONT_SIZE));

        starLabel.setMinHeight(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_HEIGHT);
        starLabel.setMaxHeight(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_HEIGHT);
        starLabel.setAlignment(Pos.CENTER);

        this.getChildren().add(starLabel);
    }

    void addTape(Tape tape){
        TapeDisplaySymbolsLabels tapeDisplaySymbolsLabels = new TapeDisplaySymbolsLabels(tape);
        tapeDisplaySymbolsLabels.managedProperty().bind(tapeDisplaySymbolsLabels.visibleProperty());
        tapes.put(tape, tapeDisplaySymbolsLabels);
        this.getChildren().add(tapeDisplaySymbolsLabels);
    }

    void removeTape(Tape tape){
        TapeDisplaySymbolsLabels tapeDisplaySymbolsLabels = tapes.remove(tape);
        this.getChildren().remove(tapeDisplaySymbolsLabels);
        if(this.isEmpty())
            starLabel.setVisible(true);
    }

    void addHead(Tape tape, Color color){
        TapeDisplaySymbolsLabels tapeDisplaySymbolsLabels = tapes.get(tape);
        tapeDisplaySymbolsLabels.addHead(color);
    }

    void editHeadColor(Tape tape, int head, Color color){
        TapeDisplaySymbolsLabels tapeDisplaySymbolsLabels = tapes.get(tape);
        tapeDisplaySymbolsLabels.editHeadColor(head, color);
    }

    void removeHead(Tape tape, int head){
        TapeDisplaySymbolsLabels tapeDisplaySymbolsLabels = tapes.get(tape);
        tapeDisplaySymbolsLabels.removeHead(head);
        if(this.isEmpty())
            starLabel.setVisible(true);
    }

    void addReadSymbol(Tape tape, int head, String symbol){
        TapeDisplaySymbolsLabels tapeDisplaySymbolsLabels = tapes.get(tape);
        tapeDisplaySymbolsLabels.addReadSymbol(head, symbol);
        starLabel.setVisible(false);
    }

    void removeReadSymbol(Tape tape, int head, String symbol){
        TapeDisplaySymbolsLabels tapeDisplaySymbolsLabels = tapes.get(tape);
        tapeDisplaySymbolsLabels.removeReadSymbol(head, symbol);

        if(this.isEmpty())
            starLabel.setVisible(true);
    }

    boolean isEmpty(){
        for(TapeDisplaySymbolsLabels tapeDisplaySymbolsLabels : tapes.values()) {
            if(!tapeDisplaySymbolsLabels.isEmpty())
                return false;
        }
        return true;
    }

    public ObservableValue<String> getSymbolDisplayTextProperty(Tape tape, int head) {
        return tapes.get(tape).getSymbolDisplayTextProperty(head);
    }
}

class TapeDisplaySymbolsLabels extends HBox {

    Tape tape;

    TapeDisplaySymbolsLabels(Tape tape) {
        this.tape = tape;
        this.setSpacing(TuringMachineDrawer.TRANSITION_DISPLAY_SPACING);
        this.setVisible(false);
    }

    void addHead(Color color){
        HeadDisplaySymbolLabel headDisplaySymbolLabel = new HeadDisplaySymbolLabel(color);
        this.getChildren().add(headDisplaySymbolLabel);
    }


    void editHeadColor(int head, Color color) {
        HeadDisplaySymbolLabel headDisplaySymbolLabel = (HeadDisplaySymbolLabel) this.getChildren().get(head);
        headDisplaySymbolLabel.setTextFill(color);
    }

    void removeHead(int head){
        this.getChildren().remove(head);
        if(this.getChildren().size() == 0)
            this.setVisible(true);
    }

    void addReadSymbol(int head, String symbol) {
        HeadDisplaySymbolLabel headDisplaySymbolLabel = (HeadDisplaySymbolLabel) this.getChildren().get(head);
        headDisplaySymbolLabel.addReadSymbol(symbol);
        this.setVisible(true);
    }

    void removeReadSymbol(int head, String symbol) {
        HeadDisplaySymbolLabel headDisplaySymbolLabel = (HeadDisplaySymbolLabel) this.getChildren().get(head);
        headDisplaySymbolLabel.removeReadSymbol(symbol);
        if(this.isEmpty())
            this.setVisible(false);
    }

    boolean isEmpty(){
        for(Node child : this.getChildren()) {
            HeadDisplaySymbolLabel headDisplaySymbolLabel = (HeadDisplaySymbolLabel) child;
            if(!headDisplaySymbolLabel.getText().equals(""))
                return false;
        }
        return true;
    }

    ObservableValue<String> getSymbolDisplayTextProperty(int head) {
        return ((HeadDisplaySymbolLabel)this.getChildren().get(head)).textProperty();
    }
}

class HeadDisplaySymbolLabel extends Label {

    private static Comparator<String> cmp = (s, t1) -> {
        if(s == null && t1 == null)
            return 0;
        if(s == null)
            return 1;
        if(t1 == null)
            return -1;
        return s.compareTo(t1);
    };

    private Set<String> symbols;

    HeadDisplaySymbolLabel(Color color) {
        this.setText("");
        this.setTextFill(color);

        this.setFont(Font.font(TuringMachineDrawer.TRANSITION_SYMBOL_FONT_NAME,
                TuringMachineDrawer.TRANSITION_SYMBOL_FONT_SIZE));

        this.setMinHeight(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_HEIGHT);
        this.setAlignment(Pos.CENTER);

        symbols = new TreeSet<>(cmp);
    }

    void addReadSymbol(String symbol) {
        if(symbols.add(symbol))
            reinitText();
    }

    void removeReadSymbol(String symbol) {
        if(symbols.remove(symbol))
            reinitText();
    }

    private void reinitText(){
        this.setText(String.join("|", symbols));
    }
}

class TransitionActionsDisplay extends HBox{

    TuringMachineDrawer drawer;
    Label noActionLabel;

    TransitionActionsDisplay(TuringMachineDrawer drawer) {
        this.drawer = drawer;
        noActionLabel = new Label("\u2205");
        noActionLabel.managedProperty().bind(noActionLabel.visibleProperty());

        noActionLabel.setFont(Font.font(TuringMachineDrawer.TRANSITION_SYMBOL_FONT_NAME,
                TuringMachineDrawer.TRANSITION_SYMBOL_FONT_SIZE));

        noActionLabel.setMinHeight(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_HEIGHT);
        noActionLabel.setMaxHeight(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_HEIGHT);
        noActionLabel.setAlignment(Pos.CENTER);

        this.getChildren().add(noActionLabel);
    }

    void editHeadColor(Tape tape, int head, Color color) {
        Color previousColor = drawer.getColorOfHead(tape, head);
        for(Node child: this.getChildren()){
            if(!(child instanceof  TransitionDisplayActionLabel))
                continue;
            TransitionDisplayActionLabel label = (TransitionDisplayActionLabel) child;
            if(label.getTextFill().equals(previousColor))
                label.setTextFill(color);
        }
    }

    void addAction(Tape tape, int head, String actionSymbol){
        Color color = drawer.getColorOfHead(tape, head);
        TransitionDisplayActionLabel label = new TransitionDisplayActionLabel(actionSymbol, color);
        this.getChildren().add(label);
        noActionLabel.setVisible(false);
    }

    void removeAction(int index){
        this.getChildren().remove(index + 1);
        if(this.getChildren().size() == 1)
            noActionLabel.setVisible(true);
    }

    List<Pair<String,Color>> getActionsDisplay() {
        List<Pair<String, Color>> pairs = new LinkedList<>();
        for(Node child: this.getChildren()){
            if(!(child instanceof  TransitionDisplayActionLabel))
                continue;
            TransitionDisplayActionLabel label = (TransitionDisplayActionLabel) child;
            pairs.add(new Pair<>(label.getText(), (Color) label.getTextFill()));
        }
        return pairs;
    }
}

class TransitionDisplayActionLabel extends Label {

    TransitionDisplayActionLabel(String actionSymbol, Color color) {
        this.setText(actionSymbol);
        this.setTextFill(color);

        this.setFont(Font.font(TuringMachineDrawer.TRANSITION_SYMBOL_FONT_NAME,
                TuringMachineDrawer.TRANSITION_SYMBOL_FONT_SIZE));

        this.setMinHeight(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_HEIGHT);
        this.setAlignment(Pos.CENTER);
    }
}