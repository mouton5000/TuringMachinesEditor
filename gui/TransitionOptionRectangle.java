package gui;

import javafx.scene.Node;

/**
 * Created by dimitri.watel on 06/06/18.
 */
public class TransitionOptionRectangle extends OptionRectangle {

    TransitionArrowGroup transitionArrowGroup;

    public TransitionOptionRectangle(TuringMachineDrawer drawer, TransitionArrowGroup transitionArrowGroup) {
        super(drawer, drawer.graphPaneMouseHandler);
        this.transitionArrowGroup = transitionArrowGroup;



    }

    @Override
    protected Node associatedNode() {
        return transitionArrowGroup;
    }
}
