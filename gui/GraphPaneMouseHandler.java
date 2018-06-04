package gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * Created by dimitri.watel on 04/06/18.
 */
public class GraphPaneMouseHandler implements EventHandler<Event> {

    private boolean cancelNextClick;
    private TuringMachineDrawer drawer;

    private Circle selected;

    public GraphPaneMouseHandler(TuringMachineDrawer drawer) {
        this.drawer = drawer;
        this.cancelNextClick = false;
        selected = null;
    }

    @Override
    public void handle(Event event) {
        System.out.println(event.getEventType()+" "+event.getSource());

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
                selected = null;
            else
                drawer.drawNewState(x, y, "State");
        }
        else if(source instanceof Circle){
            Circle circle = (Circle) source;
            if(selected == null)
                selected = circle;
            else{
                drawer.drawNewTransition(selected, circle);
                selected = null;
            }
            mouseEvent.consume();
        }
        else if(source instanceof TransitionArrow){
            mouseEvent.consume();
        }
    }

    public void handleDragEvent(MouseEvent mouseEvent) {
        if(mouseEvent.isStillSincePress())
            return;

        cancelNextClick = true;
        Object source = mouseEvent.getSource();
        int x = (int) mouseEvent.getX();
        int y = (int) mouseEvent.getY();

        if(source instanceof Circle) {
            Circle circle = (Circle) source;
            circle.setCenterX(x);
            circle.setCenterY(y);
            mouseEvent.consume();
        }
    }


}
