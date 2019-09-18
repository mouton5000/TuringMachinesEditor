/*
 * Copyright (c) 2018 Dimitri Watel
 */

package turingmachines;

import util.Pair;
import util.Subscriber;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * This class represents a Turing machine.
 * See the documentation of the {@link turingmachines} package for more general informations on the possibilities of the
 * Turing machines represented by this class.
 *
 * This class should be used in order to work with a Turing machine. Most of the methods of this class are used to
 * <ul>
 *     <li>build the states graph of the machine with {@link #addState(String)}, {@link #removeState(int)}
 *     {@link #addTransition(Integer, Integer)}, {@link #removeTransition(Transition)}.</li>
 *     <li>define which states are initial, final, accepting with {@link #setFinalState(int)},
 *     {@link #unsetFinalState(int)}, {@link #setAcceptingState(int)}, {@link #unsetAcceptingState(int)},
 *     {@link #setInitialState(int)} and {@link #unsetInitialState(int)}.</li>
 *     <li>define the symbols the machine uses with {@link #addSymbol(String)}, {@link #editSymbol(int, String)} and
 *     {@link #removeSymbol(int)}.</li>
 *     <li>build and remove tapes with {@link #addTape()}, {@link #removeTape(int)} and {@link #removeTape(Tape)}.</li>
 * </ul>
 *
 * The {@link #clear()} may be used in order to completely clear the machine.
 *
 * Once the machine is built, the {@link #execute()} method may be used in order to run the machine. The machine
 * searches for an accepting path until the maximum number of iterations is reached. This maximum is given by
 * {@link #maximumNonDeterministicSearch} and can be accessed with the getter and setter
 * {@link #getMaximumNonDeterministicSearch()} and {@link #setMaximumNonDeterministicSearch(int)}.
 *
 * Other public methods were implemented in order to build the graphical interface. They can perfectly be used for
 * another purpose.
 *
 * @see turingmachines
 */
public class TuringMachine {

    /**
     * Message sent when a transition is fired. The parameters are the machine and the fired transition.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_FIRED_TRANSITION = "TMFireTransition";

    /**
     * Message sent when a head write on a tape. The parameters are the machine, the tape and the index of the head in
     * the list of heads of that tape.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_HEAD_WRITE = "TMHeadWrite";

    /**
     * Message sent when a symbol is written on a tape. The parameters are the machine, the tape, the line, the
     * column where the symbol is written and the symbol.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_SYMBOL_WRITTEN = "TMSymolWritten";

    /**
     * Message sent when a head is moved. The parameters are the machine, the tape, the index of the head in
     *      * the list of heads of that tape, the line and the column
     * where the head is moved.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_HEAD_MOVED = "TMHeadMoved";

    /**
     * Message sent when the current state pointed by the state register is changed. The parameters are the machine
     * and the new current state.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_CURRENT_STATE_CHANGED = "TMCurrentStateChanged";

    /**
     * Message sent when the exploration of the machine starts. The parameter is the machine.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START = "TMNonDeterministicExploreStart";

    /**
     * Message sent when the exploration of the machine ends. The parameter is the machine.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END = "TMNonDeterministicExploreEnd";

    /**
     * Message sent when a state is added to the machine. The parameters are the machine and the identifier of the state.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_ADD_STATE = "TMAddState";

    /**
     * Message sent when the name of a state is changed. The parameters are the machine, the identifier of the state
     * and the new name.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_EDIT_STATE_NAME = "TMEditStateName";

    /**
     * Message sent when a state is removed from the machine. The parameters are the machine and the identifier of the
     * state.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_REMOVE_STATE = "TMRemoveState";

    /**
     * Message sent when a state is declared as initial. The parameters are the machine and the identifier of the
     * state.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_SET_INITIAL_STATE = "TMSetInitialState";

    /**
     * Message sent when a state is declared as not initial. The parameters are the machine and the identifier of the
     * state.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_UNSET_INITIAL_STATE = "TMUnsetInitialState";

    /**
     * Message sent when a state is declared as final. The parameters are the machine and the identifier of the
     * state.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_SET_FINAL_STATE = "TMSetFinalState";

    /**
     * Message sent when a state is declared as not final. The parameters are the machine and the identifier of the
     * state.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_UNSET_FINAL_STATE = "TMUnsetFinalState";

    /**
     * Message sent when a state is declared as accepting. The parameters are the machine and the identifier of the
     * state.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_SET_ACCEPTING_STATE = "TMSetAcceptingState";

    /**
     * Message sent when a state is declared as not accepting. The parameters are the machine and the identifier of the
     * state.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_UNSET_ACCEPTING_STATE = "TMUnsetAcceptingState";

    /**
     * Message sent when a transition is added. The parameters are the machine and the transition.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_ADD_TRANSITION = "TMAddTransition";

    /**
     * Message sent when a transition is removed. The parameters are the machine and the transition.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_REMOVE_TRANSITION = "TMRemoveTransition";

    /**
     * Message sent when a read symbol (a couple head/symbol) is added to the transition
     * The parameters are the machine, the transition, the tape of the head, the head and the symbol.
     * @see util.Subscriber
     * @see Transition#addReadSymbols(Tape, int, String...)
     */
    public static final String SUBSCRIBER_MSG_ADD_READ_SYMBOL = "TMAddReadSymbol";

    /**
     * Message sent when a read symbol (a couple head/symbol) is removed from the transition
     * The parameters are the machine, the transition, the tape of the head, the head and the symbol.
     * @see util.Subscriber
     * @see Transition#removeReadSymbols(Tape, int, String...)
     */
    public static final String SUBSCRIBER_MSG_REMOVE_READ_SYMBOL = "TMRemoveReadSymbol";

    /**
     * Message sent when an action is added to the transition.
     * The parameters are the machine, the transition, the tape of the head associated with the action, the head
     * associated with the action, the type of action (moving a head or writing) and the value of the action (a
     * direction if it is a moving action and a symbol if it is a writing action).
     * @see util.Subscriber
     * @see Transition#addAction(Action)
     */
    public static final String SUBSCRIBER_MSG_ADD_ACTION = "TMAddAction";

    /**
     * Message sent when an action is removed from the list of actions of the transition.
     * The parameters are the machine, the transition and the index of the removed action in the list.
     * @see util.Subscriber
     * @see Transition#removeAction(int)
     */
    public static final String SUBSCRIBER_MSG_REMOVE_ACTION = "TMRemoveAction";

    /**
     * Message sent when a tape is added to the machine. The parameters are the machine and the tape.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_ADD_TAPE = "TMHeadAddTape";

    /**
     * Message sent when a tape is removed from the machine. The parameters are the machine and the tape.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_REMOVE_TAPE = "TMRemoveTape";

    /**
     * Message sent when the left bound of a tape is changed. The parameters are the machine, the tape and the new
     * bound (null if infinite).
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_TAPE_LEFT_CHANGED = "TMTapeLeftChanged";

    /**
     * Message sent when the right bound of a tape is changed. The parameters are the machine, the tape and the new
     * bound (null if infinite).
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_TAPE_RIGHT_CHANGED = "TMTapeRightChanged";

    /**
     * Message sent when the bottom bound of a tape is changed. The parameters are the machine, the tape and the new
     * bound (null if infinite).
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_TAPE_BOTTOM_CHANGED = "TMTapeBottomChanged";

    /**
     * Message sent when the top bound of a tape is changed. The parameters are the machine, the tape and the new
     * bound (null if infinite).
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_TAPE_TOP_CHANGED = "TMTapeTopChanged";

    /**
     * Message sent when a symbol is added to the machine. The parameters are the machine and the symbol.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_ADD_SYMBOL = "TMAddSymbol";

    /**
     * Message sent when the name of a symbol is edited. The parameters are the machine, the index of the symbol in the
     * list of
     * symbols of the machine, the previous name of the symbol and the new name symbol.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_EDIT_SYMBOL = "TMEditSymbol";

    /**
     * Message sent when a symbol is removed from the machine. The parameters are the
     * machine, the index of the symbol in the list of symbols of the machine and the symbol.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_REMOVE_SYMBOL = "TMRemoveSymbol";

    /**
     * Message sent when the input words of the machine change on one of the tape. The parameters are the
     * machine, the tape, the line and the column of the cell where the input is changed and the new symbol in that
     * cell.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_INPUT_CHANGED = "TMInputChanged";

    /**
     * Message sent when a head is added to a tape of the machine. The parameters are the
     * machine, the tape where the head is added, the index of this new head in the list of heads of the tape, the
     * line and the column where the head is added.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_ADD_HEAD = "TMHeadAddHead";

    /**
     * Message sent when a head is removed from a tape of the machine. The parameters are the
     * machine, the tape from which the head is removed and the index of the removed head in the list of heads of the
     * tape.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_REMOVE_HEAD = "TMRemoveHead";

    /**
     * Message sent when a head is moved. The parameters are the
     * machine, the tape of the head, the index of this head in the list of heads of the tape, the
     * line and the column where the head is moved.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED = "TMHeadInitialPositionChanged";

    /**
     * Message sent when an error occur. The parameters are the
     * machine and the error message.
     * @see util.Subscriber
     */
    public static final String SUBSCRIBER_MSG_ERROR = "TMError";

    /**
     * The initial value of the variable {@link #maximumNonDeterministicSearch}
     * @see #maximumNonDeterministicSearch
     */
    private static final int INITIAL_MAXIMUM_NON_DETERMINISTIC_SEARCH = 100000;

    /**
     * The maximum number of iterations before the machine stops its execution.
     *
     * Each detemrinistic/non deterministic machine search for an accepting path. If the search uses more iterations
     * than this value, it stops. In that case, if no accepting path is found, a non accepting path is returned. If
     * no such path was found, an error is returned. For a deterministic machine, this number of iterations equals
     * the number of iterations of the computation of the machine. For a non deterministic machine, it equals the
     * number of explored nodes in the exploration tree used to find an accepting path.
     *
     * Its initial value is given by {@link #INITIAL_MAXIMUM_NON_DETERMINISTIC_SEARCH}.
     * @see #INITIAL_MAXIMUM_NON_DETERMINISTIC_SEARCH
     */
    private int maximumNonDeterministicSearch;

    /**
     * Number of states of the graph of the machine.
     */
    private int nbStates;

    /**
     * For each state i, this list contains, at index i, the list of output transitions of the state.
     */
    private List<List<Transition>> outputTransitions;

    /**
     * For each state i, this list contains, at index i, the name of the state.
     */
    private List<String> statesNames;

    /**
     * For each state i, this list contains, at index i, true if and only if the state is final.
     */
    private List<Boolean> finalStates;

    /**
     * For each state i, this list contains, at index i, true if and only if the state is accepting. In that case it
     * is also final.
     */
    private List<Boolean> acceptingStates;

    /**
     * For each state i, this list contains, at index i, true if and only if the state is initial.
     */
    private List<Boolean> initialStates;

    /**
     * The list of tapes of the machine.
     */
    private List<Tape> tapes;

    /**
     * The state register pointing to the current state of the execution. Used only when the machine is running.
     */
    private Integer currentState;

    /**
     * List of symbols manipulated by the machine.
     */
    private List<String> symbols;

    /**
     * List of configurations in the last built path. In automatic build mode, such a path is built when the machine is
     * executed and, if such a path exists, should reach an accepting state. Otherwise, if such a path exists, it
     * should reach a final state.
     * Otherwise this pair should be null. Each configuration is associated with a transition that can be fired to
     * reach the next configuration.
     * In manual mode, should contain a list of consecutive configuration with the associated transitions.
     *
     * @see #build()
     * @see #buildManual()
     */
    private Pair<List<HardConfiguration>, List<Transition>> builtPath;

    private Semaphore stopExplorationSemaphore;

    private boolean stopExploration;

    private Executor buildExecutor;

    /**
     * List of indexes used to explore manually the list of configurations that was previously built.
     */
    private Pair<Integer, Integer> builtIndex;

    /**
     * Initial configuration of the manual mode.
     */
    private HardConfiguration manualInitialConfiguration;

    /**
     * Construction of the machine.
     *
     * A new machine is empty. It has no state, no transition, no tape and no symbol except for the BLANK symbol.
     */
    public TuringMachine(){
        nbStates = 0;
        maximumNonDeterministicSearch = INITIAL_MAXIMUM_NON_DETERMINISTIC_SEARCH;

        outputTransitions = new ArrayList<>();
        statesNames = new ArrayList<>();

        initialStates = new ArrayList<>();
        finalStates = new ArrayList<>();
        acceptingStates = new ArrayList<>();

        currentState = null;

        tapes = new ArrayList<>();
        symbols = new ArrayList<>();

        builtPath = null;
        stopExploration = false;
        stopExplorationSemaphore = new Semaphore(1);
    }

    /**
     * @return the maximum number of iterations the machine searches for an accepting path when it is executed.
     * @see #maximumNonDeterministicSearch
     */
    public int getMaximumNonDeterministicSearch() {
        return maximumNonDeterministicSearch;
    }

    /**
     * @param maximumNonDeterministicSearch the maximum number of iterations the machine searches for an accepting
     *                                       path when it is executed.
     * @see #maximumNonDeterministicSearch
     */
    public void setMaximumNonDeterministicSearch(int maximumNonDeterministicSearch) {
        this.maximumNonDeterministicSearch = maximumNonDeterministicSearch;
    }

    /**
     * Add a new transition from the state input to the state ouput.
     *
     * A {@link #SUBSCRIBER_MSG_ADD_TRANSITION} message is broadcast to the class {@link util.Subscriber}.
     * @param input
     * @param output
     * @return the new transition or null if input/ouput are not states of the graph.
     * @see util.Subscriber
     */
    public Transition addTransition(Integer input, Integer output){
        if(input < 0 || input >= getNbStates())
            return null;
        if(output < 0 || output >= getNbStates())
            return null;
        Transition a = new Transition(this, input, output);
        outputTransitions.get(input).add(a);

        for(Tape tape : this.tapes) {
            a.addTape(tape);
            for(int head = 0; head < tape.getNbHeads(); head++){
                a.addHead(tape);
            }
        }

        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_TRANSITION, this, a);
        return a;
    }

    /**
     * Remove the transition a if a is a transition of this machine.
     *
     * A {@link #SUBSCRIBER_MSG_REMOVE_TRANSITION} message is broadcast to the class {@link util.Subscriber}.
     * @param a
     *
     * @see util.Subscriber
     */
    public void removeTransition(Transition a){
        Integer input = a.getInput();
        if(input < 0 || input >= outputTransitions.size())
            return;
        List<Transition> transitions = outputTransitions.get(input);

        if(!transitions.remove(a))
            return;

        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_TRANSITION, this, a);
    }

    /**
     * @return the number of states of the graph.
     */
    public int getNbStates() {
        return nbStates;
    }

    /**
     * Add a new state to the machine with the given name. Note that the name is not a unique identifier of the
     * state. Each state is identified with its index in the list of state. Be aware that this index may change if
     * states are removed from the machine.
     *
     * A {@link #SUBSCRIBER_MSG_ADD_STATE} message is broadcast to the class {@link util.Subscriber}.
     * @param name
     * @return the index of the added state, the unique identifier of the state.
     * @see util.Subscriber
     */
    public int addState(String name){
        nbStates++;
        statesNames.add(name);
        outputTransitions.add(new ArrayList<>());
        initialStates.add(false);
        finalStates.add(false);
        acceptingStates.add(false);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_STATE, this, nbStates - 1);
        return nbStates - 1;
    }

    /**
     * @param state index of a state
     * @return the name of the state identified by the given index or null of the state is not in the machine.
     */
    public String getStateName(int state){
        if(state < 0 || state >= getNbStates())
            return null;
        return statesNames.get(state);
    }

    /**
     * Edit the name of the state identified by the given index. If the state is not in the machine, do nothing.
     * A {@link #SUBSCRIBER_MSG_EDIT_STATE_NAME} message is broadcast to the class {@link util.Subscriber}.
     * @param state index of a state
     * @param name the new name of the state
     * @see util.Subscriber
     */
    public void editStateName(int state, String name){
        if(state < 0 || state >= getNbStates())
            return;
        statesNames.set(state, name);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_EDIT_STATE_NAME, this, state, name);
    }

    /**
     * Remove the state of the machine identified by the given index, if such a state belongs to the machine. Every
     * incident transition is removed. Be aware that all the states with a greater index will see their index
     * decreased by one.
     *
     * A {@link #SUBSCRIBER_MSG_REMOVE_TRANSITION} message is broadcast to the class {@link util.Subscriber} for each
     * removed transition.
     * A {@link #SUBSCRIBER_MSG_REMOVE_STATE} message is broadcast to the class {@link util.Subscriber}.
     * @param state index of a state
     * @see util.Subscriber
     */
    public void removeState(int state){
        if(state < 0 || state >= getNbStates())
            return;

        // List of incident transitions, initialized with the output transitions
        Set<Transition> transitions = new HashSet<>(outputTransitions.get(state));

        // For each transtion, we check if the trnasition is an input transition of the state
        // We also check if the input/output state of the transition has a greater index than the removed state in which
        // case this index is decreased by one.
        for(int state2 = 0; state2 < getNbStates(); state2++) {
            for (Transition transition : outputTransitions.get(state2)) {
                if (state == transition.getOutput())
                    transitions.add(transition);
            }
        }

        // Remove the incident transitions.
        for(Transition transition : transitions)
            this.removeTransition(transition);

        for(int state2 = 0; state2 < getNbStates(); state2++) {
            for (Transition transition : outputTransitions.get(state2)) {
                if(state < transition.getInput())
                    transition.setInput(transition.getInput() - 1);
                if(state < transition.getOutput())
                    transition.setOutput(transition.getOutput() - 1);
            }
        }

        outputTransitions.remove(state);
        nbStates--;

        initialStates.remove(state);
        statesNames.remove(state);
        finalStates.remove(state);
        acceptingStates.remove(state);

        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_STATE, this, state);
    }

    /**
     * Declare the state identified by the given index as initial.
     *
     * A {@link #SUBSCRIBER_MSG_SET_INITIAL_STATE} message is broadcast to the class {@link util.Subscriber}.
     * @param state index of a state
     * @see util.Subscriber
     */
    public void setInitialState(int state) {
        initialStates.set(state, true);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_SET_INITIAL_STATE, this, state);
    }

    /**
     * Declare the state identified by the given index as not initial.
     *
     * A {@link #SUBSCRIBER_MSG_UNSET_INITIAL_STATE} message is broadcast to the class {@link util.Subscriber}.
     * @param state index of a state
     * @see util.Subscriber
     */
    public void unsetInitialState(int state) {
        initialStates.set(state, false);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_UNSET_INITIAL_STATE, this, state);
    }

    /**
     * @param state Index of a state
     * @return true if and only if the state identified by the given index is initial.
     */
    public boolean isInitial(Integer state){ return initialStates.get(state);}


    /**
     * @return the state currently pointed by the state registered during the execution or null if the machine is
     * not executing.
     */
    public Integer getCurrentState() {
        return currentState;
    }

    /**
     * Set the state currently pointed by the state registered during the execution
     *
     * A {@link #SUBSCRIBER_MSG_CURRENT_STATE_CHANGED} message is broadcast to the class {@link util.Subscriber} if
     * log is true.
     * @param currentState
     * @param log
     * @see util.Subscriber
     */
    private void setCurrentState(Integer currentState, boolean log) {
        this.currentState = currentState;
        if(log)
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_CURRENT_STATE_CHANGED, this, currentState);
    }

    /**
     * Add a new tape to the machine. This tape is one dimensional. It has no tape and every cell is empty.
     *
     * A {@link #SUBSCRIBER_MSG_ADD_TAPE} message is broadcast to the class {@link util.Subscriber}.
     * @return the new tape.
     * @see util.Subscriber
     */
    public Tape addTape(){
        Tape tape = new Tape(this);
        tapes.add(tape);

        for(int state = 0; state < this.getNbStates(); state++)
            for(Transition transition : this.outputTransitions.get(state))
                transition.addTape(tape);

        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_TAPE, this, tape);
        return tape;
    }

    /**
     * @param i
     * @return the i-th tape if i is between 0 and the number of tapes and null otherwise.
     */
    public Tape getTape(int i){
        if(i < 0 || i >= tapes.size())
            return null;
        return tapes.get(i);
    }

    /**
     * Remove the i-th tape if i is between 0 and the number of tapes.
     * @param i
     * @see #removeTape(Tape)
     */
    public void removeTape(int i){
        if(i < 0 || i >= tapes.size())
            return;
        removeTape(tapes.get(i));
    }

    /**
     * Remove the tape.
     *
     * A {@link #SUBSCRIBER_MSG_REMOVE_TAPE} message is broadcast to the class {@link util.Subscriber}.
     * @param tape
     * @see util.Subscriber
     */
    public void removeTape(Tape tape){
        for(int head = tape.getNbHeads() - 1; head >= 0; head--)
            this.removeHead(tape, head);

        for(int state = 0; state < this.getNbStates(); state++)
            for(Transition transition : this.outputTransitions.get(state))
                transition.removeTape(tape);

        tapes.remove(tape);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_TAPE, this, tape);
    }

    /**
     * @return an iterator to the list of tapes.
     */
    public Iterator<Tape> getTapes() {
        return tapes.iterator();
    }

    /**
     * @return the number of tapes of the machine.
     */
    public int getNbTapes(){
        return tapes.size();
    }

    /**
     * Add a new head to the tape at the position given by {@link Tape#initColumn()} and {@link Tape#initLine()}.
     */
    public void addHead(Tape tape){
        this.addHead(tape, tape.initLine(), tape.initColumn());
    }

    /**
     * Add a new head to the tape at the given line and column.
     * A {@link TuringMachine#SUBSCRIBER_MSG_ADD_HEAD} message is broadcast to the class {@link util.Subscriber}.
     * @param line
     * @param column
     * @see util.Subscriber
     */
    public void addHead(Tape tape, int line, int column){
        tape.addHead(line, column);

        for(int state = 0; state < this.getNbStates(); state++)
            for(Transition transition : this.outputTransitions.get(state))
                transition.addHead(tape);

        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_HEAD, this, tape, tape.getNbHeads() - 1,
                line, column);
    }

    /**
     * Remove the given head from the given tape. Be aware that every head with a greater index identifier will see
     * their index decreased by one.
     * A {@link TuringMachine#SUBSCRIBER_MSG_ADD_HEAD} message is broadcast to the class {@link util.Subscriber}.
     * @param tape
     * @param head index of the head in the list of heads of the given tape
     * @see util.Subscriber
     */
    public void removeHead(Tape tape, int head){
        tape.removeHead(head);

        for(int state = 0; state < this.getNbStates(); state++)
            for(Transition transition : this.outputTransitions.get(state))
                transition.removeHead(tape, head);

        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_HEAD, this, tape, head);
    }


    /**
     * Add a new symbol to the machine.
     *
     * A {@link #SUBSCRIBER_MSG_ERROR} message is broadcast to the class {@link util.Subscriber} if the symbol is
     * already in the list of symbols of the machine. Otherwise, a {@link #SUBSCRIBER_MSG_ADD_SYMBOL} message is
     * broadcast.
     * @param symbol
     * @see util.Subscriber
     */
    public void addSymbol(String symbol){
        if(symbols.contains(symbol)) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "The symbol already exists.");
            return;
        }
        symbols.add(symbol);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_SYMBOL, this, symbol);
    }

    /**
     * Edit the name of the i-th symbol and replace it by the given name if i is between 0 and the number of symbols
     * - 1.
     *
     * A {@link #SUBSCRIBER_MSG_ERROR} message is broadcast to the class {@link util.Subscriber} if the symbol is
     * already in the list of symbols of the machine. Otherwise, a {@link #SUBSCRIBER_MSG_EDIT_SYMBOL} message is
     * broadcast.
     * @param i
     * @param symbol name that should replace the name of the i-th symbol.
     * @see util.Subscriber
     */
    public void editSymbol(int i, String symbol){
        if(i < 0 || i >= symbols.size())
            return;
        if(symbols.contains(symbol)) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "The symbol already exists.");
            return;
        }
        String prevSymbol = symbols.set(i, symbol);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_EDIT_SYMBOL, this, i, prevSymbol, symbol);
    }

    /**
     * Remove the i-th symbol if i is between 0 and the number of symbols - 1.
     *
     * A {@link #SUBSCRIBER_MSG_REMOVE_SYMBOL} message is broadcast to the class {@link util.Subscriber}.
     * @param i
     * @see util.Subscriber
     */
    public void removeSymbol(int i){
        if(i < 0 || i >= symbols.size())
            return;

        String symbol = symbols.remove(i);

        for(int state = 0; state < this.getNbStates(); state++)
            for(Transition transition : this.outputTransitions.get(state)) {
                transition.removeSymbol(symbol);
            }

        for(Tape tape: tapes){
            tape.removeSymbol(symbol);
        }

        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_SYMBOL, this, i, symbol);
    }

    /**
     * @return an iterator to the list of symbols of the machine.
     */
    public Iterator<String> getSymbols(){
        return symbols.iterator();
    }

    /**
     * @param i
     * @return the i-th symbol if i is between 0 and the number of symbols - 1 and null otherwise.
     */
    public String getSymbol(int i){
        if(i < 0 || i >= symbols.size())
            return null;
        return symbols.get(i);
    }

    /**
     * @param symbol
     * @return true if the given symbol is in the list of symbols of the machine.
     */
    public boolean hasSymbol(String symbol) {
        return symbols.contains(symbol);
    }

    /**
     * @return the number of symbols of the machine.
     */
    public int getNbSymbols(){
        return symbols.size();
    }

    /**
     * Declare the state identified by the given index as final. In a machine answering a decision problem;, if the
     * state is final but not accepting, the answer is considered as a NO.
     *
     * A {@link #SUBSCRIBER_MSG_SET_FINAL_STATE} message is broadcast to the class {@link util.Subscriber}.
     * @param state index of a state
     * @see util.Subscriber
     * @see #setAcceptingState(int)
     * @see #unsetFinalState(int)
     * @see #unsetAcceptingState(int)
     */
    public void setFinalState(int state){
        finalStates.set(state, true);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_SET_FINAL_STATE, this, state);
    }

    /**
     * Declare the state identified by the given index as accepting, which means a final state such that, if the
     * machine reaches that state, it answers YES.
     *
     * A {@link #SUBSCRIBER_MSG_SET_FINAL_STATE} message and possibly a {@link #SUBSCRIBER_MSG_SET_FINAL_STATE}
     * message are broadcast to the class {@link util.Subscriber}.
     * @param state index of a state
     * @see util.Subscriber
     * @see #setFinalState(int) (int)
     * @see #unsetFinalState(int)
     * @see #unsetAcceptingState(int)
     */
    public void setAcceptingState(int state){
        acceptingStates.set(state, true);
        setFinalState(state);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_SET_ACCEPTING_STATE, this, state);
    }

    /**
     * Declare the state identified by the given index as not final. If the state was accepting, this method declares
     * also it as not accepting.
     *
     * A {@link #SUBSCRIBER_MSG_UNSET_FINAL_STATE} message and possibly a {@link #SUBSCRIBER_MSG_UNSET_ACCEPTING_STATE}
     * message are broadcast to the class {@link util.Subscriber}.
     * @param state index of a state
     * @see util.Subscriber
     * @see #setFinalState(int) (int)
     * @see #setAcceptingState(int)
     * @see #unsetAcceptingState(int)
     */
    public void unsetFinalState(int state){
        finalStates.set(state, false);
        unsetAcceptingState(state);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_UNSET_FINAL_STATE, this, state);
    }

    /**
     * Declare the state identified by the given index as not accepting. The state is still final.
     *
     * A {@link #SUBSCRIBER_MSG_UNSET_ACCEPTING_STATE} message is broadcast to the class {@link util.Subscriber}.
     * @param state index of a state
     * @see util.Subscriber
     * @see #setFinalState(int) (int)
     * @see #unsetFinalState(int)
     * @see #unsetAcceptingState(int)
     */
    public void unsetAcceptingState(int state){
        acceptingStates.set(state, false);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_UNSET_ACCEPTING_STATE, this, state);
    }
    /**
     * @param state Index of a state
     * @return true if and only if the state identified by the given index is final.
     */
    public boolean isFinal(int state){
        return finalStates.get(state);
    }
    /**
     * @param state Index of a state
     * @return true if and only if the state identified by the given index is accepting.
     */
    public boolean isAccepting(int state){
        return acceptingStates.get(state);
    }

    /**
     * @return true if and only if the state currently pointed by the state registered during the execution is final.
     */
    public boolean isTerminated(){
        return isFinal(currentState);
    }

    /**
     * @return true if and only if the state currently pointed by the state registered during the execution is
     * accepting.
     */
    public boolean isAccepting(){
        return isAccepting(currentState);
    }

    /**
     * Clear the graph, remove all the tapes and the symbols.
     */
    public void clear() {
        for(int i = this.getNbStates() - 1; i >= 0; i--)
            this.removeState(i);
        for(int i = this.getNbTapes() - 1; i >= 0; i--)
            this.removeTape(i);
        for(int i = this.getNbSymbols() - 1; i >= 0; i--)
            this.removeSymbol(i);
    }

    /**
     * @return the list of transitions that can be fired by the machine from the current state pointed by the
     * state register.
     */
    private List<Transition> currentValidArcs(){
        List<Transition> transitions = new LinkedList<>();
        for(Transition transition : outputTransitions.get(currentState)) {
            if(transition.isCurrentlyValid())
                transitions.add(transition);
        }
        return transitions;
    }

    /**
     * @return the current configuration of the machine
     */
    private HardConfiguration saveConfiguration(){

        // Save the configurations of all the tapes.
        Map<Tape, TapeConfiguration> tapeConfigurations = new HashMap<>();
        for (Tape tape : tapes)
            tapeConfigurations.put(tape, tape.saveConfiguration());

        // Associate those configurations to the current state.
        return new HardConfiguration(currentState, tapeConfigurations);
    }

    /**
     * Put the machine in the given configuration.
     * @param configuration
     * @see #loadConfiguration(HardConfiguration, boolean)
     */
    private void loadConfiguration(Configuration configuration){
            Pair<HardConfiguration, List<Transition>> pair = configuration.transitionsFromHard();

            loadConfiguration(pair.first, false);

            // If the configuration is a hard configuration, pair.second is empty.
            for(Transition transition: pair.second) {
                transition.fire(false);
                setCurrentState(transition.getOutput(), false);
            }
    }

    /**
     * Put the machine in the given configuration and broadcast messages to tell to listeners what changed in the
     * machine if log is true.
     * @param configuration
     * @param log
     */
    private void loadConfiguration(HardConfiguration configuration, boolean log){
        for(Map.Entry<Tape, TapeConfiguration> entry: configuration.tapesConfigurations.entrySet())
            entry.getKey().loadConfiguration(entry.getValue(), log);
        this.setCurrentState(configuration.state, log);
    }

    /**
     * @param configuration
     * @return true if and only if the current state if the given configuration is final.
     */
    private boolean isFinalConfiguration(Configuration configuration){
        return isFinal(configuration.getState());
    }

    /**
     * @param configuration
     * @return true if and only if the current state if the given configuration is accepting.
     */
    private boolean isAcceptingConfiguration(Configuration configuration){
        return isAccepting(configuration.getState());
    }

    /**
     * @param configuration
     * @return the set of all configuration that can be reached from the current configuration by firing one valid
     * transition.
     */
    private Set<Configuration> explore(Configuration configuration){

        // Set the machine in the given configuration
        this.loadConfiguration(configuration);

        Set<Configuration> children = new HashSet<>();
        List<Transition> outputs = outputTransitions.get(currentState);

        Configuration childConfiguration;

        // List all the transtitions that can be fired and build new configurations by firing them
        for(Transition transition : outputs)
            if(transition.isCurrentlyValid()) {
                // No log is done : the exploration is not broadcasted.
                transition.fire(false);
                setCurrentState(transition.getOutput(), false);
                childConfiguration = new Configuration();
                childConfiguration.transitionFromParent = transition;
                childConfiguration.parent = configuration;
                children.add(childConfiguration);
                // Reload the given configuration before firing the next transition.
                this.loadConfiguration(configuration);
            }
        return children;
    }

    /**
     * Explore the possible configurations the machine can reach from the given initial configurations and return, if
     * such a path exists, the list of configurations and transitions needed to reach an accepting state. Otherwise, if
     * such a path exists, it returns the list of configurations and transitions needed to reach a final
     * non accepting state. Otherwise it returns null. The exploration is done with a BFS algorithm.
     *
     * Otherwise, a {@link #SUBSCRIBER_MSG_ERROR} message is broadcast to the class
     * {@link util.Subscriber} if the number of iterations of the search algorithm is greater than
     * {@link #maximumNonDeterministicSearch}.
     *
     * @param initialConfigurations
     * @return a list of configurations corresponding to an execution of the machine.
     * @see util.Subscriber
     */
    private Pair<List<HardConfiguration>, List<Transition>> exploreNonDeterministic(
            Set<HardConfiguration> initialConfigurations){

        // List of configurations that should be explored later.
        // The exploration builds an exploration tree. A BFS algorithm is used to explore the (possibly infinite) tree.
        LinkedList<Configuration> toExplore = new LinkedList<>();

        toExplore.addAll(initialConfigurations);
        Configuration configuration = null;

        // Will contain the first configuration that is reached by the exploration and that is final.
        Configuration firstFinalConfiguration = null;
        // Tree if an accepting configuration is explored.
        boolean accepting = false;

        // Count the number of iterations of the exploration, should not be greater than a maximum in order to avoid
        // infinite exploration.
        int iteration = 0;
        boolean stop = false;
        while(!toExplore.isEmpty() && iteration < maximumNonDeterministicSearch){
            try {
                stopExplorationSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stop = stopExploration;
            stopExplorationSemaphore.release();
            if(stop)
                break;

            iteration++;
            configuration = toExplore.pollFirst();

            // Check if the configuration is accepting, in that case we end the exploration
            if(this.isAcceptingConfiguration(configuration)) {
                accepting = true;
                break;
            }
            // Check if the configuration is final but not accepting. In that case we do not explore the children.
            if(this.isFinalConfiguration(configuration)) {
                if(firstFinalConfiguration == null)
                    firstFinalConfiguration = configuration;
                continue;
            }

            // Explore the children and add them to the list.
            toExplore.addAll(this.explore(configuration));

        }
        System.out.println(iteration);

        // If the maximum number of iterations is reached, an error message is broadcase.
        if(iteration == maximumNonDeterministicSearch){
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Reached maximum number of iterations.");
        }

        if(stop){
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Computation was stopped.");
        }

        // If it is not accepting, return the first final non accepting configuration or null if such a configuration
        // is not given.
        if(!accepting) {
            if (firstFinalConfiguration == null)
                return null;
            else
                configuration = firstFinalConfiguration;
        }

        //  Build the transitions from the initial configuration to the final configuration.
        ArrayList<HardConfiguration> toReturnC = new ArrayList<>();
        Pair<HardConfiguration, List<Transition>> pair = configuration.transitionsFromRoot();
        loadConfiguration(pair.first);

        // Build a hard copy of all the configurations from the initial to the final configuration.
        toReturnC.add(pair.first);
        for(Transition transition : pair.second){
            transition.fire(false);
            setCurrentState(transition.getOutput(), false);
            toReturnC.add(saveConfiguration());
        }

        return new Pair<>(toReturnC, new ArrayList<>(pair.second));

    }

    /**
     * Explore the machine configurations with {@link #exploreNonDeterministic(Set)} and build, if
     * such a path exists, the list of configuration and transition needed to reach an accepting state. Otherwise, if
     * such a path exists, it builds a list of configurations and transitions needed to reach a final
     * non accepting state.
     *
     * A {@link #SUBSCRIBER_MSG_ERROR} message is broadcast to the class {@link util.Subscriber} if the number of
     * iterations of the search algorithm is greater than {@link #maximumNonDeterministicSearch} during the
     * exploration, if the machine is not valid (no initial or final state) or if the exploration could not reach a
     * final state.
     *
     * A {@link #SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START} message is broadcast when the exploration starts.
     * A {@link #SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END} message is broadcast when the exploration ends.
     *
     * @see #isValid()
     * @see util.Subscriber
     */
    public void build(){
        if(!isValid()) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Invalid machine. No initial and/or final state.");
            return;
        }

        for(Tape tape : tapes)
                tape.reinit();

        Subscriber.broadcast(SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START, this);

        HashSet<HardConfiguration> initialConfigurations = new HashSet<>();

        for (int state = 0; state < this.getNbStates(); state++) {
            if(this.isInitial(state)) {
                this.setCurrentState(state, false);
                initialConfigurations.add(saveConfiguration());
            }
        }


        builtPath = this.exploreNonDeterministic(initialConfigurations);
        builtIndex = new Pair<>(0, 0);

        if(builtPath == null)
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Cannot end computation.");

        Subscriber.broadcast(SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END, this);

    }

    public void buildAsync(Runnable callback){
        if(buildExecutor != null)
            return;

        buildExecutor = Executors.newSingleThreadExecutor();
        buildExecutor.execute(() -> {
            this.build();
            callback.run();
            stopExploration = false;
            buildExecutor = null;
        });
    }

    public void cancelBuild(){
        try {
            stopExplorationSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopExploration = true;
        stopExplorationSemaphore.release();
    }

    /**
     * Once the machine is explored with the {@link #build()} method, load the next configuration by firing the next
     * transition unless the current configuration is final.
     *
     * Message can be broadcast in order to tell that the current state change, a transition is fired, a head is
     * moved or a symbol is written on a cell of a tape.
     * @return true if the current configuration is not final and if a next configuration could have been loaded.
     * @see util.Subscriber
     * @see #loadFirstConfiguration()
     * @see #loadLastConfiguration()
     * @see #loadPreviousConfiguration()
     */
    public boolean tick(){
        if(builtPath == null) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Computation not built. Cannot execute.");
            return false;
        }

        if(builtIndex.second < builtPath.second.size()){
            Transition transition = builtPath.second.get(builtIndex.second);
            builtIndex.first++;
            builtIndex.second++;
            transition.fire(true);
            setCurrentState(transition.getOutput(), true);
            return true;
        }
        return false;
    }

    /**
     * Once the machine is explored with the {@link #build()} method, load the previous configuration unless the
     * current configuration is the initial configuration of the built exploration path.
     *
     * Message can be broadcast in order to tell that the current state change, a transition is fired, a head is
     * moved or a symbol is written on a cell of a tape.
     * @return true if the current configuration is not final and if a next configuration could have been loaded.
     * @see util.Subscriber
     * @see #loadFirstConfiguration()
     * @see #loadLastConfiguration()
     * @see #tick()
     */
    public boolean loadPreviousConfiguration(){
        if(builtPath == null) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Computation not built. Cannot execute.");
            return false;
        }

        if(builtIndex.second > 0){
            builtIndex.first--;
            builtIndex.second--;
            this.loadConfiguration(builtPath.first.get(builtIndex.first), true);
            return true;
        }

        return false;
    }

    /**
     * Once the machine is explored with the {@link #build()} method, load the first configuration of the built
     * exploration path.
     *
     * Message can be broadcast in order to tell that the current state change, a transition is fired, a head is
     * moved or a symbol is written on a cell of a tape.
     * @return true if the current configuration is not final and if a next configuration could have been loaded.
     * @see util.Subscriber
     * @see #tick()
     * @see #loadLastConfiguration()
     * @see #loadPreviousConfiguration()
     */
    public void loadFirstConfiguration(){
        if(builtPath == null) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Computation not built. Cannot execute.");
            return;
        }

        builtIndex.first = 0;
        builtIndex.second = 0;
        this.loadConfiguration(builtPath.first.get(builtIndex.first), true);
    }

    /**
     * Once the machine is explored with the {@link #build()} method, load the final configuration of the built
     * exploration path.
     *
     * Message can be broadcast in order to tell that the current state change, a transition is fired, a head is
     * moved or a symbol is written on a cell of a tape.
     * @return true if the current configuration is not final and if a next configuration could have been loaded.
     * @see util.Subscriber
     * @see #loadFirstConfiguration()
     * @see #tick()
     * @see #loadPreviousConfiguration()
     */
    public void loadLastConfiguration(){
        if(builtPath == null) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Computation not built. Cannot execute.");
            return;
        }

        builtIndex.first = builtIndex.second = builtPath.first.size() - 1;
        this.loadConfiguration(builtPath.first.get(builtIndex.first), true);
    }

    /**
     * Cancel the exploration done with the {@link #build()} method.
     */
    public void clearBuild(){
        this.builtPath = null;
        this.builtIndex = null;
    }

    /**
     * Start a manual exploration. Contrary to the exploration done with the {@link #build()} method, this
     * exploration is manually done using the {@link #manualSetCurrentState(Integer)} and
     * {@link #manualFireTransition(Transition)} methods.
     *
     *  A {@link #SUBSCRIBER_MSG_ERROR} message is broadcast to the class {@link util.Subscriber} if the machine is
     *  not valid (no initial or final state).
     *
     * @see #isValid()
     * @see util.Subscriber
     */
    public void buildManual(){
        if(!isValid()) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Invalid machine. No initial and/or final state.");
            return;
        }

        for(Tape tape : tapes)
            tape.reinit();

        for(int state = 0; state < this.getNbStates(); state++){
            if(this.isInitial(state)){
                manualSetCurrentState(state);
                manualInitialConfiguration = builtPath.first.get(0);
                break;
            }
        }

    }

    /**
     * Manually set the current state to the given state. The {@link #buildManual} method should have been called before
     * to initialize manual exploration of the machine.
     *
     * A {@link #SUBSCRIBER_MSG_CURRENT_STATE_CHANGED} message is broadcast to {@link util.Subscriber}.
     * @param state
     * @see {@link #manualFireTransition(Transition)}
     * @see util.Subscriber
     */
    public void manualSetCurrentState(Integer state) {
        this.setCurrentState(state, false);
        HardConfiguration configuration = this.saveConfiguration();
        builtPath = new Pair<>(new ArrayList<>(), new ArrayList<>());
        builtPath.first.add(configuration);
        builtIndex = new Pair<>(0, 0);
    }

    /**
     * Manually fire a transition. The {@link #buildManual} method should have been called before
     * to initialize manual exploration of the machine.
     *
     * A {@link #SUBSCRIBER_MSG_ERROR} message is broadcast to the class {@link util.Subscriber} if the transition
     * cannot be fired.
     * Message can be broadcast in order to tell that the current state change, a transition is fired, a head is
     * moved or a symbol is written on a cell of a tape.
     * @param transition
     * @see {@link #manualSetCurrentState(Integer)}
     * @see util.Subscriber
     */
    public void manualFireTransition(Transition transition){
        if(this.currentState != null && transition.getInput() != this.currentState){
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Cannot fire transition, invalid current state.");
            return;
        }
        if(!transition.isCurrentlyValid()){
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Cannot fire transition, symbols on tape do not match.");
            return;
        }

        transition.fire(true);
        setCurrentState(transition.getOutput(), true);

        HardConfiguration configuration = this.saveConfiguration();
        
        if(builtIndex.first != builtPath.first.size() - 1) {
            builtPath.first = builtPath.first.subList(0, builtIndex.first + 1);
            builtPath.second = builtPath.second.subList(0, builtIndex.second);
        }
        builtPath.first.add(configuration);
        builtPath.second.add(transition);
        builtIndex.first++;
        builtIndex.second++;

    }

    /**
     * Cancel the exploration done with the {@link #buildManual()} method.
     */
    public void clearManual(){
        if(manualInitialConfiguration == null)
            return;
        this.loadConfiguration(manualInitialConfiguration, true);
        this.builtPath = null;
        this.builtIndex = null;
        this.manualInitialConfiguration = null;
    }

    /**
     * @param state
     * @return true if the set of output transition of the given state is deterministic.
     */
    private boolean isDeterministic(int state){
        HashSet<List<List<Object>>> allSymbols = new HashSet<>();
        List<Transition> transitions = outputTransitions.get(state);

        for(Transition transition : transitions){
            List<List<List<Object>>> allSymbolsOfTransition = new ArrayList<>();

            Iterator<Map.Entry<Tape, List<Set<String>>>> readSymbolIt = transition.getReadSymbols();
            while(readSymbolIt.hasNext()){
                Map.Entry<Tape, List<Set<String>>> readSymbol = readSymbolIt.next();
                Tape tape = readSymbol.getKey();

                int head = 0;
                for(Set<String> symbols : readSymbol.getValue()){
                    if(symbols.isEmpty())
                        continue;
                    List<List<Object>> readSymbolList = new LinkedList<>();
                    for(String symbol : symbols) {
                        List<Object> l = new LinkedList<>();
                        l.add(tape);
                        l.add(head);
                        l.add(symbol);
                        readSymbolList.add(l);
                    }
                    allSymbolsOfTransition.add(readSymbolList);
                    head++;
                }
            }

            List<Integer> allSymbolsOfTransitionIndexes = new ArrayList<>();
            for(int i = 0; i < allSymbolsOfTransition.size(); i++)
                allSymbolsOfTransitionIndexes.add(0);

            int currentIndex;
            outer: while(true){
                List<List<Object>> current = new LinkedList<>();
                for(int i = 0; i < allSymbolsOfTransition.size(); i++){
                    current.add(allSymbolsOfTransition.get(i).get(allSymbolsOfTransitionIndexes.get(i)));
                }
                if(!allSymbols.add(current)) {
                    return false;
                }

                currentIndex = allSymbolsOfTransition.size() - 1;
                allSymbolsOfTransitionIndexes.set(currentIndex, allSymbolsOfTransitionIndexes.get(currentIndex) + 1);
                int size = allSymbolsOfTransition.get(currentIndex).size();
                while(size == allSymbolsOfTransitionIndexes.get(currentIndex)){
                    allSymbolsOfTransitionIndexes.set(currentIndex, 0);
                    currentIndex--;
                    if(currentIndex == -1)
                        break outer;
                    allSymbolsOfTransitionIndexes.set(currentIndex, allSymbolsOfTransitionIndexes.get(currentIndex) + 1);
                    size = allSymbolsOfTransition.get(currentIndex).size();

                }
            }

        }
        return true;
    }

    /**
     *
     * @return true if the machine is deterministic.
     */
    public boolean isDeterministic(){
        int nbInitial = 0;
        for(int state = 0; state < nbStates; state++){
            if(isInitial(state)){
                nbInitial++;
                if(nbInitial > 1)
                    return false;
            }
            if(!isDeterministic(state))
                return false;
        }
        return true;
    }

    /**
     *
     * @return true if the machine is valid : it contains at least ont initial state and ont final state.
     */
    public boolean isValid(){
        boolean initialState = false;
        boolean finalState = false;
        for(int state = 0; state < nbStates; state++){
            if(!initialState && isInitial(state))
                initialState = true;
            if(!finalState && isFinal(state))
                finalState = true;
            if(initialState && finalState)
                return true;
        }
        return false;
    }

    /**
     * Explore the machine configurations using the {@link #build()} method and then fire all the transitions of the
     * explored path using the {@link #tick()}  method.
     */
    public void execute(){
        this.build();
        while(this.tick()){}
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("State: ");
        s.append(getStateName(currentState));
        s.append('\n');
        for(Tape tape: tapes)
            s.append(tape);
        return s.toString();
    }

    static void testDeterministic(){
        TuringMachine t = new TuringMachine();

        t.addSymbol("0");
        t.addSymbol("1");

        Tape tape1 = t.addTape();
        t.addHead(tape1, 0, 0);
        t.addHead(tape1, 0, 0);

        tape1.writeInput(0, 0, "1");
        tape1.writeInput(0, 1, "0");
        tape1.writeInput(0, 2, "1");
        tape1.writeInput(0, 3, "1");
        tape1.writeInput(0, 4, "1");
        tape1.writeInput(0, 5, "0");
        tape1.writeInput(0, 6, "1");

        int a = t.addState("A");
        int b = t.addState("B");
        int y = t.addState("Y");
        int n = t.addState("N");

        Transition t1 = t.addTransition(a, a);
        t1.addReadSymbols(tape1, 1, "1", "0");
        t1.addAction(new MoveAction(tape1, 1, Direction.RIGHT));

        Transition t2 = t.addTransition(a, b);
        t2.addReadSymbols(tape1, 1, (String)null);
        t2.addAction(new MoveAction(tape1, 1, Direction.LEFT));

        Transition t3 = t.addTransition(b, b);
        t3.addReadSymbols(tape1, 0, "1");
        t3.addReadSymbols(tape1, 1, "1");
        t3.addAction(new WriteAction(tape1, 0, null));
        t3.addAction(new WriteAction(tape1, 1, null));
        t3.addAction(new MoveAction(tape1, 0, Direction.RIGHT));
        t3.addAction(new MoveAction(tape1, 1, Direction.LEFT));

        Transition t4 = t.addTransition(b, b);
        t4.addReadSymbols(tape1, 0, "0");
        t4.addReadSymbols(tape1, 1, "0");
        t4.addAction(new WriteAction(tape1, 0, null));
        t4.addAction(new WriteAction(tape1, 1, null));
        t4.addAction(new MoveAction(tape1, 0, Direction.RIGHT));
        t4.addAction(new MoveAction(tape1, 1, Direction.LEFT));

        Transition t5 = t.addTransition(b, n);
        t5.addReadSymbols(tape1, 0, "0");
        t5.addReadSymbols(tape1, 1, "1");

        Transition t6 = t.addTransition(b, n);
        t6.addReadSymbols(tape1, 0, "1");
        t6.addReadSymbols(tape1, 1, "0");

        Transition t7 = t.addTransition(b, n);
        t7.addReadSymbols(tape1, 0, (String)null);
        t7.addReadSymbols(tape1, 1, "1");

        Transition t8 = t.addTransition(b, n);
        t8.addReadSymbols(tape1, 0, "1");
        t8.addReadSymbols(tape1, 1, (String)null);

        Transition t9 = t.addTransition(b, n);
        t9.addReadSymbols(tape1, 0, (String)null);
        t9.addReadSymbols(tape1, 1, "0");

        Transition t10 = t.addTransition(b, n);
        t10.addReadSymbols(tape1, 0, "0");
        t10.addReadSymbols(tape1, 1, (String)null);

        Transition t11 = t.addTransition(b, y);
        t11.addReadSymbols(tape1, 0, (String)null);
        t11.addReadSymbols(tape1, 1, (String)null);

        t.setInitialState(a);
        t.setAcceptingState(y);
        t.setFinalState(n);

        Subscriber s = new Subscriber() {
            @Override
            public void read(String msg, Object... parameters) {
                if(msg.equals(TuringMachine.SUBSCRIBER_MSG_CURRENT_STATE_CHANGED))
                    System.out.println(t.getStateName(t.getCurrentState()));
                else if(msg.equals(TuringMachine.SUBSCRIBER_MSG_FIRED_TRANSITION))
                    System.out.println(parameters[1]);
                else if(msg.equals(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED))
                    System.out.println(((Tape)parameters[1]).print());
                else if(msg.equals(TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE))
                    System.out.println(((Tape)parameters[1]).print());
                else if(msg.equals(TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START))
                    System.out.println("Explore Start");
                else if(msg.equals(TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END))
                    System.out.println("Explore End");
            }
        };

        s.subscribe(TuringMachine.SUBSCRIBER_MSG_CURRENT_STATE_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_FIRED_TRANSITION);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END);

        t.execute();
    }

    static void testNonDeterministic(){
        TuringMachine t = new TuringMachine();

        t.addSymbol("0");
        t.addSymbol("1");
        t.addSymbol("2");

        Tape tape1 = t.addTape();
        tape1.setBottomBound(0);
        tape1.setTopBound(2);
        tape1.setLeftBound(0);
        tape1.setRightBound(2);

        t.addHead(tape1, 0, 0);

        tape1.writeInput(0, 0, "1");
        tape1.writeInput(0, 1, "1");
        tape1.writeInput(0, 2, "1");
        tape1.writeInput(1, 0, "1");
        tape1.writeInput(1, 1, "2");
        tape1.writeInput(1, 2, "2");
        tape1.writeInput(2, 0, "1");
        tape1.writeInput(2, 1, "1");
        tape1.writeInput(2, 2, "0");

        int a = t.addState("A");
        int y = t.addState("Y");
        int n = t.addState("N");

        Transition t1 = t.addTransition(a, a);
        t1.addReadSymbols(tape1, 0, "1");
        t1.addAction(new MoveAction(tape1, 0, Direction.RIGHT));

        Transition t2 = t.addTransition(a, a);
        t2.addReadSymbols(tape1, 0, "1");
        t2.addAction(new MoveAction(tape1, 0, Direction.UP));

        Transition t3 = t.addTransition(a, n);
        t3.addReadSymbols(tape1, 0, "2");

        Transition t4 = t.addTransition(a, y);
        t4.addReadSymbols(tape1, 0, "0");

        t.setInitialState(a);
        t.setAcceptingState(y);
        t.setFinalState(n);

        Subscriber s = new Subscriber() {
            @Override
            public void read(String msg, Object... parameters) {
                switch (msg) {
                    case TuringMachine.SUBSCRIBER_MSG_CURRENT_STATE_CHANGED:
                        System.out.println(t.getStateName(t.getCurrentState()));
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_FIRED_TRANSITION:
                        System.out.println(parameters[1]);
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED:
                        System.out.println(((Tape)parameters[1]).print());
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE:
                        System.out.println(((Tape)parameters[1]).print());
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START:
                        System.out.println("Explore Start");
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END:
                        System.out.println("Explore End");
                        break;
                }
            }
        };

        s.subscribe(TuringMachine.SUBSCRIBER_MSG_CURRENT_STATE_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_FIRED_TRANSITION);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END);

        t.execute();
    }

    public static void main(String[] args){
        testNonDeterministic();
    }

}

