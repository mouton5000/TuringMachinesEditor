package gui;

import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.json.JSONObject;
import turingmachines.*;
import util.BidirMap;
import util.Colors;
import util.Pair;
import util.Subscriber;

import java.io.*;
import java.util.*;

public class TuringMachineDrawer extends Application {

    private static TuringMachineDrawer instance;

    static TuringMachineDrawer getInstance(){
        return instance;
    }


    private static final int SEPARATOR_WIDTH = 2;
    private static final int MARGIN = 30;
    static int WIDTH;
    static int HEIGHT;
    static final int GRAPH_GRID_WIDTH = 10;
    private static final double RATIO_HEIGHT_GRAPH_TAPES = 1.0/2;
    static final String SYMBOL_FONT_NAME = "Cambria";

    static long ANIMATION_DURATION = 500;
    static final Color STATE_CURRENT_COLOR = Color.DARKBLUE;
    static final Color TRANSITION_FIRED_COLOR = Color.RED;
    static final double TRANSITION_FIRED_STROKE_WIDTH = 10;
    static final double HEAD_WRITE_STROKE_WIDTH = 10;

    static final String BLANK_SYMBOL = "\u2205";
    static final String LEFT_SYMBOL = "\u21D0";
    static final String RIGHT_SYMBOL = "\u21D2";
    static final String DOWN_SYMBOL = "\u21D3";
    static final String UP_SYMBOL = "\u21D1";
    static final String NO_ACTION_SYMBOL = "-";

    static final int STATE_RADIUS = 25;
    static final double FINAL_STATE_RADIUS_RATIO = 0.8;
    static final Color STATE_OUTER_COLOR = Color.BLACK;
    static final Color SELECTED_STATE_COLOR = Color.GRAY;
    static final Color UNSELECTED_STATE_COLOR = Color.WHITE;
    static final Color STATE_PRESS_COLOR = Color.DARKGRAY;
    static final long STATE_PRESS_DURATION = 300;
    static final int STATE_NAME_FONT_SIZE = 20;
    static final String STATE_NAME_FONT_NAME = "Cambria";

    static final double TRANSITION_ANGLE = Math.PI/6;
    static final double TRANSITION_SIZE = STATE_RADIUS;
    static final double TRANSITION_HITBOX_WIDTH = STATE_RADIUS * 2;
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
    static final int TRANSITION_SYMBOL_FONT_SIZE = 25;
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

    static final double MENU_ICON_RADIUS = 20;
    static final double MENU_WIDTH = MENU_ICON_RADIUS * 15;
    static final double MENU_HEIGHT = MENU_ICON_RADIUS * 3;
    static final double MENU_SELECTED_OPACITY = 0.75;
    static final double MENU_UNSELECTED_OPACITY = 0.5;
    static final Color MENU_CLICKABLE_ICON_COLOR = Color.BLACK;
    static final Color MENU_NON_CLICKABLE_ICON_COLOR = Color.LIGHTGRAY;

    boolean animating;

    boolean enableToSave;

    boolean editGraphMode;
    boolean manualMode;
    boolean buildMode;
    boolean playing;

    private Stage stage;
    SplitPane splitPane;
    GraphPane graphPane;
    TapesVBox tapesPane;
    Notification notification;
    TuringMenu menu;
    HelpMessages help;

    public TuringMachine machine;

    private Color nextHeadColor;
    private BidirMap<Color, Pair<Tape, Integer>> headsColors;
    private static List<Color> defaultColors = Colors.allColors();
    private int defaultColorsIndex;

    private Double nextX;
    private Double nextY;

    private Double nextControl1X;
    private Double nextControl1Y;
    private Double nextControl2X;
    private Double nextControl2Y;

    GraphPaneMouseHandler graphPaneMouseHandler;
    TapesMouseHandler tapesMouseHandler;
    TuringMenuMouseHandler turingMenuMouseHandler;
    TuringMenuKeyHandler turingMenuKeyHandler;

