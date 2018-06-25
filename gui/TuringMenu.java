package gui;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;


/**
 * Created by dimitri.watel on 22/06/18.
 */
class TuringMenu extends Group {
    TuringMachineDrawer drawer;

    private Rectangle rectangle;
    private NewFileIcon newFileIcon;
    private OpenFileIcon openFileIcon;
    private SaveFileIcon saveFileIcon;
    private SaveAsFileIcon saveAsFileIcon;

    private StopIcon stopIcon;
    private PlayIcon playIcon;
    private PauseIcon pauseIcon;
    private OneFrameIcon oneFrameIcon;
    private LastFrameIcon lastFrameIcon;

    private ParametersIcon parametersIcon;
    private BuildIcon buildIcon;

    TuringMenu(TuringMachineDrawer drawer){
        this.drawer = drawer;

        rectangle = new Rectangle(
                - TuringMachineDrawer.MENU_WIDTH / 2,
                - TuringMachineDrawer.MENU_HEIGHT / 2,
                TuringMachineDrawer.MENU_WIDTH,
                TuringMachineDrawer.MENU_HEIGHT
        );
        rectangle.setFill(Color.TRANSPARENT);

        newFileIcon = new NewFileIcon(this.drawer);
        openFileIcon = new OpenFileIcon(this.drawer);
        saveFileIcon = new SaveFileIcon(this.drawer);
        saveAsFileIcon = new SaveAsFileIcon(this.drawer);

        stopIcon = new StopIcon(this.drawer);
        playIcon = new PlayIcon(this.drawer);
        pauseIcon = new PauseIcon(this.drawer);
        oneFrameIcon = new OneFrameIcon(this.drawer);
        lastFrameIcon = new LastFrameIcon(this.drawer);

        parametersIcon = new ParametersIcon(this.drawer);
        buildIcon = new BuildIcon(this.drawer);

        newFileIcon.setLayoutX(-TuringMachineDrawer.MENU_ICON_RADIUS * 6.25);
        openFileIcon.setLayoutX(-TuringMachineDrawer.MENU_ICON_RADIUS * 3.75);
        saveFileIcon.setLayoutX(-TuringMachineDrawer.MENU_ICON_RADIUS * 1.25);
        saveAsFileIcon.setLayoutX(TuringMachineDrawer.MENU_ICON_RADIUS * 1.25);

        stopIcon.setLayoutX(-TuringMachineDrawer.MENU_ICON_RADIUS * 6.25);
        playIcon.setLayoutX(-TuringMachineDrawer.MENU_ICON_RADIUS * 3.75);
        pauseIcon.setLayoutX(-TuringMachineDrawer.MENU_ICON_RADIUS * 3.75);
        oneFrameIcon.setLayoutX(-TuringMachineDrawer.MENU_ICON_RADIUS * 1.25);
        lastFrameIcon.setLayoutX(TuringMachineDrawer.MENU_ICON_RADIUS * 1.25);

        parametersIcon.setLayoutX(TuringMachineDrawer.MENU_ICON_RADIUS * 3.75);
        buildIcon.setLayoutX(TuringMachineDrawer.MENU_ICON_RADIUS * 6.25);

        newFileIcon.setSelected();
        openFileIcon.setSelected();
        saveFileIcon.setSelected();
        saveAsFileIcon.setSelected();

        stopIcon.setSelected();
        playIcon.setUnselected();
        pauseIcon.setUnselected();
        oneFrameIcon.setUnselected();
        lastFrameIcon.setUnselected();

        parametersIcon.setSelected();
        buildIcon.setSelected();


        setPlay();
        hidePlayer();

        this.getChildren().addAll(rectangle,
                newFileIcon, openFileIcon, saveFileIcon, saveAsFileIcon,
                stopIcon, playIcon, pauseIcon, oneFrameIcon, lastFrameIcon,
                parametersIcon, buildIcon);


    }


    void setPlay(){
        playIcon.setVisible(false);
        pauseIcon.setVisible(true);
        stopIcon.setUnselected();
        playIcon.setUnselected();
        pauseIcon.setSelected();
        oneFrameIcon.setUnselected();
        lastFrameIcon.setUnselected();
    }

    void setPause(){
        playIcon.setVisible(true);
        pauseIcon.setVisible(false);
        stopIcon.setSelected();
        playIcon.setSelected();
        pauseIcon.setUnselected();
        oneFrameIcon.setSelected();
        lastFrameIcon.setSelected();
    }

    void setFirstFrame() {
        playIcon.setVisible(true);
        pauseIcon.setVisible(false);
        stopIcon.setUnselected();
        playIcon.setSelected();
        pauseIcon.setUnselected();
        oneFrameIcon.setSelected();
        lastFrameIcon.setSelected();
    }

    void setLastFrame() {
        playIcon.setVisible(true);
        pauseIcon.setVisible(false);
        stopIcon.setSelected();
        playIcon.setUnselected();
        pauseIcon.setUnselected();
        oneFrameIcon.setUnselected();
        lastFrameIcon.setUnselected();
    }

    void showPlayer() {

        newFileIcon.setVisible(false);
        openFileIcon.setVisible(false);
        saveFileIcon.setVisible(false);
        saveAsFileIcon.setVisible(false);

        stopIcon.setVisible(true);
        playIcon.setVisible(true);
        pauseIcon.setVisible(false);
        oneFrameIcon.setVisible(true);
        lastFrameIcon.setVisible(true);
        setFirstFrame();
    }

    void hidePlayer(){

        newFileIcon.setVisible(true);
        openFileIcon.setVisible(true);
        saveFileIcon.setVisible(true);
        saveAsFileIcon.setVisible(true);

        stopIcon.setVisible(false);
        playIcon.setVisible(false);
        pauseIcon.setVisible(false);
        oneFrameIcon.setVisible(false);
        lastFrameIcon.setVisible(false);
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

class OneFrameIcon extends PlayerIcon{
    OneFrameIcon(TuringMachineDrawer drawer) {
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