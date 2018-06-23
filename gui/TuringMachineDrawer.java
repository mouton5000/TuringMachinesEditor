package gui;

import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import turingmachines.Tape;
import turingmachines.Transition;
import turingmachines.TuringMachine;
import util.BidirMap;
import util.Colors;
import util.Pair;
import util.Subscriber;

import java.util.LinkedList;
import java.util.List;

public class TuringMachineDrawer extends Application {

    private static final int SEPARATOR_WIDTH = 2;
    private static final int MARGIN = 30;
    private static int WIDTH;
    private static int HEIGHT;
    static final int GRAPH_GRID_WIDTH = 10;
    private static final double RATIO_HEIGHT_GRAPH_TAPES = 1.0/2;
    static final String SYMBOL_FONT_NAME = "Cambria";

    static final double CURRENT_STATE_ANIMATION_DURATION = 500;
    static final Color STATE_CURRENT_COLOR = Color.DARKBLUE;
    static final double TRANSITION_FIRED_ANIMATION_DURATION = 500;
    static final Color TRANSITION_FIRED_COLOR = Color.RED;
    static final double TRANSITION_FIRED_STROKE_WIDTH = 10;
    static final double HEAD_WRITE_ANIMATION_DURATION = 250;
    static final double HEAD_WRITE_STROKE_WIDTH = 10;
    static final double HEAD_MOVE_ANIMATION_DURATION = 500;
    static final double SYMBOL_WRITE_ANIMATION_DURATION = 500;

    static final String BLANK_SYMBOL = "\u2205";
    static final String LEFT_SYMBOL = "\u21D0";
    static final String RIGHT_SYMBOL = "\u21D2";
    static final String DOWN_SYMBOL = "\u21D3";
    static final String UP_SYMBOL = "\u21D1";
    static final String NO_ACTION_SYMBOL = "-";

    static final int STATE_RADIUS = 20;
    static final double FINAL_STATE_RADIUS_RATIO = 0.8;
    static final Color STATE_OUTER_COLOR = Color.BLACK;
    static final Color SELECTED_STATE_COLOR = Color.GRAY;
    static final Color UNSELECTED_STATE_COLOR = Color.WHITE;
    static final Color STATE_PRESS_COLOR = Color.DARKGRAY;
    static final long STATE_PRESS_DURATION = 300;
    static final int STATE_NAME_FONT_SIZE = 12;
    static final String STATE_NAME_FONT_NAME = "Cambria";

    static final double TRANSITION_ANGLE = Math.PI/6;
    static final double TRANSITION_SIZE = STATE_RADIUS;
    static final double TRANSITION_HITBOX_WIDTH = STATE_RADIUS * 2.3;
    static final double TRANSITION_KEY_RADIUS = 8;
    static final Color TRANSITION_KEY_COLOR = Color.GREENYELLOW;
    static final Color TRANSITION_KEY_STROKE_COLOR = Color.BLACK;
    static final Color TRANSITION_KEY_LINE_COLOR = Color.GREENYELLOW.darker();
    static final double TRANSITION_KEY_LINE_STROKE_WIDTH = 3;
    static final double TRANSITION_KEY_DISTANCE_RATIO = 0.25;
    static final double TRANSITION_SAME_STATE_DEFAULT_CONTROL_DISTANCE_RATIO = 8;
    static final double TRANSITION_SAME_STATE_DEFAULT_CONTROL_ANGLE = Math.PI / 4;
    static final double TRANSITION_PRESS_OPACITY = 0.8;
    static final long TRANSITION_PRESS_DURATION = 300;
    static final int TRANSITION_SYMBOL_FONT_SIZE = 15;
    static final double TRANSITION_DISPLAY_MAX_HEIGHT = 30;
    static final double TRANSITION_DISPLAY_MAX_WIDTH = 200;
    static final double TRANSITION_DISPLAY_MARGIN = 20;
    static final int TRANSITION_DISPLAY_SPACING = 5;

