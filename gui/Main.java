package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage stage;
    private Pane graphPane;
    private Pane tapesPane;

    public MenuItem newButton;
    public MenuItem saveButton;
    public MenuItem loadButton;
    
    public static final int WIDTH = 800;
    public static final int HEIGTH = 640;
    public static final int MARGIN = 30;

    @Override
    public void start(Stage stage) throws Exception{
        this.stage = stage;

        reinitDraw();

        stage.show();
    }

    public void reinitDraw(){
        graphPane = new Pane();
        tapesPane = new Pane();

        MenuBar menuBar = new MenuBar();
        menuBar.setMinHeight(MARGIN);
        menuBar.setMaxHeight(MARGIN);

        Menu fileMenu = new Menu("File");
        menuBar.getMenus().addAll(fileMenu);

//        NewSaveLoadButtonHandler slh = new NewSaveLoadButtonHandler(this);
        newButton = new MenuItem();
        newButton.setText("New");
//        newButton.setOnAction(slh);

        saveButton = new MenuItem();
        saveButton.setText("Save");

        loadButton = new MenuItem();
        loadButton.setText("Load");

        fileMenu.getItems().addAll(newButton, new SeparatorMenuItem(),
                saveButton, loadButton);

        VBox box = new VBox();
        box.getChildren().addAll(menuBar, graphPane, tapesPane);

        Scene scene = new Scene(box, WIDTH, HEIGTH);
        stage.setTitle("Turing Machine Editor");
        stage.setScene(scene);
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGTH);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
