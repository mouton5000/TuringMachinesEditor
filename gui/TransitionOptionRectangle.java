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

    private final ReadGroup readGroup;
    private final ActionsGroup actionsGroup;
    TransitionArrowGroup currentTransitionArrowGroup;
    GraphPane graphPane;

    private ReadMenu readMenu;
    private ActionsMenu actionsMenu;

    private Rectangle ReadBackgroundColor;
    private Rectangle ActionsBackgroupeColor;

    TransitionOptionRectangle(TuringMachineDrawer drawer, GraphPane graphPane) {
        super(drawer, drawer.graphPaneMouseHandler);
        this.graphPane = graphPane;
        this.setOnMouseClicked(drawer.graphPaneMouseHandler);

        VBox vbox = new VBox();

        HBox iconsHBox = new HBox();
        iconsHBox.setMinWidth(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH);
        iconsHBox.setMaxWidth(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH);
        iconsHBox.setAlignment(Pos.CENTER);

        readGroup = new ReadGroup(drawer, this);
        actionsGroup = new ActionsGroup(drawer, this);

        iconsHBox.setSpacing((TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH
                - readGroup.getBoundsInLocal().getWidth()
                - actionsGroup.getBoundsInLocal().getWidth()) / 4);

        iconsHBox.getChildren().addAll(readGroup, actionsGroup);

        readMenu = new ReadMenu();
        actionsMenu = new ActionsMenu();


        vbox.getChildren().addAll(iconsHBox, new Separator(), readMenu, actionsMenu);

        vbox.setLayoutX(- TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2);
        vbox.setLayoutY(TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
        - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_HEIGHT);


        this.selectReadMenu();

        this.getChildren().add(vbox);

    }

    void setCurrentTransitionArrowGroup(TransitionArrowGroup transitionArrowGroup) {
        this.currentTransitionArrowGroup = transitionArrowGroup;
    }

    @Override
    protected Node associatedNode() {
        return graphPane;
    }

    void selectReadMenu(){
        readGroup.setSelected(true);
        readMenu.setVisible(true);
        actionsGroup.setSelected(false);
        actionsMenu.setVisible(false);
    }

    void selectActionsMenu(){
        readGroup.setSelected(false);
        readMenu.setVisible(false);
        actionsGroup.setSelected(true);
        actionsMenu.setVisible(true);
    }
}

class ReadGroup extends Group{

    TuringMachineDrawer drawer;
    TransitionOptionRectangle optionRectangle;

    private Rectangle backgroundColor;

    ReadGroup(TuringMachineDrawer drawer, TransitionOptionRectangle optionRectangle) {

        ImageView readIcon = new ImageView("./images/read_tape_icon.png");
        this.drawer = drawer;
        this.optionRectangle = optionRectangle;

        readIcon.setLayoutX(-readIcon.getBoundsInLocal().getWidth() / 2);
        readIcon.setLayoutY(-readIcon.getBoundsInLocal().getHeight() / 2);

        backgroundColor = new Rectangle(
                - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 4,
                - readIcon.getBoundsInLocal().getHeight() / 2,
                TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2,
                readIcon.getBoundsInLocal().getHeight());
        backgroundColor.setFill(Color.GREEN);

        this.setOnMouseClicked(drawer.graphPaneMouseHandler);

        this.getChildren().addAll(backgroundColor, readIcon);
    }

    void setSelected(boolean selected){
        this.backgroundColor.setOpacity(selected?1:0);
    }
}

class ActionsGroup extends Group{

    TuringMachineDrawer drawer;
    TransitionOptionRectangle optionRectangle;
    private Rectangle backgroundColor;

    ActionsGroup(TuringMachineDrawer drawer, TransitionOptionRectangle optionRectangle) {

        ImageView actionsIcon = new ImageView("./images/action_tape_icon.png");
        this.drawer = drawer;
        this.optionRectangle = optionRectangle;

        actionsIcon.setLayoutX(-actionsIcon.getBoundsInLocal().getWidth() / 2);
        actionsIcon.setLayoutY(-actionsIcon.getBoundsInLocal().getHeight() / 2);

        backgroundColor = new Rectangle(
                - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 4,
                - actionsIcon.getBoundsInLocal().getHeight() / 2,
                TuringMachineDrawer.STATE_OPTION_RECTANGLE_MAXIMIZED_WIDTH / 2,
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