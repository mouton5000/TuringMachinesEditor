package gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import util.MouseListener;

/**
 * Created by dimitri.watel on 04/06/18.
 */
class MouseHandler implements EventHandler<Event> {

    @Override
    public void handle(Event event) {
        if(TuringMachineDrawer.getInstance().animating)
            return;

        boolean consume = false;

        if(event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            MouseEvent mouseEvent = (MouseEvent)event;
            if(!mouseEvent.isStillSincePress())
                return;
            Object source = mouseEvent.getSource();
            if(source instanceof MouseListener)
                consume = ((MouseListener) source).onMouseClicked(mouseEvent);
        }
        else if(event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            MouseEvent mouseEvent = (MouseEvent)event;
            if(mouseEvent.isStillSincePress())
                return;
            Object source = mouseEvent.getSource();
            if(source instanceof MouseListener)
                consume = ((MouseListener) source).onMouseDragged(mouseEvent);
        }
        else if(event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            MouseEvent mouseEvent = (MouseEvent)event;
            Object source = mouseEvent.getSource();
            if(source instanceof MouseListener)
                consume = ((MouseListener) source).onMousePressed(mouseEvent);
        }

        if(consume)
            event.consume();
    }

}
