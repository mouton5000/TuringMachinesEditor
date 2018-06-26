package gui;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.List;


/**
 * Created by dimitri.watel on 22/06/18.
 */
class TuringMenu extends Group {
    TuringMachineDrawer drawer;

    private Rectangle rectangle;

    private EditGraphIcon editGraphIcon;
    private NewFileIcon newFileIcon;
    private OpenFileIcon openFileIcon;
    private SaveFileIcon saveFileIcon;
    private SaveAsFileIcon saveAsFileIcon;
    private ManualIcon manualIcon;

    private StopIcon stopIcon;
    private PreviousFrameIcon previousFrameIcon;
    private PlayIcon playIcon;
    private PauseIcon pauseIcon;
    private NextFrameIcon nextFrameIcon;
    private LastFrameIcon lastFrameIcon;

    private ParametersIcon parametersIcon;
    private BuildIcon buildIcon;

    List<PlayerIcon> nonPlayerMenu;
    List<PlayerIcon> playerMenu;
    List<PlayerIcon> allMenu;

    TuringMenu(TuringMachineDrawer drawer){
        this.drawer = drawer;

        editGraphIcon = new EditGraphIcon(this.drawer);
        newFileIcon = new NewFileIcon(this.drawer);
        openFileIcon = new OpenFileIcon(this.drawer);
        saveFileIcon = new SaveFileIcon(this.drawer);
        saveAsFileIcon = new SaveAsFileIcon(this.drawer);
        manualIcon = new ManualIcon(this.drawer);

        stopIcon = new StopIcon(this.drawer);
        previousFrameIcon = new PreviousFrameIcon(this.drawer);
        playIcon = new PlayIcon(this.drawer);
        pauseIcon = new PauseIcon(this.drawer);
        nextFrameIcon = new NextFrameIcon(this.drawer);
        lastFrameIcon = new LastFrameIcon(this.drawer);

        parametersIcon = new ParametersIcon(this.drawer);
        buildIcon = new BuildIcon(this.drawer);

        nonPlayerMenu = Arrays.asList(editGraphIcon, newFileIcon, openFileIcon, saveFileIcon, saveAsFileIcon, parametersIcon);
        playerMenu = Arrays.asList(stopIcon, previousFrameIcon, pauseIcon, playIcon, nextFrameIcon, lastFrameIcon);
        allMenu = Arrays.asList(manualIcon, buildIcon);

        int maxSize = Math.max(nonPlayerMenu.size(), playerMenu.size()) + allMenu.size();
        for(int i = 0; i < nonPlayerMenu.size(); i++)
            nonPlayerMenu.get(i).setLayoutX(TuringMachineDrawer.MENU_ICON_RADIUS * 2.5 *
                    (maxSize * 0.5 - allMenu.size() - nonPlayerMenu.size() + i));
        for(int i = 0; i < playerMenu.size(); i++)
            playerMenu.get(i).setLayoutX(TuringMachineDrawer.MENU_ICON_RADIUS * 2.5 *
                    (maxSize * 0.5 - allMenu.size() - playerMenu.size() + i));
        for(int i = 0; i < allMenu.size(); i++)
            allMenu.get(i).setLayoutX(TuringMachineDrawer.MENU_ICON_RADIUS * 2.5 * (maxSize * 0.5 - allMenu.size() + i));

        rectangle = new Rectangle(
                - TuringMachineDrawer.MENU_ICON_RADIUS * 2.5 * maxSize / 2,
                - TuringMachineDrawer.MENU_HEIGHT / 2,
                - TuringMachineDrawer.MENU_ICON_RADIUS * 2.5 * maxSize,
                TuringMachineDrawer.MENU_HEIGHT
        );
        rectangle.setFill(Color.TRANSPARENT);

        editGraphIcon.setSelected();
        newFileIcon.setSelected();
        openFileIcon.setSelected();
        saveFileIcon.setSelected();
        saveAsFileIcon.setSelected();
        manualIcon.setSelected();
        parametersIcon.setSelected();

        stopIcon.setSelected();
        previousFrameIcon.setUnselected();
        playIcon.setUnselected();
        pauseIcon.setUnselected();
        nextFrameIcon.setUnselected();
        lastFrameIcon.setUnselected();

        buildIcon.setSelected();


        setPlay();
        hidePlayer();

        this.getChildren().addAll(rectangle,
                editGraphIcon, newFileIcon, openFileIcon, saveFileIcon, saveAsFileIcon, manualIcon, parametersIcon,
                stopIcon, previousFrameIcon, playIcon, pauseIcon, nextFrameIcon, lastFrameIcon,
                buildIcon);


    }


