package gui;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import turingmachines.Transition;
import turingmachines.TuringMachine;

import java.util.HashMap;
import java.util.Map;

public class TuringMachineDrawer extends Application {

    private static final int MARGIN = 30;
    private static int WIDTH;
    private static int HEIGHT;
    private static final int GRAPH_GRID_WIDTH = 10;
    private static final double RATIO_HEIGHT_GRAPH_TAPES = 2.0/3;

    static final int STATE_RADIUS = 20;
    public static final double FINAL_STATE_RADIUS_RATIO = 0.9;

    static final Color SELECTED_STATE_COLOR = Color.GRAY;
    static final Color UNSELECTED_STATE_COLOR = Color.BLACK;

    static final double ARROW_ANGLE = Math.PI/6;
    static final double ARROW_HITBOX_WIDTH = STATE_RADIUS * 2.3;
    static final double ARROW_KEY_RADIUS = 8;
    static final Color ARROW_KEY_COLOR = Color.GREENYELLOW;
    static final Color ARROW_KEY_STROKE_COLOR = Color.BLACK;
    static final Color ARROW_KEY_LINE_COLOR = Color.GREENYELLOW.darker();
    static final double ARROW_KEY_LINE_STROKE_WIDTH = 3;
    static final double ARROW_KEY_DISTANCE_RATIO = 0.25;

    private Stage stage;
    private Pane graphPane;
    private Pane tapesPane;

    public MenuItem newButton;
    public MenuItem saveButton;
    public MenuItem loadButton;


    public TuringMachine machine;
    public Map<StateCircle, Integer> circleToState;
    private Map<Group, Transition> arrowToTransition;

    GraphPaneMouseHandler graphPaneMouseHandler;
    private TapesPaneMouseHandler tapesPaneMouseHandler;

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

    ReadOnlyDoubleProperty graphWidthProperty(){
        return graphPane.widthProperty();
    }

    ReadOnlyDoubleProperty graphHeightProperty(){
        return graphPane.heightProperty();
    }

    private int gridClosest(double value){
        return ((int)value / GRAPH_GRID_WIDTH) * GRAPH_GRID_WIDTH;
    }

    public void drawNewState(double x, double y, String name){

        StateCircle circle = new StateCircle(this);

        int state = machine.addState(name);
        circleToState.put(circle, state);
        circle.setOnMouseClicked(graphPaneMouseHandler);
        circle.setOnMouseDragged(graphPaneMouseHandler);

        graphPane.getChildren().add(circle);
        moveState(circle, x, y);
    }

    public void moveState(StateCircle stateCircle, double x, double y){
        int xg = gridClosest(x);
        int yg = gridClosest(y);
        stateCircle.setCenterX(x);
        stateCircle.setCenterY(y);
    }

    public void drawNewTransition(StateCircle start, StateCircle end){
        TransitionArrow arrow;
        if(start != end){
            arrow = new TransitionArrow(this, start, end);
        }
        else
            return;

        Integer input = circleToState.get(start);
        Integer output = circleToState.get(end);
        arrowToTransition.put(arrow, machine.addTransition(input, output));

        graphPane.getChildren().add(arrow);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
