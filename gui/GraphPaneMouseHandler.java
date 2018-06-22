package gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import turingmachines.Tape;
import util.widget.VirtualKeyboard;

import java.util.Optional;

/**
 * Created by dimitri.watel on 04/06/18.
 */
public class GraphPaneMouseHandler implements EventHandler<Event> {

    private TuringMachineDrawer drawer;

    private Node selected;

    private Double dragX;
    private Double dragY;

    public GraphPaneMouseHandler(TuringMachineDrawer drawer) {
        this.drawer = drawer;
        selected = null;
    }

    @Override
    public void handle(Event event) {
        if(drawer.animating)
            return;

        System.out.println(event.getEventType()+" "+event.getSource().getClass());

        if(event.getEventType() == MouseEvent.MOUSE_CLICKED)
            this.handleClickedEvent((MouseEvent) event);
        else if(event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            this.handlePressEvent((MouseEvent) event);
        }
        else if(event.getEventType() == MouseEvent.MOUSE_DRAGGED)
            this.handleDragEvent((MouseEvent) event);
    }

    private void handlePressEvent(MouseEvent mouseEvent) {
        Object source = mouseEvent.getSource();

        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        if(source instanceof GraphPane
                || source instanceof HeadOptionsGroup
                || source instanceof ReadSymbolMenu
                || source instanceof ActionsMenu
                || source instanceof TransitionOptionRectangleSymbolsDisplay
                || source instanceof ActionDisplay) {
            dragX = x;
            dragY = y;
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof StateGroup){
            StateGroup stateGroup = ((StateGroup) source);
            if(!stateGroup.drawer.graphPane.stateOptionRectangle.isMaximized()
                    && !stateGroup.drawer.graphPane.transitionOptionRectangle.isMaximized())
                stateGroup.startTimeline();
        }
        else if(!drawer.buildMode && source instanceof TransitionArrowInvisibleLine){
            TransitionArrowGroup transitionArrowGroup = ((TransitionArrowInvisibleLine) source).transitionArrowGroup;
            if(!transitionArrowGroup.drawer.graphPane.stateOptionRectangle.isMaximized()
                    && !transitionArrowGroup.drawer.graphPane.transitionOptionRectangle.isMaximized())
                transitionArrowGroup.startTimeline();
        }
    }

