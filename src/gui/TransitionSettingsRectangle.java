/*
 * Copyright (c) 2018 Dimitri Watel
 */

package gui;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import turingmachines.Tape;
import util.MouseListener;
import util.Pair;
import util.Ressources;

import java.util.*;

/**
 * Widget containing the settings of a transition of the machine.
 * It allows the user to
 * <ul>
 *     <li>change the read symbols and the actions of the transition</li>
 *     <li>delete the transition.</li>
 * </ul>
 *
 * The rectangle is separated in four parts, from top to bottom:
 * <ul>
 *     <li>A menu to select either the read symbols submenu or the actions submenu. This line contains also the
 *     delete icon.</li>
 *     <li>A line where the user can select a head.</li>
 *     <li>A line where the user can either select a read symbol or an action related to the head that is currently
 *     selected.
 *     </li>
 *     <li>A line where all the read symbols or all the actions of the transitions are displayed.
 *     </li>
 * </ul>
 *
 * The widget is unique for all the transitions of the machine: it displays the settings of one
 * transition at a time.
 */
class TransitionSettingsRectangle extends SettingsRectangle {

    /**
     * Icon the user can click on to enter the submenu of the settings to change the read symbols of the transition
     */
    private final ReadIcon readIcon;

    /**
     * Icon the user can click on to enter the submenu of the settings to change the actions of the transition
     */
    private final ActionsIcon actionsIcon;

    /**
     * True if the submenu used to set the read symbols of the transition is selected. If false, the actions are
     * displayed.
     */
    private boolean readMenuSelected;

    /**
     * Transition currently associated with this settings rectangle.
     */
    TransitionGroup currentTransitionGroup;

    /**
     * Widget containing icons the user can click on to select a head on a tape and, then, adding read symbols and
     * actions related to that head.
     */
    private HeadOptionsGroup headOptionsGroup;

    /**
     * Icon the user can click on to remove the transition
     */
    private RemoveTransitionIcon removeTransitionIcon;

    /**
     * Widget containing icons the user can click on to set the read symbols of the transition.
     */
    private ReadSymbolsMenu readSymbolsMenu;

    /**
     * Widget of the settings used to display the list of read symbols of the transition.
     */
    private ReadSymbolsDisplay readSymbolsDisplay;

    /**
     * Widget containing icons the user can click on to set the actions of the transition.
     */
    private ActionsMenu actionsMenu;

    /**
     * Widget of the settings used to display the list of actions of the transition.
     */
    private final ActionDisplay actionsDisplay;

    private VBox vbox;

    /**
     * Color of the head currently chosen by the user.
     */
    private Color currentColor;

    /**
     * Tape of the head currently chosen by the user
     */
    Tape currentTape;

    /**
     * Head currently chosen by the user.
     */
    int currentHead;

    /**
     * Build a settings rectangle displaying the settings of the transitions of the machine.
     */
    TransitionSettingsRectangle() {
        super();
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        vbox = new VBox();

        // Set the icons for the two submenus (read symbols/icons) and the icon to delete the transition.
        HBox iconsHBox = new HBox();
        iconsHBox.setMinWidth(getMaximizedWidth());
        iconsHBox.setMaxWidth(getMaximizedWidth());
        iconsHBox.setAlignment(Pos.CENTER);

        readIcon = new ReadIcon(this);
        actionsIcon = new ActionsIcon( this);
        removeTransitionIcon = new RemoveTransitionIcon(this);

        iconsHBox.setSpacing((getMaximizedWidth()
                - readIcon.getBoundsInLocal().getWidth()
                - actionsIcon.getBoundsInLocal().getWidth()
        - removeTransitionIcon.getBoundsInLocal().getWidth()) / 4);

        iconsHBox.getChildren().addAll(readIcon, actionsIcon, removeTransitionIcon);

        // Other groups contained in this widget
        headOptionsGroup = new HeadOptionsGroup(this);
        readSymbolsMenu = new ReadSymbolsMenu(this);
        readSymbolsDisplay = new ReadSymbolsDisplay( this);
        actionsMenu = new ActionsMenu(this);
        actionsDisplay = new ActionDisplay( this);

        vbox.getChildren().addAll(
                iconsHBox, new Separator(),
                headOptionsGroup, new Separator(),
                readSymbolsMenu, new Separator(), readSymbolsDisplay);

        vbox.setLayoutX(- getMaximizedWidth() / 2);
        vbox.setLayoutY(TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT / 2
                - getMaximizedHeight());

        // Set the first submenu selected
        readMenuSelected = true;
        readIcon.setSelected(true);
        actionsIcon.setSelected(false);

        this.getChildren().add(vbox);

    }