    SequentialTransition machineTimeLine;
    ParallelTransition directTimeline;
    LinkedList<Timeline> toPlay;

    private String lastSaveFilename;



    @Override
    public void start(Stage stage) throws Exception{
        if(instance != null)
            return;

        instance = this;

        WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth() * 3 / 4;
        HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight()* 3 / 4;

        this.headsColors = new BidirMap<>();

        this.stage = stage;

        enableToSave = true;

        this.animating = false;
        this.editGraphMode = false;
        this.manualMode = false;
        this.buildMode = false;
        this.playing = false;

        this.defaultColorsIndex = 0;

        lastSaveFilename = null;

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
                    case TuringMachine.SUBSCRIBER_MSG_ADD_STATE:{
                        Integer state = (Integer) parameters[1];
                        addStateFromMachine(state);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_REMOVE_STATE:{
                        Integer state = (Integer) parameters[1];
                        removeStateFromMachine(state);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_ADD_TRANSITION:{
                        Transition transition = (Transition) parameters[1];
                        addTransitionFromMachine(transition);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_REMOVE_TRANSITION:{
                        Transition transition = (Transition) parameters[1];
                        removeTransitionFromMachine(transition);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_ADD_READ_SYMBOL:{
                        Transition transition = (Transition) parameters[1];
                        Tape tape = (Tape)parameters[2];
                        Integer head = (Integer)parameters[3];
                        String symbol = (String)parameters[4];
                        addReadSymbolFromMachine(transition, tape, head, symbol);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_REMOVE_READ_SYMBOL:{
                        Transition transition = (Transition) parameters[1];
                        Tape tape = (Tape)parameters[2];
                        Integer head = (Integer)parameters[3];
                        String symbol = (String)parameters[4];
                        removeReadSymbolFromMachine(transition, tape, head, symbol);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_ADD_ACTION:{
                        Transition transition = (Transition) parameters[1];
                        Tape tape = (Tape)parameters[2];
                        Integer head = (Integer)parameters[3];
                        ActionType type = (ActionType) parameters[4];
                        Object value = parameters[5];
                        addActionFromMachine(transition, tape, head, type, value);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_REMOVE_ACTION:{
                        Transition transition = (Transition) parameters[1];
                        Integer index = (Integer) parameters[2];
                        removeActionFromMachine(transition, index);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_SET_FINAL_STATE:{
                        Integer state = (Integer) parameters[1];
                        setFinalStateFromMachine(state, true);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_UNSET_FINAL_STATE:{
                        Integer state = (Integer) parameters[1];
                        setFinalStateFromMachine(state, false);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_SET_ACCEPTING_STATE:{
                        Integer state = (Integer) parameters[1];
                        setAcceptingStateFromMachine(state, true);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_UNSET_ACCEPTING_STATE:{
                        Integer state = (Integer) parameters[1];
                        setAcceptingStateFromMachine(state, false);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_SET_INITIAL_STATE:{
                        Integer state = (Integer) parameters[1];
                        setInitialStateFromMachine(state, true);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_UNSET_INITIAL_STATE:{
                        Integer state = (Integer) parameters[1];
                        setInitialStateFromMachine(state, false);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED: {
                        Tape tape = (Tape) parameters[1];
                        Integer head = (Integer) parameters[2];
                        Integer line = (Integer) parameters[3];
                        Integer column = (Integer) parameters[4];
                        moveHeadFromMachine(tape, line, column, head);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_INPUT_CHANGED: {
                        Tape tape = (Tape) parameters[1];
                        Integer line = (Integer) parameters[2];
                        Integer column = (Integer) parameters[3];
                        String symbol = (String) parameters[4];
                        setInputSymbolFromMachine(tape, line, column, symbol);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_TAPE_LEFT_CHANGED: {
                        Tape tape = (Tape) parameters[1];
                        Integer left = (Integer) parameters[2];
                        setTapeLeftBoundFromMachine(tape, left);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_TAPE_RIGHT_CHANGED: {
                        Tape tape = (Tape) parameters[1];
                        Integer right = (Integer) parameters[2];
                        setTapeRightBoundFromMachine(tape, right);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_TAPE_BOTTOM_CHANGED: {
                        Tape tape = (Tape) parameters[1];
                        Integer bottom = (Integer) parameters[2];
                        setTapeBottomBoundFromMachine(tape, bottom);
                    }
                    break;
                    case TuringMachine.SUBSCRIBER_MSG_TAPE_TOP_CHANGED: {
                        Tape tape = (Tape) parameters[1];
                        Integer top = (Integer) parameters[2];
                        setTapeTopBoundFromMachine(tape, top);
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

        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_REMOVE_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_TRANSITION);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_REMOVE_TRANSITION);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_READ_SYMBOL);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_REMOVE_READ_SYMBOL);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_ADD_ACTION);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_REMOVE_ACTION);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_SET_FINAL_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_UNSET_FINAL_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_SET_ACCEPTING_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_UNSET_ACCEPTING_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_SET_INITIAL_STATE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_UNSET_INITIAL_STATE);

        s.subscribe(TuringMachine.SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_INPUT_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_TAPE_LEFT_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_TAPE_RIGHT_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_TAPE_BOTTOM_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_TAPE_TOP_CHANGED);

        this.machine = new TuringMachine();

        initDraw();

        this.stage.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
            WIDTH = newVal.intValue();
            resizePanes();
        });

        this.stage.getScene().heightProperty().addListener((obs, oldVal, newVal) -> {
            HEIGHT = newVal.intValue();
            resizePanes();
        });

        stage.setOnCloseRequest(event -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.setTitle("Exit Turing machine editor.");
            alert.setHeaderText(null);
            if(enableToSave)
                alert.setContentText("The current file has not been save. Do you want to save before closing?");
            else
                alert.setContentText("Do you want to exit?");


            ButtonType yes = ButtonType.YES;
            ButtonType no = ButtonType.NO;
            ButtonType cancel = ButtonType.CANCEL;

            if(enableToSave)
                alert.getButtonTypes().setAll(yes, no, cancel);
            else
                alert.getButtonTypes().setAll(yes, no);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == yes){
                if(enableToSave)
                    if(!saveMachine())
                        event.consume();
            } else if (result.get() == no) {
                if(!enableToSave)
                    event.consume();
            } else if (result.get() == cancel)
                event.consume();
        });

        newMachine();

        stage.show();
    }

    private void initDraw(){

        graphPaneMouseHandler = new GraphPaneMouseHandler();
        tapesMouseHandler = new TapesMouseHandler();
        turingMenuMouseHandler = new TuringMenuMouseHandler();
        turingMenuKeyHandler = new TuringMenuKeyHandler();

        graphPane = new GraphPane();
        tapesPane = new TapesVBox();

        splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().addAll(graphPane, tapesPane);
        splitPane.setDividerPositions(0.5f);

        notification = new Notification();
        menu = new TuringMenu();
        help = new HelpMessages();

        notification.setLayoutY(NOTIFICATION_HEIGHT / 2);

        help.setLayoutX(0);
        help.setLayoutY(0);
        help.setVisible(false);

        menu.setLayoutY(MENU_HEIGHT / 2);
        resizePanes();

        tapesPane.setAlignment(Pos.CENTER);

        Separator separator = new Separator();
        separator.setMaxHeight(SEPARATOR_WIDTH);
        separator.setMinHeight(SEPARATOR_WIDTH);

        Pane mainPane = new Pane();

        mainPane.getChildren().addAll(splitPane, menu, notification, help);
        Scene scene = new Scene(mainPane, WIDTH, HEIGHT);
        scene.setOnKeyPressed(turingMenuKeyHandler);

        stage.setScene(scene);


    }

    private void resizePanes(){
        splitPane.setMinHeight(HEIGHT);
        splitPane.setMaxHeight(HEIGHT);

        graphPane.setMinWidth(WIDTH);
        graphPane.setMaxWidth(WIDTH);
        graphPane.setMinHeight(0);
        graphPane.setMaxHeight(HEIGHT - MARGIN);

        tapesPane.setMinWidth(WIDTH);
        tapesPane.setMaxWidth(WIDTH);
        tapesPane.setMinHeight(0);
        tapesPane.setMaxHeight(HEIGHT - MARGIN);

        notification.setLayoutX(WIDTH / 2);
        menu.setLayoutX(WIDTH - menu.getWidth() / 2);
        help.setFitWidth(WIDTH);
        help.setFitHeight(HEIGHT);


    }

    ReadOnlyDoubleProperty screenWidthProperty(){
        return this.stage.widthProperty();
    }

    ReadOnlyDoubleProperty screenHeightProperty(){
        return this.stage.heightProperty();
    }

    void addSymbol(String symbol){
        instance.machine.addSymbol(symbol);
    }

    private void addSymbolFromMachine(String symbol){
        graphPane.addSymbol(symbol);
        tapesPane.addSymbol(symbol);
        setEnableToSave();
    }

    void editSymbol(int index, String symbol){
        this.machine.editSymbol(index, symbol);
    }

    private void editSymbolFromMachine(int index, String prevSymbol, String symbol){
        graphPane.editSymbol(index, prevSymbol, symbol);
        tapesPane.editSymbol(index, prevSymbol, symbol);
        setEnableToSave();
    }

    void removeSymbol(int index){
        this.removeSymbol(index, true);
    }

    void removeSymbol(int index, boolean doConfirm){
        if(doConfirm){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Supprimer le symbole?");
            alert.setHeaderText("");
            alert.setContentText("Confirmer la suppression.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    this.machine.removeSymbol(index);
            });
        }
        else
            this.machine.removeSymbol(index);
    }

    private void removeSymbolFromMachine(int index, String symbol){
        graphPane.removeSymbol(index, symbol);
        tapesPane.removeSymbol(index, symbol);
        setEnableToSave();
    }

    void addTape(){
        this.machine.addTape();
    }

    private void addTapeFromMachine(Tape tape){
        graphPane.addTape(tape);
        tapesPane.addTape(tape);
        setEnableToSave();
    }

    void removeTape(Tape tape){ removeTape(tape, true);}

    void removeTape(Tape tape, boolean doConfirm){
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
        setEnableToSave();
    }

    void moveHead(Tape tape, int line, int column, int head) {
        tape.setInitialHeadColumn(head, column);
        tape.setInitialHeadLine(head, line);
        setEnableToSave();
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
        setEnableToSave();
    }

    boolean isAvailable(Color color) {
        return !headsColors.containsK(color);
    }

    private void moveHeadFromMachine(Tape tape, int line, int column, int head){
        tapesPane.moveHead(tape, line, column, head);
        setEnableToSave();
    }

    void editHeadColor(Tape tape, int head, Color color) {
        if(!isAvailable(color)) {
            notifyMsg("That color was already given to another head.");
            return;
        }
        graphPane.editHeadColor(tape, head, color);
        tapesPane.editHeadColor(tape, head, color);
        this.headsColors.put(color, new Pair<>(tape, head));
        setEnableToSave();
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

    private void removeHeadFromMachine(Tape tape, int head){
        headsColors.removeV(new Pair<>(tape, head));
        for(Pair<Tape, Integer> pair : new HashSet<>(headsColors.values())) {
            if (pair.first == tape && pair.second > head) {
                Color color = headsColors.removeV(pair);
                pair.second--;
                headsColors.put(color, pair);
            }
        }

        graphPane.removeHead(tape, head);
        tapesPane.removeHead(tape, head);
        setEnableToSave();
    }

    void setInputSymbol(Tape tape, int line, int column, String symbol){
        tape.writeInput(line, column, symbol);
    }

    private void setInputSymbolFromMachine(Tape tape, int line, int column, String symbol){
        tapesPane.setInputSymbol(tape, line, column, symbol);
        setEnableToSave();
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

    int addState(double x, double y){
        if(!editGraphMode)
            return -1;
        String name = graphPane.nextStateName();
        return this.addState(x, y, name);
    }

    int addState(double x, double y, String name){
        if(!editGraphMode)
            return -1;
        nextX = x;
        nextY = y;
        return machine.addState(name);
    }

    void addStateFromMachine(Integer state){
        graphPane.addState(nextX, nextY, state);
        setEnableToSave();
    }

    void removeState(Integer state) {
        removeState(state, true);
    }

    void removeState(Integer state, boolean doConfirm){
        if(!editGraphMode)
            return;

        if(doConfirm){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Supprimer l'état?");
            alert.setHeaderText("");
            alert.setContentText("Confirmer la suppression.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    TuringMachineDrawer.getInstance().machine.removeState(state);
            });
        }
        else
            TuringMachineDrawer.getInstance().machine.removeState(state);
    }

    void removeStateFromMachine(Integer state){
        graphPane.removeState(state);
        this.setEnableToSave();
    }

    Transition addTransition(Integer input, Integer output){
        if(!TuringMachineDrawer.getInstance().editGraphMode)
            return null;
        return addTransition(input, output, null, null, null, null);
    }


    Transition addTransition(Integer input, Integer output,
                             Double control1X, Double control1Y,
                             Double control2X, Double control2Y
    ){
        if(!editGraphMode)
            return null;

        this.nextControl1X = control1X;
        this.nextControl1Y = control1Y;
        this.nextControl2X = control2X;
        this.nextControl2Y = control2Y;

        return TuringMachineDrawer.getInstance().machine.addTransition(input, output);
    }

    private void addTransitionFromMachine(Transition transition){
        graphPane.addTransition(transition, nextControl1X, nextControl1Y, nextControl2X, nextControl2Y);

        this.setEnableToSave();
    }

    void removeTransition(Transition transition){
        removeTransition(transition, true);
    }

    void removeTransition(Transition transition, boolean doConfirm){
        if(!editGraphMode)
            return;

        if(doConfirm){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Supprimer la transition?");
            alert.setHeaderText("");
            alert.setContentText("Confirmer la suppression.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    machine.removeTransition(transition);
            });
        }
        else
            machine.removeTransition(transition);
    }

    void removeTransitionFromMachine(Transition transition){
        graphPane.removeTransition(transition);
        setEnableToSave();
    }

    void setFinalState(Integer state, boolean isFinal){
        if(isFinal)
            machine.setFinalState(state);
        else
            machine.unsetFinalState(state);
    }

    private void setFinalStateFromMachine(Integer state, boolean isFinal){
        graphPane.setFinalState(state, isFinal);
        setEnableToSave();
    }

    void setAcceptingState(Integer state, boolean isAccepting){
        if(isAccepting)
            machine.setAcceptingState(state);
        else
            machine.unsetAcceptingState(state);
    }

    private void setAcceptingStateFromMachine(Integer state, boolean isAccepting){
        graphPane.setAcceptingState(state, isAccepting);
        setEnableToSave();
    }

    void setInitialState(Integer state, boolean isInitial){
        if(isInitial)
            machine.setInitialState(state);
        else
            machine.unsetInitialState(state);
    }

    private void setInitialStateFromMachine(Integer state, boolean isInitial){
        graphPane.setInitialState(state, isInitial);
        setEnableToSave();
    }

    void addReadSymbol(Transition transition, Tape tape, int head, String symbol){
        symbol = (symbol.equals(TuringMachineDrawer.BLANK_SYMBOL))?
                null:symbol;
        transition.addReadSymbols(tape, head, symbol);
    }

    private void addReadSymbolFromMachine(Transition transition, Tape tape, int head, String symbol){
        graphPane.addReadSymbol(transition, tape, head, symbol);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    void removeReadSymbol(Transition transition, Tape tape, int head, String symbol){
        symbol = (symbol.equals(TuringMachineDrawer.BLANK_SYMBOL))?
                null:symbol;
        transition.removeReadSymbols(tape, head, symbol);
    }

    private void removeReadSymbolFromMachine(Transition transition, Tape tape, int head, String symbol){
        graphPane.removeReadSymbol(transition, tape, head, symbol);
        TuringMachineDrawer.getInstance().setEnableToSave();
    }

    void setTapeLeftBound(Tape tape, Integer left){
        tape.setLeftBound(left);
    }

    private void setTapeLeftBoundFromMachine(Tape tape, Integer left){
        tapesPane.setTapeLeftBound(tape, left);
        this.setEnableToSave();
    }

    void setTapeRightBound(Tape tape, Integer right){
        tape.setRightBound(right);
    }

    private void setTapeRightBoundFromMachine(Tape tape, Integer right){
        tapesPane.setTapeRightBound(tape, right);
        this.setEnableToSave();
    }

    void setTapeBottomBound(Tape tape, Integer bottom){
        tape.setBottomBound(bottom);
    }

    private void setTapeBottomBoundFromMachine(Tape tape, Integer bottom){
        tapesPane.setTapeBottomBound(tape, bottom);
        this.setEnableToSave();
    }

    void setTapeTopBound(Tape tape, Integer top){
        tape.setTopBound(top);
    }

    private void setTapeTopBoundFromMachine(Tape tape, Integer top){
        tapesPane.setTapeTopBound(tape, top);
        this.setEnableToSave();
    }

    void addAction(Transition transition, Tape tape, int head, String actionSymbol) {

        Action action;
        switch (actionSymbol){
            case TuringMachineDrawer.LEFT_SYMBOL:
                action = new MoveAction(tape, head, Direction.LEFT);
                break;
            case TuringMachineDrawer.RIGHT_SYMBOL:
                action = new MoveAction(tape, head, Direction.RIGHT);
                break;
            case TuringMachineDrawer.DOWN_SYMBOL:
                action = new MoveAction(tape, head, Direction.DOWN);
                break;
            case TuringMachineDrawer.UP_SYMBOL:
                action = new MoveAction(tape, head, Direction.UP);
                break;
            case TuringMachineDrawer.BLANK_SYMBOL:
                actionSymbol = null;
            default:
                action = new WriteAction(tape, head, actionSymbol);
                break;
        }

        transition.addAction(action);
    }

    void addActionFromMachine(Transition transition, Tape tape, int head, ActionType type, Object value){
        graphPane.addAction(transition, tape, head, type, value);
        setEnableToSave();
    }

    void removeAction(Transition transition){
        transition.removeAction(transition.getNbActions() - 1);
    }

    void removeActionFromMachine(Transition transition, int index){
        graphPane.removeAction(transition, index);
        setEnableToSave();
    }

    void setEditGraph(){
        notification.notifyMsg("Enter \"Add/remove node/transitions\" mode");
        this.editGraphMode = true;
        menu.setEditGraph();
    }

    void setNotEditGraph(){
        notification.notifyMsg("Quit \"Add/remove node/transitions\" mode");
        this.editGraphMode = false;
        menu.setNotEditGraph();
    }

    void setManual() {
        notification.notifyMsg("Enter \"Manual firing \" mode");
        menu.setManual();
        manualMode = true;
        this.playing = false;
        closeAllOptionRectangle();
        graphPaneMouseHandler.unselect();

        this.machine.buildManual();
        this.goToFirstConfiguration();
    }

    void setNotManual() {
        notification.notifyMsg("Quit \"Manual firing \" mode");
        menu.setNotManual();
        manualMode = false;

        this.playing = true;
        this.directTimeline.setOnFinished(actionEvent -> {
            this.playing = false;
        });

        this.machine.clearManual();
        Timeline removeFirst = graphPane.getRemoveCurrentStateTimeline();
        if(removeFirst != null)
            toPlay.add(removeFirst);
        this.flushDirect();

    }

    void manualSelectCurrentState(StateGroup stateGroup) {
        if(this.playing)
            return;
        machine.manualSetCurrentState(graphPane.getState(stateGroup));
        this.goToFirstConfiguration();
    }

    void manualFireTransition(TransitionGroup transitionGroup) {
        if(this.playing)
            return;
        machine.manualFireTransition(graphPane.getTransition(transitionGroup));

        this.playing = true;
        this.machineTimeLine.setOnFinished(actionEvent -> {
            this.playing = false;
        });

        this.menu.setPause();
        flushTimeline();
    }

    void build() {
        notification.notifyMsg("Enter \"Automatic firing \" mode");
        menu.setBuild();
        buildMode = true;
        this.playing = false;
        closeAllOptionRectangle();
        graphPaneMouseHandler.unselect();

        this.machine.build();
        this.goToFirstConfiguration();
    }

    void unbuild(){
        notification.notifyMsg("Quit \"Automatic firing \" mode");
        if(this.playing)
            return;
        menu.setNotBuild();
        buildMode = false;

        this.playing = true;
        this.directTimeline.setOnFinished(actionEvent -> {
            this.playing = false;
        });

        this.machine.loadFirstConfiguration();
        this.machine.clearBuild();
        Timeline removeFirst = graphPane.getRemoveCurrentStateTimeline();
        if(removeFirst != null)
            toPlay.add(removeFirst);
        this.flushDirect();
    }

    void goToPreviousConfiguration(){
        if(this.playing)
            return;
        this.directTimeline.setOnFinished(actionEvent -> {
            this.playing = false;
        });
        this.playing = true;
        if(this.machine.loadPreviousConfiguration()) {
            this.menu.setPause();
            this.flushDirect();
        }
        else {
            this.menu.setFirstFrame();
            this.playing = false;
        }
    }

    void goToFirstConfiguration() {
        if(this.playing)
            return;
        this.directTimeline.setOnFinished(actionEvent -> {
            this.playing = false;
        });
        this.playing = true;
        this.machine.loadFirstConfiguration();
        this.flushDirect();
        this.menu.setFirstFrame();
    }

    void goToLastConfiguration() {
        if(this.playing)
            return;
        this.directTimeline.setOnFinished(actionEvent -> {
            this.playing = false;
        });
        this.playing = true;
        this.machine.loadLastConfiguration();
        this.flushDirect();
        this.menu.setLastFrame();
    }


    void tick(){
        if(this.playing)
            return;
        this.playing = true;
        this.machineTimeLine.setOnFinished(actionEvent -> {
            this.playing = false;
        });
        if(this.machine.tick()) {
            this.menu.setPause();
            flushTimeline();
        }
        else {
            menu.setLastFrame();
            this.playing = false;
        }
    }

    void play(){
        this.menu.setPlay();
        this.machineTimeLine.setOnFinished(actionEvent -> {
            if(this.playing)
                this.play();
        });
        this.playing = true;

        if(this.machine.tick())
            flushTimeline();
        else {
            menu.setLastFrame();
            this.playing = false;
        }

    }

    void pause(){
        if(!this.playing)
            return;
        this.menu.setPause();
        this.playing = false;
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

    private void clearMachine(){
        if(buildMode)
            this.unbuild();
        this.machine.clear();
        this.graphPane.clear();
        this.tapesPane.clear();
    }

    void newMachine(){
        clearMachine();

        this.addTape();
        this.addHead(this.machine.getTape(0), 0, 0, Color.BLACK);
        this.addSymbol("0");
        this.addSymbol("1");

        lastSaveFilename = null;
        this.stage.setTitle("Turing Machine Editor");
        this.setNotEnableToSave();
    }

    void setEnableToSave(){
        if(enableToSave)
            return;
        enableToSave = true;
        this.stage.setTitle(this.stage.getTitle() + " *");
        menu.setSave(true);
    }

    private void setNotEnableToSave(){
        if(!enableToSave)
            return;
        enableToSave = false;
        if(this.stage.getTitle().endsWith(" *"))
            this.stage.setTitle(this.stage.getTitle().substring(0, this.stage.getTitle().length() - 2));
        menu.setSave(false);

    }

    boolean saveMachine(){
        if(lastSaveFilename != null)
            return saveAsMachine(lastSaveFilename);
        else
            return saveAsMachine();
    }

    boolean saveAsMachine() {
        if(!enableToSave)
            return false;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose save file");

        if(lastSaveFilename != null) {
            File dir = new File(lastSaveFilename).getParentFile();
            if (dir != null)
                fileChooser.setInitialDirectory(dir);
        }

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TuringMachine files", "*.tm")
        );

        File file = fileChooser.showOpenDialog(stage);
        if(file != null) {
            String filename = file.getAbsolutePath();
            if(!filename.endsWith(".tm"))
                filename += ".tm";
            return saveAsMachine(filename);
        }
        return false;
    }

    private boolean saveAsMachine(String filename){
        if(!enableToSave)
            return false;
        if(buildMode)
            this.unbuild();

        lastSaveFilename = filename;
        this.stage.setTitle(lastSaveFilename);

        JSONObject jsonMachine = getJSON();
        try {
            FileWriter fw = new FileWriter(filename, false);
            jsonMachine.write(fw);
            fw.close();
            setNotEnableToSave();
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    void loadMachine(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose loadMachine file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TuringMachine files", "*.tm")
        );

        File file = fileChooser.showOpenDialog(stage);
        if(file != null)
            loadMachine(file.getAbsolutePath());
    }

    private void loadMachine(String filename){
        StringBuilder sb = new StringBuilder();
        try {

            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while((line = br.readLine()) != null)
                sb.append(line);

            JSONObject jsonObject = new JSONObject(sb.toString());

            clearMachine();
            loadJSON(jsonObject);

            br.close();
            lastSaveFilename = filename;
            this.stage.setTitle(lastSaveFilename);
            setNotEnableToSave();
        } catch (IOException ignored) {

        }


    }

    void openParameters() {
        Dialog<Settings> dialog = Settings.getDialog(
                TuringMachineDrawer.ANIMATION_DURATION,
                TuringMachine.MAXIMUM_NON_DETERMINISTIC_SEARCH,
                tapesPane.getTapesString());
        Optional<Settings> result = dialog.showAndWait();

        if(result.isPresent()){
            Settings settings = result.get();
            ANIMATION_DURATION = settings.duration;
            TuringMachine.MAXIMUM_NON_DETERMINISTIC_SEARCH = settings.nbIterations;

            if(settings.changeTapesCells)
                tapesPane.eraseTapes(settings.tapesCellsDescription);

            this.setEnableToSave();
        }
    }

    private JSONObject getJSON(){
        JSONObject jsonOptions = new JSONObject();
        jsonOptions.put("animationDuration", ANIMATION_DURATION);
        jsonOptions.put("maximumNonDeterministicSearch", TuringMachine.MAXIMUM_NON_DETERMINISTIC_SEARCH);

        JSONObject jsonGraph = graphPane.getJSON();
        JSONObject jsonTape = tapesPane.getJSON();

        return new JSONObject()
                .put("options", jsonOptions)
                .put("graph", jsonGraph)
                .put("tapes", jsonTape);
    }

    private void loadJSON(JSONObject jsonObject){
        setEditGraph();

        JSONObject jsonOptions = jsonObject.getJSONObject("options");
        ANIMATION_DURATION = jsonOptions.getLong("animationDuration");
        TuringMachine.MAXIMUM_NON_DETERMINISTIC_SEARCH = jsonOptions.getInt("maximumNonDeterministicSearch");

        JSONObject jsonTapes = jsonObject.getJSONObject("tapes");
        tapesPane.loadJSON(jsonTapes);


        JSONObject jsonGraph =  jsonObject.getJSONObject("graph");
        graphPane.loadJSON(jsonGraph);

        setNotEditGraph();

    }

    public static void main(String[] args) {
        Platform.setImplicitExit(true);
        launch(args);
        System.exit(0);
    }
}