    public void handleClickedEvent(MouseEvent mouseEvent) {
        if(!mouseEvent.isStillSincePress()) {
            return;
        }

        Object source = mouseEvent.getSource();
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();


        if(!drawer.buildMode && !(source instanceof StateOptionRectangle
                || source instanceof FinalStateOption
                || source instanceof AcceptingStateOption
                || source instanceof InitialStateOption
                || source instanceof EditStateNameOptionIcon
                || source instanceof RemoveStateOptionIcon)
                && drawer.graphPane.stateOptionRectangle.isMaximized()) {
            drawer.graphPane.closeStateOptionRectangle();
        }
        else if(!drawer.buildMode && !(source instanceof TransitionOptionRectangle
                || source instanceof ReadIcon
                || source instanceof ActionsIcon
                || source instanceof RemoveTransitionIcon
                || source instanceof TransitionOptionRectangleChooseHead
                || source instanceof ChooseActionOptionLabel
                || source instanceof ChooseSymbolOptionLabel
                || source instanceof RemoveActionIcon)
                && drawer.graphPane.transitionOptionRectangle.isMaximized())
            drawer.graphPane.closeTransitionOptionRectangle();
        else if(!drawer.buildMode && source instanceof Pane) {

            if(selected != null)
                unselect();
            else
                drawer.graphPane.addState(x, y);

        }
        else if(!drawer.buildMode && source instanceof StateGroup){

            StateGroup circle = ((StateGroup) source);

            boolean pressFinished = !circle.animating;
            circle.stopTimeline();

            if(pressFinished){
                unselect();
                circle.drawer.graphPane.openStateOptionRectangle(circle);
            }
            else{
                if(selected == null)
                    select(circle);
                else if(selected instanceof StateGroup){
                    drawer.graphPane.addTransition((StateGroup) selected, circle);
                    unselect();
                }
                else
                    unselect();
            }

            mouseEvent.consume();

        }
        else if(!drawer.buildMode && source instanceof FinalStateOption){
            drawer.graphPane.toggleFinal(((FinalStateOption) source).optionRectangle.currentState);
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof AcceptingStateOption){
            drawer.graphPane.toggleAccepting(((AcceptingStateOption) source).optionRectangle.currentState);
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof InitialStateOption){
            drawer.graphPane.toggleInitial(((InitialStateOption) source).optionRectangle.currentState);
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof EditStateNameOptionIcon){

            EditStateNameOptionIcon editStateNameOptionIcon = (EditStateNameOptionIcon) source;
            StateGroup stateGroup = editStateNameOptionIcon.optionRectangle.currentState;

            VirtualKeyboard virtualKeyboard = new VirtualKeyboard(stateGroup.getName());
            virtualKeyboard.setX(mouseEvent.getScreenX() - virtualKeyboard.getWidth() / 2);
            virtualKeyboard.setY(mouseEvent.getScreenY());

            Optional<String> result = virtualKeyboard.showAndWait();
            if(result.isPresent())
                stateGroup.setName(result.get());
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof RemoveStateOptionIcon){
            drawer.graphPane.removeState(((RemoveStateOptionIcon) source).optionRectangle.currentState);
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof TransitionArrowInvisibleLine){
            TransitionArrowGroup transitionArrowGroup = ((TransitionArrowInvisibleLine) source).transitionArrowGroup;

            boolean pressFinished = !transitionArrowGroup.animating;
            transitionArrowGroup.stopTimeline();

            if(pressFinished){
                unselect();
                transitionArrowGroup.drawer.graphPane.openTransitionOptionRectangle(transitionArrowGroup);
            }
            else
                select(transitionArrowGroup);

            mouseEvent.consume();

        }
        else if(!drawer.buildMode && source instanceof ReadIcon){
            ((ReadIcon) source).optionRectangle.selectReadMenu();
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof ActionsIcon){
            ((ActionsIcon) source).optionRectangle.selectActionsMenu();
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof RemoveTransitionIcon){
            drawer.graphPane.removeTransition(((RemoveTransitionIcon) source).optionRectangle.currentTransitionArrowGroup);
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof TransitionOptionRectangleChooseHead){
            TransitionOptionRectangleChooseHead transitionOptionRectangleChooseHead =
                    (TransitionOptionRectangleChooseHead) source;
            Tape tape = transitionOptionRectangleChooseHead.transitionOptionRectangleTapeHBox.tape;
            int head  = transitionOptionRectangleChooseHead.getHead();
            transitionOptionRectangleChooseHead.optionRectangle.chooseHead(tape, head);
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof ChooseSymbolOptionLabel){
            ChooseSymbolOptionLabel chooseSymbolOptionLabel = (ChooseSymbolOptionLabel) source;
            TransitionOptionRectangle optionRectangle = chooseSymbolOptionLabel.optionRectangle;

            String symbol = chooseSymbolOptionLabel.getText();

            if(optionRectangle.currentTape != null) {
                if(chooseSymbolOptionLabel.selected)
                    drawer.graphPane.removeReadSymbol(optionRectangle.currentTransitionArrowGroup,
                            optionRectangle.currentTape, optionRectangle.currentHead, symbol);
                else
                    drawer.graphPane.addReadSymbol(optionRectangle.currentTransitionArrowGroup,
                            optionRectangle.currentTape, optionRectangle.currentHead, symbol);
            }

            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof ChooseActionOptionLabel){
            ChooseActionOptionLabel chooseActionOptionLabel = (ChooseActionOptionLabel) source;
            TransitionOptionRectangle optionRectangle = chooseActionOptionLabel.optionRectangle;

            String actionSymbol = chooseActionOptionLabel.getText();

            if(optionRectangle.currentTape != null) {
                drawer.graphPane.addAction(optionRectangle.currentTransitionArrowGroup,
                        optionRectangle.currentTape, optionRectangle.currentHead, actionSymbol);
            }
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof RemoveActionIcon){
            RemoveActionIcon removeActionIcon = (RemoveActionIcon) source;
            TransitionOptionRectangle optionRectangle = removeActionIcon.optionRectangle;

            if(optionRectangle.currentTape != null)
                drawer.graphPane.removeAction(optionRectangle.currentTransitionArrowGroup);
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof OptionRectangle)
            mouseEvent.consume();

    }

