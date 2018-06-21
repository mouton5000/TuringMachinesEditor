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

    TransitionDisplaySymbolsHBox transitionDisplaySymbolsHBox;
    TransitionDisplayActionsHBox transitionDisplayActionsHBox;

    TransitionDisplay(TuringMachineDrawer drawer) {
        this.drawer = drawer;


        transitionDisplaySymbolsHBox = new TransitionDisplaySymbolsHBox(drawer);
        transitionDisplayActionsHBox = new TransitionDisplayActionsHBox(drawer);

        this.setAlignment(Pos.CENTER);
        this.setMinWidth(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_WIDTH);
        this.setMaxWidth(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_WIDTH);
        this.setMinHeight(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.TRANSITION_DISPLAY_MAX_HEIGHT);
        this.setSpacing(TuringMachineDrawer.TRANSITION_DISPLAY_SPACING);

        this.getChildren().addAll(transitionDisplaySymbolsHBox, new Separator(Orientation.VERTICAL),
                transitionDisplayActionsHBox);
    }

    void addTape(Tape tape){
        transitionDisplaySymbolsHBox.addTape(tape);
    }

    void removeTape(Tape tape){
        transitionDisplaySymbolsHBox.removeTape(tape);
    }

    void addHead(Tape tape, Color color){
        transitionDisplaySymbolsHBox.addHead(tape, color);
    }

    void editHeadColor(Tape tape, int head, Color color){
        transitionDisplaySymbolsHBox.editHeadColor(tape, head, color);
        transitionDisplayActionsHBox.editHeadColor(tape, head, color);
    }

    void removeHead(Tape tape, int head){
        transitionDisplaySymbolsHBox.removeHead(tape, head);
    }

    void addReadSymbol(Tape tape, int head, String symbol){
        transitionDisplaySymbolsHBox.addReadSymbol(tape, head, symbol);
    }

    void removeReadSymbol(Tape tape, int head, String symbol){
        transitionDisplaySymbolsHBox.removeReadSymbol(tape, head, symbol);
    }

    void addAction(Tape tape, int head, String actionSymbol) {
        transitionDisplayActionsHBox.addAction(tape, head, actionSymbol);
    }

    void removeAction(int index){
        transitionDisplayActionsHBox.removeAction(index);
    }

    ObservableValue<String> getSymbolDisplayTextProperty(Tape tape, int head) {
        return transitionDisplaySymbolsHBox.getSymbolDisplayTextProperty(tape, head);
    }

    List<Pair<String, Color>> getActionsDisplay() {
        return transitionDisplayActionsHBox.getActionsDisplay();
    }
}

class TransitionDisplaySymbolsHBox extends HBox {
    TuringMachineDrawer drawer;

    Map<Tape, TransitionDisplaySymbolsByTapeHBox> tapes;
    Label starLabel;

    TransitionDisplaySymbolsHBox(TuringMachineDrawer drawer) {
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
        TransitionDisplaySymbolsByTapeHBox transitionDisplaySymbolsByTapeHBox = new TransitionDisplaySymbolsByTapeHBox(tape);
        transitionDisplaySymbolsByTapeHBox.managedProperty().bind(transitionDisplaySymbolsByTapeHBox.visibleProperty());
        tapes.put(tape, transitionDisplaySymbolsByTapeHBox);
        this.getChildren().add(transitionDisplaySymbolsByTapeHBox);
    }

    void removeTape(Tape tape){
        TransitionDisplaySymbolsByTapeHBox transitionDisplaySymbolsByTapeHBox = tapes.remove(tape);
        this.getChildren().remove(transitionDisplaySymbolsByTapeHBox);
        if(this.isEmpty())
            starLabel.setVisible(true);
    }

    void addHead(Tape tape, Color color){
        TransitionDisplaySymbolsByTapeHBox transitionDisplaySymbolsByTapeHBox = tapes.get(tape);
        transitionDisplaySymbolsByTapeHBox.addHead(color);
    }

    void editHeadColor(Tape tape, int head, Color color){
        TransitionDisplaySymbolsByTapeHBox transitionDisplaySymbolsByTapeHBox = tapes.get(tape);
        transitionDisplaySymbolsByTapeHBox.editHeadColor(head, color);
    }

