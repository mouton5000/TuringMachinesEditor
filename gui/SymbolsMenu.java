package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.json.JSONArray;
import util.MouseListener;
import util.Ressources;
import util.widget.VirtualKeyboard;

import java.util.Optional;

class SymbolsMenu extends HBox implements MouseListener {

    SymbolSettingsRectangle symbolSettingsRectangle;
    AddSymbolIcon addSymbolIcon;
    private double offsetX;
    private Double dragX;

    SymbolsMenu(){
        this.symbolSettingsRectangle = new SymbolSettingsRectangle(this);
        this.addSymbolIcon = new AddSymbolIcon();
        this.offsetX = 0;

        this.setMinHeight(TuringMachineDrawer.TAPES_MENU_HEIGHT);
        this.setMaxHeight(TuringMachineDrawer.TAPES_MENU_HEIGHT);
        this.setSpacing(TuringMachineDrawer.TAPES_MENU_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);

        symbolSettingsRectangle.managedProperty().bind(symbolSettingsRectangle.visibleProperty());
        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);

        this.getChildren().addAll(addSymbolIcon);
    }

    void addSymbol(String symbol){
        SymbolLabel label = new SymbolLabel(this, symbol);
        this.getChildren().add(label);
    }

    void editSymbol(int index, String newSymbol){
        this.getSymbolLabel(index).setText(newSymbol);
    }

    void removeSymbol(int index){
        closeSymbolSettingsRectangle();
        this.getChildren().remove(getSymbolLabel(index));
    }

    private SymbolLabel getSymbolLabel(int index){
        int current = 0;
        for(Node child : this.getChildren()){
            if(!(child instanceof SymbolLabel))
                continue;

            if(current == index)
                return (SymbolLabel) child;
            else
                current++;
        }
        return null;
    }

    void openSymbolOptionRectantle(int index){
        symbolSettingsRectangle.setSymbolIndex(index);
        symbolSettingsRectangle.setVisible(true);
        this.getChildren().remove(symbolSettingsRectangle);
        this.getChildren().add(index + 2, symbolSettingsRectangle);
        symbolSettingsRectangle.maximize();
    }

    void closeSymbolSettingsRectangle(){
        closeSymbolSettingsRectangle(true);
    }
    void closeSymbolSettingsRectangle(boolean animate){
        symbolSettingsRectangle.minimize(animate);
    }

    int getIndex(SymbolLabel symbolLabel){
        int index = 0;
        for(Node child : this.getChildren()){
            if(!(child instanceof  SymbolLabel))
                continue;
            if(child == symbolLabel)
                return index;
            else
                index++;
        }
        return -1;
    }

    void translate(double dx){
        if(dx > offsetX)
            dx = offsetX;

        if(dx == 0)
            return;

        offsetX -= dx;
        for(Node child: this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    void closeAllSettingsRectangle() { closeSymbolSettingsRectangle(false); }

    JSONArray getJSON() {
        JSONArray jsonArray = new JSONArray();
        for(Node child : this.getChildren()){
            if(!(child instanceof  SymbolLabel))
                continue;
            jsonArray.put(((SymbolLabel)child).getText());
        }
        return jsonArray;
    }

    void clear() {
        closeSymbolSettingsRectangle();
        symbolSettingsRectangle.clear();
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(this.symbolSettingsRectangle.isMaximized())
            this.closeSymbolSettingsRectangle();
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        if(dragX == null)
            dragX = x;
        else {
            this.translate(x - dragX);
            dragX = x;
        }
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }
}

class AddSymbolIcon extends ImageView implements MouseListener{

    AddSymbolIcon() {
        super(Ressources.getRessource("add_symbol.png"));
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
        if(result.isPresent()){
            TuringMachineDrawer.getInstance().addSymbol(result.get());
        }
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

class SymbolLabel extends Group implements MouseListener {

    SymbolsMenu symbolsMenu;
    boolean animating;
    private Timeline timeline;

    private Rectangle backgroundRectangle;
    private Label symbolLabel;

    SymbolLabel(SymbolsMenu symbolsMenu, String symbol) {
        this.symbolsMenu = symbolsMenu;
        this.animating = false;

        this.timeline = new Timeline();
        this.timeline.setOnFinished(actionEvent -> animating = false);

        symbolLabel = new Label();
        symbolLabel.setText(symbol);

        symbolLabel.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
                TuringMachineDrawer.TAPE_MENU_SYMBOL_FONT_SIZE));

        symbolLabel.setMinWidth(TuringMachineDrawer.TAPE_MENU_SYMBOL_SIZE);
        symbolLabel.setMaxWidth(TuringMachineDrawer.TAPE_MENU_SYMBOL_SIZE);
        symbolLabel.setMinHeight(TuringMachineDrawer.TAPE_MENU_SYMBOL_SIZE);
        symbolLabel.setMaxHeight(TuringMachineDrawer.TAPE_MENU_SYMBOL_SIZE);
        symbolLabel.setAlignment(Pos.CENTER);

        backgroundRectangle = new Rectangle(
                0, 0,
                TuringMachineDrawer.TAPE_MENU_SYMBOL_SIZE,
                TuringMachineDrawer.TAPE_MENU_SYMBOL_SIZE);
        backgroundRectangle.setStroke(TuringMachineDrawer.TAPE_MENU_DEFAULT_FILL_COLOR);
        backgroundRectangle.setFill(TuringMachineDrawer.TAPE_MENU_DEFAULT_FILL_COLOR);

        this.getChildren().addAll(backgroundRectangle, symbolLabel);

        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);
    }

    String getText(){ return symbolLabel.getText(); }
    void setText(String symbol){
        symbolLabel.setText(symbol);
    }


    void startTimeline(){
        animating = true;
        timeline.getKeyFrames().clear();
        KeyValue kbg = new KeyValue(
                backgroundRectangle.fillProperty(),
                TuringMachineDrawer.EDIT_PRESS_COLOR,
                Interpolator.EASE_BOTH);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(TuringMachineDrawer.SETTINGS_PRESS_DURATION), kbg)
        );
        timeline.play();
    }

    void stopTimeline(){
        timeline.stop();
        backgroundRectangle.setFill(TuringMachineDrawer.TAPE_MENU_DEFAULT_FILL_COLOR);
        animating = false;
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(this.symbolsMenu.symbolSettingsRectangle.isMaximized())
            this.symbolsMenu.closeSymbolSettingsRectangle();
        else {
            boolean pressFinished = !this.animating;
            this.stopTimeline();

            if (pressFinished)
                this.symbolsMenu.openSymbolOptionRectantle(this.symbolsMenu.getIndex(this));
        }
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        this.stopTimeline();
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(!this.symbolsMenu.symbolSettingsRectangle.isMaximized())
            this.startTimeline();

        return false;
    }
}
