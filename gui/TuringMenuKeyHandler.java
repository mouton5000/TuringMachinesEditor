package gui;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TuringMenuKeyHandler implements EventHandler<KeyEvent> {
    TuringMachineDrawer drawer;

    TuringMenuKeyHandler(TuringMachineDrawer drawer){
        super();
        this.drawer = drawer;
    }


    @Override
    public void handle(KeyEvent keyEvent) {
        if(drawer.animating)
            return;

        if(!keyEvent.isControlDown())
            return;

        if(keyEvent.getCode() == KeyCode.N)
            this.drawer.newMachine();
        else if(keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.S)
            this.drawer.saveAsMachine();
        else if(keyEvent.getCode() == KeyCode.S)
            this.drawer.saveMachine();
        else if(keyEvent.getCode() == KeyCode.O)
            this.drawer.loadMachine();


    }
}
