package gui;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

class SymbolOptionRectangle extends OptionRectangle {

    SymbolsMenu symbolsMenu;
    int currentSymbolIndex;

    SymbolOptionRectangle(SymbolsMenu symbolsMenu) {
        super(TuringMachineDrawer.getInstance().tapesMouseHandler);
        this.symbolsMenu = symbolsMenu;

        EditSymbolIcon editSymbolIcon = new EditSymbolIcon(this);
        RemoveSymbolIcon removeSymbolIcon = new RemoveSymbolIcon(this);

        editSymbolIcon.setLayoutX(
                TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_WIDTH
                        + TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING
                        + TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE / 2
                        - editSymbolIcon.getBoundsInLocal().getWidth() / 2
        );

        editSymbolIcon.setLayoutY(
                - editSymbolIcon.getBoundsInLocal().getHeight() / 2
        );


        removeSymbolIcon.setLayoutX(
                TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_WIDTH
                        + TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SIZE * 3 / 2
                        + TuringMachineDrawer.OPTION_RECTANGLE_SYMBOL_SPACING * 3
                        - removeSymbolIcon.getBoundsInLocal().getWidth() / 2
        );
        removeSymbolIcon.setLayoutY(
                - removeSymbolIcon.getBoundsInLocal().getHeight() / 2
        );

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().tapesMouseHandler);

        this.getChildren().addAll(editSymbolIcon, removeSymbolIcon);
    }

    @Override
    protected double getMaximizedHeight() {
        return TuringMachineDrawer.TAPES_MENU_HEIGHT - 2;
    }
    @Override
    protected double getMaximizedWidth() {
        return  TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_WIDTH
                + TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SIZE * 2
                + TuringMachineDrawer.OPTION_RECTANGLE_HEAD_SPACING * 3;
    }

    @Override
    protected double getOffsetY() {
        return - TuringMachineDrawer.TAPES_MENU_HEIGHT / 2 + 1;
    }

    @Override
    protected double getOffsetX() {
        return -TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_WIDTH / 2;
    }

    @Override
    protected Node associatedNode() {
        return null;
    }

    void setSymbolIndex(int currentSymbolIndex) {
        this.currentSymbolIndex = currentSymbolIndex;
    }

    @Override
    public void clear() {
        currentSymbolIndex = 0;
    }
}

class EditSymbolIcon extends ImageView {
    SymbolOptionRectangle optionRectangle;

    EditSymbolIcon(SymbolOptionRectangle optionRectangle){
        super("./images/cursor_icon.png");
        this.optionRectangle = optionRectangle;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().tapesMouseHandler);
    }
}

class RemoveSymbolIcon extends ImageView {
    SymbolOptionRectangle optionRectangle;

    RemoveSymbolIcon(SymbolOptionRectangle optionRectangle){
        super("./images/remove_symbol.png");
        this.optionRectangle = optionRectangle;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().tapesMouseHandler);
    }
}