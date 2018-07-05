package gui;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.List;


/**
 * Created by dimitri.watel on 22/06/18.
 */
class TuringMenu extends Group {

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

    private ShowIcon showIcon;
    private HideIcon hideIcon;
    private HelpIcon helpIcon;

    List<PlayerIcon> nonPlayerMenu;
    List<PlayerIcon> playerMenu;
    List<PlayerIcon> allMenu;

    TuringMenu(){

        editGraphIcon = new EditGraphIcon();
        newFileIcon = new NewFileIcon();
        openFileIcon = new OpenFileIcon();
        saveFileIcon = new SaveFileIcon();
        saveAsFileIcon = new SaveAsFileIcon();
        parametersIcon = new ParametersIcon();

        stopIcon = new StopIcon();
        previousFrameIcon = new PreviousFrameIcon();
        playIcon = new PlayIcon();
        pauseIcon = new PauseIcon();
        nextFrameIcon = new NextFrameIcon();
        lastFrameIcon = new LastFrameIcon();

        buildIcon = new BuildIcon();
        manualIcon = new ManualIcon();
        helpIcon = new HelpIcon();

        showIcon = new ShowIcon();
        hideIcon = new HideIcon();



        nonPlayerMenu = Arrays.asList(editGraphIcon, newFileIcon, openFileIcon, saveFileIcon, saveAsFileIcon, parametersIcon);
        playerMenu = Arrays.asList(stopIcon, previousFrameIcon, pauseIcon, playIcon, nextFrameIcon, lastFrameIcon);
        allMenu = Arrays.asList(manualIcon, buildIcon, helpIcon);

        int menuSize = getMenuSize();
        for(int i = 0; i < nonPlayerMenu.size(); i++)
            nonPlayerMenu.get(i).setLayoutX(TuringMachineDrawer.MENU_ICON_RADIUS * 2.5 *
                    (menuSize * 0.5 - allMenu.size() - 0.5 - nonPlayerMenu.size() + i));
        for(int i = 0; i < playerMenu.size(); i++)
            playerMenu.get(i).setLayoutX(TuringMachineDrawer.MENU_ICON_RADIUS * 2.5 *
                    (menuSize * 0.5 - allMenu.size() - 0.5 - playerMenu.size() + i));
        for(int i = 0; i < allMenu.size(); i++)
            allMenu.get(i).setLayoutX(TuringMachineDrawer.MENU_ICON_RADIUS * 2.5 * (menuSize * 0.5 - allMenu.size() - 0.5 + i));

        hideIcon.setLayoutX(TuringMachineDrawer.MENU_ICON_RADIUS * 2.5 * (menuSize * 0.5 - 0.5));
        showIcon.setLayoutX(TuringMachineDrawer.MENU_ICON_RADIUS * 2.5 * (menuSize * 0.5 - 0.5));

        double width = getWidth();
        rectangle = new Rectangle(
                - width / 2 ,
                - TuringMachineDrawer.MENU_HEIGHT * 0.5,
                width,
                TuringMachineDrawer.MENU_HEIGHT
        );
        rectangle.setFill(Color.TRANSPARENT);

        editGraphIcon.setClickable();
        newFileIcon.setClickable();
        openFileIcon.setClickable();
        saveFileIcon.setClickable();
        saveAsFileIcon.setClickable();
        manualIcon.setClickable();
        parametersIcon.setClickable();

        stopIcon.setClickable();
        previousFrameIcon.setNonClickable();
        playIcon.setNonClickable();
        pauseIcon.setNonClickable();
        nextFrameIcon.setNonClickable();
        lastFrameIcon.setNonClickable();

        buildIcon.setClickable();

        helpIcon.setClickable();
        showIcon.setClickable();
        hideIcon.setClickable();


        setPlay();
        hidePlayer();
        hideMenu();

        this.getChildren().addAll(rectangle,
                editGraphIcon, newFileIcon, openFileIcon, saveFileIcon, saveAsFileIcon, manualIcon, parametersIcon,
                stopIcon, previousFrameIcon, playIcon, pauseIcon, nextFrameIcon, lastFrameIcon,
                buildIcon, helpIcon, showIcon, hideIcon);


    }


    void setPlay(){
        previousFrameIcon.setNonClickable();
        stopIcon.setNonClickable();
        playIcon.setNonClickable();
        pauseIcon.setClickable();
        nextFrameIcon.setNonClickable();
        lastFrameIcon.setNonClickable();
    }

    void setPause(){
        previousFrameIcon.setClickable();
        stopIcon.setClickable();
        playIcon.setClickable();
        pauseIcon.setNonClickable();
        nextFrameIcon.setClickable();
        lastFrameIcon.setClickable();
    }

