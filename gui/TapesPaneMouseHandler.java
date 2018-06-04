package gui;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Created by dimitri.watel on 04/06/18.
 */
public class TapesPaneMouseHandler implements EventHandler<MouseEvent> {

    private TuringMachineDrawer drawer;

    public TapesPaneMouseHandler(TuringMachineDrawer drawer) {
        this.drawer = drawer;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        int x = (int) mouseEvent.getX();
        int y = (int) mouseEvent.getY();

        System.out.println("Tape " + x+" "+y);
    }
}
