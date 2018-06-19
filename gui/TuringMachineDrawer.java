package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import turingmachines.Tape;
import turingmachines.TuringMachine;
import util.Colors;
import util.Pair;
import util.Subscriber;

import java.util.*;

public class TuringMachineDrawer extends Application {

    private static final int SEPARATOR_WIDTH = 2;
    private static final int MARGIN = 30;
    private static int WIDTH;
    private static int HEIGHT;
    static final int GRAPH_GRID_WIDTH = 10;
    private static final double RATIO_HEIGHT_GRAPH_TAPES = 1.0/2;

    static final int OPTION_RECTANGLE_TIMELINE_DURATION = 200;

    static final int STATE_RADIUS = 20;
    static final double FINAL_STATE_RADIUS_RATIO = 0.8;
    static final Color STATE_OUTER_COLOR = Color.BLACK;
    static final Color SELECTED_STATE_COLOR = Color.GRAY;
    static final Color UNSELECTED_STATE_COLOR = Color.WHITE;
    static final Color STATE_PRESS_COLOR = Color.DARKGRAY;
    static final long STATE_PRESS_DURATION = 300;

    static final double ARROW_ANGLE = Math.PI/6;
    static final double ARROW_SIZE = STATE_RADIUS;
    static final double ARROW_HITBOX_WIDTH = STATE_RADIUS * 2.3;
    static final double ARROW_KEY_RADIUS = 8;
    static final Color ARROW_KEY_COLOR = Color.GREENYELLOW;
    static final Color ARROW_KEY_STROKE_COLOR = Color.BLACK;
    static final Color ARROW_KEY_LINE_COLOR = Color.GREENYELLOW.darker();
    static final double ARROW_KEY_LINE_STROKE_WIDTH = 3;
    static final double ARROW_KEY_DISTANCE_RATIO = 0.25;
    static final double ARROW_SAME_STATE_DEFAULT_CONTROL_DISTANCE_RATIO = 8;
    static final double ARROW_SAME_STATE_DEFAULT_CONTROL_ANGLE = Math.PI / 4;
    static final double ARROW_PRESS_OPACITY = 0.8;
    static final long ARROW_PRESS_DURATION = 300;

    static final double OPTION_RECTANGLE_MINIMIZED_HEIGHT = 10;
    static final double OPTION_RECTANGLE_MINIMIZED_WIDTH = 20;
    static final double OPTION_RECTANGLE_MARGIN = 10;
    static final double OPTION_RECTANGLE_MAXIMIZED_HEIGHT = OPTION_RECTANGLE_MINIMIZED_HEIGHT +
            4 * STATE_RADIUS + 3 * OPTION_RECTANGLE_MARGIN;
    static final double OPTION_RECTANGLE_MAXIMIZED_WIDTH = (6 + Math.cos(ARROW_ANGLE)) * STATE_RADIUS +
            4 * OPTION_RECTANGLE_MARGIN;

    static final double STATE_OPTION_RECTANGLE_DISTANCE_RATIO = 1.4;
    static final Color STATE_OPTION_RECTANGLE_OUTER_COLOR = Color.BLACK;
    static final Color STATE_OPTION_RECTANGLE_INNER_COLOR = Color.WHITE;

    static final double TRANSITION_OPTION_RECTANGLE_MAXIMIZED_HEIGHT = OPTION_RECTANGLE_MAXIMIZED_HEIGHT + 50;

    static final double TAPE_WIDTH_RATIO = 4.0/5;
    static final double TAPE_HOBX_ARROW_HEIGHT = 80;
    static final double TAPE_HOBX_ARROW_WIDTH = 40;

    static final double TAPE_COORDINATES_WIDTH = 30;
    static final double TAPES_HEAD_MENU_HEIGHT = 50;
    static final double TAPES_HEAD_MENU_SPACING = 15;
    static final double TAPES_HEAD_MENU_HEAD_SIZE = 40;
    static final double TAPE_HEAD_MENU_HEAD_STROKE_WIDTH = 3;
    static final long EDIT_PRESS_DURATION = 300;
    static final Color EDIT_PRESS_COLOR = Color.DARKGRAY;
    static final Color TAPE_HEAD_MENU_DEFAULT_FILL_COLOR = Color.WHITE;

    static final double TAPE_CELL_WIDTH = 50;
    static final Integer TAPE_DEFAULT_TOP = 0;
    static final Integer TAPE_DEFAULT_BOTTOM = 0;
    static final Integer TAPE_DEFAULT_LEFT = null;
    static final Integer TAPE_DEFAULT_RIGHT = null;
    static final double TAPE_CELL_SYMBOL_FONT_SIZE = TAPE_CELL_WIDTH * 0.75;
    static final double TAPE_CELL_HEAD_SIZE = TAPE_CELL_WIDTH - 8;
    static final double TAPE_CELL_HEAD_STROKE_WIDTH = 3;

    static final int OPTION_RECTANGLE_SYMBOL_SIZE = 34;
    static final int OPTION_RECTANGLE_SYMBOL_SPACING = 15;
    static final int OPTION_RECTANGLE_SYMBOL_FONT_SIZE = 32;
    static final String OPTION_RECTANGLE_SYMBOL_FONT_NAME = "Cambria";

