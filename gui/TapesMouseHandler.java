package gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Created by dimitri.watel on 04/06/18.
 */
public class TapesMouseHandler implements EventHandler<Event> {

    private TuringMachineDrawer drawer;

    private Double dragX;
    private Double dragY;

    public TapesMouseHandler(TuringMachineDrawer drawer) {
        this.drawer = drawer;
    }

    @Override
    public void handle(Event event) {
        if(drawer.animating)
            return;

        System.out.println(event.getEventType()+" "+event.getSource().getClass());

        if(event.getEventType() == MouseEvent.MOUSE_CLICKED)
            this.handleClickedEvent((MouseEvent) event);
        else if(event.getEventType() == MouseEvent.MOUSE_DRAGGED)
            this.handleDragEvent((MouseEvent) event);
    }

    private void handleClickedEvent(MouseEvent mouseEvent) {
        if(!mouseEvent.isStillSincePress()) {
            if(dragX != null){
                dragX = null;
                dragY = null;
            }
            return;
        }
    }

    private void handleDragEvent(MouseEvent mouseEvent) {
        if(mouseEvent.isStillSincePress())
            return;

        Object source = mouseEvent.getSource();
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        if(source instanceof TapePane){
            if(dragX == null){
                dragX = x;
                dragY = y;
            }
            else {
                TapePane pane = (TapePane) source;
                pane.translate(x - dragX, y - dragY);
                dragX = x;
                dragY = y;
            }
            mouseEvent.consume();
        }
    }
}