    @Override
    double getMaximizedHeight(){
        // The height of this rectangle is bigger than the default height
        return TuringMachineDrawer.TRANSITION_SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT;
    }


    /**
     * Change the transition this rectangle currently associated with.
     * If transition is null, this rectangle is not associated with anymore transition.
     * @param transitionGroup
     */
    void setCurrentTransitionGroup(TransitionGroup transitionGroup) {

        this.layoutXProperty().unbind();
        this.layoutYProperty().unbind();

        this.currentTransitionGroup = transitionGroup;

        if(transitionGroup == null)
            return;

        chooseHead(currentTape, currentHead);

        // The rectangle is always on the middle point of the transition.
        this.layoutXProperty().bind(transitionGroup.centerXProperty());
        this.layoutYProperty().bind(transitionGroup.centerYProperty());

        readSymbolsDisplay.setCurrentTransitionArrowGroup(transitionGroup);
        actionsDisplay.setCurrentTransitionArrowGroup(transitionGroup);
    }

    /**
     * Display the submenu related to the read symbols.
     */
    void selectReadMenu(){
        if(readMenuSelected)
            return;
        readMenuSelected = true;

        readIcon.setSelected(true);
        actionsIcon.setSelected(false);

        vbox.getChildren().remove(4);
        vbox.getChildren().add(4, readSymbolsMenu);
        vbox.getChildren().remove(6);
        vbox.getChildren().add(6, readSymbolsDisplay);
    }


    /**
     * Display the submenu related to the actions.
     */
    void selectActionsMenu(){
        if(!readMenuSelected)
            return;
        readMenuSelected = false;
        readIcon.setSelected(false);
        actionsIcon.setSelected(true);

        vbox.getChildren().remove(4);
        vbox.getChildren().add(4, actionsMenu);
        vbox.getChildren().remove(6);
        vbox.getChildren().add(6, actionsDisplay);
    }

    /**
     * Add the given tape the the list of tapes of this widgets. This affects two subwidgets: the list of heads and the
     * field displaying the read symbols of the current transition.
     * @param tape
     */
    void addTape(Tape tape){
        headOptionsGroup.addTape(tape);
        readSymbolsDisplay.addTape(tape);
    }

    /**
     * Remove the given tape from the list of tapes of this widgets. This affects two subwidgets: the list of heads and
     * the field displaying the read symbols of the current transition.
     * @param tape
     */
    void removeTape(Tape tape){
        headOptionsGroup.removeTape(tape);
        readSymbolsDisplay.removeTape(tape);
    }

    /**
     * Add a head of the given tape to the list of heads of the widget with the given color. This affects two
     * subwidgets: the list of heads and the field displaying the read symbols of the current transition.
     *
     * If no head is currently selected by the widget, this new head is selected.
     * @param tape
     * @param color
     */
    void addHead(Tape tape, Color color) {
        headOptionsGroup.addHead(tape, color);
        readSymbolsDisplay.addHead(tape, color);
        if(currentColor == null){
            chooseHead(tape, tape.getNbHeads() - 1);
        }
    }

    /**
     * Change the color of the given head (identified by its index in the list of heads of the given tape)
     * to the given color. This affects three subwidgets: the list of heads and the two fields displaying the read
     * symbols and the actions of the current transition.
     *
     * If the current head is the edited head, the subwidget allowing to select a read symbol or an action is
     * also updated.
     * @param tape
     * @param head
     * @param color
     */
    void editHeadColor(Tape tape, int head, Color color) {
        headOptionsGroup.editHeadColor(tape, head, color);
        if(tape == currentTape && head == currentHead)
            chooseHead(tape, head);
        readSymbolsDisplay.editHeadColor(tape, head, color);
        actionsDisplay.editHeadColor(tape, head, color);
    }

    /**
     * Remove the given head (identified by its index in the list of heads of the given tape) from the list
     * of heads of the widget. This affects two subwidgets: the list of heads and the field displaying the read
     * symbols of the current transition.
     *
     * If the head was selected, an arbitrary head is selected instead.
     *
     * This method does not remove the read symbols and actions associated with this head as this method should not
     * be called if such symbols or actions exist.
     * @param tape
     * @param head
     */
    void removeHead(Tape tape, int head){
        Color color = headOptionsGroup.removeHead(tape, head);
        readSymbolsDisplay.removeHead(tape, head);
        if(currentColor == color){
            Pair<Tape, Integer> pair = headOptionsGroup.getArbitraryHead();
            if(pair != null)
                chooseHead(pair.first, pair.second);
            else
                chooseHead(null, 0);
        }
        else
            // The index of all the heads with a higher index is decreased by one.
            // Update the index of the current head is such a head.
            if(currentTape == tape && currentHead > head)
                currentHead--;
    }

