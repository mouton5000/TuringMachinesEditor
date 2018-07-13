package gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import turingmachines.Tape;
import turingmachines.TuringMachine;
import util.widget.VirtualKeyboard;

import java.util.Optional;

/**
 * Created by dimitri.watel on 04/06/18.
 */
public class GraphPaneMouseHandler implements EventHandler<Event> {

    private Node selected;

    private Double dragX;
    private Double dragY;

    public GraphPaneMouseHandler() {
        selected = null;
    }

    @Override
    public void handle(Event event) {
        if(TuringMachineDrawer.getInstance().animating)
            return;

//        System.out.println(event.getEventType()+" "+event.getSource().getClass());

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
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof StateGroup){
            StateGroup stateGroup = ((StateGroup) source);
            if(!TuringMachineDrawer.getInstance().graphPane.stateOptionRectangle.isMaximized()
                    && !TuringMachineDrawer.getInstance().graphPane.transitionOptionRectangle.isMaximized())
                stateGroup.startTimeline();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof TransitionArrowInvisibleLine){
            TransitionGroup transitionGroup = ((TransitionArrowInvisibleLine) source).transitionGroup;
            if(!TuringMachineDrawer.getInstance().graphPane.stateOptionRectangle.isMaximized()
                    && !TuringMachineDrawer.getInstance().graphPane.transitionOptionRectangle.isMaximized())
                transitionGroup.startTimeline();
        }
    }

    public void handleClickedEvent(MouseEvent mouseEvent) {
        if(!mouseEvent.isStillSincePress()) {
            return;
        }

        Object source = mouseEvent.getSource();
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();


        if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode &&
                !(source instanceof StateOptionRectangle
                || source instanceof FinalStateOption
                || source instanceof AcceptingStateOption
                || source instanceof InitialStateOption
                || source instanceof EditStateNameOptionIcon
                || source instanceof RemoveStateOptionIcon)
                && TuringMachineDrawer.getInstance().graphPane.stateOptionRectangle.isMaximized()) {
            TuringMachineDrawer.getInstance().graphPane.closeStateOptionRectangle();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode &&
                !(source instanceof TransitionOptionRectangle
                || source instanceof ReadIcon
                || source instanceof ActionsIcon
                || source instanceof RemoveTransitionIcon
                || source instanceof TransitionOptionRectangleChooseHead
                || source instanceof ChooseActionOptionLabel
                || source instanceof ChooseSymbolOptionLabel
                || source instanceof RemoveActionIcon)
                && TuringMachineDrawer.getInstance().graphPane.transitionOptionRectangle.isMaximized())
            TuringMachineDrawer.getInstance().graphPane.closeTransitionOptionRectangle();
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof GraphPane) {

            if(selected != null)
                unselect();
            else
                TuringMachineDrawer.getInstance().addState(x, y);

        }
        else if(!TuringMachineDrawer.getInstance().buildMode && source instanceof StateGroup){

            StateGroup stateGroup = ((StateGroup) source);

            if(TuringMachineDrawer.getInstance().manualMode){
                TuringMachineDrawer.getInstance().manualSelectCurrentState(stateGroup);
                mouseEvent.consume();
            }
            else {
                boolean pressFinished = !stateGroup.animating;
                stateGroup.stopTimeline();

                if (pressFinished) {
                    unselect();
                    TuringMachineDrawer.getInstance().graphPane.openStateOptionRectangle(stateGroup);
                } else {
                    if (selected == null)
                        select(stateGroup);
                    else if (selected instanceof StateGroup) {
                        TuringMachineDrawer.getInstance().graphPane.addTransition((StateGroup) selected, stateGroup);
                        unselect();
                    } else
                        unselect();
                }

                mouseEvent.consume();
            }

        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof FinalStateOption){
            TuringMachineDrawer.getInstance().graphPane.toggleFinal(((FinalStateOption) source).optionRectangle.currentState);
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof AcceptingStateOption){
            TuringMachineDrawer.getInstance().graphPane.toggleAccepting(((AcceptingStateOption) source).optionRectangle.currentState);
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof InitialStateOption){
            TuringMachineDrawer.getInstance().graphPane.toggleInitial(((InitialStateOption) source).optionRectangle.currentState);
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof EditStateNameOptionIcon){

            EditStateNameOptionIcon editStateNameOptionIcon = (EditStateNameOptionIcon) source;
            StateGroup stateGroup = editStateNameOptionIcon.optionRectangle.currentState;

            VirtualKeyboard virtualKeyboard = new VirtualKeyboard(stateGroup.getName());
            virtualKeyboard.setX(mouseEvent.getScreenX() - virtualKeyboard.getWidth() / 2);
            virtualKeyboard.setY(mouseEvent.getScreenY());

            Optional<String> result = virtualKeyboard.showAndWait();
            if(result.isPresent()) {
                stateGroup.setName(result.get());
                TuringMachineDrawer.getInstance().setEnableToSave();
            }
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof RemoveStateOptionIcon){
            TuringMachineDrawer.getInstance().graphPane.removeState(((RemoveStateOptionIcon) source).optionRectangle.currentState);
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && source instanceof TransitionArrowInvisibleLine){
            TransitionGroup transitionGroup = ((TransitionArrowInvisibleLine) source).transitionGroup;

            if(TuringMachineDrawer.getInstance().manualMode){
                TuringMachineDrawer.getInstance().manualFireTransition(transitionGroup);
                mouseEvent.consume();
            }
            else {
                boolean pressFinished = !transitionGroup.animating;
                transitionGroup.stopTimeline();

                if (pressFinished) {
                    unselect();
                    TuringMachineDrawer.getInstance().graphPane.openTransitionOptionRectangle(transitionGroup);
                } else
                    select(transitionGroup);

                mouseEvent.consume();
            }

        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof ReadIcon){
            ((ReadIcon) source).optionRectangle.selectReadMenu();
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof ActionsIcon){
            ((ActionsIcon) source).optionRectangle.selectActionsMenu();
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof RemoveTransitionIcon){
            TuringMachineDrawer.getInstance().graphPane.removeTransition(((RemoveTransitionIcon) source).optionRectangle.currentTransitionGroup);
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof TransitionOptionRectangleChooseHead){
            TransitionOptionRectangleChooseHead transitionOptionRectangleChooseHead =
                    (TransitionOptionRectangleChooseHead) source;
            Tape tape = transitionOptionRectangleChooseHead.transitionOptionRectangleTapeHBox.tape;
            int head  = transitionOptionRectangleChooseHead.getHead();
            transitionOptionRectangleChooseHead.optionRectangle.chooseHead(tape, head);
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof ChooseSymbolOptionLabel){
            ChooseSymbolOptionLabel chooseSymbolOptionLabel = (ChooseSymbolOptionLabel) source;
            TransitionOptionRectangle optionRectangle = chooseSymbolOptionLabel.optionRectangle;

            String symbol = chooseSymbolOptionLabel.getText();

            if(optionRectangle.currentTape != null) {
                if(chooseSymbolOptionLabel.selected)
                    TuringMachineDrawer.getInstance().graphPane.removeReadSymbol(optionRectangle.currentTransitionGroup,
                            optionRectangle.currentTape, optionRectangle.currentHead, symbol);
                else
                    TuringMachineDrawer.getInstance().graphPane.addReadSymbol(optionRectangle.currentTransitionGroup,
                            optionRectangle.currentTape, optionRectangle.currentHead, symbol);
            }

            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof ChooseActionOptionLabel){
            ChooseActionOptionLabel chooseActionOptionLabel = (ChooseActionOptionLabel) source;
            TransitionOptionRectangle optionRectangle = chooseActionOptionLabel.optionRectangle;

            String actionSymbol = chooseActionOptionLabel.getText();

            if(optionRectangle.currentTape != null) {
                TuringMachineDrawer.getInstance().graphPane.addAction(optionRectangle.currentTransitionGroup,
                        optionRectangle.currentTape, optionRectangle.currentHead, actionSymbol);
            }
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof RemoveActionIcon){
            RemoveActionIcon removeActionIcon = (RemoveActionIcon) source;
            TransitionOptionRectangle optionRectangle = removeActionIcon.optionRectangle;

            if(optionRectangle.currentTape != null)
                TuringMachineDrawer.getInstance().graphPane.removeAction(optionRectangle.currentTransitionGroup);
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof OptionRectangle)
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
                TuringMachineDrawer.getInstance().graphPane.translate(x - dragX, y - dragY);
                dragX = x;
                dragY = y;
            }
            mouseEvent.consume();
        }
       else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof TransitionArrowControl1KeyCircle){
            if(!(selected instanceof TransitionGroup))
                return;
            TransitionGroup transitionGroup = ((TransitionArrowControl1KeyCircle) source).transitionGroup;
            transitionGroup.setControl1(x, y);
            TuringMachineDrawer.getInstance().setEnableToSave();
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof TransitionArrowControl2KeyCircle){
            if(!(selected instanceof TransitionGroup))
                return;
            TransitionGroup transitionGroup = ((TransitionArrowControl2KeyCircle) source).transitionGroup;
            transitionGroup.setControl2(x, y);
            TuringMachineDrawer.getInstance().setEnableToSave();
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof StateGroup) {
            StateGroup stateGroup = ((StateGroup) source);
            stateGroup.stopTimeline();
            select(stateGroup);
            TuringMachineDrawer.getInstance().graphPane.moveStateGroup(stateGroup,
                    stateGroup.getLayoutX() + x,
                    stateGroup.getLayoutY() + y);
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof TransitionArrowInvisibleLine) {
            TransitionGroup transitionGroup = ((TransitionArrowInvisibleLine) source).transitionGroup;
            transitionGroup.stopTimeline();
            select(transitionGroup);
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof StateOptionRectangle)
            mouseEvent.consume();
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof TransitionOptionRectangle)
            mouseEvent.consume();
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof HeadOptionsGroup){
            if(dragX == null)
                dragX = x;
            else {
                HeadOptionsGroup group = (HeadOptionsGroup) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof ReadSymbolMenu){
            if(dragX == null)
                dragX = x;
            else {
                ReadSymbolMenu group = (ReadSymbolMenu) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof ActionsMenu){
            if(dragX == null)
                dragX = x;
            else {
                ActionsMenu group = (ActionsMenu) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof TransitionOptionRectangleSymbolsDisplay){
            if(dragX == null)
                dragX = x;
            else {
                TransitionOptionRectangleSymbolsDisplay group = (TransitionOptionRectangleSymbolsDisplay) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof ActionDisplay){
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

        if(node instanceof TransitionGroup)
            ((TransitionGroup) node).setSelected(true);
        else if(node instanceof StateGroup)
            ((StateGroup) node).setSelected();
    }

    void unselect() {
        if(selected == null)
            return;

        if(selected instanceof TransitionGroup) {
            TransitionGroup transitionGroup = (TransitionGroup) selected;
            transitionGroup.setSelected(false);
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
