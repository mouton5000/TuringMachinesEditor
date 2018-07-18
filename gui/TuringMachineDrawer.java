package gui;

import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.json.JSONObject;
import turingmachines.*;
import util.BidirMap;
import util.Pair;
import util.Subscriber;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Main class of the GUI.
 *
 * This class contains the main method and is then the starting point of the application. It build all the children
 * widgets and maintain them. Finally, this class acts as a controller for the {@link turingmachines} package
 * and is a interface for the {@link TuringMachine} object the GUI is displaying. This means that any time the user
 * sends a request to edit/modify the Turing machine, that request is relayed by this class. The only not-relayed
 * requests are the "read-only" requests that are occasionally sent by other objects.
 *
 * In order to get and interpret the requests answers, this class uses a {@link util.Subscriber} object that listen
 * to all the events the machine can send and react to those events. The {@link TuringMachine} object does not
 * directly send information back to the GUI so that the base class remains independent with the GUI.
 *
 * The class (almost) implements the singleton pattern. In order to get a TuringMachineDrawer instance, it is not
 * recommended to use the constructor. The static method {@link #getInstance()} should be used instead. As this class
 * is a JavaFX Application, it is not possible to make the constructor not public.
 *
 *
 *
 */
public class TuringMachineDrawer extends Application {

    /**
     * The unique instance of the class.
     */
    private static TuringMachineDrawer instance;

    /**
     * @return the unique instance of the class.
     */
    static TuringMachineDrawer getInstance(){
        return instance;
    }

    /**
     * Estimated size of the title bar.
     */
    private static final int MARGIN = 30;

    /**
     * Width of the window.
     */
    static int WIDTH;

    /**
     * Height of the window.
     */
    static int HEIGHT;

    /**
     * Size of an imaginary grid on which the nodes of the graph of the machine can be drawn. This enables a greater
     * precision when placing the nodes on the pane.
     */
    static final int GRAPH_GRID_WIDTH = 10;

    /**
     * Initial ratio between the {@link GraphPane} widget containing the graph and the {@link TapePane}
     * widget containing the tapes. This ratio can be modified manually by the user.
     */
    private static final double INITIAL_RATIO_HEIGHT_GRAPH_TAPES = 1.0/2;

    /**
     * Font name of the symbols drawn on the graph and on the tapes.
     */
    static final String SYMBOL_FONT_NAME = "Cambria";

    /**
     * Initial duration of the animations (used while the machine is executed). This value can be changed manually by
     * the users in the settings of the application.
     */
    static long ANIMATION_DURATION = 500;

    /**
     * Color used to represent the current state of the machine pointed by the state register while the machine is
     * executed.
     */
    static final Color STATE_CURRENT_COLOR = Color.DARKBLUE;

    /**
     * Color used to represent the current fired transition of the machine while the machine is executed.
     */
    static final Color TRANSITION_FIRED_COLOR = Color.RED;

    /**
     * Stroke width of the current fired transition of the machine while the machine is executed.
     */
    static final double TRANSITION_FIRED_STROKE_WIDTH = 10;

    /**
     * Stroke width of a head writing on a tape while the machine is executed.
     */
    static final double HEAD_WRITE_STROKE_WIDTH = 10;

    /**
     * Symbol used to represent the BLANK symbol.
     */
    static final String BLANK_SYMBOL = "\u2205";

    /**
     * Symbol used to represent the LEFT ARROW symbol.
     */
    static final String LEFT_SYMBOL = "\u21D0";

    /**
     * Symbol used to represent the RIGHT ARROW symbol.
     */
    static final String RIGHT_SYMBOL = "\u21D2";

    /**
     * Symbol used to represent the DOWN ARROW symbol.
     */
    static final String DOWN_SYMBOL = "\u21D3";

    /**
     * Symbol used to represent the TOP ARROW symbol.
     */
    static final String UP_SYMBOL = "\u21D1";

    /**
     * Symbol used when a transition can be fired whatever the symbols on the tape are.
     */
    static final String ALL_SYMBOLS_SYMBOL = "*";

    /**
     * Symbol used when a transition has no action to execute when it is fired.
     */
    static final String NO_ACTION_SYMBOL = "-";

    /**
     * Radius of a state of the machine.
     */
    static final int STATE_RADIUS = 25;

    /**
     * Ratio of the radius of the inner circle drawn when a state of the machine is declared final over the ratio of
     * the state.
     */
    static final double FINAL_STATE_RADIUS_RATIO = 0.8;

    /**
     * Color of circles the states
     */
    static final Color STATE_OUTER_COLOR = Color.BLACK;

    /**
     * Inner color of the selected states
     */
    static final Color SELECTED_STATE_COLOR = Color.GRAY;

    /**
     * Inner color of the unselected states.
     */
    static final Color UNSELECTED_STATE_COLOR = Color.WHITE;

    /**
     * Inner color of the state when the user press the mouse over it.
     */
    static final Color STATE_PRESS_COLOR = Color.DARKGRAY;

    /**
     * Duration (in millisecond) during which the user should press the mouse before a settings rectangle appears on
     * the screen.
     */
    static final long SETTINGS_PRESS_DURATION = 300;

    /**
     * Font size of the name of a state.
     */
    static final int STATE_NAME_FONT_SIZE = 20;

    /**
     * Font name of the name of a state.
     */
    static final String STATE_NAME_FONT_NAME = "Cambria";

    /**
     * Angle between each side of the arrow of a transition and its main line.
     */
    static final double TRANSITION_ANGLE = Math.PI/6;

    /**
     * Length of the sides of the arrow
     */
    static final double TRANSITION_SIZE = STATE_RADIUS;

    /**
     * Width of the hitbox of the transition
     */
    static final double TRANSITION_HITBOX_WIDTH = STATE_RADIUS * 2;

    /**
     * Radius of the two key circles used to redraw the transition
     */
    static final double TRANSITION_KEY_RADIUS = 8;

    /**
     * Inner color of the two key circles used to redraw the transition
     */
    static final Color TRANSITION_KEY_COLOR = Color.GREENYELLOW;

    /**
     * Outer color of the two key circles used to redraw the transition
     */
    static final Color TRANSITION_KEY_STROKE_COLOR = Color.BLACK;

    /**
     * Color of the lines linking the two key circles of the transition to the border of the transition
     */
    static final Color TRANSITION_KEY_LINE_COLOR = Color.GREENYELLOW.darker();

    /**
     * Stroke width of the lines linking the two key circles of the transition to the border of the transition
     */
    static final double TRANSITION_KEY_LINE_STROKE_WIDTH = 3;

    /**
     * Initial ratio between the distance between the two extremities of the transtion and the distance between one
     * extremity and its closest key circle.
     */
    static final double TRANSITION_KEY_DISTANCE_RATIO = 0.25;

    /**
     * Initial ratio between the distance between the radius of a state and the distance between the extremity and its
     * closest key circle when the transition link twice the same state.
     */
    static final double TRANSITION_SAME_STATE_DEFAULT_CONTROL_DISTANCE_RATIO = 8;

    /**
     * Initial angle between up direction (vector (0, 1)) and the line linking the extremity and its
     * closest key circle when the transition link twice the same state.
     */
    static final double TRANSITION_SAME_STATE_DEFAULT_CONTROL_ANGLE = Math.PI / 4;

    /**
     * Maximum opacity of a transition when a user press the mouse on it.
     */
    static final double TRANSITION_PRESS_OPACITY = 0.8;

    /**
     * Font size of the symbols written next to a transition.
     */
    static final int TRANSITION_SYMBOL_FONT_SIZE = 25;

    /**
     * Maximum height of the symbols written next to a transition.
     */
    static final double TRANSITION_DISPLAY_MAX_HEIGHT = 30;
    /**
     * Maximum width of the symbols written next to a transition.
     */
    static final double TRANSITION_DISPLAY_MAX_WIDTH = 200;

    /**
     * Distance between a transition and the symbols written next to that transition.
     */
    static final double TRANSITION_DISPLAY_MARGIN = 20;

    /**
     * Spacing between two symbols written next to that transition.
     */
    static final int TRANSITION_DISPLAY_SPACING = 5;

    static final double SETTINGS_RECTANGLE_MINIMIZED_HEIGHT = 10;
    static final double SETTING_RECTANGLE_MINIMIZED_WIDTH = 20;
    static final double SETTINGS_RECTANGLE_MARGIN = 10;
    static final double SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT = SETTINGS_RECTANGLE_MINIMIZED_HEIGHT +
            4 * STATE_RADIUS + 3 * SETTINGS_RECTANGLE_MARGIN;
    static final double SETTINGS_RECTANGLE_MAXIMIZED_WIDTH = (6 + Math.cos(TRANSITION_ANGLE)) * STATE_RADIUS +
            4 * SETTINGS_RECTANGLE_MARGIN;

    /**
     * Duration needed to animate the display of a settings rectangle.
     */
    static final int SETTINGS_RECTANGLE_TIMELINE_DURATION = 200;

    /**
     * Ratio between the radius of a state and the distance between the center of the state and the bottom of the
     * associated settings rectangle.
     */
    static final double STATE_SETTINGS_RECTANGLE_DISTANCE_RATIO = 1.4;

    static final Color STATE_SETTINGS_RECTANGLE_OUTER_COLOR = Color.BLACK;
    static final Color STATE_SETTINGS_RECTANGLE_INNER_COLOR = Color.WHITE;
    static final double STATE_SETTINGS_RECTANGLE_SPACING = STATE_RADIUS / 2;

    static final double TRANSITION_SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT =
            SETTINGS_RECTANGLE_MAXIMIZED_HEIGHT * 3.0 / 2 + 50;

    /**
     * Color in the rectangle representing a head currently selected by the user (to add a read symbol or an action)
     * and color around a currently selected read symbol of the transition.
     */
    static final Color TRANSITION_SETTINGS_RECTANGLE_SELECTED_FILL_COLOR = Color.LIGHTGRAY;

    /**
     * Color in the rectangle representing a head currently not selected by the user and color around a currently
     * not selected read symbol of the transition.
     */
    static final Color TRANSITION_SETTINGS_RECTANGLE_UNSELECTED_FILL_COLOR = Color.WHITE;

    /**
     * Ratio between the width of a tape when there is no other tape and the width of a tape when there are other
     * tapes to display.
     */
    static final double TAPE_WIDTH_RATIO = 4.0/5;

    /**
     * Height of the arrow used to display the next or the previous tape
     */
    static final double TAPE_HOBX_ARROW_HEIGHT = 80;

    /**
     * Width of the arrow used to display the next or the previous tape
     */
    static final double TAPE_HOBX_ARROW_WIDTH = 40;

    static final double TAPE_COORDINATES_WIDTH = 30;
    static final double TAPES_MENU_HEIGHT = 50;
    static final double TAPES_MENU_SPACING = 15;
    static final double TAPES_HEAD_MENU_HEAD_SIZE = 40;
    static final double TAPE_HEAD_MENU_HEAD_STROKE_WIDTH = 3;
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

    static final int SETTINGS_RECTANGLE_SYMBOL_SIZE = 34;
    static final int SETTINGS_RECTANGLE_SYMBOL_SPACING = 8;
    static final int SETTINGS_RECTANGLE_SYMBOL_FONT_SIZE = 32;

    static final double SETTINGS_RECTANGLE_HEAD_STROKE_WIDTH = 3;
    static final double SETTINGS_RECTANGLE_HEAD_SPACING = 15;
    static final int SETTINGS_RECTANGLE_HEAD_SIZE = 32;

    static final int TAPE_SETTINGS_RECTANGLE_SPACING = 15;
    static final int TAPE_SETTINGS_RECTANGLE_ICON_WIDTH = 30;

    static final double NOTIFICATION_WIDTH = 400;
    static final double NOTIFICATION_HEIGHT = 100;
    static final double NOTIFICATION_DURATION = 4000;
    static final int NOTIFICATION_FONT_SIZE = 25;
    static final String NOTIFICATION_FONT_NAME = "Cambria";

    static final double MENU_ICON_RADIUS = 20;
    static final double MENU_HEIGHT = MENU_ICON_RADIUS * 3;
    static final double MENU_SELECTED_OPACITY = 0.75;
    static final double MENU_UNSELECTED_OPACITY = 0.5;
    static final Color MENU_CLICKABLE_ICON_COLOR = Color.BLACK;
    static final Color MENU_NON_CLICKABLE_ICON_COLOR = Color.LIGHTGRAY;

    /**
     * Set to true when an animation is started. Enable or block the user possibilities when an animation starts.
     */
    boolean animating;

    /**
     * True if the machine was edited.
     */
    private boolean enableToSave;

    /**
     * True if the GUI allows the user to add and remove nodes and transitions.
     */
    boolean editGraphMode;

    /**
     * True if the GUI is in manual mode meaning that the user may manually fire transitions.
     */
    boolean manualMode;

    /**
     * True if the GUI is in automatic build mode meaning that the machine decides which transitions should be fired.
     */
    boolean buildMode;

    /**
     * True if the GUI is currently executing a machine.
     */
    boolean playing;

    private Stage stage;

    /**
     * Pane separating the graph and the tapes panes and allowing to redimension the two panes manually.
     */
    private SplitPane splitPane;

    /**
     * Pane displaying the graph of the machine
     */
    GraphPane graphPane;

    /**
     * Pane displaying the tapes of the machine.
     */
    TapesVBox tapesPane;

    /**
     * Small group displaying messages on the top of the application.
     */
    private Notification notification;

    /**
     * Small group displaying the menu on the top right corner of the application
     */
    TuringMenu menu;

    /**
     * Group displaying help screen shots on the screen when asked by the user.
     */
    HelpMessages help;

    /**
     * The machine edited and executed by the GUI.
     */
    TuringMachine machine;

    /**
     * The color of the next head that will be added to the machine. As there is no color in the heads of the
     * machine, after the user request the heads, the color he requested is stored here. Then, the machine
     * effectively adds the head and, when it is done, the drawer receives a message telling it that a head was
     * effectively added. The stored color is then used to identify the head.
     */
    private Color nextHeadColor;

    /**
     * Each head is identified with a unique color (whatever the tape it is on). Each color is then pointing on a
     * pair containing the tape of the head and the index of the head in the list of heads of the tape.
     */
    private BidirMap<Color, Pair<Tape, Integer>> headsColors;

    /**
     * The abscissa of the next state that will be added to the machine. As there is no coordinates for the states of
     * the machine, after the user request the state, the abscissa he requested is stored here. Then, the machine
     * effectively adds the state and, when it is done, the drawer receives a message telling it that a state was
     * effectively added. The stored abscissa is then used to place the state.
     */
    private Double nextX;

    /**
     * The ordinate of the next state that will be added to the machine. As there is no coordinates for the states of
     * the machine, after the user request the state, the ordinate he requested is stored here. Then, the machine
     * effectively adds the state and, when it is done, the drawer receives a message telling it that a state was
     * effectively added. The stored ordinate is then used to place the state.
     */
    private Double nextY;

    /**
     * The abscissa of the first key of the next transition that will be added to the machine. As there is no
     * coordinates for the transitions of the machine, after the user request the transition, the abscissa he
     * requested is stored here. Then, the machine effectively adds the transition and, when it is done, the drawer
     * receives a message telling it that a transition was effectively added. The stored abscissa is then used to
     * draw the transition.
     */
    private Double nextControl1X;

    /**
     * The ordinate of the first key of the next transition that will be added to the machine. As there is no
     * coordinates for the transitions of the machine, after the user request the transition, the ordinate he
     * requested is stored here. Then, the machine effectively adds the transition and, when it is done, the drawer
     * receives a message telling it that a transition was effectively added. The stored ordinate is then used to
     * draw the transition.
     */
    private Double nextControl1Y;

    /**
     * The abscissa of the second key of the next transition that will be added to the machine. As there is no
     * coordinates for the transitions of the machine, after the user request the transition, the abscissa he
     * requested is stored here. Then, the machine effectively adds the transition and, when it is done, the drawer
     * receives a message telling it that a transition was effectively added. The stored abscissa is then used to
     * draw the transition.
     */
    private Double nextControl2X;

    /**
     * The ordinate of the second key of the next transition that will be added to the machine. As there is no
     * coordinates for the transitions of the machine, after the user request the transition, the ordinate he
     * requested is stored here. Then, the machine effectively adds the transition and, when it is done, the drawer
     * receives a message telling it that a transition was effectively added. The stored ordinate is then used to
     * draw the transition.
     */
    private Double nextControl2Y;

    /**
     * Mouse handler of the GUI listening to all the mouse events.
     */
    MouseHandler mouseHandler;

    /**
     * Key handler of the GUI listening to all the key events.
     */
    TuringMenuKeyHandler turingMenuKeyHandler;

    /**
     * Timeline used to animate the execution of the machine
     */
    private SequentialTransition machineTimeLine;

    /**
     * Timeline used to animate the execution of the machine (when the user request going to a frame not accessible
     * by firing one transition : first, previous and last frames).
     */
    private ParallelTransition directTimeline;

    /**
     * List of animations that should be played by the {@link #machineTimeLine} or the {@link #directTimeline}
     * depending on what request he asked.
     */
    private LinkedList<Timeline> toPlay;

    /**
     * Name of the last file the user used to save the machine.
     */
    private String lastSaveFilename;

    @Override
    public void start(Stage stage){

        // For the singleton pattern
        if(instance != null)
            return;
        instance = this;

        // Default size of the window : 3/4 of the screen.
        WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth() * 3 / 4;
        HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight()* 3 / 4;

        this.headsColors = new BidirMap<>();

        this.stage = stage;

        // For the initialization, this boolean is set to true even if there is nothing to save.
        // The method #setNotEnableToSave is called later to set it to false.
        this.enableToSave = true;
        lastSaveFilename = null;

        this.animating = false;
        this.editGraphMode = false;
        this.manualMode = false;
        this.buildMode = false;
        this.playing = false;

        this.machineTimeLine = new SequentialTransition();
        this.directTimeline = new ParallelTransition();
        toPlay = new LinkedList<>();

        // Set the subscriber, listening to the machine messages.
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
                    case TuringMachine.SUBSCRIBER_MSG_EDIT_STATE_NAME:{
                        Integer state = (Integer) parameters[1];
                        String name = (String) parameters[2];
                        editStateNameFromMachine(state, name);
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
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_EDIT_STATE_NAME);
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

        // Init the widgets
        initDraw();

        // If the window is resized, the widgets are also resized.

        this.stage.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
            WIDTH = newVal.intValue();
            resizePanes();
        });

        this.stage.getScene().heightProperty().addListener((obs, oldVal, newVal) -> {
            HEIGHT = newVal.intValue();
            resizePanes();
        });

        // Close confirmation
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

            if(result.isPresent()) {
                if (result.get() == yes) {
                    if (enableToSave)
                        if (!saveMachine())
                            event.consume();
                } else if (result.get() == no) {
                    if (!enableToSave)
                        event.consume();
                } else if (result.get() == cancel)
                    event.consume();
            }
            else
                event.consume();
        });

        // Init the machine with the default machine as if the user asked a new machine (CTRL + N)
        newMachine();

        // Here we go
        stage.show();
    }

    /**
     * Init all the widgets
     */
    private void initDraw(){

        mouseHandler = new MouseHandler();
        turingMenuKeyHandler = new TuringMenuKeyHandler();

        graphPane = new GraphPane();
        tapesPane = new TapesVBox();

        // The graph and tapes panes are children by a split pane.
        // Thus the user may manually resize the two panes (by dragging the separation line)
        splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().addAll(graphPane, tapesPane);
        splitPane.setDividerPositions(INITIAL_RATIO_HEIGHT_GRAPH_TAPES);


        // The notification panel is placed on the top at the middle.
        // The x layout is set in the resizePanes() method.
        notification = new Notification();
        notification.setLayoutY(NOTIFICATION_HEIGHT / 2);

        // The help panel is a set of snapshots shown over the application if the user asks.
        help = new HelpMessages();
        help.setLayoutX(0);
        help.setLayoutY(0);
        help.setVisible(false);

        // The menu is set on the top right
        // The x layout is set in the resizePanes() method.
        menu = new TuringMenu();
        menu.setLayoutY(MENU_HEIGHT / 2);

        // Set all the width, height and x layout depending on the width and the height of the window.
        resizePanes();

        Pane mainPane = new Pane();
        mainPane.getChildren().addAll(splitPane, menu, notification, help);
        Scene scene = new Scene(mainPane, WIDTH, HEIGHT);

        scene.setOnKeyPressed(turingMenuKeyHandler);

        stage.setScene(scene);
    }

    /**
     * Set all the width, height and x layout depending on the width and the height of the window.
     */
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

    /*
     **************************************
     *
     * MACHINE INTERFACE
     *
     * All the following functions are used to communicate with the machine except for some methods which are purely
     * visual (and thus does only affect the GUI and not the machine).
     * For each method, a request is sent to the machine. The subscriber built in the start method is then listening
     * to any message the machine could send in return.
     *
     * Each message is then treated with a "XXXXFromMachine" method.
     *
     **************************************
     */

    /**
     * Request the machine to add the given symbol
     * @param symbol
     * @see #addSymbolFromMachine(String)
     */
    void addSymbol(String symbol){
        instance.machine.addSymbol(symbol);
    }

    /**
     * Response from the machine. The given symbol was added to the machine and the GUI must be updated.
     * @param symbol
     * @see #addSymbol(String)
     */
    private void addSymbolFromMachine(String symbol){
        // Update the widgets
        graphPane.addSymbol(symbol);
        tapesPane.addSymbol(symbol);
        setEnableToSave();
    }
    /**
     * Request the machine to edit the symbol (identified by the given index in the list of all the symbols) and to
     * replace it by the given symbol.
     * @param symbol
     * @see #editSymbolFromMachine(int, String, String)
     */
    void editSymbol(int index, String symbol){
        this.machine.editSymbol(index, symbol);
    }

    /**
     * Response from the machine. The symbol (identified by the given index in the list of all the symbols and the
     * given string prevSymbol) was replaced by the given symbol and the GUI must be updated.
     * @param index
     * @param prevSymbol
     * @param symbol
     * @see #editSymbol(int, String)
     */
    private void editSymbolFromMachine(int index, String prevSymbol, String symbol){
        // Update the widgets
        graphPane.editSymbol(index, prevSymbol, symbol);
        tapesPane.editSymbol(index, prevSymbol, symbol);
        setEnableToSave();
    }

    /**
     * Request the machine to remove the symbol (identified by the given index in the list of all the symbols). Ask
     * for a confirmation before.
     * @param index
     * @see #removeSymbol(int, boolean)
     * @see #removeSymbolFromMachine(int, String)
     */
    void removeSymbol(int index){
        this.removeSymbol(index, true);

    }

    /**
     * Request the machine to remove the symbol (identified by the given index in the list of all the symbols). If
     * doConfirm is true, a confirmation is asked before.
     * @param index
     * @see #removeSymbol(int)
     * @see #removeSymbolFromMachine(int, String)
     */
    void removeSymbol(int index, boolean doConfirm){
        if(doConfirm){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remove the symbol?");
            alert.setHeaderText("");
            alert.setContentText("Confirm the deletion.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    this.machine.removeSymbol(index);
            });
        }
        else
            this.machine.removeSymbol(index);
    }

    /**
     * Response from the machine. The symbol (identified by the given index in the list of all the symbols and the
     * given string symbol) was removed and the GUI must be updated.
     * @param index
     * @param symbol
     * @see #removeSymbol(int)
     * @see #removeSymbol(int, boolean)
     */
    private void removeSymbolFromMachine(int index, String symbol){
        // Update the widgets
        graphPane.removeSymbol(index, symbol);
        tapesPane.removeSymbol(index, symbol);
        setEnableToSave();
    }

    /**
     * Request the machine to add a new tape.
     * @see #addTapeFromMachine(Tape)
     */
    void addTape(){
        this.machine.addTape();
    }

    /**
     * Response from the machine. The given tape was added and the GUI must be updated.
     * @param tape
     * @see #addTape()
     */
    private void addTapeFromMachine(Tape tape){
        // Update the widgets
        graphPane.addTape(tape);
        tapesPane.addTape(tape);
        setEnableToSave();
    }

    /**
     * Request the machine to remove the given tape. Ask for a confirmation before.
     * @param tape
     * @see #removeTape(Tape, boolean)
     * @see #removeTapeFromMachine(Tape)
     */
    void removeTape(Tape tape){ removeTape(tape, true);}

    /**
     * Request the machine to remove the given tape. If doConfirm is true, ask for a confirmation before.
     * @param tape
     * @param doConfirm
     * @see #removeTape(Tape)
     * @see #removeTapeFromMachine(Tape)
     */
    void removeTape(Tape tape, boolean doConfirm){
        if(doConfirm){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remove the tape?");
            alert.setHeaderText("");
            alert.setContentText("Confirm the deletion.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    this.machine.removeTape(tape);
            });
        }
        else
            this.machine.removeTape(tape);
    }

    /**
     * Response from the machine. The given tape was removed and the GUI must be updated.
     * @param tape
     * @see #removeTape(Tape)
     * @see #removeTape(Tape, boolean)
     */
    private void removeTapeFromMachine(Tape tape){
        // Update the widgets
        graphPane.removeTape(tape);
        tapesPane.removeTape(tape);
        setEnableToSave();
    }

    /**
     * Request the machine to add a head to the given tape at the given line and the given column. Once this is done,
     * the color of the head on the GUI is the given color. If the color is already used, an error message is
     * displayed on the notification panel.
     * @param tape
     * @param line
     * @param column
     * @param color
     * @see #addHeadFromMachine(Tape, int, int, int)
     */
    void addHead(Tape tape, int line, int column, Color color) {
        if(!isAvailable(color)) {
            // Two heads cannot have the same color on the GUI.
            notifyMsg("That color was already given to another head.");
            return;
        }
        this.nextHeadColor = color;
        tape.addHead(line, column);
    }

    /**
     * Response from the machine. A head was added to the given tape with the given index (in the list of heads of
     * the tape) at the given line and the given column and the GUI must be updated. The color of the head is the one
     * that was previously choosed by the the #addHead method.
     * @param tape
     * @param head
     * @param line
     * @param column
     * @see #addHead(Tape, int, int, Color)
     */
    private void addHeadFromMachine(Tape tape, int head, int line, int column){
        Color color = nextHeadColor;

        nextHeadColor = null;

        headsColors.put(color, new Pair<>(tape, head));

        // Update the widgets
        graphPane.addHead(tape, color);
        tapesPane.addHead(tape, color, line, column);
        setEnableToSave();
    }

    /**
     * @param color
     * @return true if the given color is not already assigned to a head of the machine in the GUI.
     */
    boolean isAvailable(Color color) {
        return !headsColors.containsK(color);
    }

    /**
     * Request the machine to move the given head (identified by the given tape and the index of the head in the list
     * of heads of the tape) at the given line and the given column.
     * @param tape
     * @param line
     * @param column
     * @param head
     * @see #moveHeadFromMachine(Tape, int, int, int)
     */
    void moveHead(Tape tape, int line, int column, int head) {
        tape.setInitialHeadColumn(head, column);
        tape.setInitialHeadLine(head, line);
        setEnableToSave();
    }

    /**
     * Response from the machine. A head of the given tape with the given index (in the list of heads of
     * the tape) was moved at the given line and the given column and the GUI must be updated.
     * @param tape
     * @param head
     * @param line
     * @param column
     * @see #moveHead(Tape, int, int, int)
     */
    private void moveHeadFromMachine(Tape tape, int line, int column, int head){
        // Update the widget
        tapesPane.moveHead(tape, line, column, head);
        setEnableToSave();
    }

    /**
     * Change the color of the given head (identified by the index in the list of heads of
     * the given tape) to the given color.
     * @param tape
     * @param head
     * @param color
     */
    void editHeadColor(Tape tape, int head, Color color) {
        // This method is not requesting the machine as the machine does not color the heads, only the GUI does.

        if(!isAvailable(color)) {
            // Two heads cannot have the same color on the GUI.
            notifyMsg("That color was already given to another head.");
            return;
        }

        // Update the widgets.
        graphPane.editHeadColor(tape, head, color);
        tapesPane.editHeadColor(tape, head, color);
        this.headsColors.put(color, new Pair<>(tape, head));
        setEnableToSave();
    }

    /**
     * @param color
     * @return the head (identified by a pair of a tape and the index in the list of heads of the tape) assigned to
     * the given color.
     */
    Pair<Tape, Integer> getHead(Color color){
        return headsColors.getV(color);
    }

    /**
     * @param tape
     * @param head
     * @return the color assigned to the given head (identified by a pair of a tape and the index in the list of
     * heads of the tape).
     */
    Color getColorOfHead(Tape tape, int head) { return headsColors.getK(new Pair<>(tape, head)); }

    /**
     * Request the machine to remove the given head (identified by a pair of a tape and the index in the list of
     * heads of the tape). Ask for a confirmation before.
     * @param tape
     * @param head
     * @see #removeHead(Tape, int, boolean)
     * @see #removeHeadFromMachine(Tape, int)
     */
    void removeHead(Tape tape, int head){ this.removeHead(tape, head, true);}

    /**
     * Request the machine to remove the given head (identified by a pair of a tape and the index in the list of
     * heads of the tape). If doConfirm is true, ask for a confirmation before.
     * @param tape
     * @param head
     * @see #removeHead(Tape, int)
     * @see #removeHeadFromMachine(Tape, int)
     */
    void removeHead(Tape tape, int head, boolean doConfirm){
        if(doConfirm){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remove the head?");
            alert.setHeaderText("");
            alert.setContentText("Confirm the deletion.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    tape.removeHead(head);
            });
        }
        else
            tape.removeHead(head);
    }

    /**
     * Response from the machine. The given head (identified by a pair of a tape and the index in the list of
     * heads of the tape) was removed and the GUI must be updated.
     * @param tape
     * @param head
     * @see #removeHead(Tape, int)
     * @see #removeHead(Tape, int, boolean)
     */
    private void removeHeadFromMachine(Tape tape, int head){
        headsColors.removeV(new Pair<>(tape, head));

        // Update all the head indexes. Every head with a greater index than the removed head sees its index
        // decreased by one
        for(Pair<Tape, Integer> pair : new HashSet<>(headsColors.values())) {
            if (pair.first == tape && pair.second > head) {
                Color color = headsColors.removeV(pair);
                pair.second--;
                headsColors.put(color, pair);
            }
        }

        // Update the widgets
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

    void closeAllSettingsRectangle(){
        graphPane.closeAllSettingsRectangle();
        tapesPane.closeAllSettingsRectangle();
    }

    void notifyMsg(String msg){
        notification.notifyMsg(msg);
    }

    void addState(double x, double y){
        if(!editGraphMode)
            return;
        String name = graphPane.nextStateName();
        this.addState(x, y, name);
    }

    int addState(double x, double y, String name){
        if(!editGraphMode)
            return -1;
        nextX = x;
        nextY = y;

        // Must return the state for the #loadJSON method
        return machine.addState(name);
    }

    private void addStateFromMachine(Integer state){
        graphPane.addState(nextX, nextY, state);
        setEnableToSave();
    }

    void editStateName(Integer state, String name){
        this.machine.editStateName(state, name);
    }

    private void editStateNameFromMachine(Integer state, String name){
        this.graphPane.editStateName(state, name);
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
            alert.setTitle("Remove the state?");
            alert.setHeaderText("");
            alert.setContentText("Confirm the deletion.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    TuringMachineDrawer.getInstance().machine.removeState(state);
            });
        }
        else
            TuringMachineDrawer.getInstance().machine.removeState(state);
    }

    private void removeStateFromMachine(Integer state){
        graphPane.removeState(state);
        this.setEnableToSave();
    }

    void addTransition(Integer input, Integer output){
        addTransition(input, output, null, null, null, null);
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

        // Must return the state for the #loadJSON method
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
            alert.setTitle("Remove the transition?");
            alert.setHeaderText("");
            alert.setContentText("Confirm the deletion.");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK)
                    machine.removeTransition(transition);
            });
        }
        else
            machine.removeTransition(transition);
    }

    private void removeTransitionFromMachine(Transition transition){
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

    private void addActionFromMachine(Transition transition, Tape tape, int head, ActionType type, Object value){
        graphPane.addAction(transition, tape, head, type, value);
        setEnableToSave();
    }

    void removeAction(Transition transition){
        transition.removeAction(transition.getNbActions() - 1);
    }

    private void removeActionFromMachine(Transition transition, int index){
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
        closeAllSettingsRectangle();
        graphPane.unselect();

        this.machine.buildManual();
        this.goToFirstConfiguration();
    }

    void setNotManual() {
        notification.notifyMsg("Quit \"Manual firing \" mode");
        menu.setNotManual();
        manualMode = false;

        this.playing = true;
        this.directTimeline.setOnFinished(actionEvent -> this.playing = false);

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
        this.machineTimeLine.setOnFinished(actionEvent -> this.playing = false);

        this.menu.setPause();
        flushTimeline();
    }

    void build() {
        notification.notifyMsg("Enter \"Automatic firing \" mode");
        menu.setBuild();
        buildMode = true;
        this.playing = false;
        closeAllSettingsRectangle();
        graphPane.unselect();

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
        this.directTimeline.setOnFinished(actionEvent -> this.playing = false);

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
        this.directTimeline.setOnFinished(actionEvent -> this.playing = false);
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
        this.directTimeline.setOnFinished(actionEvent -> this.playing = false);
        this.playing = true;
        this.machine.loadFirstConfiguration();
        this.flushDirect();
        this.menu.setFirstFrame();
    }

    void goToLastConfiguration() {
        if(this.playing)
            return;
        this.directTimeline.setOnFinished(actionEvent -> this.playing = false);
        this.playing = true;
        this.machine.loadLastConfiguration();
        this.flushDirect();
        this.menu.setLastFrame();
    }


    void tick(){
        if(this.playing)
            return;
        this.playing = true;
        this.machineTimeLine.setOnFinished(actionEvent -> this.playing = false);
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

    private void flushTimeline(){
        this.machineTimeLine.getChildren().clear();
        this.machineTimeLine.getChildren().addAll(this.toPlay);
        toPlay.clear();

        this.machineTimeLine.play();
    }

    private void flushDirect(){
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
                machine.getMaximumNonDeterministicSearch(),
                tapesPane.getTapesString());
        Optional<Settings> result = dialog.showAndWait();

        if(result.isPresent()){
            Settings settings = result.get();
            ANIMATION_DURATION = settings.duration;
            machine.setMaximumNonDeterministicSearch(settings.nbIterations);

            if(settings.changeTapesCells)
                tapesPane.eraseTapes(settings.tapesCellsDescription);

            this.setEnableToSave();
        }
    }

    private JSONObject getJSON(){
        JSONObject jsonOptions = new JSONObject();
        jsonOptions.put("animationDuration", ANIMATION_DURATION);
        jsonOptions.put("maximumNonDeterministicSearch", machine.getMaximumNonDeterministicSearch());

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
        machine.setMaximumNonDeterministicSearch(jsonOptions.getInt("maximumNonDeterministicSearch"));

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