    /**
     * Select the given head (identified by its index in the list of heads of the given tape) as the current head of
     * the widget. The user can then change the read symbols and actions associated with that head.
     * @param tape
     * @param head
     */
    void chooseHead(Tape tape, int head){
        currentTape = tape;
        currentHead = head;

        // If the machine does not contain anymore head, the given tape is null.
        // In that case, remove the subwidgets from the screen.
        if(tape == null){
            readSymbolsMenu.setVisible(false);
            actionsMenu.setVisible(false);
            currentColor = null;
            return;
        }

        headOptionsGroup.chooseHead(tape, head);
        Color color = headOptionsGroup.getColor(tape, head);

        // If no head was previously selected
        if(currentColor == null){
            readSymbolsMenu.setVisible(true);
            actionsMenu.setVisible(true);
        }
        currentColor = color;

        // Update the colors of the subwidget used to add a read symbol or an action to the transition.
        readSymbolsMenu.changeColor(tape, head, color);
        actionsMenu.changeColor(color);
    }

    /**
     * Add the given symbol to the list of symbols of this widget.
     * @param symbol
     */
    void addSymbol(String symbol) {
        readSymbolsMenu.addSymbol(symbol);
        actionsMenu.addSymbol(symbol);
    }

    /**
     * Change the given symbol (identified by the given index in the list of symbols of the machine and by its
     * given previous name) to the given name.
     * @param index
     * @param previousSymbol
     * @param symbol
     */
    void editSymbol(int index, String previousSymbol, String symbol){
        readSymbolsMenu.editSymbol(index, symbol);
        actionsMenu.editSymbol(index, symbol);
        actionsDisplay.editSymbol(previousSymbol, symbol);
    }

    /**
     * Remove the given symbol (identified by the given index in the list of symbols of the machine and by its
     *      * given previous name)
     * @param index
     * @param symbol
     */
    void removeSymbol(int index, String symbol) {
        readSymbolsMenu.removeSymbol(index);
        actionsMenu.removeSymbol(index);
        actionsDisplay.removeSymbol(symbol);
    }

    void addReadSymbol(Tape tape, int head, String symbol) {
        if(currentTape == tape && currentHead == head)
            readSymbolsMenu.addReadSymbol(symbol);
    }

    void removeReadSymbol(Tape tape, int head, String symbol) {
        if(currentTape == tape && currentHead == head)
            readSymbolsMenu.removeReadSymbol(symbol);
    }

    void addAction(Tape tape, int head, String actionSymbol) {
        actionsDisplay.addAction(tape, head, actionSymbol);
    }

    void removeAction(int index){
        actionsDisplay.removeAction(index);
    }

    @Override
    void clear() {
        currentTape = null;
        currentHead = 0;
        currentTransitionGroup = null;
    }
}

class HeadOptionsGroup extends HBox implements MouseListener {
    TransitionSettingsRectangle settingsRectangle;
    private double offsetX;
    private Map<Tape, TransitionSettingsRectangleTapeHBox> tapes;

    private TransitionSettingsRectangleTapeHBox selected;

    private Double dragX;

    HeadOptionsGroup(TransitionSettingsRectangle settingsRectangle) {
        this.settingsRectangle = settingsRectangle;
        this.tapes = new HashMap<>();

        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setSpacing(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setTranslateX(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING);

        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);
    }

    void addTape(Tape tape){
        TransitionSettingsRectangleTapeHBox transitionSettingsRectangleTapeHBox =
                new TransitionSettingsRectangleTapeHBox(settingsRectangle, tape);
        tapes.put(tape, transitionSettingsRectangleTapeHBox);
        if(this.getChildren().size() != 0) {
            Separator separator = new Separator(Orientation.VERTICAL);
            separator.setTranslateX(-offsetX);
            this.getChildren().add(separator);
        }

        transitionSettingsRectangleTapeHBox.setTranslateX(-offsetX);
        this.getChildren().add(transitionSettingsRectangleTapeHBox);
        transitionSettingsRectangleTapeHBox.setTranslateX(-offsetX);
    }