    void removeHead(Tape tape, int head){
        TransitionDisplaySymbolsByTapeHBox transitionDisplaySymbolsByTapeHBox = tapes.get(tape);
        transitionDisplaySymbolsByTapeHBox.removeHead(head);
        if(this.isEmpty())
            starLabel.setVisible(true);
    }

    void addReadSymbol(Tape tape, int head, String symbol){
        TransitionDisplaySymbolsByTapeHBox transitionDisplaySymbolsByTapeHBox = tapes.get(tape);
        transitionDisplaySymbolsByTapeHBox.addReadSymbol(head, symbol);
        starLabel.setVisible(false);
    }

    void removeReadSymbol(Tape tape, int head, String symbol){
        TransitionDisplaySymbolsByTapeHBox transitionDisplaySymbolsByTapeHBox = tapes.get(tape);
        transitionDisplaySymbolsByTapeHBox.removeReadSymbol(head, symbol);

        if(this.isEmpty())
            starLabel.setVisible(true);
    }

    boolean isEmpty(){
        for(TransitionDisplaySymbolsByTapeHBox transitionDisplaySymbolsByTapeHBox : tapes.values()) {
            if(!transitionDisplaySymbolsByTapeHBox.isEmpty())
                return false;
        }
        return true;
    }

    public ObservableValue<String> getSymbolDisplayTextProperty(Tape tape, int head) {
        return tapes.get(tape).getSymbolDisplayTextProperty(head);
    }
}

class TransitionDisplaySymbolsByTapeHBox extends HBox {

    Tape tape;

    TransitionDisplaySymbolsByTapeHBox(Tape tape) {
        this.tape = tape;
        this.setSpacing(TuringMachineDrawer.TRANSITION_DISPLAY_SPACING);
        this.setVisible(false);
    }

    void addHead(Color color){
        TransitionDisplaySymbolsByHeadHBox transitionDisplaySymbolsByHeadHBox = new TransitionDisplaySymbolsByHeadHBox(color);
        this.getChildren().add(transitionDisplaySymbolsByHeadHBox);
    }


    void editHeadColor(int head, Color color) {
        TransitionDisplaySymbolsByHeadHBox transitionDisplaySymbolsByHeadHBox = (TransitionDisplaySymbolsByHeadHBox) this.getChildren().get(head);
        transitionDisplaySymbolsByHeadHBox.setTextFill(color);
    }

    void removeHead(int head){
        this.getChildren().remove(head);
        if(this.getChildren().size() == 0)
            this.setVisible(true);
    }

    void addReadSymbol(int head, String symbol) {
        TransitionDisplaySymbolsByHeadHBox transitionDisplaySymbolsByHeadHBox = (TransitionDisplaySymbolsByHeadHBox) this.getChildren().get(head);
        transitionDisplaySymbolsByHeadHBox.addReadSymbol(symbol);
        this.setVisible(true);
    }

    void removeReadSymbol(int head, String symbol) {
        TransitionDisplaySymbolsByHeadHBox transitionDisplaySymbolsByHeadHBox = (TransitionDisplaySymbolsByHeadHBox) this.getChildren().get(head);
        transitionDisplaySymbolsByHeadHBox.removeReadSymbol(symbol);
        if(this.isEmpty())
            this.setVisible(false);
    }

    boolean isEmpty(){
        for(Node child : this.getChildren()) {
            TransitionDisplaySymbolsByHeadHBox transitionDisplaySymbolsByHeadHBox = (TransitionDisplaySymbolsByHeadHBox) child;
            if(!transitionDisplaySymbolsByHeadHBox.getText().equals(""))
                return false;
        }
        return true;
    }

    ObservableValue<String> getSymbolDisplayTextProperty(int head) {
        return ((TransitionDisplaySymbolsByHeadHBox)this.getChildren().get(head)).textProperty();
    }
}

class TransitionDisplaySymbolsByHeadHBox extends Label {

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

    TransitionDisplaySymbolsByHeadHBox(Color color) {
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

class TransitionDisplayActionsHBox extends HBox{

    TuringMachineDrawer drawer;
    Label noActionLabel;

    TransitionDisplayActionsHBox(TuringMachineDrawer drawer) {
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