/**
 * Represent a configuration of a Turing machine, consisting in a snapshot of the state of the machine:
 * <ul>
 *     <li>The current state of the graph.</li>
 *     <li>Where are the heads.</li>
 *     <li>What is written on the tapes.</li>
 * </ul>
 *
 * There are two implementations of configurations
 * - hard configuration, an absolute configuration, copying all the data in the object
 * - soft configuration, a relative configuration, which is deduced from its parent and a transition fired from that
 * parent configuration to get this configuration.
 * The hard and the soft configurations always get a pointer to their parent configuration and the linking transition
 * except if the configuration is an initial configuration (with no parent).
 *
 * By default configuration is a soft configuration.
 * If the configuration is an initial configuration, it is necessarily a hard configuration.
 *
 * @see HardConfiguration
 */

class Configuration{

    /**
     * The configuration from which this configuration is get after firing the transition {@link #transitionFromParent}.
     * The parent configuration is null if this configuration is an initial configuration of the machine.
     *
     * @see #transitionFromParent
     */
    Configuration parent;

    /**
     * The transition that should be fired from the configuration {@link #parent} in order to get this configuration.
     * This transition is null if the configuration is an initial configuration of the machine.
     *
     * @see #parent
     */
    Transition transitionFromParent;

    /**
     * @return the state of the machine in the current configuration.
     */
    int getState(){
        return transitionFromParent.getOutput();
    }