    void removeTape(Tape tape){
        TransitionSettingsRectangleTapeHBox transitionSettingsRectangleTapeHBox = tapes.remove(tape);
        int index = this.getChildren().indexOf(transitionSettingsRectangleTapeHBox);
        this.getChildren().remove(index);
        int size = this.getChildren().size();
        if(size == 0)
            return;

        if(index != 0)
            this.getChildren().remove(index - 1);
        else
            this.getChildren().remove(index);
    }

    void addHead(Tape tape, Color color){
        TransitionSettingsRectangleTapeHBox transitionSettingsRectangleTapeHBox = tapes.get(tape);
        transitionSettingsRectangleTapeHBox.addHead(color);
    }

    Color removeHead(Tape tape, int head){
        TransitionSettingsRectangleTapeHBox transitionSettingsRectangleTapeHBox = tapes.get(tape);
        return transitionSettingsRectangleTapeHBox.removeHead(head);
    }

    void editHeadColor(Tape tape, int head, Color color) {
        TransitionSettingsRectangleTapeHBox transitionSettingsRectangleTapeHBox = tapes.get(tape);
        transitionSettingsRectangleTapeHBox.editHeadColor(head, color);
    }

    Pair<Tape, Integer> getArbitraryHead() {
        if(tapes.isEmpty())
            return null;
        for(Tape tape : tapes.keySet()){
            if(tape.getNbHeads() == 0)
                continue;
            return new Pair<>(tape, 0);
        }
        return null;
    }

    void chooseHead(Tape tape, int head) {

        if(selected != null)
            selected.unchoose();

        TransitionSettingsRectangleTapeHBox transitionSettingsRectangleTapeHBox = tapes.get(tape);
        selected = transitionSettingsRectangleTapeHBox;
        transitionSettingsRectangleTapeHBox.choose(head);
    }

    Color getColor(Tape tape, int head) {
        return tapes.get(tape).getColor(head);
    }

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        double maxTranslate = 0;
        for(TransitionSettingsRectangleTapeHBox transitionSettingsRectangleTapeHBox : tapes.values())
            maxTranslate += transitionSettingsRectangleTapeHBox.getChildren().size() *
                    (TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING +
                            TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE) +
                    2 * TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING;

        maxTranslate -= TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE +
                2 * TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING;
        if(dx < offsetX - maxTranslate)
            dx = offsetX - maxTranslate;

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(dragX == null)
            dragX = mouseEvent.getX();
        else {
            this.translate(mouseEvent.getX() - dragX);
            dragX = mouseEvent.getX();
        }
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        dragX = mouseEvent.getX();
        return true;
    }
}

class RemoveTransitionIcon extends ImageView implements MouseListener {
    TransitionSettingsRectangle settingsRectangle;

