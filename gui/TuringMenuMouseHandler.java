package gui;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Created by dimitri.watel on 22/06/18.
 */
public class TuringMenuMouseHandler implements EventHandler<MouseEvent> {

    TuringMenuMouseHandler(){ }

    @Override
    public void handle(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().animating)
            return;

        if(mouseEvent.getEventType() != MouseEvent.MOUSE_CLICKED)
            return;

        Object source = mouseEvent.getSource();

        if(!TuringMachineDrawer.getInstance().playing && source instanceof BuildIcon){
            if(TuringMachineDrawer.getInstance().buildMode)
                TuringMachineDrawer.getInstance().unbuild();
            else
                TuringMachineDrawer.getInstance().build();
        }
        else if(!TuringMachineDrawer.getInstance().playing && source instanceof ManualIcon){
            if(TuringMachineDrawer.getInstance().manualMode)
                TuringMachineDrawer.getInstance().setNotManual();
            else
                TuringMachineDrawer.getInstance().setManual();
        }
        else if(!TuringMachineDrawer.getInstance().playing && source instanceof EditGraphIcon){
            if(TuringMachineDrawer.getInstance().editGraphMode)
                TuringMachineDrawer.getInstance().setNotEditGraph();
            else
                TuringMachineDrawer.getInstance().setEditGraph();
        }
        else if(!TuringMachineDrawer.getInstance().playing && source instanceof ParametersIcon){
            TuringMachineDrawer.getInstance().openParameters();
        }
        else if(!TuringMachineDrawer.getInstance().playing && source instanceof StopIcon){
            TuringMachineDrawer.getInstance().goToFirstConfiguration();
        }
        if(!TuringMachineDrawer.getInstance().playing && source instanceof PlayIcon){
            TuringMachineDrawer.getInstance().menu.setPause();
            TuringMachineDrawer.getInstance().play();
        }
        else if(source instanceof PauseIcon){
            TuringMachineDrawer.getInstance().menu.setPlay();
            TuringMachineDrawer.getInstance().pause();
        }
        else if(!TuringMachineDrawer.getInstance().playing && source instanceof PreviousFrameIcon){
            TuringMachineDrawer.getInstance().goToPreviousConfiguration();
        }
        else if(!TuringMachineDrawer.getInstance().playing && source instanceof NextFrameIcon){
            TuringMachineDrawer.getInstance().tick();
        }
        else if(!TuringMachineDrawer.getInstance().playing && source instanceof LastFrameIcon){
            TuringMachineDrawer.getInstance().goToLastConfiguration();
        }
        else if(!TuringMachineDrawer.getInstance().playing && source instanceof NewFileIcon){
            TuringMachineDrawer.getInstance().newMachine();
        }
        else if(!TuringMachineDrawer.getInstance().playing && source instanceof SaveFileIcon){
            TuringMachineDrawer.getInstance().saveMachine();
        }
        else if(!TuringMachineDrawer.getInstance().playing && source instanceof SaveAsFileIcon){
            TuringMachineDrawer.getInstance().saveAsMachine();
        }
        else if(!TuringMachineDrawer.getInstance().playing && source instanceof OpenFileIcon){
            TuringMachineDrawer.getInstance().loadMachine();
        }
        else if(!TuringMachineDrawer.getInstance().playing && source instanceof HelpIcon){
            TuringMachineDrawer.getInstance().help.setVisible(true);
        }
        else if(source instanceof ShowIcon){
            TuringMachineDrawer.getInstance().menu.showMenu();
        }
        else if(source instanceof HideIcon){
            TuringMachineDrawer.getInstance().menu.hideMenu();
        }


        mouseEvent.consume();
    }
}