    static final double OPTION_RECTANGLE_MINIMIZED_HEIGHT = 10;
    static final double OPTION_RECTANGLE_MINIMIZED_WIDTH = 20;
    static final double OPTION_RECTANGLE_MARGIN = 10;
    static final double OPTION_RECTANGLE_MAXIMIZED_HEIGHT = OPTION_RECTANGLE_MINIMIZED_HEIGHT +
            4 * STATE_RADIUS + 3 * OPTION_RECTANGLE_MARGIN;
    static final double OPTION_RECTANGLE_MAXIMIZED_WIDTH = (6 + Math.cos(TRANSITION_ANGLE)) * STATE_RADIUS +
            4 * OPTION_RECTANGLE_MARGIN;
    static final int OPTION_RECTANGLE_TIMELINE_DURATION = 200;

    static final double STATE_OPTION_RECTANGLE_DISTANCE_RATIO = 1.4;
    static final Color STATE_OPTION_RECTANGLE_OUTER_COLOR = Color.BLACK;
    static final Color STATE_OPTION_RECTANGLE_INNER_COLOR = Color.WHITE;
    static final double STATE_OPTION_RECTANGLE_SPACING = STATE_RADIUS / 2;

    static final double TRANSITION_OPTION_RECTANGLE_MAXIMIZED_HEIGHT =
            OPTION_RECTANGLE_MAXIMIZED_HEIGHT * 3.0 / 2 + 50;
    static final Color TRANSITION_OPTION_RECTANGLE_SELECTED_FILL_COLOR = Color.LIGHTGRAY;
    static final Color TRANSITION_OPTION_RECTANGLE_UNSELECTED_FILL_COLOR = Color.WHITE;

    static final double TAPE_WIDTH_RATIO = 4.0/5;
    static final double TAPE_HOBX_ARROW_HEIGHT = 80;
    static final double TAPE_HOBX_ARROW_WIDTH = 40;

    static final double TAPE_COORDINATES_WIDTH = 30;
    static final double TAPES_MENU_HEIGHT = 50;
    static final double TAPES_MENU_SPACING = 15;
    static final double TAPES_HEAD_MENU_HEAD_SIZE = 40;
    static final double TAPE_HEAD_MENU_HEAD_STROKE_WIDTH = 3;
    static final long EDIT_PRESS_DURATION = 300;
    static final Color EDIT_PRESS_COLOR = Color.DARKGRAY;
    static final Color TAPE_MENU_DEFAULT_FILL_COLOR = Color.WHITE;
    static final Color TAPE_HEAD_MENU_CENTERED_FILL_COLOR = Color.LIGHTGRAY;
    static final Color TAPE_HEAD_MENU_NOT_CENTERED_FILL_COLOR = Color.WHITE;
    static final int TAPE_MENU_SYMBOL_FONT_SIZE = 35;
    static final int TAPE_MENU_SYMBOL_SIZE = 40;
    static final double TAPE_MENU_RATIO = 2.0 / 3;

    static final double TAPE_CELL_WIDTH = 50;
    static final Integer TAPE_DEFAULT_TOP = 0;
    static final Integer TAPE_DEFAULT_BOTTOM = 0;
    static final Integer TAPE_DEFAULT_LEFT = null;
    static final Integer TAPE_DEFAULT_RIGHT = null;
    static final double TAPE_CELL_SYMBOL_FONT_SIZE = TAPE_CELL_WIDTH * 0.75;
    static final double TAPE_CELL_HEAD_SIZE = TAPE_CELL_WIDTH - 8;
    static final double TAPE_CELL_HEAD_STROKE_WIDTH = 3;

    static final int OPTION_RECTANGLE_SYMBOL_SIZE = 34;
    static final int OPTION_RECTANGLE_SYMBOL_SPACING = 8;
    static final int OPTION_RECTANGLE_SYMBOL_FONT_SIZE = 32;

    static final double OPTION_RECTANGLE_HEAD_STROKE_WIDTH = 3;
    static final double OPTION_RECTANGLE_HEAD_SPACING = 15;
    static final int OPTION_RECTANGLE_HEAD_SIZE = 32;