    RemoveTransitionIcon(TransitionSettingsRectangle settingsRectangle) {
        super(Ressources.getRessource("remove_transition.png"));
        this.settingsRectangle = settingsRectangle;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TuringMachineDrawer.getInstance().graphPane.removeTransition(this.settingsRectangle.currentTransitionGroup);
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

class ReadIcon extends Group implements MouseListener {

    TransitionSettingsRectangle settingsRectangle;

    private Rectangle backgroundColor;

    ReadIcon(TransitionSettingsRectangle settingsRectangle) {

        ImageView readIcon = new ImageView(Ressources.getRessource("read_tape_icon.png"));
        this.settingsRectangle = settingsRectangle;

        readIcon.setLayoutX(-readIcon.getBoundsInLocal().getWidth() / 2);
        readIcon.setLayoutY(-readIcon.getBoundsInLocal().getHeight() / 2);

        backgroundColor = new Rectangle(
                - this.settingsRectangle.getMaximizedWidth() / 6,
                - readIcon.getBoundsInLocal().getHeight() / 2,
                this.settingsRectangle.getMaximizedWidth() / 3,
                readIcon.getBoundsInLocal().getHeight());
        backgroundColor.setFill(Color.GREEN);

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        this.getChildren().addAll(backgroundColor, readIcon);
    }

    void setSelected(boolean selected){
        this.backgroundColor.setOpacity(selected?1:0);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        this.settingsRectangle.selectReadMenu();
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

class TransitionSettingsRectangleTapeHBox extends HBox{

    TransitionSettingsRectangle settingsRectangle;
    TransitionSettingsRectangleChooseHead selected;
    Tape tape;

    TransitionSettingsRectangleTapeHBox(TransitionSettingsRectangle settingsRectangle, Tape tape) {
        this.settingsRectangle = settingsRectangle;
        this.tape = tape;

        this.setSpacing(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
    }

    void addHead(Color color) {
        TransitionSettingsRectangleChooseHead headRectangle = new TransitionSettingsRectangleChooseHead(
                settingsRectangle, this,
                0, 0,
                TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE,
                TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE);
        headRectangle.setFill(Color.WHITE);
        headRectangle.setStroke(color);
        headRectangle.setStrokeWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_STROKE_WIDTH);
        this.getChildren().add(headRectangle);
    }

    Color removeHead(int head) {
        return (Color) ((TransitionSettingsRectangleChooseHead)this.getChildren().remove(head)).getStroke();
    }

    void editHeadColor(int head, Color color) {
        ((TransitionSettingsRectangleChooseHead) this.getChildren().get(head)).setStroke(color);
    }

    void choose(int head) {
        selected = (TransitionSettingsRectangleChooseHead) this.getChildren().get(head);
        selected.setFill(TuringMachineDrawer.TRANSITION_SETTINGS_RECTANGLE_SELECTED_FILL_COLOR);
    }

    void unchoose() {
        if(selected == null)
            return;

        selected.setFill(TuringMachineDrawer.TRANSITION_SETTINGS_RECTANGLE_UNSELECTED_FILL_COLOR);
        selected = null;
    }

    Color getColor(int head) {
        return (Color) ((TransitionSettingsRectangleChooseHead)this.getChildren().get(head)).getStroke();
    }
}

class TransitionSettingsRectangleChooseHead extends Rectangle implements MouseListener {
    TransitionSettingsRectangle settingsRectangle;
    TransitionSettingsRectangleTapeHBox transitionSettingsRectangleTapeHBox;

    TransitionSettingsRectangleChooseHead(TransitionSettingsRectangle settingsRectangle,
                                        TransitionSettingsRectangleTapeHBox transitionSettingsRectangleTapeHBox,
                                        double v, double v1, double v2, double v3) {
        super(v, v1, v2, v3);
        this.settingsRectangle = settingsRectangle;
        this.transitionSettingsRectangleTapeHBox = transitionSettingsRectangleTapeHBox;
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    int getHead() {
        return this.getParent().getChildrenUnmodifiable().indexOf(this);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        this.settingsRectangle.chooseHead(this.transitionSettingsRectangleTapeHBox.tape, this.getHead());
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

class ReadSymbolsMenu extends HBox implements MouseListener {
    TransitionSettingsRectangle settingsRectangle;
    private double offsetX;

    private Double dragX;

    ReadSymbolsMenu(TransitionSettingsRectangle settingsRectangle) {
        this.settingsRectangle = settingsRectangle;
        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING);
        this.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);
        this.setTranslateX(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING);

        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);

        ChooseSymbolOptionLabel label =
                new ChooseSymbolOptionLabel(settingsRectangle, TuringMachineDrawer.BLANK_SYMBOL);
        this.getChildren().add(label);
    }

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        int nbSymbols = this.getChildren().size() - 1;
        if(dx < offsetX - (nbSymbols - 1) * (TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING +
                TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE))
            dx = offsetX - (nbSymbols - 1) * (TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING +
                    TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    void addSymbol(String symbol){
        ChooseSymbolOptionLabel label =
                new ChooseSymbolOptionLabel(settingsRectangle, symbol);
        label.setTranslateX(-offsetX);
        this.getChildren().add(label);
    }

    void editSymbol(int index, String symbol) {
        ((ChooseSymbolOptionLabel)this.getChildren().get(index + 1)).setText(symbol);
    }

    void removeSymbol(int index) {
        this.getChildren().remove(index + 1);
    }

    void changeColor(Tape tape, int head, Color color) {

        Set<String> selectedSymbols = new HashSet<>();

        if(settingsRectangle.currentTransitionGroup != null) {
            selectedSymbols = TuringMachineDrawer.getInstance().graphPane.getReadSymbols(
                    settingsRectangle.currentTransitionGroup, tape, head);
        }

        for(Node child : this.getChildren()){
            ChooseSymbolOptionLabel label =
                    (ChooseSymbolOptionLabel) child;
            label.setTextFill(color);
            if(selectedSymbols.contains(label.getText()))
                label.setSelected();
            else
                label.setUnselected();
        }
    }

    void addReadSymbol(String symbol) {
        for(Node child : this.getChildren()){
            ChooseSymbolOptionLabel label =
                    (ChooseSymbolOptionLabel) child;
            if(label.getText().equals(symbol)) {
                label.setSelected();
                return;
            }
        }
    }

    void removeReadSymbol(String symbol) {
        for(Node child : this.getChildren()){
            ChooseSymbolOptionLabel label =
                    (ChooseSymbolOptionLabel) child;
            if(label.getText().equals(symbol)) {
                label.setUnselected();
                return;
            }
        }
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(dragX == null)
            dragX = mouseEvent.getX();
        else {
            this.translate(mouseEvent.getX() - dragX);
            dragX = mouseEvent.getX();
        }
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        dragX = mouseEvent.getX();
        return true;
    }
}

class ChooseSymbolOptionLabel extends Label implements MouseListener {


    private static final Background SELECTED_BACKGROUND = new Background(
            new BackgroundFill(TuringMachineDrawer.TRANSITION_SETTINGS_RECTANGLE_SELECTED_FILL_COLOR,
                    CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background UNSELECTED_BACKGROUND = new Background(
            new BackgroundFill(TuringMachineDrawer.TRANSITION_SETTINGS_RECTANGLE_UNSELECTED_FILL_COLOR,
                    CornerRadii.EMPTY, Insets.EMPTY));

    TransitionSettingsRectangle settingsRectangle;

    boolean selected = false;

    ChooseSymbolOptionLabel(TransitionSettingsRectangle settingsRectangle, String s) {
        super(s);
        this.settingsRectangle = settingsRectangle;

        this.setBackground(UNSELECTED_BACKGROUND);
        this.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
                TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_FONT_SIZE));
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        this.setMinWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);
        this.setMaxWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);
        this.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
        this.setAlignment(Pos.CENTER);
    }

    void setSelected(){
        selected = true;
        this.setBackground(SELECTED_BACKGROUND);
    }

    void setUnselected(){
        selected = false;
        this.setBackground(UNSELECTED_BACKGROUND);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TransitionSettingsRectangle settingsRectangle = this.settingsRectangle;
        String symbol = this.getText();

        if(settingsRectangle.currentTape != null) {
            if(this.selected)
                TuringMachineDrawer.getInstance().graphPane.removeReadSymbol(settingsRectangle.currentTransitionGroup,
                        settingsRectangle.currentTape, settingsRectangle.currentHead, symbol);
            else
                TuringMachineDrawer.getInstance().graphPane.addReadSymbol(settingsRectangle.currentTransitionGroup,
                        settingsRectangle.currentTape, settingsRectangle.currentHead, symbol);
        }

        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

class ReadSymbolsDisplay extends HBox implements MouseListener {
    TransitionSettingsRectangle settingsRectangle;

    private Map<Tape, TapeSymbolsDisplay> tapes;
    private double offsetX;
    private Double dragX;

    ReadSymbolsDisplay(TransitionSettingsRectangle settingsRectangle) {
        this.settingsRectangle = settingsRectangle;
        this.tapes = new HashMap<>();

        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING);
        this.setTranslateX(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING);


        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);
    }

    void addTape(Tape tape){
        TapeSymbolsDisplay transitionSymbolsLabels =
                new TapeSymbolsDisplay(tape);
        transitionSymbolsLabels.managedProperty().bind(transitionSymbolsLabels.visibleProperty());
        tapes.put(tape, transitionSymbolsLabels);
        transitionSymbolsLabels.setTranslateX(-offsetX);
        this.getChildren().add(transitionSymbolsLabels);
    }

    void removeTape(Tape tape){
        TapeSymbolsDisplay transitionSymbolsLabels = tapes.remove(tape);
        this.getChildren().remove(transitionSymbolsLabels);
    }

    void addHead(Tape tape, Color color){
        TapeSymbolsDisplay transitionSymbolsLabels = tapes.get(tape);
        transitionSymbolsLabels.addHead(color);
        if(settingsRectangle.currentTransitionGroup != null)
            transitionSymbolsLabels.setCurrentTransitionArrowGroup(settingsRectangle.currentTransitionGroup);
    }

    void editHeadColor(Tape tape, int head, Color color){
        TapeSymbolsDisplay transitionSymbolsLabels = tapes.get(tape);
        transitionSymbolsLabels.editHeadColor(head, color);
    }

    void removeHead(Tape tape, int head){
        TapeSymbolsDisplay transitionSymbolsLabels = tapes.get(tape);
        transitionSymbolsLabels.removeHead(head);
    }

    void setCurrentTransitionArrowGroup(TransitionGroup transitionGroup) {
        for(TapeSymbolsDisplay tapeSymbolsDisplay: tapes.values())
            tapeSymbolsDisplay.setCurrentTransitionArrowGroup(transitionGroup);
    }

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(dragX == null)
            dragX = mouseEvent.getX();
        else {
            this.translate(mouseEvent.getX() - dragX);
            dragX = mouseEvent.getX();
        }
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        dragX = mouseEvent.getX();
        return true;
    }
}

class TapeSymbolsDisplay extends HBox {

    Tape tape;

    TapeSymbolsDisplay(Tape tape) {
        this.tape = tape;
        this.setSpacing(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING);
    }

    void addHead(Color color){
        HeadSymbolsLabelDisplay transitionSymbolLabel =
                new HeadSymbolsLabelDisplay(color);
        this.getChildren().add(transitionSymbolLabel);
    }


    void editHeadColor(int head, Color color) {
        HeadSymbolsLabelDisplay transitionSymbolLabel =
                (HeadSymbolsLabelDisplay) this.getChildren().get(head);
        transitionSymbolLabel.setTextFill(color);
    }

    void removeHead(int head){
        this.getChildren().remove(head);
    }

    void setCurrentTransitionArrowGroup(TransitionGroup transitionGroup) {
        int head = 0;
        for(Node child : this.getChildren()){
            HeadSymbolsLabelDisplay headSymbolsLabelDisplay = (HeadSymbolsLabelDisplay) child;
            ObservableValue<String> property = transitionGroup.getSymbolDisplayTextProperty(tape, head);
            headSymbolsLabelDisplay.textProperty().bind(property);
            head++;
        }
    }
}

class HeadSymbolsLabelDisplay extends Label {

    HeadSymbolsLabelDisplay(Color color) {
        this.setTextFill(color);

        this.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
                TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_FONT_SIZE));

        this.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setAlignment(Pos.CENTER);
    }
}