    static final double OPTION_RECTANGLE_HEAD_STROKE_WIDTH = 3;
    static final double OPTION_RECTANGLE_HEAD_SPACING = 15;
    static final int OPTION_RECTANGLE_HEAD_SIZE = 32;

    static final double TAPE_CELL_OPTION_COLOR_DIALOG_WIDTH = 300;

    static final int TAPE_TAPE_OPTION_RECTANGLE_SPACING = 15;
    static final int TAPE_TAPE_OPTION_RECTANGLE_ICON_WIDTH = 30;

    boolean animating;

    private Stage stage;
    GraphPane graphPane;
    private VBox tapesPaneVBox;
    TapesHeadMenu tapesHeadMenu;
    TapeBorderPanesHBox tapesPane;

    public MenuItem newButton;
    public MenuItem saveButton;
    public MenuItem loadButton;


    public TuringMachine machine;

    private Color nextHeadColor;
    private Map<Color, Pair<Tape, Integer>> headsColors;
    private static List<Color> defaultColors = Colors.allColors();
    private int defaultColorsIndex;

    GraphPaneMouseHandler graphPaneMouseHandler;
    TapesMouseHandler tapesMouseHandler;


    @Override
    public void start(Stage stage) throws Exception{
        WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth() * 3 / 4;
        HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight()* 3 / 4;

        this.headsColors = new HashMap<>();

        this.stage = stage;
        this.animating = false;
        this.defaultColorsIndex = 0;

        Subscriber s = new Subscriber() {
            @Override
            public void read(String msg, Object... parameters) {

                switch (msg) {
                    case TuringMachine.SUBSCRIBER_MSG_ADD_TAPE:{
                        Tape tape = (Tape) parameters[1];
                        addTapeFromMachine(tape);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_REMOVE_TAPE:{
                        Tape tape = (Tape) parameters[1];
                        removeTapeFromMachine(tape);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_ADD_HEAD:{
                        Tape tape = (Tape) parameters[1];
                        Integer head = (Integer) parameters[2];
                        Integer line = (Integer) parameters[3];
                        Integer column = (Integer) parameters[4];
                        addHeadFromMachine(tape, head, line, column);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_ADD_SYMBOL:{
                        String symbol = (String) parameters[1];
                        addSymbolFromMachine(symbol);
                    }
                    break;
                }
            }
        };

        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_TAPE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_REMOVE_TAPE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_HEAD);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_SYMBOL);

        this.machine = new TuringMachine();

        reinitDraw();

        this.stage.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
            WIDTH = newVal.intValue();
            resizePanes();
        });

        this.stage.getScene().heightProperty().addListener((obs, oldVal, newVal) -> {
            HEIGHT = newVal.intValue();
            resizePanes();
        });



        Tape tape = this.machine.addTape();
        tape.addHead();

        this.addSymbol("0");
        this.addSymbol("1");