    static final int TAPE_OPTION_RECTANGLE_SPACING = 15;
    static final int TAPE_OPTION_RECTANGLE_ICON_WIDTH = 30;

    static final double NOTIFICATION_WIDTH = 400;
    static final double NOTIFICATION_HEIGHT = 100;
    static final double NOTIFICATION_DURATION = 4000;
    static final int NOTIFICATION_FONT_SIZE = 25;
    static final String NOTIFICATION_FONT_NAME = "Cambria";

    static final double PLAYER_ICON_RADIUS = 20;
    static final double PLAYER_WIDTH = PLAYER_ICON_RADIUS * 13;
    static final double PLAYER_HEIGHT = 100;
    static final Color PLAYER_SELECTED_ICON_COLOR = Color.BLACK;
    static final Color PLAYER_UNSELECTED_ICON_COLOR = Color.LIGHTGRAY;

    boolean animating;
    boolean buildMode;
    boolean playing;

    private Stage stage;
    GraphPane graphPane;
    protected TapesVBox tapesPane;
    Notification notification;
    TuringPlayer player;

    public MenuItem newButton;
    public MenuItem saveButton;
    public MenuItem loadButton;


    public TuringMachine machine;

    private Color nextHeadColor;
    private BidirMap<Color, Pair<Tape, Integer>> headsColors;
    private static List<Color> defaultColors = Colors.allColors();
    private int defaultColorsIndex;

    GraphPaneMouseHandler graphPaneMouseHandler;
    TapesMouseHandler tapesMouseHandler;
    TuringPlayerMouseHandler turingPlayerMouseHandler;

    SequentialTransition machineTimeLine;
    ParallelTransition directTimeline;
    LinkedList<Timeline> toPlay;

    @Override
    public void start(Stage stage) throws Exception{
        WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth() * 3 / 4;
        HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight()* 3 / 4;

        this.headsColors = new BidirMap<>();

        this.stage = stage;
        this.animating = false;
        this.buildMode = false;
        this.playing = false;

        this.defaultColorsIndex = 0;

        this.machineTimeLine = new SequentialTransition();
        this.directTimeline = new ParallelTransition();
        toPlay = new LinkedList<>();

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
                    case TuringMachine.SUBSCRIBER_MSG_REMOVE_HEAD:{
                        Tape tape = (Tape) parameters[1];
                        Integer head = (Integer) parameters[2];
                        removeHeadFromMachine(tape, head);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_ADD_SYMBOL:{
                        String symbol = (String) parameters[1];
                        addSymbolFromMachine(symbol);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_EDIT_SYMBOL:{
                        Integer index = (Integer) parameters[1];
                        String prevSymbol = (String) parameters[2];
                        String symbol = (String) parameters[3];
                        editSymbolFromMachine(index, prevSymbol, symbol);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_REMOVE_SYMBOL:{
                        Integer index = (Integer) parameters[1];
                        String symbol = (String) parameters[2];
                        removeSymbolFromMachine(index, symbol);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_ERROR:{
                        String error_msg = (String) parameters[1];
                        notifyMsg(error_msg);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_CURRENT_STATE_CHANGED:{
                        Integer state = (Integer)parameters[1];
                        toPlay.add(graphPane.getChangeCurrentStateTimeline(state));
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_FIRED_TRANSITION:{
                        Transition transition = (Transition) parameters[1];
                        toPlay.add(graphPane.getFiredTransitionTimeline(transition));
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED:{
                        Tape tape = (Tape) parameters[1];
                        Integer head = (Integer) parameters[2];
                        Integer line = (Integer) parameters[3];
                        Integer column = (Integer) parameters[4];
                        toPlay.add(tapesPane.getMoveHeadTimeline(tape, head, line, column));
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE:{
                        Tape tape = (Tape) parameters[1];
                        Integer head = (Integer) parameters[2];
                        toPlay.add(tapesPane.getHeadWriteTimeline(tape, head));
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_SYMBOL_WRITTEN:{
                        Tape tape = (Tape) parameters[1];
                        Integer line = (Integer) parameters[2];
                        Integer column = (Integer) parameters[3];
                        String symbol = (String) parameters[4];
                        Timeline timeline = tapesPane.getWriteSymbolTimeline(tape, line, column, symbol);
                        if(timeline != null)
                            toPlay.add(timeline);
                    }
                    break;
                }
            }
        };

        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_TAPE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_REMOVE_TAPE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_HEAD);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_REMOVE_HEAD);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_SYMBOL);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_EDIT_SYMBOL);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_REMOVE_SYMBOL);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ERROR);

        s.subscribe(TuringMachine.SUBSCRIBER_MSG_CURRENT_STATE_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_FIRED_TRANSITION);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_SYMBOL_WRITTEN);

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