class ActionsIcon extends Group implements MouseListener {

    TransitionSettingsRectangle settingsRectangle;
    private Rectangle backgroundColor;

    ActionsIcon(TransitionSettingsRectangle settingsRectangle) {

        ImageView actionsIcon = new ImageView(Ressources.getRessource("action_tape_icon.png"));
        this.settingsRectangle = settingsRectangle;

        actionsIcon.setLayoutX(-actionsIcon.getBoundsInLocal().getWidth() / 2);
        actionsIcon.setLayoutY(-actionsIcon.getBoundsInLocal().getHeight() / 2);

        backgroundColor = new Rectangle(
                - this.settingsRectangle.getMaximizedWidth() / 6,
                - actionsIcon.getBoundsInLocal().getHeight() / 2,
                this.settingsRectangle.getMaximizedWidth() / 3,
                actionsIcon.getBoundsInLocal().getHeight());
        backgroundColor.setFill(Color.GREEN);

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        this.getChildren().addAll(backgroundColor, actionsIcon);
    }

    void setSelected(boolean selected){
        this.backgroundColor.setOpacity(selected?1:0);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        this.settingsRectangle.selectActionsMenu();
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

class ActionsMenu extends HBox implements MouseListener {
    TransitionSettingsRectangle settingsRectangle;
    RemoveActionIcon removeActionIcon;
    private double offsetX;
    private Double dragX;

    ActionsMenu(TransitionSettingsRectangle settingsRectangle) {
        this.settingsRectangle = settingsRectangle;
        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING);
        this.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);
        this.setTranslateX(TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING);

        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);

