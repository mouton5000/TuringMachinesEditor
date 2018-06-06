package gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

/**
 * Created by dimitri.watel on 04/06/18.
 */
public class GraphPaneMouseHandler implements EventHandler<Event> {

    private TuringMachineDrawer drawer;

    private Node selected;

    public GraphPaneMouseHandler(TuringMachineDrawer drawer) {
        this.drawer = drawer;
        selected = null;
    }

    @Override
    public void handle(Event event) {

        if(event.getEventType() == MouseEvent.MOUSE_CLICKED)
            this.handleClickedEvent((MouseEvent) event);
        else if(event.getEventType() == MouseEvent.MOUSE_DRAGGED)
            this.handleDragEvent((MouseEvent) event);
    }

    public void handleClickedEvent(MouseEvent mouseEvent) {
        if(!mouseEvent.isStillSincePress())
            return;

        Object source = mouseEvent.getSource();
        int x = (int) mouseEvent.getX();
        int y = (int) mouseEvent.getY();

        if(source instanceof Pane) {

            if(selected != null)
                unselect();
            else
                drawer.drawNewState(x, y, "State");

        }
        else if(source instanceof StateCircle){

            StateCircle circle = (StateCircle) source;
            if(selected == null)
                select(circle);
            else if(selected instanceof StateCircle){
                drawer.drawNewTransition((StateCircle) selected, circle);
                unselect();
            }
            else {
                unselect();
            }
            mouseEvent.consume();

        }
        else if(source instanceof TransitionArrowInvisibleLine){
            TransitionArrow transitionArrow = ((TransitionArrowInvisibleLine) source).transitionArrow;
            if(selected == null){
                select(transitionArrow);
            }
            else
                unselect();

            mouseEvent.consume();

        }

    }

    public void handleDragEvent(MouseEvent mouseEvent) {
        if(mouseEvent.isStillSincePress())
            return;

        Object source = mouseEvent.getSource();
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        if(source instanceof TransitionArrowControl1KeyCircle){
            TransitionArrow transitionArrow = ((TransitionArrowControl1KeyCircle) source).transitionArrow;
            transitionArrow.setControl1(x, y);
            mouseEvent.consume();
        }
        else if(source instanceof TransitionArrowControl2KeyCircle){
            TransitionArrow transitionArrow = ((TransitionArrowControl2KeyCircle) source).transitionArrow;
            transitionArrow.setControl2(x, y);
            mouseEvent.consume();
        }
        else if(source instanceof StateCircle) {
            StateCircle circle = (StateCircle) source;
            drawer.moveState(circle, x, y);
            mouseEvent.consume();
        }
    }

    private void select(Node node) {
        selected = node;

        if(node instanceof TransitionArrow)
            ((TransitionArrow) node).setKeysVisible(true);
        else if(node instanceof Circle)
            ((Circle) node).setFill(TuringMachineDrawer.SELECTED_STATE_COLOR);
    }

    private void unselect() {

        if(selected instanceof TransitionArrow)
            ((TransitionArrow) selected).setKeysVisible(false);
        else if(selected instanceof Circle)
            ((Circle) selected).setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        selected = null;
    }


}
