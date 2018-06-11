package gui;

import javafx.scene.Node;

/**
 * Created by dimitri.watel on 11/06/18.
 */
public class CellOptionRectangle extends OptionRectangle{

    CellOptionRectangle(TuringMachineDrawer drawer) {
        super(drawer);
    }

    @Override
    protected Node associatedNode() {
        return null;
    }
}