        removeActionIcon = new RemoveActionIcon(settingsRectangle);
        this.getChildren().add(removeActionIcon);

        String defaultActions[] = {TuringMachineDrawer.LEFT_SYMBOL,
                TuringMachineDrawer.RIGHT_SYMBOL,
                TuringMachineDrawer.DOWN_SYMBOL,
                TuringMachineDrawer.UP_SYMBOL,
                TuringMachineDrawer.BLANK_SYMBOL
        } ;
        for(String direction : defaultActions) {
            ChooseActionOptionLabel label =
                    new ChooseActionOptionLabel(settingsRectangle, direction);

            this.getChildren().add(label);
        }
    }

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        int nbSymbols = this.getChildren().size() - 1;
        if(dx < offsetX - (nbSymbols - 1) * (TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING +
                TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE)
                - TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING
                - removeActionIcon.getBoundsInLocal().getWidth())
            dx = offsetX - (nbSymbols - 1) * (TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING +
                    TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE)
                    - TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING
                    - removeActionIcon.getBoundsInLocal().getWidth();

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    void addSymbol(String symbol){
        ChooseActionOptionLabel label =
                new ChooseActionOptionLabel(settingsRectangle, symbol);


        label.setTranslateX(-offsetX);
        this.getChildren().add(label);
    }

    void editSymbol(int index, String symbol) {
        ((ChooseActionOptionLabel)this.getChildren().get(index + 6)).setText(symbol);
    }

    void removeSymbol(int index) {
        this.getChildren().remove(index +6);
    }

    void changeColor(Color color) {
        for(Node child : this.getChildren()){
            if(!(child instanceof ChooseActionOptionLabel))
                continue;
            ChooseActionOptionLabel label =
                    (ChooseActionOptionLabel) child;
            label.setTextFill(color);
        }
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(dragX == null)
            dragX = mouseEvent.getX();
        else {
            this.translate(mouseEvent.getX() - dragX);
            dragX = mouseEvent.getX();
        }
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        dragX = mouseEvent.getX();
        return true;
    }
}

