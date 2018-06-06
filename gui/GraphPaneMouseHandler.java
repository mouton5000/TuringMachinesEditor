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

    public GraphPaneMouseHandler(TuringMachineDrawer drawer) {
        this.drawer = drawer;
        selected = null;
    }

    @Override
    public void handle(Event event) {

        System.out.println(event.getEventType()+" "+event.getSource().getClass());

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
                drawer.drawNewState(x, y);

        }
        else if(source instanceof StateCircle){

            StateGroup circle = ((StateCircle) source).stateGroup;
            if(selected == null)
                select(circle);
            else if(selected instanceof StateGroup){
                drawer.drawNewTransition((StateGroup) selected, circle);
                unselect();
            }
            else {
                unselect();
            }
            mouseEvent.consume();

        }
        else if(source instanceof StateOptionRectangle){
            StateOptionRectangle stateOptionRectangle = (StateOptionRectangle) source;
            if(selected == null) {
                stateOptionRectangle.maximize();
            }
            else if(selected == source){

            }
            else {
                stateOptionRectangle.maximize();
                unselect();
                select(stateOptionRectangle);
            }

            mouseEvent.consume();
        }
        else if(source instanceof TransitionArrowInvisibleLine){
            TransitionArrowGroup transitionArrowGroup = ((TransitionArrowInvisibleLine) source).transitionArrowGroup;
            if(selected == null){
                select(transitionArrowGroup);
            }
            else
                unselect();

            mouseEvent.consume();

        }
        else if(source instanceof TransitionOptionRectangle){
            ((TransitionOptionRectangle) source).maximize();
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
            TransitionArrowGroup transitionArrowGroup = ((TransitionArrowControl1KeyCircle) source).transitionArrowGroup;
            transitionArrowGroup.setControl1(x, y);
            mouseEvent.consume();
        }
        else if(source instanceof TransitionArrowControl2KeyCircle){
            TransitionArrowGroup transitionArrowGroup = ((TransitionArrowControl2KeyCircle) source).transitionArrowGroup;
            transitionArrowGroup.setControl2(x, y);
            mouseEvent.consume();
        }
        else if(source instanceof StateCircle) {
            unselect();
            StateGroup circle = ((StateCircle) source).stateGroup;
            drawer.moveStateGroup(circle, x, y);
            mouseEvent.consume();
        }
    }

    private void select(Node node) {
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
            ((TransitionArrowGroup) selected).setSelected(false);
            selected = null;
        }
        else if(selected instanceof StateGroup){
            ((StateGroup) selected).setUnselected();
            selected = null;
        }
        else if(selected instanceof StateOptionRectangle){
            StateOptionRectangle stateOptionRectangle = (StateOptionRectangle) selected;
            stateOptionRectangle.minimize(true);
            select(stateOptionRectangle.stateGroup);
        }
    }


}