    /**
     * @return true if this configuration is a hard configuration.
     */
    boolean isHard(){
        return false;
    }

    /**
     * @return a pair containing the least ancestor of this configuration that is a hard configuration and all the
     * transitions linking that ancestor to this configuration.
     */
    Pair<HardConfiguration, List<Transition>> transitionsFromHard(){
        LinkedList<Transition> transitions = new LinkedList<>();
        Configuration current = this;

        while(!current.isHard()){
            transitions.addFirst(current.transitionFromParent);
            current = current.parent;
        }

        return new Pair<>((HardConfiguration)current, transitions);
    }


    /**
     * @return a pair containing the highest ancestor of this configuration (the initial ancestor) and all the
     * transitions linking that ancestor to this configuration.
     */
    Pair<HardConfiguration, List<Transition>> transitionsFromRoot(){
        LinkedList<Transition> transitions = new LinkedList<>();
        Configuration current = this;

        while(current.parent != null){
            transitions.addFirst(current.transitionFromParent);
            current = current.parent;
        }

        return new Pair<>((HardConfiguration)current, transitions);
    }
}

/**
 * Represent a configuration of a Turing machine, consisting in a snapshot of the state of the machine:
 * <ul>
 *     <li>The current state of the graph.</li>
 *     <li>Where are the heads.</li>
 *     <li>What is written on the tapes.</li>
 * </ul>
 *
 * Contrary to the class Configuration, which is a soft configuraiton, a hard configuration contains a copy of the state
 * and the tapes of the machine.
 **
 * @see Configuration
 */

class HardConfiguration extends Configuration {
    /**
     * Current state of the machine
     */
    int state;

    /**
     * Copy of the tapes of the machine, containing the position of the heads and the word written on the tape.
     */
    Map<Tape, TapeConfiguration> tapesConfigurations;

    HardConfiguration(int state, Map<Tape, TapeConfiguration> tapeConfigurations) {
        this.state = state;
        this.tapesConfigurations = tapeConfigurations;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    boolean isHard() {
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(state);
        sb.append("\n");
        for(Tape tape : tapesConfigurations.keySet()){
            sb.append(tapesConfigurations.get(tape));
            sb.append("\n");
        }
        return sb.toString();
    }
}
