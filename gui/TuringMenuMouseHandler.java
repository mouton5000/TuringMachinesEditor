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

        if(!drawer.playing && source instanceof BuildIcon){
            if(drawer.buildMode)
                drawer.reinitMachine();
            else
                drawer.build();
        }
        else if(!drawer.playing && source instanceof ParametersIcon){
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
        else if(!drawer.playing && source instanceof OneFrameIcon){
            drawer.tick();
        }
        else if(!drawer.playing && source instanceof LastFrameIcon){
            drawer.goToLastConfiguration();
        }
        else if(!drawer.playing && source instanceof NewFileIcon){
            drawer.newMachine();
        }
        else if(!drawer.playing && source instanceof SaveFileIcon){
            drawer.saveMachine();
        }
        else if(!drawer.playing && source instanceof SaveAsFileIcon){
            drawer.saveAsMachine();
        }
        else if(!drawer.playing && source instanceof OpenFileIcon){
            drawer.loadMachine();
        }


        mouseEvent.consume();
    }
}