    void setPlay(){
        playIcon.setVisible(false);
        pauseIcon.setVisible(true);
        previousFrameIcon.setUnselected();
        stopIcon.setUnselected();
        playIcon.setUnselected();
        pauseIcon.setSelected();
        nextFrameIcon.setUnselected();
        lastFrameIcon.setUnselected();
    }

    void setPause(){
        playIcon.setVisible(true);
        pauseIcon.setVisible(false);
        previousFrameIcon.setSelected();
        stopIcon.setSelected();
        playIcon.setSelected();
        pauseIcon.setUnselected();
        nextFrameIcon.setSelected();
        lastFrameIcon.setSelected();
    }

    void setFirstFrame() {
        playIcon.setVisible(true);
        pauseIcon.setVisible(false);
        stopIcon.setUnselected();
        previousFrameIcon.setUnselected();
        playIcon.setSelected();
        pauseIcon.setUnselected();
        nextFrameIcon.setSelected();
        lastFrameIcon.setSelected();
    }

    void setLastFrame() {
        playIcon.setVisible(true);
        pauseIcon.setVisible(false);
        previousFrameIcon.setSelected();
        stopIcon.setSelected();
        playIcon.setUnselected();
        pauseIcon.setUnselected();
        nextFrameIcon.setUnselected();
        lastFrameIcon.setUnselected();
    }

    void setBuild(){
        manualIcon.setUnselected();
        showPlayer();
    }

    void setNotBuild(){
        manualIcon.setSelected();
        hidePlayer();
    }

    void setManual() {
        buildIcon.setUnselected();
        showPlayer();
    }

    void setNotManual() {
        buildIcon.setSelected();
        hidePlayer();
    }

    void setEditGraph(){
        manualIcon.setUnselected();
        buildIcon.setUnselected();
    }

    void setNotEditGraph(){
        manualIcon.setSelected();
        buildIcon.setSelected();
    }

    private void showPlayer() {

        for(PlayerIcon playerIcon : this.nonPlayerMenu)
            playerIcon.setVisible(false);
        for(PlayerIcon playerIcon : this.playerMenu)
            playerIcon.setVisible(true);
        setFirstFrame();
    }

    private void hidePlayer(){

        for(PlayerIcon playerIcon : this.nonPlayerMenu)
            playerIcon.setVisible(true);
        for(PlayerIcon playerIcon : this.playerMenu)
            playerIcon.setVisible(false);
    }
}

abstract class PlayerIcon extends Group{

    TuringMachineDrawer drawer;
    private Circle circle;

    PlayerIcon(TuringMachineDrawer drawer){

        circle = new Circle();
        circle.setRadius(TuringMachineDrawer.MENU_ICON_RADIUS);
        this.setOpacity(0.5);

        this.setOnMouseClicked(drawer.turingMenuMouseHandler);

        this.getChildren().add(circle);

    }

    void setSelected(){
        circle.setFill(TuringMachineDrawer.MENU_SELECTED_ICON_COLOR);
    }
    void setUnselected(){
        circle.setFill(TuringMachineDrawer.MENU_UNSELECTED_ICON_COLOR);
    }
}

class EditGraphIcon extends PlayerIcon {