    void setFirstFrame() {
        stopIcon.setNonClickable();
        previousFrameIcon.setNonClickable();
        playIcon.setClickable();
        pauseIcon.setNonClickable();
        nextFrameIcon.setClickable();
        lastFrameIcon.setClickable();
    }

    void setLastFrame() {
        previousFrameIcon.setClickable();
        stopIcon.setClickable();
        playIcon.setNonClickable();
        pauseIcon.setNonClickable();
        nextFrameIcon.setNonClickable();
        lastFrameIcon.setNonClickable();
    }

    void setBuild(){
        buildIcon.setSelected();
        manualIcon.setNonClickable();
        showPlayer();
    }

    void setNotBuild(){
        buildIcon.setUnselected();
        manualIcon.setClickable();
        hidePlayer();
    }

    void setManual() {
        manualIcon.setSelected();
        buildIcon.setNonClickable();
        showPlayer();
    }

    void setNotManual() {
        manualIcon.setUnselected();
        buildIcon.setClickable();
        hidePlayer();
    }

    void setEditGraph(){
        editGraphIcon.setSelected();
        manualIcon.setNonClickable();
        buildIcon.setNonClickable();
    }

    void setNotEditGraph(){
        editGraphIcon.setUnselected();
        manualIcon.setClickable();
        buildIcon.setClickable();
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

    void showMenu(){
        this.showIcon.setVisible(false);
        this.hideIcon.setVisible(true);

        rectangle.setVisible(true);


        for(PlayerIcon playerIcon : this.nonPlayerMenu)
            playerIcon.show();
        for(PlayerIcon playerIcon : this.playerMenu)
            playerIcon.show();
        for(PlayerIcon playerIcon : this.allMenu)
            playerIcon.show();
    }

    void hideMenu(){
        this.showIcon.setVisible(true);
        this.hideIcon.setVisible(false);

        rectangle.setVisible(false);

        for(PlayerIcon playerIcon : this.nonPlayerMenu)
            playerIcon.hide();
        for(PlayerIcon playerIcon : this.playerMenu)
            playerIcon.hide();
        for(PlayerIcon playerIcon : this.allMenu)
            playerIcon.hide();

    }

    private int getMenuSize(){
        return Math.max(nonPlayerMenu.size(), playerMenu.size()) + allMenu.size() + 1;
    }

    double getWidth(){
        return TuringMachineDrawer.MENU_ICON_RADIUS * 2.5 * getMenuSize();
    }

    void setSave(boolean maySave){
        if(maySave)
            saveFileIcon.setClickable();
        else
            saveFileIcon.setNonClickable();
    }
}

abstract class PlayerIcon extends Group{

    private Circle circle;
    private boolean wasVisible;

    PlayerIcon(){

        circle = new Circle();
        circle.setRadius(TuringMachineDrawer.MENU_ICON_RADIUS);
        setUnselected();
        this.getChildren().add(circle);

    }

    void setSelected(){
        this.setOpacity(TuringMachineDrawer.MENU_SELECTED_OPACITY);
    }

    void setUnselected(){
        this.setOpacity(TuringMachineDrawer.MENU_UNSELECTED_OPACITY);
    }

    void setClickable(){
        circle.setFill(TuringMachineDrawer.MENU_CLICKABLE_ICON_COLOR);
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().turingMenuMouseHandler);
    }
    void setNonClickable(){
        circle.setFill(TuringMachineDrawer.MENU_NON_CLICKABLE_ICON_COLOR);
        this.setOnMouseClicked(null);

    }

    void hide(){
        wasVisible = isVisible();
        this.setVisible(false);
    }

    void show(){
        this.setVisible(wasVisible);
    }

}

class EditGraphIcon extends PlayerIcon {

