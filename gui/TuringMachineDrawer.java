package gui;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;
import javafx.stage.Stage;
import turingmachines.Transition;
import turingmachines.TuringMachine;
import util.Vector;

import java.util.HashMap;
import java.util.Map;

public class TuringMachineDrawer extends Application {

    private static final int MARGIN = 30;
    private static int WIDTH;
    private static int HEIGHT;
    private static final double RATIO_HEIGHT_GRAPH_TAPES = 2.0/3;
    static final int STATE_RADIUS = 20;
    private static final int GRID_WIDTH = 10;
    static final double ARROW_ANGLE = Math.PI/6;
    static final double ARROW_HITBOX_WIDTH = STATE_RADIUS;

    private Stage stage;
    private Pane graphPane;
    private Pane tapesPane;

    public MenuItem newButton;
    public MenuItem saveButton;
    public MenuItem loadButton;


    public TuringMachine machine;
    public Map<Shape, Integer> circleToState;
    private GraphPaneMouseHandler graphPaneMouseHandler;
    private TapesPaneMouseHandler tapesPaneMouseHandler;
    private Map<Group, Transition> arrowToTransition;

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
        arrowToTransition = new HashMap<Group, Transition>();

        stage.show();
    }

    private void resizePanes(){
        graphPane.setMinWidth(WIDTH);
        graphPane.setMaxWidth(WIDTH);
        graphPane.setMinHeight((HEIGHT - MARGIN) * RATIO_HEIGHT_GRAPH_TAPES);
        graphPane.setMaxHeight((HEIGHT - MARGIN) * RATIO_HEIGHT_GRAPH_TAPES);

        tapesPane.setMinWidth(WIDTH);
        tapesPane.setMaxWidth(WIDTH);
        tapesPane.setMinHeight((HEIGHT - MARGIN) * (1 - RATIO_HEIGHT_GRAPH_TAPES));
        tapesPane.setMaxHeight((HEIGHT - MARGIN) * (1 - RATIO_HEIGHT_GRAPH_TAPES));
    }

    public void reinitDraw(){
        graphPane = new Pane();
        tapesPane = new Pane();

        Rectangle graphClip = new Rectangle();
        Rectangle tapesClip = new Rectangle();

        graphPane.setClip(graphClip);
        tapesPane.setClip(tapesClip);

        graphPane.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            graphClip.setWidth(newValue.getWidth());
            graphClip.setHeight(newValue.getHeight());
        });
        tapesPane.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            tapesClip.setWidth(newValue.getWidth());
            tapesClip.setHeight(newValue.getHeight());
        });

        resizePanes();

        graphPaneMouseHandler = new GraphPaneMouseHandler(this);
        graphPane.setOnMouseClicked(graphPaneMouseHandler);

        tapesPaneMouseHandler = new TapesPaneMouseHandler(this);
        tapesPane.setOnMouseClicked(tapesPaneMouseHandler);
        tapesPane.setOnMouseDragged(tapesPaneMouseHandler);



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

        Separator separator = new Separator();

        VBox box = new VBox();
        box.getChildren().addAll(menuBar, graphPane, separator, tapesPane);

        Scene scene = new Scene(box, WIDTH, HEIGHT);
        stage.setTitle("Turing Machine Editor");
        stage.setScene(scene);
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);

    }

    private int gridClosest(int x){
        return (x / GRID_WIDTH) * GRID_WIDTH;
    }

    public void drawNewState(int x, int y, String name){
        int xg = gridClosest(x);
        int yg = gridClosest(y);

        Circle circle = new Circle(xg, yg, STATE_RADIUS);

        int state = machine.addState(name);
        circleToState.put(circle, state);
        circle.setOnMouseClicked(graphPaneMouseHandler);
        circle.setOnMouseDragged(graphPaneMouseHandler);

        graphPane.getChildren().add(circle);
    }

    public void drawNewTransition(Circle start, Circle end){
        TransitionArrow arrow;
        if(start != end){
            arrow = new TransitionArrow(start, end);
        }
        else
            return;

        Integer input = circleToState.get(start);
        Integer output = circleToState.get(end);
        arrow.setOnMouseClicked(graphPaneMouseHandler);
        arrowToTransition.put(arrow, machine.addTransition(input, output));

        graphPane.getChildren().add(arrow);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