        tapesPane.setMinWidth(WIDTH);
        tapesPane.setMaxWidth(WIDTH);
        tapesPane.setMinHeight((HEIGHT - MARGIN - SEPARATOR_WIDTH) * (1 - RATIO_HEIGHT_GRAPH_TAPES));
        tapesPane.setMaxHeight((HEIGHT - MARGIN - SEPARATOR_WIDTH) * (1 - RATIO_HEIGHT_GRAPH_TAPES));

        notification.setLayoutX(WIDTH / 2);
        player.setLayoutX(WIDTH - PLAYER_WIDTH / 2);
    }

    public void reinitDraw(){

        graphPaneMouseHandler = new GraphPaneMouseHandler(this);
        tapesMouseHandler = new TapesMouseHandler(this);
        turingPlayerMouseHandler = new TuringPlayerMouseHandler(this);

        graphPane = new GraphPane(this);
        tapesPane = new TapesVBox(this);
        notification = new Notification();
        player = new TuringPlayer(this);

        notification.setLayoutY(MARGIN + NOTIFICATION_HEIGHT / 2);
        player.setLayoutY(MARGIN + PLAYER_HEIGHT / 2);
        resizePanes();

        tapesPane.setAlignment(Pos.CENTER);

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

        Pane mainPane = new Pane();

        VBox box = new VBox();
        box.getChildren().addAll(menuBar, graphPane, separator, tapesPane);

        mainPane.getChildren().addAll(box, player, notification);
        Scene scene = new Scene(mainPane, WIDTH, HEIGHT);
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

    private void addSymbolFromMachine(String symbol){
        graphPane.addSymbol(symbol);
        tapesPane.addSymbol(symbol);
    }

    void editSymbol(int index, String symbol){
        this.machine.editSymbol(index, symbol);
    }

    private void editSymbolFromMachine(int index, String prevSymbol, String symbol){
        graphPane.editSymbol(index, prevSymbol, symbol);
        tapesPane.editSymbol(index, prevSymbol, symbol);
    }

    void removeSymbol(int index){
        this.machine.removeSymbol(index);
    }

    private void removeSymbolFromMachine(int index, String symbol){
        graphPane.removeSymbol(index, symbol);
        tapesPane.removeSymbol(index, symbol);
    }

    void addTape(){
        this.machine.addTape();
    }

    private void addTapeFromMachine(Tape tape){
        graphPane.addTape(tape);
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
        graphPane.removeTape(tape);
        tapesPane.removeTape(tape);
    }

    void moveHead(Tape tape, int line, int column, int head) {
        tape.setInitialHeadColumn(head, column);
        tape.setInitialHeadLine(head, line);
    }

    void addHead(Tape tape, int line, int column, Color color) {
        if(!isAvailable(color)) {
            notifyMsg("That color was already given to another head.");
            return;
        }
        this.nextHeadColor = color;
        tape.addHead(line, column);
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
        graphPane.addHead(tape, color);
        tapesPane.addHead(tape, color, line, column);
    }

    private boolean isAvailable(Color color) {
        return !headsColors.containsK(color);
    }

    void editHeadColor(Tape tape, int head, Color color) {
        if(!isAvailable(color)) {
            notifyMsg("That color was already given to another head.");
            return;
        }
        graphPane.editHeadColor(tape, head, color);
        tapesPane.editHeadColor(tape, head, color);
        this.headsColors.put(color, new Pair<>(tape, head));
    }

    Pair<Tape, Integer> getHead(Color color){
        return headsColors.getV(color);
    }

    Color getColorOfHead(Tape tape, int head) { return headsColors.getK(new Pair<>(tape, head)); }

    void removeHead(Tape tape, int head, boolean doConfirm){
        if(doConfirm){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Supprimer la tête?");
            alert.setHeaderText("");
            alert.setContentText("Confirmer la suppression.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    tape.removeHead(head);
            });
        }
        else
            tape.removeHead(head);
    }

    void removeHeadFromMachine(Tape tape, int head){
        headsColors.removeV(new Pair<>(tape, head));
        for(Pair<Tape, Integer> pair : headsColors.values())
            if(pair.first == tape && pair.second > head)
                pair.second--;

        graphPane.removeHead(tape, head);
        tapesPane.removeHead(tape, head);
    }

    void centerOn(Tape tape, int head) {
        tapesPane.centerOn(tape, head);
    }

    void closeAllOptionRectangle(){
        graphPane.closeAllOptionRectangle();
        tapesPane.closeAllOptionRectangle();
    }

    void notifyMsg(String msg){
        notification.notifyMsg(msg);
    }

    void build() {
        buildMode = true;
        this.playing = false;
        closeAllOptionRectangle();
        graphPaneMouseHandler.unselect();

        this.machine.build();
        this.goToFirstConfiguration();
    }

    void reinitMachine(){
        this.directTimeline.setOnFinished(actionEvent -> {
            this.playing = false;
        });
        buildMode = false;
        this.playing = true;
        this.machine.reinit();
        this.machine.clearBuild();
        Timeline removeFirst = graphPane.getRemoveCurrentStateTimeline();
        if(removeFirst != null)
            toPlay.add(removeFirst);
        this.flushDirect();
    }

    void goToFirstConfiguration() {
        this.directTimeline.setOnFinished(actionEvent -> {
            this.playing = false;
        });
        this.playing = true;
        this.machine.reinit();
        this.flushDirect();
        this.player.setFirstFrame();
    }

    void goToLastConfiguration() {
        this.directTimeline.setOnFinished(actionEvent -> {
            this.playing = false;
        });
        this.playing = true;
        Timeline removeFirst = graphPane.getRemoveCurrentStateTimeline();
        while(this.machine.tick()){
            if(this.machine.isTerminated())
                break;
            clearTimeline();
        }
        if(removeFirst != null)
            toPlay.add(removeFirst);
        flushDirect();
        player.setLastFrame();
    }

    void tick(){
        this.playing = true;
        this.machineTimeLine.setOnFinished(actionEvent -> {
            this.playing = false;
        });
        if(this.machine.tick())
            flushTimeline();
        else {
            player.setLastFrame();
            this.playing = false;
        }
    }

    void play(){
        this.player.setPlay();
        this.machineTimeLine.setOnFinished(actionEvent -> {
            if(this.playing)
                this.play();
        });
        this.playing = true;

        if(this.machine.tick())
            flushTimeline();
        else {
            player.setLastFrame();
            this.playing = false;
        }

    }

    void pause(){
        this.player.setPause();
        this.playing = false;
    }

    void clearTimeline(){
        toPlay.clear();
    }

    void flushTimeline(){
        this.machineTimeLine.getChildren().clear();
        this.machineTimeLine.getChildren().addAll(this.toPlay);
        toPlay.clear();

        this.machineTimeLine.play();
    }

    void flushDirect(){
        this.directTimeline.getChildren().clear();
        this.directTimeline.getChildren().addAll(this.toPlay);
        toPlay.clear();

        this.directTimeline.play();
    }

    public static void main(String[] args) {
        Platform.setImplicitExit(true);
        launch(args);
        System.exit(0);
    }
}