        stage.show();
    }

    private void resizePanes(){
        graphPane.setMinWidth(WIDTH);
        graphPane.setMaxWidth(WIDTH);
        graphPane.setMinHeight((HEIGHT - MARGIN - SEPARATOR_WIDTH) * RATIO_HEIGHT_GRAPH_TAPES);
        graphPane.setMaxHeight((HEIGHT - MARGIN - SEPARATOR_WIDTH) * RATIO_HEIGHT_GRAPH_TAPES);

        tapesPaneVBox.setMinWidth(WIDTH);
        tapesPaneVBox.setMaxWidth(WIDTH);
        tapesPaneVBox.setMinHeight((HEIGHT - MARGIN - SEPARATOR_WIDTH) * (1 - RATIO_HEIGHT_GRAPH_TAPES));
        tapesPaneVBox.setMaxHeight((HEIGHT - MARGIN - SEPARATOR_WIDTH) * (1 - RATIO_HEIGHT_GRAPH_TAPES));
    }

    public void reinitDraw(){

        graphPaneMouseHandler = new GraphPaneMouseHandler(this);
        tapesMouseHandler = new TapesMouseHandler(this);

        graphPane = new GraphPane(this);
        tapesPaneVBox = new VBox();

        resizePanes();

        tapesPaneVBox.setAlignment(Pos.CENTER);

        tapesHeadMenu = new TapesHeadMenu(this);
        tapesHeadMenu.setTranslateX(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);

        tapesPane = new TapeBorderPanesHBox(this);
        tapesPaneVBox.getChildren().addAll(tapesHeadMenu, new Separator(), tapesPane);
        tapesPaneVBox.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.getWidth();
            double height = newVal.getHeight();
            tapesHeadMenu.setMinWidth(width);
            tapesHeadMenu.setMaxWidth(width);
            tapesPane.setMinHeight(height - TuringMachineDrawer.TAPES_HEAD_MENU_HEIGHT);
            tapesPane.setMaxHeight(height - TuringMachineDrawer.TAPES_HEAD_MENU_HEIGHT);
            tapesPane.setMinWidth(width);
            tapesPane.setMaxWidth(width);
        });


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
        separator.setMaxHeight(SEPARATOR_WIDTH);
        separator.setMinHeight(SEPARATOR_WIDTH);

        VBox box = new VBox();
        box.getChildren().addAll(menuBar, graphPane, separator, tapesPaneVBox);

        Scene scene = new Scene(box, WIDTH, HEIGHT);
        stage.setTitle("Turing Machine Editor");
        stage.setScene(scene);


    }

    ReadOnlyDoubleProperty screenWidthProperty(){
        return this.stage.widthProperty();
    }

    ReadOnlyDoubleProperty screenHeightProperty(){
        return this.stage.heightProperty();
    }

    void addSymbol(String symbol){
        this.machine.addSymbol(symbol);
    }

    void addSymbolFromMachine(String symbol){
        tapesPane.addSymbol(symbol);
        graphPane.transitionOptionRectangle.addSymbol(symbol);
    }

    void addTape(){
        this.machine.addTape();
    }

    private void addTapeFromMachine(Tape tape){
        graphPane.transitionOptionRectangle.addTape(tape);
        tapesHeadMenu.addTape(tape);
        tapesPane.addTape(tape);
    }

    void removeTape(Tape tape){ removeTape(tape, true);}

    private void removeTape(Tape tape, boolean doConfirm){
        if(doConfirm){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Supprimer la bande?");
            alert.setHeaderText("");
            alert.setContentText("Confirmer la suppression.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    this.machine.removeTape(tape);
            });
        }
        else
            this.machine.removeTape(tape);
    }

    private void removeTapeFromMachine(Tape tape){
        Iterator<Map.Entry<Color, Pair<Tape, Integer>>> it = headsColors.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Color, Pair<Tape, Integer>> entry = it.next();
            if(entry.getValue().first == tape)
                it.remove();
        }
        this.graphPane.transitionOptionRectangle.removeTape(tape);
        this.tapesHeadMenu.removeTape(tape);
        this.tapesPane.removeTape(tape);
    }

    void moveHead(Tape tape, int line, int column, int head) {
        tape.setInitialHeadColumn(head, column);
        tape.setInitialHeadLine(head, line);
    }

    private Optional<Color> colorDialog(){
        Dialog<Color> colorDialog = new Dialog<>();
        colorDialog.setTitle("Choose new head color.");
        colorDialog.initStyle(StageStyle.UTILITY);
        colorDialog.setWidth(TuringMachineDrawer.TAPE_CELL_OPTION_COLOR_DIALOG_WIDTH);
        colorDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


        VBox box = new VBox();
        box.setMaxWidth(TuringMachineDrawer.TAPE_CELL_OPTION_COLOR_DIALOG_WIDTH);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(5);

        ColorPicker colorPicker = new ColorPicker();

        Label warningLabel = new Label("This color was already chosen for another head.");
        warningLabel.setTextFill(Color.RED);
        warningLabel.setVisible(false);

        colorPicker.setOnAction(actionEvent ->
        {
            boolean notavailable = headsColors.keySet().contains(colorPicker.getValue());
            warningLabel.setVisible(notavailable);
            colorDialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(notavailable);
        });

        box.getChildren().addAll(colorPicker, warningLabel);

        colorDialog.getDialogPane().setContent(box);
        colorDialog.setResultConverter(buttonType ->{
            if(buttonType == ButtonType.OK){
                return colorPicker.getValue();
            }
            return null;
        });

        return colorDialog.showAndWait();
    }

    void addHead(Tape tape, int line, int column) {
        this.colorDialog().ifPresent(color -> {
            this.nextHeadColor = color;
            tape.addHead(line, column);
        });
    }

    private Color getDefaultColor(){
        return defaultColors.get(defaultColorsIndex++);
    }

    void addHeadFromMachine(Tape tape, int head, int line, int column){
        Color color = nextHeadColor;
        if(color == null)
            color = getDefaultColor();

        nextHeadColor = null;

        headsColors.put(color, new Pair<>(tape, head));
        graphPane.transitionOptionRectangle.addHead(tape, color);
        tapesHeadMenu.addHead(tape, color);
        tapesPane.addHead(tape, line, column, color);
    }

    Pair<Tape, Integer> getHead(Color color) {
        return headsColors.get(color);
    }

    public void editHeadColor(Color color) {
        Pair<Tape, Integer> pair = getHead(color);
        Tape tape = pair.first;
        Integer head = pair.second;
        this.colorDialog().ifPresent(color2 -> {
            this.headsColors.remove(color);
            this.headsColors.put(color2, pair);
            graphPane.transitionOptionRectangle.editHeadColor(tape, head, color2);
            tapesHeadMenu.editHeadColor(tape, head, color2);
            tapesPane.editHeadColor(tape, head, color2);
        });
    }

    void translateTo(Color color) {
        Pair<Tape, Integer> pair = getHead(color);
        tapesPane.translateTo(pair.first, pair.second);
    }

    public static void main(String[] args) {
        Platform.setImplicitExit(true);
        launch(args);
        System.exit(0);
    }
}
