package gui;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by dimitri.watel on 06/06/18.
 */
class TransitionOptionRectangle extends OptionRectangle {

    private final ReadIcon readIcon;
    private final ActionsIcon actionsIcon;
    private boolean readMenuSelected;
    TransitionArrowGroup currentTransitionArrowGroup;
    GraphPane graphPane;

    private ReadMenu readMenu;
    private ActionsMenu actionsMenu;

    private VBox vbox;
    private Rectangle ReadBackgroundColor;
    private Rectangle ActionsBackgroupeColor;

    TransitionOptionRectangle(TuringMachineDrawer drawer, GraphPane graphPane) {
        super(drawer, drawer.graphPaneMouseHandler);
        this.graphPane = graphPane;
        this.setOnMouseClicked(drawer.graphPaneMouseHandler);

        vbox = new VBox();

        HBox iconsHBox = new HBox();
        iconsHBox.setMinWidth(getMaximizedWidth());
        iconsHBox.setMaxWidth(getMaximizedWidth());
        iconsHBox.setAlignment(Pos.CENTER);

        readIcon = new ReadIcon(drawer, this);
        actionsIcon = new ActionsIcon(drawer, this);

        iconsHBox.setSpacing((getMaximizedWidth()
                - readIcon.getBoundsInLocal().getWidth()
                - actionsIcon.getBoundsInLocal().getWidth()) / 4);

        iconsHBox.getChildren().addAll(readIcon, actionsIcon);

        readMenu = new ReadMenu();
        actionsMenu = new ActionsMenu();


        vbox.getChildren().addAll(iconsHBox, new Separator(), readMenu);

        vbox.setLayoutX(- getMaximizedWidth() / 2);
        vbox.setLayoutY(TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
            - getMaximizedHeight());

        readMenuSelected = true;
        readIcon.setSelected(true);
        actionsIcon.setSelected(false);

        this.getChildren().add(vbox);

    }

    @Override
    protected double getMaximizedHeight(){
        return TuringMachineDrawer.TRANSITION_OPTION_RECTANGLE_MAXIMIZED_HEIGHT;
    }

    void setCurrentTransitionArrowGroup(TransitionArrowGroup transitionArrowGroup) {

        if(transitionArrowGroup == null && this.currentTransitionArrowGroup != null){
            this.layoutXProperty().unbind();
            this.layoutYProperty().unbind();
        }

        this.currentTransitionArrowGroup = transitionArrowGroup;

        if(transitionArrowGroup == null)
            return;

        this.layoutXProperty().bind(transitionArrowGroup.centerXProperty());
        this.layoutYProperty().bind(transitionArrowGroup.centerYProperty());
    }

    @Override
    protected Node associatedNode() {
        return graphPane;
    }

    void selectReadMenu(){
        if(readMenuSelected)
            return;
        readMenuSelected = true;
        vbox.getChildren().add(readMenu);
        readIcon.setSelected(true);
        vbox.getChildren().remove(actionsMenu);
        actionsIcon.setSelected(false);
    }

    void selectActionsMenu(){
        if(!readMenuSelected)
            return;
        readMenuSelected = false;
        readIcon.setSelected(false);
        vbox.getChildren().remove(readMenu);
        actionsIcon.setSelected(true);
        vbox.getChildren().add(actionsMenu);
    }
}

class ReadIcon extends Group{

    TuringMachineDrawer drawer;
    TransitionOptionRectangle optionRectangle;

    private Rectangle backgroundColor;

    ReadIcon(TuringMachineDrawer drawer, TransitionOptionRectangle optionRectangle) {

        ImageView readIcon = new ImageView("./images/read_tape_icon.png");
        this.drawer = drawer;
        this.optionRectangle = optionRectangle;

        readIcon.setLayoutX(-readIcon.getBoundsInLocal().getWidth() / 2);
        readIcon.setLayoutY(-readIcon.getBoundsInLocal().getHeight() / 2);

        backgroundColor = new Rectangle(
                - this.optionRectangle.getMaximizedWidth() / 4,
                - readIcon.getBoundsInLocal().getHeight() / 2,
                this.optionRectangle.getMaximizedWidth() / 2,
                readIcon.getBoundsInLocal().getHeight());
        backgroundColor.setFill(Color.GREEN);

        this.setOnMouseClicked(drawer.graphPaneMouseHandler);

        this.getChildren().addAll(backgroundColor, readIcon);
    }

    void setSelected(boolean selected){
        this.backgroundColor.setOpacity(selected?1:0);
    }
}

class ActionsIcon extends Group{

    TuringMachineDrawer drawer;
    TransitionOptionRectangle optionRectangle;
    private Rectangle backgroundColor;

    ActionsIcon(TuringMachineDrawer drawer, TransitionOptionRectangle optionRectangle) {

        ImageView actionsIcon = new ImageView("./images/action_tape_icon.png");
        this.drawer = drawer;
        this.optionRectangle = optionRectangle;

        actionsIcon.setLayoutX(-actionsIcon.getBoundsInLocal().getWidth() / 2);
        actionsIcon.setLayoutY(-actionsIcon.getBoundsInLocal().getHeight() / 2);

        backgroundColor = new Rectangle(
                - this.optionRectangle.getMaximizedWidth() / 4,
                - actionsIcon.getBoundsInLocal().getHeight() / 2,
                this.optionRectangle.getMaximizedWidth() / 2,
                actionsIcon.getBoundsInLocal().getHeight());
        backgroundColor.setFill(Color.GREEN);

        this.setOnMouseClicked(drawer.graphPaneMouseHandler);

        this.getChildren().addAll(backgroundColor, actionsIcon);
    }

    void setSelected(boolean selected){
        this.backgroundColor.setOpacity(selected?1:0);
    }
}



class ReadMenu extends Group {
    public ReadMenu() {
        this.getChildren().add(new Label("Read"));
    }
}

class ActionsMenu extends Group {
    public ActionsMenu() {
        this.getChildren().add(new Label("Actions"));
    }
}