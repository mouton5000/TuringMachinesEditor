package gui;

import javafx.scene.Node;

/**
 * Created by dimitri.watel on 11/06/18.
 */
public class CellOptionRectangle extends OptionRectangle{

    final TapePane tapePane;

    CellOptionRectangle(TuringMachineDrawer drawer, TapePane tapePane) {
        super(drawer, drawer.tapesMouseHandler);
        this.tapePane = tapePane;
    }

    @Override
    protected Node associatedNode() {
        return tapePane;
    }
}