    EditGraphIcon() {
        

        ImageView imageView = new ImageView("images/edit_graph_icon.png");
        imageView.setFitHeight(1.5 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setFitWidth(1.5 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setLayoutX(-imageView.getBoundsInLocal().getWidth() / 2);
        imageView.setLayoutY(-imageView.getBoundsInLocal().getHeight() / 2);
        this.getChildren().add(imageView);
    }
}

class NewFileIcon extends PlayerIcon {

    NewFileIcon() {
        

        ImageView imageView = new ImageView("./images/new_file.png");
        imageView.setFitHeight(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setFitWidth(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setLayoutX(-imageView.getBoundsInLocal().getWidth() / 2);
        imageView.setLayoutY(-imageView.getBoundsInLocal().getHeight() / 2);
        this.getChildren().add(imageView);
    }
}

class OpenFileIcon extends PlayerIcon {

    OpenFileIcon() {
        

        ImageView imageView = new ImageView("./images/open_file.png");
        imageView.setFitHeight(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setFitWidth(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setLayoutX(-imageView.getBoundsInLocal().getWidth() / 2);
        imageView.setLayoutY(-imageView.getBoundsInLocal().getHeight() / 2);
        this.getChildren().add(imageView);
    }
}

class SaveFileIcon extends PlayerIcon {

    SaveFileIcon() {
        

        ImageView imageView = new ImageView("./images/save_file.png");
        imageView.setFitHeight(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setFitWidth(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setLayoutX(-imageView.getBoundsInLocal().getWidth() / 2);
        imageView.setLayoutY(-imageView.getBoundsInLocal().getHeight() / 2);
        this.getChildren().add(imageView);
    }
}

class SaveAsFileIcon extends PlayerIcon {

    SaveAsFileIcon() {
        

        ImageView imageView = new ImageView("./images/saveas_file.png");
        imageView.setFitHeight(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setFitWidth(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setLayoutX(-imageView.getBoundsInLocal().getWidth() / 2);
        imageView.setLayoutY(-imageView.getBoundsInLocal().getHeight() / 2);
        this.getChildren().add(imageView);
    }
}

class ManualIcon extends PlayerIcon{

    ManualIcon() {
        

        ImageView imageView = new ImageView("./images/manual_icon.png");
        imageView.setFitHeight(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setFitWidth(1.7 * TuringMachineDrawer.MENU_ICON_RADIUS);
        imageView.setLayoutX(-imageView.getBoundsInLocal().getWidth() / 2);
        imageView.setLayoutY(-imageView.getBoundsInLocal().getHeight() / 2);
        this.getChildren().add(imageView);
    }
}

class ParametersIcon extends PlayerIcon {

    ParametersIcon() {
        

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

    BuildIcon(){
        

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

    StopIcon() {
        

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

    PlayIcon() {
        

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

    PauseIcon() {
        

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
    PreviousFrameIcon() {
        


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
    NextFrameIcon() {
        


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
    LastFrameIcon() {
        

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

class HideIcon extends PlayerIcon{
    HideIcon() {
        

        double playEdgeLength = TuringMachineDrawer.MENU_ICON_RADIUS * 7.0 / 8;
        double height = Math.sqrt(3) * playEdgeLength / 2;
        double width = playEdgeLength / 5;

        Polygon arrow = new Polygon(
                - height / 3, playEdgeLength / 2,
                height * 2.0 / 3, 0.0,
                - height / 3, -playEdgeLength / 2,
                - height / 3, -playEdgeLength / 2 + width,
                height * 2.0 / 3 - width, 0.0,
                - height / 3, playEdgeLength / 2 - width
        );
        arrow.setFill(Color.WHITE);

        this.getChildren().add(arrow);
    }
}

class ShowIcon extends PlayerIcon{
    ShowIcon() {
        

        double playEdgeLength = TuringMachineDrawer.MENU_ICON_RADIUS * 7.0 / 8;
        double height = Math.sqrt(3) * playEdgeLength / 2;
        double width = playEdgeLength / 5;

        Polygon arrow = new Polygon(
                 height / 3, playEdgeLength / 2,
                - height * 2.0 / 3, 0.0,
                 height / 3, -playEdgeLength / 2,
                 height / 3, -playEdgeLength / 2 + width,
                - height * 2.0 / 3 + width, 0.0,
                  height / 3, playEdgeLength / 2 - width
        );
        arrow.setFill(Color.WHITE);

        this.getChildren().add(arrow);
    }
}

class HelpIcon extends PlayerIcon{
    HelpIcon() {
        

        Label label = new Label("?");
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME, 2 * TuringMachineDrawer.MENU_ICON_RADIUS));
        label.setMinWidth(2 * TuringMachineDrawer.MENU_ICON_RADIUS);
        label.setMaxWidth(2 * TuringMachineDrawer.MENU_ICON_RADIUS);
        label.setMinHeight(2 * TuringMachineDrawer.MENU_ICON_RADIUS);
        label.setMaxHeight(2 * TuringMachineDrawer.MENU_ICON_RADIUS);
        label.setLayoutX( - TuringMachineDrawer.MENU_ICON_RADIUS);
        label.setLayoutY( - TuringMachineDrawer.MENU_ICON_RADIUS - 3);
        label.setAlignment(Pos.CENTER);

        this.getChildren().add(label);
    }
}