class ChooseActionOptionLabel extends Label implements MouseListener {
    TransitionSettingsRectangle settingsRectangle;

    ChooseActionOptionLabel(TransitionSettingsRectangle settingsRectangle, String s) {
        super(s);
        this.settingsRectangle = settingsRectangle;

        this.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
                TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_FONT_SIZE));
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        this.setMinWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);
        this.setMaxWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);
        this.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TransitionSettingsRectangle settingsRectangle = this.settingsRectangle;

        String actionSymbol = this.getText();

        if(settingsRectangle.currentTape != null)
            TuringMachineDrawer.getInstance().graphPane.addAction(settingsRectangle.currentTransitionGroup,
                    settingsRectangle.currentTape, settingsRectangle.currentHead, actionSymbol);
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

class RemoveActionIcon extends ImageView implements MouseListener {

    TransitionSettingsRectangle settingsRectangle;

    RemoveActionIcon(TransitionSettingsRectangle settingsRectangle) {
        super(Ressources.getRessource("remove_action.png"));
        this.settingsRectangle = settingsRectangle;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TransitionSettingsRectangle settingsRectangle = this.settingsRectangle;

        if(settingsRectangle.currentTape != null)
            TuringMachineDrawer.getInstance().graphPane.removeAction(settingsRectangle.currentTransitionGroup);

        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

class ActionDisplay extends HBox implements MouseListener {

    TransitionSettingsRectangle settingsRectangle;
    private double offsetX;
    private Double dragX;

    ActionDisplay(TransitionSettingsRectangle settingsRectangle) {
        this.settingsRectangle = settingsRectangle;
        this.offsetX = 0;

        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING);

        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);
    }

    void addAction(Tape tape, int head, String actionSymbol) {
        addAction(TuringMachineDrawer.getInstance().getColorOfHead(tape, head), actionSymbol);
    }

    private void addAction(Color color, String actionSymbol){
        Label label = new Label(actionSymbol);
        label.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
                TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_FONT_SIZE));
        label.setTextFill(color);

        label.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        label.setMinWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);
        label.setMaxWidth(TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);
        label.setMinHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);
        label.setMaxHeight(TuringMachineDrawer.SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT / 2 - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT);
        label.setAlignment(Pos.CENTER);

        label.setTranslateX(-offsetX);

        this.getChildren().add(label);
    }

    void removeAction(int index){
        this.getChildren().remove(index);
    }

    void setCurrentTransitionArrowGroup(TransitionGroup transitionGroup) {
        this.getChildren().clear();
        for(Pair<String, Color> pair: transitionGroup.getActionsDisplay()){
            this.addAction(pair.second, pair.first);
        }
    }

    void editSymbol(String previousSymbol, String symbol){
        for(Node child : this.getChildren()) {
            Label label = (Label) child;
            if(label.getText().equals(previousSymbol))
                label.setText(symbol);
        }
    }

    void removeSymbol(String symbol){
        Iterator<Node> it = this.getChildren().iterator();
        while(it.hasNext()){
            Label label = (Label)it.next();
            if(label.getText().equals(symbol))
                it.remove();
        }
    }

    void editHeadColor(Tape tape, int head, Color color) {
        Color previousColor = TuringMachineDrawer.getInstance().getColorOfHead(tape, head);

        for(Node child: this.getChildren()){
            Label label = (Label) child;
            if(label.getTextFill().equals(previousColor))
                label.setTextFill(color);
        }
    }

    void translate(double dx) {

        if(dx > offsetX)
            dx = offsetX;

        int nbSymbols = this.getChildren().size() - 1;
        if(dx < offsetX - (nbSymbols - 1) * (TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING +
                TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE))
            dx = offsetX - (nbSymbols - 1) * (TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING +
                    TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE);

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(dragX == null)
            dragX = mouseEvent.getX();
        else {
            this.translate(mouseEvent.getX() - dragX);
            dragX = mouseEvent.getX();
        }
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        dragX = mouseEvent.getX();
        return true;
    }
}