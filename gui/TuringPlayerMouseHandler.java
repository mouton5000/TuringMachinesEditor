package gui;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Created by dimitri.watel on 22/06/18.
 */
public class TuringPlayerMouseHandler implements EventHandler<MouseEvent> {

    TuringMachineDrawer drawer;
    TuringPlayerMouseHandler(TuringMachineDrawer drawer){
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
            if(drawer.buildMode) {
                drawer.player.buildIcon.setSelected();
                drawer.player.hidePlayer();
                drawer.reinitMachine();
            }
            else{
                drawer.player.buildIcon.setUnselected();
                drawer.player.showPlayer();
                drawer.build();
            }
        }
        else if(!drawer.playing && source instanceof StopIcon){
            drawer.goToFirstConfiguration();
        }
        if(!drawer.playing && source instanceof PlayIcon){
            drawer.player.setPause();
            drawer.play();
        }
        else if(source instanceof PauseIcon){
            drawer.player.setPlay();
            drawer.pause();
        }
        else if(!drawer.playing && source instanceof OneFrameIcon){
            drawer.tick();
        }
        else if(!drawer.playing && source instanceof LastFrameIcon){
            drawer.goToLastConfiguration();
        }


        mouseEvent.consume();
    }
}