    EditGraphIcon(TuringMachineDrawer drawer) {
        super(drawer);

        ImageView imageView = new ImageView("images/edit_graph_icon.png");
        imageView.setFitHeight(1.5 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setFitWidth(1.5 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setLayoutX(-imageView.getBoundsInLocal().getWidth() / 2);
        imageView.setLayoutY(-imageView.getBoundsInLocal().getHeight() / 2);
        this.getChildren().add(imageView);
    }
}

class NewFileIcon extends PlayerIcon {

    NewFileIcon(TuringMachineDrawer drawer) {
        super(drawer);

        ImageView imageView = new ImageView("./images/new_file.png");
        imageView.setFitHeight(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setFitWidth(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setLayoutX(-imageView.getBoundsInLocal().getWidth() / 2);
        imageView.setLayoutY(-imageView.getBoundsInLocal().getHeight() / 2);
        this.getChildren().add(imageView);
    }
}

class OpenFileIcon extends PlayerIcon {

    OpenFileIcon(TuringMachineDrawer drawer) {
        super(drawer);

        ImageView imageView = new ImageView("./images/open_file.png");
        imageView.setFitHeight(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setFitWidth(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setLayoutX(-imageView.getBoundsInLocal().getWidth() / 2);
        imageView.setLayoutY(-imageView.getBoundsInLocal().getHeight() / 2);
        this.getChildren().add(imageView);
    }
}

class SaveFileIcon extends PlayerIcon {

    SaveFileIcon(TuringMachineDrawer drawer) {
        super(drawer);

        ImageView imageView = new ImageView("./images/save_file.png");
        imageView.setFitHeight(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setFitWidth(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setLayoutX(-imageView.getBoundsInLocal().getWidth() / 2);
        imageView.setLayoutY(-imageView.getBoundsInLocal().getHeight() / 2);
        this.getChildren().add(imageView);
    }
}

class SaveAsFileIcon extends PlayerIcon {

    SaveAsFileIcon(TuringMachineDrawer drawer) {
        super(drawer);

        ImageView imageView = new ImageView("./images/saveas_file.png");
        imageView.setFitHeight(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setFitWidth(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setLayoutX(-imageView.getBoundsInLocal().getWidth() / 2);
        imageView.setLayoutY(-imageView.getBoundsInLocal().getHeight() / 2);
        this.getChildren().add(imageView);
    }
}

class ManualIcon extends PlayerIcon{

    ManualIcon(TuringMachineDrawer drawer) {
        super(drawer);

        ImageView imageView = new ImageView("./images/manual_icon.png");
        imageView.setFitHeight(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setFitWidth(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setLayoutX(-imageView.getBoundsInLocal().getWidth() / 2);
        imageView.setLayoutY(-imageView.getBoundsInLocal().getHeight() / 2);
        this.getChildren().add(imageView);
    }
}

class ParametersIcon extends PlayerIcon {

    ParametersIcon(TuringMachineDrawer drawer) {
        super(drawer);

        double smallRadius = TuringMachineDrawer.STATE_RADIUS / 6;

        Circle circle1 = new Circle(-smallRadius * 3, 0, smallRadius);
        circle1.setFill(Color.WHITE);

        Circle circle2 = new Circle(smallRadius);
        circle2.setFill(Color.WHITE);

        Circle circle3 = new Circle(smallRadius * 3, 0, smallRadius);
        circle3.setFill(Color.WHITE);

        this.getChildren().addAll(circle1, circle2, circle3);
    }
}


class BuildIcon extends PlayerIcon{

    BuildIcon(TuringMachineDrawer drawer){
        super(drawer);

        double outerRadius = TuringMachineDrawer.MENU_ICON_RADIUS * 7 / 12;
        double circleStroke = TuringMachineDrawer.MENU_ICON_RADIUS / 6;
        double cogSize = circleStroke;

        Circle circle = new Circle();
        circle.setRadius(outerRadius - circleStroke / 2);
        circle.setStrokeWidth(TuringMachineDrawer.MENU_ICON_RADIUS / 6);
        circle.setStroke(Color.WHITE);
        circle.setFill(Color.TRANSPARENT);
        this.getChildren().add(circle);

        int nbCog = 8;
        double alpha = 2 * Math.PI / nbCog;
        double delta = 0.07;

        for(int i = 0; i < nbCog; i++){
            double angle = i * alpha - alpha / 3;
            Polygon polygon = new Polygon(
                    outerRadius * Math.cos(angle), outerRadius * Math.sin(angle),
                    (outerRadius + cogSize) * Math.cos(angle + delta),
                    (outerRadius + cogSize) * Math.sin(angle + delta),
                    (outerRadius + cogSize) * Math.cos(angle + alpha * 2/ 3 - delta),
                    (outerRadius + cogSize) * Math.sin(angle + alpha * 2/ 3 - delta),
                    (outerRadius) * Math.cos(angle + alpha * 2 / 3),
                    (outerRadius) * Math.sin(angle + alpha * 2 / 3)
            );
            polygon.setFill(Color.WHITE);
            this.getChildren().add(polygon);
        }

    }

}


class StopIcon extends PlayerIcon{

    StopIcon(TuringMachineDrawer drawer) {
        super(drawer);

        Rectangle rectangle = new Rectangle(
                - TuringMachineDrawer.MENU_ICON_RADIUS * 3.0 / 8,
                - TuringMachineDrawer.MENU_ICON_RADIUS * 3.0 / 8,
                TuringMachineDrawer.MENU_ICON_RADIUS * 3.0 / 4,
                TuringMachineDrawer.MENU_ICON_RADIUS * 3.0 / 4);
        rectangle.setFill(Color.WHITE);
        rectangle.setArcHeight(5);
        rectangle.setArcWidth(5);
        this.getChildren().add(rectangle);
    }
}

class PlayIcon extends PlayerIcon{

    PlayIcon(TuringMachineDrawer drawer) {
        super(drawer);

        double playEdgeLength = TuringMachineDrawer.MENU_ICON_RADIUS * 7.0 / 8;
        double height = Math.sqrt(3) * playEdgeLength / 2;

        Polygon triangle = new Polygon(
                - height / 3, playEdgeLength / 2,
                height * 2.0 / 3, 0.0,
                - height / 3, -playEdgeLength / 2
        );
        triangle.setFill(Color.WHITE);

        this.getChildren().add(triangle);
    }
}

class PauseIcon extends PlayerIcon{

    PauseIcon(TuringMachineDrawer drawer) {
        super(drawer);

        double playEdgeLength = TuringMachineDrawer.MENU_ICON_RADIUS * 7.0 / 8;
        double height = Math.sqrt(3) * playEdgeLength / 2;

        Rectangle rectangle1 = new Rectangle(
                - height / 2, - playEdgeLength / 2,
                height / 3, playEdgeLength);
        rectangle1.setArcHeight(3);
        rectangle1.setArcWidth(3);

        Rectangle rectangle2 = new Rectangle(
                height / 6, - playEdgeLength / 2,
                height / 3, playEdgeLength);
        rectangle2.setArcHeight(3);
        rectangle2.setArcWidth(3);

        rectangle1.setFill(Color.WHITE);
        rectangle2.setFill(Color.WHITE);

        this.getChildren().addAll(rectangle1, rectangle2);
    }
}

class PreviousFrameIcon extends PlayerIcon{
    PreviousFrameIcon(TuringMachineDrawer drawer) {
        super(drawer);


        double playEdgeLength = TuringMachineDrawer.MENU_ICON_RADIUS * 7.0 / 8;
        double height = Math.sqrt(3) * playEdgeLength / 2;

        double rightX = height / 2;

        Rectangle rectangle = new Rectangle(
                rightX - height / 6, - playEdgeLength / 2,
                height / 6, playEdgeLength);
        rectangle.setArcHeight(3);
        rectangle.setArcWidth(3);

        Polygon triangle = new Polygon(
                rightX - height / 3 , playEdgeLength / 2,
                rightX - height * 4.0 / 3 , 0.0,
                rightX - height / 3 , -playEdgeLength / 2
        );

        rectangle.setFill(Color.WHITE);
        triangle.setFill(Color.WHITE);

        this.getChildren().addAll(rectangle, triangle);
    }
}

class NextFrameIcon extends PlayerIcon{
    NextFrameIcon(TuringMachineDrawer drawer) {
        super(drawer);


        double playEdgeLength = TuringMachineDrawer.MENU_ICON_RADIUS * 7.0 / 8;
        double height = Math.sqrt(3) * playEdgeLength / 2;

        double leftX = -height / 2;

        Rectangle rectangle = new Rectangle(
                leftX, - playEdgeLength / 2,
                height / 6, playEdgeLength);
        rectangle.setArcHeight(3);
        rectangle.setArcWidth(3);

        Polygon triangle = new Polygon(
                leftX + height / 3 , playEdgeLength / 2,
                leftX + height * 4.0 / 3 , 0.0,
                leftX + height / 3 , -playEdgeLength / 2
        );

        rectangle.setFill(Color.WHITE);
        triangle.setFill(Color.WHITE);

        this.getChildren().addAll(rectangle, triangle);
    }
}

class LastFrameIcon extends PlayerIcon{
    LastFrameIcon(TuringMachineDrawer drawer) {
        super(drawer);

        double playEdgeLength = TuringMachineDrawer.MENU_ICON_RADIUS * 7.0 / 8;
        double height = Math.sqrt(3) * playEdgeLength / 2;

        double leftX = - height * 11.0 / 12;

        Polygon triangle1 = new Polygon(
                leftX , playEdgeLength / 2,
                leftX + height , 0.0,
                leftX , -playEdgeLength / 2
        );

        Polygon triangle2 = new Polygon(
                leftX + height * 5.0 / 6 , playEdgeLength / 2,
                leftX + height * 11.0 / 6 , 0.0,
                leftX + height * 5.0 / 6 , -playEdgeLength / 2
        );

        Rectangle rectangle = new Rectangle(
                leftX + height * 10.0 / 6, - playEdgeLength / 2,
                height / 6, playEdgeLength);
        rectangle.setArcHeight(3);
        rectangle.setArcWidth(3);


        rectangle.setFill(Color.WHITE);
        triangle1.setFill(Color.WHITE);
        triangle2.setFill(Color.WHITE);

        this.getChildren().addAll(triangle1, triangle2, rectangle);
    }
}