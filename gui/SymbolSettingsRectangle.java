/*
 * Copyright (c) 2018 Dimitri Watel
 */

package gui;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import util.MouseListener;
import util.Ressources;
import util.widget.VirtualKeyboard;

import java.util.Optional;

class SymbolSettingsRectangle extends SettingsRectangle {

    SymbolsMenu symbolsMenu;
    int currentSymbolIndex;

    SymbolSettingsRectangle(SymbolsMenu symbolsMenu) {
        super();
        this.symbolsMenu = symbolsMenu;

        EditSymbolIcon editSymbolIcon = new EditSymbolIcon(this);
        RemoveSymbolIcon removeSymbolIcon = new RemoveSymbolIcon(this);

        editSymbolIcon.setLayoutX(
                TuringMachineDrawer.SETTING_RECTANGLE_MINIMIZED_WIDTH
                        + TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING
                        + TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE / 2
                        - editSymbolIcon.getBoundsInLocal().getWidth() / 2
        );

        editSymbolIcon.setLayoutY(
                - editSymbolIcon.getBoundsInLocal().getHeight() / 2
        );


        removeSymbolIcon.setLayoutX(
                TuringMachineDrawer.SETTING_RECTANGLE_MINIMIZED_WIDTH
                        + TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SIZE * 3 / 2
                        + TuringMachineDrawer.SETTINGS_RECTANGLE_SYMBOL_SPACING * 3
                        - removeSymbolIcon.getBoundsInLocal().getWidth() / 2
        );
        removeSymbolIcon.setLayoutY(
                - removeSymbolIcon.getBoundsInLocal().getHeight() / 2
        );

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        this.getChildren().addAll(editSymbolIcon, removeSymbolIcon);
    }

    @Override
    double getMaximizedHeight() {
        return TuringMachineDrawer.TAPES_MENU_HEIGHT - 2;
    }
    @Override
    double getMaximizedWidth() {
        return  TuringMachineDrawer.SETTING_RECTANGLE_MINIMIZED_WIDTH
                + TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SIZE * 2
                + TuringMachineDrawer.SETTINGS_RECTANGLE_HEAD_SPACING * 3;
    }

    @Override
    double getOffsetY() {
        return - TuringMachineDrawer.TAPES_MENU_HEIGHT / 2 + 1;
    }

    @Override
    double getOffsetX() {
        return -TuringMachineDrawer.SETTING_RECTANGLE_MINIMIZED_WIDTH / 2;
    }

    void setSymbolIndex(int currentSymbolIndex) {
        this.currentSymbolIndex = currentSymbolIndex;
    }

    @Override
    void clear() {
        currentSymbolIndex = 0;
    }
}

class EditSymbolIcon extends ImageView implements MouseListener {
    SymbolSettingsRectangle settingsRectangle;

    EditSymbolIcon(SymbolSettingsRectangle settingsRectangle){
        super(Ressources.getRessource("cursor_icon.png"));
        this.settingsRectangle = settingsRectangle;

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
            TuringMachineDrawer.getInstance().editSymbol(this.settingsRectangle.currentSymbolIndex, result.get());

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
    SymbolSettingsRectangle settingsRectangle;

    RemoveSymbolIcon(SymbolSettingsRectangle settingsRectangle){
        super(Ressources.getRessource("remove_symbol.png"));
        this.settingsRectangle = settingsRectangle;

        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        TuringMachineDrawer.getInstance().removeSymbol(this.settingsRectangle.currentSymbolIndex);
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