    public void handleDragEvent(MouseEvent mouseEvent) {
        if(mouseEvent.isStillSincePress())
            return;

        Object source = mouseEvent.getSource();
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();


        if(source instanceof GraphPane){
            if(dragX == null){
                dragX = x;
                dragY = y;
            }
            else {
                drawer.graphPane.translate(x - dragX, y - dragY);
                dragX = x;
                dragY = y;
            }
            mouseEvent.consume();
        }
       else if(!drawer.buildMode && source instanceof TransitionArrowControl1KeyCircle){
            if(!(selected instanceof TransitionArrowGroup))
                return;
            TransitionArrowGroup transitionArrowGroup = ((TransitionArrowControl1KeyCircle) source).transitionArrowGroup;
            transitionArrowGroup.setControl1(x, y);
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof TransitionArrowControl2KeyCircle){
            if(!(selected instanceof TransitionArrowGroup))
                return;
            TransitionArrowGroup transitionArrowGroup = ((TransitionArrowControl2KeyCircle) source).transitionArrowGroup;
            transitionArrowGroup.setControl2(x, y);
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof StateGroup) {
            StateGroup stateGroup = ((StateGroup) source);
            stateGroup.stopTimeline();
            select(stateGroup);
            drawer.graphPane.moveStateGroup(stateGroup, stateGroup.getLayoutX() + x, stateGroup.getLayoutY() + y);
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof TransitionArrowInvisibleLine) {
            TransitionArrowGroup transitionArrowGroup = ((TransitionArrowInvisibleLine) source).transitionArrowGroup;
            transitionArrowGroup.stopTimeline();
            select(transitionArrowGroup);
        }
        else if(!drawer.buildMode && source instanceof StateOptionRectangle)
            mouseEvent.consume();
        else if(!drawer.buildMode && source instanceof TransitionOptionRectangle)
            mouseEvent.consume();
        else if(!drawer.buildMode && source instanceof HeadOptionsGroup){
            if(dragX == null)
                dragX = x;
            else {
                HeadOptionsGroup group = (HeadOptionsGroup) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof ReadSymbolMenu){
            if(dragX == null)
                dragX = x;
            else {
                ReadSymbolMenu group = (ReadSymbolMenu) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof ActionsMenu){
            if(dragX == null)
                dragX = x;
            else {
                ActionsMenu group = (ActionsMenu) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof TransitionOptionRectangleSymbolsDisplay){
            if(dragX == null)
                dragX = x;
            else {
                TransitionOptionRectangleSymbolsDisplay group = (TransitionOptionRectangleSymbolsDisplay) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(!drawer.buildMode && source instanceof ActionDisplay){
            if(dragX == null)
                dragX = x;
            else {
                ActionDisplay group = (ActionDisplay) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
    }

    private void select(Node node) {
        unselect();
        selected = node;

        if(node instanceof TransitionArrowGroup)
            ((TransitionArrowGroup) node).setSelected(true);
        else if(node instanceof StateGroup)
            ((StateGroup) node).setSelected();
    }

    void unselect() {
        if(selected == null)
            return;

        if(selected instanceof TransitionArrowGroup) {
            TransitionArrowGroup transitionArrowGroup = (TransitionArrowGroup) selected;
            transitionArrowGroup.setSelected(false);
            selected = null;
        }
        else if(selected instanceof StateGroup){
            StateGroup stateGroup = (StateGroup) selected;
            stateGroup.setUnselected();
            selected = null;
        }
        else
            selected = null;
    }


}
