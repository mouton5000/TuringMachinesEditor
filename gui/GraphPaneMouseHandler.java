package gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

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

        if(source instanceof Pane) {
            dragX = x;
            dragY = y;
            mouseEvent.consume();
        }
        else if(source instanceof StateGroup){
            StateGroup stateGroup = ((StateGroup) source);
            if(!stateGroup.drawer.graphPane.stateOptionRectangle.isMaximized()
                    && !stateGroup.drawer.graphPane.transitionOptionRectangle.isMaximized())
                stateGroup.startTimeline();
        }
        else if(source instanceof TransitionArrowInvisibleLine){
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


        if(!(source instanceof StateOptionRectangle
                || source instanceof FinalStateOption
                || source instanceof AcceptingStateOption
                || source instanceof InitialStateOption
                || source instanceof EditStateNameOptionIcon)
                && drawer.graphPane.stateOptionRectangle.isMaximized()) {
            drawer.graphPane.closeStateOptionRectangle();

        }
        else if(!(source instanceof TransitionOptionRectangle
                || source instanceof ReadGroup
                || source instanceof ActionsGroup)
                && drawer.graphPane.transitionOptionRectangle.isMaximized())
            drawer.graphPane.closeTransitionOptionRectangle();
        else if(source instanceof Pane) {

            if(selected != null)
                unselect();
            else
                drawer.graphPane.addState(x, y);

        }
        else if(source instanceof StateGroup){

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
        else if(source instanceof FinalStateOption){
            drawer.graphPane.toggleFinal(((FinalStateOption) source).optionRectangle.currentState);
            mouseEvent.consume();
        }
        else if(source instanceof AcceptingStateOption){
            drawer.graphPane.toggleAccepting(((AcceptingStateOption) source).optionRectangle.currentState);
            mouseEvent.consume();
        }
        else if(source instanceof InitialStateOption){
            drawer.graphPane.toggleInitial(((InitialStateOption) source).optionRectangle.currentState);
            mouseEvent.consume();
        }
        else if(source instanceof EditStateNameOptionIcon){
            mouseEvent.consume();
        }
        else if(source instanceof TransitionArrowInvisibleLine){
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
        else if(source instanceof ReadGroup){
            ((ReadGroup) source).optionRectangle.selectReadMenu();
        }
        else if(source instanceof ActionsGroup){
            ((ActionsGroup) source).optionRectangle.selectActionsMenu();
        }
        else if(source instanceof OptionRectangle)
            mouseEvent.consume();

    }

    public void handleDragEvent(MouseEvent mouseEvent) {
        if(mouseEvent.isStillSincePress())
            return;

        Object source = mouseEvent.getSource();
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();


        if(!(source instanceof StateOptionRectangle)
                && drawer.graphPane.stateOptionRectangle.isMaximized())
            drawer.graphPane.closeStateOptionRectangle();
        else if(!(source instanceof TransitionOptionRectangle) && drawer.graphPane.transitionOptionRectangle.isMaximized())
            drawer.graphPane.closeTransitionOptionRectangle();
        else if(source instanceof Pane){
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
       else if(source instanceof TransitionArrowControl1KeyCircle){
            if(!(selected instanceof TransitionArrowGroup))
                return;
            TransitionArrowGroup transitionArrowGroup = ((TransitionArrowControl1KeyCircle) source).transitionArrowGroup;
            transitionArrowGroup.setControl1(x, y);
            mouseEvent.consume();
        }
        else if(source instanceof TransitionArrowControl2KeyCircle){
            if(!(selected instanceof TransitionArrowGroup))
                return;
            TransitionArrowGroup transitionArrowGroup = ((TransitionArrowControl2KeyCircle) source).transitionArrowGroup;
            transitionArrowGroup.setControl2(x, y);
            mouseEvent.consume();
        }
        else if(source instanceof TransitionArrowGroup){
        }
        else if(source instanceof StateGroup) {
            StateGroup stateGroup = ((StateGroup) source);
            stateGroup.stopTimeline();
            select(stateGroup);
            drawer.graphPane.moveStateGroup(stateGroup, stateGroup.getLayoutX() + x, stateGroup.getLayoutY() + y);
            mouseEvent.consume();
        }
        else if(source instanceof StateOptionRectangle)
            mouseEvent.consume();
        else if(source instanceof TransitionOptionRectangle)
            mouseEvent.consume();
    }

    private void select(Node node) {
        unselect();
        selected = node;

        if(node instanceof TransitionArrowGroup)
            ((TransitionArrowGroup) node).setSelected(true);
        else if(node instanceof StateGroup)
            ((StateGroup) node).setSelected();
    }

    private void unselect() {
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
