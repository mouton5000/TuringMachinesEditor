package gui;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import util.MouseListener;
import util.Ressources;
import util.widget.VirtualKeyboard;

import java.util.Optional;

class SymbolOptionRectangle extends OptionRectangle {

    SymbolsMenu symbolsMenu;
    int currentSymbolIndex;

    SymbolOptionRectangle(SymbolsMenu symbolsMenu) {
        super();
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

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

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

class EditSymbolIcon extends ImageView implements MouseListener {
    SymbolOptionRectangle optionRectangle;

    EditSymbolIcon(SymbolOptionRectangle optionRectangle){
        super(Ressources.getRessource("cursor_icon.png"));
        this.optionRectangle = optionRectangle;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        VirtualKeyboard virtualKeyboard = new VirtualKeyboard();
        virtualKeyboard.setX(mouseEvent.getScreenX() - virtualKeyboard.getWidth() / 2);
        virtualKeyboard.setY(mouseEvent.getScreenY());

        Optional<String> result = virtualKeyboard.showAndWait();
        if(result.isPresent())
            TuringMachineDrawer.getInstance().editSymbol(this.optionRectangle.currentSymbolIndex, result.get());

        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

class RemoveSymbolIcon extends ImageView implements MouseListener {
    SymbolOptionRectangle optionRectangle;

    RemoveSymbolIcon(SymbolOptionRectangle optionRectangle){
        super(Ressources.getRessource("remove_symbol.png"));
        this.optionRectangle = optionRectangle;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TuringMachineDrawer.getInstance().removeSymbol(this.optionRectangle.currentSymbolIndex);
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}