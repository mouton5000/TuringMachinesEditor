/*
 * Copyright (c) 2018 Dimitri Watel
 */

package gui;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

class TuringMenuKeyHandler implements EventHandler<KeyEvent> {

    TuringMenuKeyHandler(){
        super();
    }


    @Override
    public void handle(KeyEvent keyEvent) {
        if(TuringMachineDrawer.getInstance().animating)
            return;
        if(TuringMachineDrawer.getInstance().buildMode)
            return;
        if(TuringMachineDrawer.getInstance().manualMode)
            return;
        if(TuringMachineDrawer.getInstance().playing)
            return;
        if(!keyEvent.isControlDown())
            return;

        if(keyEvent.getCode() == KeyCode.N)
            TuringMachineDrawer.getInstance().newMachine();
        else if(keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.S)
            TuringMachineDrawer.getInstance().saveAsMachine();
        else if(keyEvent.getCode() == KeyCode.S)
            TuringMachineDrawer.getInstance().saveMachine();
        else if(keyEvent.getCode() == KeyCode.O)
            TuringMachineDrawer.getInstance().loadMachine();


    }
}
