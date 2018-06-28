package gui;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Created by dimitri.watel on 22/06/18.
 */
public class TuringMenuMouseHandler implements EventHandler<MouseEvent> {

    TuringMachineDrawer drawer;
    TuringMenuMouseHandler(TuringMachineDrawer drawer){
        this.drawer = drawer;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        if(drawer.animating)
            return;

        if(mouseEvent.getEventType() != MouseEvent.MOUSE_CLICKED)
            return;

        Object source = mouseEvent.getSource();

        if(!drawer.playing && !drawer.manualMode && !drawer.editGraphMode && source instanceof BuildIcon){
            if(drawer.buildMode)
                drawer.unbuild();
            else
                drawer.build();
        }
        else if(!drawer.playing && !drawer.buildMode && !drawer.editGraphMode && source instanceof ManualIcon){
            if(drawer.manualMode)
                drawer.setNotManual();
            else
                drawer.setManual();
        }
        else if(!drawer.playing && !drawer.buildMode &&!drawer.manualMode && source instanceof EditGraphIcon){
            if(drawer.editGraphMode)
                drawer.setNotEditGraph();
            else
                drawer.setEditGraph();
        }
        else if(!drawer.playing && !drawer.manualMode && source instanceof ParametersIcon){
            drawer.openParameters();
        }
        else if(!drawer.playing && source instanceof StopIcon){
            drawer.goToFirstConfiguration();
        }
        if(!drawer.playing && source instanceof PlayIcon){
            drawer.menu.setPause();
            drawer.play();
        }
        else if(source instanceof PauseIcon){
            drawer.menu.setPlay();
            drawer.pause();
        }
        else if(!drawer.playing && source instanceof PreviousFrameIcon){
            drawer.goToPreviousConfiguration();
        }
        else if(!drawer.playing && source instanceof NextFrameIcon){
            drawer.tick();
        }
        else if(!drawer.playing && source instanceof LastFrameIcon){
            drawer.goToLastConfiguration();
        }
        else if(!drawer.playing && !drawer.manualMode && source instanceof NewFileIcon){
            drawer.newMachine();
        }
        else if(!drawer.playing && !drawer.manualMode && source instanceof SaveFileIcon){
            drawer.saveMachine();
        }
        else if(!drawer.playing && !drawer.manualMode && source instanceof SaveAsFileIcon){
            drawer.saveAsMachine();
        }
        else if(!drawer.playing && !drawer.manualMode && source instanceof OpenFileIcon){
            drawer.loadMachine();
        }
        else if(source instanceof ShowIcon){
            drawer.menu.showMenu();
        }
        else if(source instanceof HideIcon){
            drawer.menu.hideMenu();
        }


        mouseEvent.consume();
    }
}
