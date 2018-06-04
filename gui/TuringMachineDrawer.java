package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;
import javafx.stage.Stage;
import turingmachines.TuringMachine;

import java.util.HashMap;
import java.util.Map;

public class TuringMachineDrawer extends Application {

    public static final int MARGIN = 30;
    public static int WIDTH;
    public static int HEIGHT;
    public static final double RATIO_HEIGHT_GRAPH_TAPES = 2.0/3;
    public static final int STATE_CIRCLE = 20;

    private Stage stage;
    private Pane graphPane;
    private Pane tapesPane;

    public MenuItem newButton;
    public MenuItem saveButton;
    public MenuItem loadButton;


    public TuringMachine machine;
    public Map<Shape, Integer> circleToState;

    @Override
    public void start(Stage stage) throws Exception{
        WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth() * 3 / 4;
        HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight()* 3 / 4;

        this.stage = stage;

        this.machine = new TuringMachine();

        reinitDraw();


        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            WIDTH = newVal.intValue();
            resizePanes();
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            HEIGHT = newVal.intValue();
            resizePanes();
        });

        circleToState = new HashMap<>();

        stage.show();
    }

    private void resizePanes(){
        graphPane.setMinWidth(WIDTH);
        graphPane.setMaxWidth(WIDTH);
        tapesPane.setMinWidth(WIDTH);
        tapesPane.setMaxWidth(WIDTH);
        graphPane.setMinHeight((HEIGHT - MARGIN) * RATIO_HEIGHT_GRAPH_TAPES);
        graphPane.setMaxHeight((HEIGHT - MARGIN) * RATIO_HEIGHT_GRAPH_TAPES);
        tapesPane.setMinHeight((HEIGHT - MARGIN) * (1 - RATIO_HEIGHT_GRAPH_TAPES));
        tapesPane.setMaxHeight((HEIGHT - MARGIN) * (1 - RATIO_HEIGHT_GRAPH_TAPES));
    }

    public void reinitDraw(){
        graphPane = new Pane();
        tapesPane = new Pane();
        resizePanes();

        GraphPaneMouseHandler graphPaneMouseHandler = new GraphPaneMouseHandler(this);
        graphPane.setOnMousePressed(graphPaneMouseHandler);

        TapesPaneMouseHandler tapesPaneMouseHandler = new TapesPaneMouseHandler(this);
        tapesPane.setOnMousePressed(tapesPaneMouseHandler);


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

        Scene scene = new Scene(box, WIDTH, HEIGHT);
        stage.setTitle("Turing Machine Editor");
        stage.setScene(scene);
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);

    }

    public void drawNewState(int x, int y, String name){
        Circle stateShape = new Circle(x, y, STATE_CIRCLE);
        graphPane.getChildren().add(stateShape);

        int state = machine.addState(name);
        circleToState.put(stateShape, state);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
