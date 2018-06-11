package gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

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

//        System.out.println(event.getEventType()+" "+event.getClass()+" "+event.getSource().getClass());

        if(event.getEventType() == MouseEvent.MOUSE_CLICKED)
            this.handleClickedEvent((MouseEvent) event);
        else if(event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            this.handlePressEvent((MouseEvent) event);
        }
        else if(event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            this.handleDragEvent((MouseEvent) event);
        }
    }

    private void handleClickedEvent(MouseEvent mouseEvent){
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        Object source = mouseEvent.getSource();
        if(source instanceof TapePane){
            TapePane tapePane = (TapePane) source;
            Integer line = tapePane.getLine(y);
            Integer column = tapePane.getColumn(x);
            if(line != null && column != null)
                tapePane.openCellOptionRectangle(line, column);
            mouseEvent.consume();
        }
    }

    private void handlePressEvent(MouseEvent mouseEvent) {
        dragX = mouseEvent.getX();
        dragY = mouseEvent.getY();
        mouseEvent.consume();
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
