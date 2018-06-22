package gui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * Created by dimitri.watel on 22/06/18.
 */
class TuringPlayer extends Group {
    TuringMachineDrawer drawer;

    BuildIcon buildIcon;
    StopIcon stopIcon;
    PlayIcon playIcon;
    PauseIcon pauseIcon;
    OneFrameIcon oneFrameIcon;
    LastFrameIcon lastFrameIcon;

    TuringPlayer(TuringMachineDrawer drawer){
        this.drawer = drawer;

        Rectangle rectangle = new Rectangle(
                - TuringMachineDrawer.PLAYER_WIDTH / 2,
                - TuringMachineDrawer.PLAYER_HEIGHT / 2,
                TuringMachineDrawer.PLAYER_WIDTH,
                TuringMachineDrawer.PLAYER_HEIGHT
        );
        rectangle.setFill(Color.TRANSPARENT);

        buildIcon = new BuildIcon(this.drawer);
        stopIcon = new StopIcon(this.drawer);
        playIcon = new PlayIcon(this.drawer);
        pauseIcon = new PauseIcon(this.drawer);
        oneFrameIcon = new OneFrameIcon(this.drawer);
        lastFrameIcon = new LastFrameIcon(this.drawer);

        buildIcon.setLayoutX(-TuringMachineDrawer.PLAYER_ICON_RADIUS * 5);
        stopIcon.setLayoutX(-TuringMachineDrawer.PLAYER_ICON_RADIUS * 2.5);
        oneFrameIcon.setLayoutX(TuringMachineDrawer.PLAYER_ICON_RADIUS * 2.5);
        lastFrameIcon.setLayoutX(TuringMachineDrawer.PLAYER_ICON_RADIUS * 5);

        buildIcon.setUnselected();
        stopIcon.setSelected();
        playIcon.setUnselected();
        pauseIcon.setUnselected();
        oneFrameIcon.setUnselected();
        lastFrameIcon.setUnselected();

        setPlay();
        hidePlayer();

        this.getChildren().addAll(rectangle, buildIcon, stopIcon, playIcon, pauseIcon, oneFrameIcon, lastFrameIcon);


    }


    void setPlay(){
        playIcon.setVisible(true);
        pauseIcon.setVisible(false);
    }

    void setPause(){
        playIcon.setVisible(false);
        pauseIcon.setVisible(true);
    }

    void setLastFrame() {
        setPlay();
        stopIcon.setUnselected();
        playIcon.setUnselected();
        pauseIcon.setUnselected();
        oneFrameIcon.setUnselected();
        lastFrameIcon.setUnselected();
    }

    void showPlayer() {
        buildIcon.setLayoutX(-TuringMachineDrawer.PLAYER_ICON_RADIUS * 5);
        buildIcon.setSelected();

        stopIcon.setVisible(true);
        playIcon.setVisible(true);
        pauseIcon.setVisible(false);
        oneFrameIcon.setVisible(true);
        lastFrameIcon.setVisible(true);
        stopIcon.setSelected();
    }

    void hidePlayer(){
        buildIcon.setLayoutX(TuringMachineDrawer.PLAYER_ICON_RADIUS * 5);
        buildIcon.setUnselected();

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
        circle.setRadius(TuringMachineDrawer.PLAYER_ICON_RADIUS);
        this.setOpacity(0.5);

        this.setOnMouseClicked(drawer.turingPlayerMouseHandler);

        this.getChildren().add(circle);

    }

    void setSelected(){
        circle.setFill(TuringMachineDrawer.PLAYER_SELECTED_ICON_COLOR);
    }
    void setUnselected(){
        circle.setFill(TuringMachineDrawer.PLAYER_UNSELECTED_ICON_COLOR);
    }
}

class BuildIcon extends PlayerIcon{

    BuildIcon(TuringMachineDrawer drawer){
        super(drawer);

        double outerRadius = TuringMachineDrawer.PLAYER_ICON_RADIUS * 7 / 12;
        double circleStroke = TuringMachineDrawer.PLAYER_ICON_RADIUS / 6;
        double cogSize = circleStroke;

        Circle circle = new Circle();
        circle.setRadius(outerRadius - circleStroke / 2);
        circle.setStrokeWidth(TuringMachineDrawer.PLAYER_ICON_RADIUS / 6);
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
                - TuringMachineDrawer.PLAYER_ICON_RADIUS * 3.0 / 8,
                - TuringMachineDrawer.PLAYER_ICON_RADIUS * 3.0 / 8,
                TuringMachineDrawer.PLAYER_ICON_RADIUS * 3.0 / 4,
                TuringMachineDrawer.PLAYER_ICON_RADIUS * 3.0 / 4);
        rectangle.setFill(Color.WHITE);
        rectangle.setArcHeight(5);
        rectangle.setArcWidth(5);
        this.getChildren().add(rectangle);
    }
}

class PlayIcon extends PlayerIcon{

    PlayIcon(TuringMachineDrawer drawer) {
        super(drawer);

        double playEdgeLength = TuringMachineDrawer.PLAYER_ICON_RADIUS * 7.0 / 8;
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

        double playEdgeLength = TuringMachineDrawer.PLAYER_ICON_RADIUS * 7.0 / 8;
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


        double playEdgeLength = TuringMachineDrawer.PLAYER_ICON_RADIUS * 7.0 / 8;
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

        double playEdgeLength = TuringMachineDrawer.PLAYER_ICON_RADIUS * 7.0 / 8;
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