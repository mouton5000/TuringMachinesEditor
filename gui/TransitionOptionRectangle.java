package gui;

import javafx.scene.Node;

/**
 * Created by dimitri.watel on 06/06/18.
 */
public class TransitionOptionRectangle extends OptionRectangle {

    TransitionArrowGroup currentTransitionArrowGroup;
    GraphPane graphPane;

    public TransitionOptionRectangle(TuringMachineDrawer drawer, GraphPane graphPane) {
        super(drawer, drawer.graphPaneMouseHandler);
        this.graphPane = graphPane;
        this.setOnMouseClicked(drawer.graphPaneMouseHandler);
    }

    public void setCurrentTransitionArrowGroup(TransitionArrowGroup transitionArrowGroup) {
        this.currentTransitionArrowGroup = transitionArrowGroup;
    }

    @Override
    protected Node associatedNode() {
        return graphPane;
    }
}
