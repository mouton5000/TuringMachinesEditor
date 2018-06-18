package turingmachines;

import util.Pair;
import util.Subscriber;

import java.util.*;

public class TuringMachine {

    private static final int MAXIMUM_NON_DETERMINISTIC_SEARCH = 10000;

    public static final String SUBSCRIBER_MSG_FIRED_TRANSITION = "TMFireTransition";
    public static final String SUBSCRIBER_MSG_HEAD_WRITE = "TMHeadWrite";
    public static final String SUBSCRIBER_MSG_HEAD_MOVED = "TMHeadMoved";
    public static final String SUBSCRIBER_MSG_CURRENT_STATE_CHANGED = "TMCurrentStateChanged";

    public static final String SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START = "TMNonDeterministicExploreStart";
    public static final String SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END = "TMNonDeterministicExploreEnd";
    public static final String SUBSCRIBER_MSG_COMPUTE_START = "TMComputeStart";
    public static final String SUBSCRIBER_MSG_COMPUTE_END = "TMComputeEnd";

    public static final String SUBSCRIBER_MSG_ADD_STATE = "TMAddState";
    public static final String SUBSCRIBER_MSG_REMOVE_STATE = "TMRemoveState";
    public static final String SUBSCRIBER_MSG_SET_INITIAL_STATE = "TMSetInitialState";
    public static final String SUBSCRIBER_MSG_UNSET_INITIAL_STATE = "TMUnsetInitialState";
    public static final String SUBSCRIBER_MSG_SET_FINAL_STATE = "TMSetFinalState";
    public static final String SUBSCRIBER_MSG_UNSET_FINAL_STATE = "TMUnsetFinalState";
    public static final String SUBSCRIBER_MSG_SET_ACCEPTING_STATE = "TMSetAcceptingState";
    public static final String SUBSCRIBER_MSG_UNSET_ACCEPTING_STATE = "TMUnsetAcceptingState";

    public static final String SUBSCRIBER_MSG_ADD_TRANSITION = "TMAddTransition";
    public static final String SUBSCRIBER_MSG_REMOVE_TRANSITION = "TMRemoveTransition";

    public static final String SUBSCRIBER_MSG_ADD_TAPE = "TMHeadAddTape";
    public static final String SUBSCRIBER_MSG_REMOVE_TAPE = "TMRemoveTape";
    public static final String SUBSCRIBER_MSG_TAPE_LEFT_CHANGED = "TMTapeLeftChanged";
    public static final String SUBSCRIBER_MSG_TAPE_RIGHT_CHANGED = "TMTapeRightChanged";
    public static final String SUBSCRIBER_MSG_TAPE_BOTTOM_CHANGED = "TMTapeBottomChanged";
    public static final String SUBSCRIBER_MSG_TAPE_TOP_CHANGED = "TMTapeTopChanged";

    public static final String SUBSCRIBER_MSG_ADD_SYMBOL = "TMAddSymbol";
    public static final String SUBSCRIBER_MSG_REMOVE_SYMBOL = "TMRemoveSymbol";

    public static final String SUBSCRIBER_MSG_INPUT_CHANGED= "TMInputChanged";

    public static final String SUBSCRIBER_MSG_ADD_HEAD = "TMHeadAddHead";
    public static final String SUBSCRIBER_MSG_REMOVE_HEAD = "TMRemoveHead";
    public static final String SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED = "TMHeadInitialPositionChanged";

    private int nbStates;

    private List<List<Transition>> outputTransitions;
    private List<String> statesNames;

    private List<Boolean> finalStates;
    private List<Boolean> acceptingStates;

    private List<Tape> tapes;

    // State Register
    private Set<Integer> initialStates;
    private Integer currentState;

    // Alphabet
    private List<String> symbols;


    public TuringMachine(){
        nbStates = 0;

        outputTransitions = new ArrayList<>();
        statesNames = new ArrayList<>();

        initialStates = new HashSet<>();
        finalStates = new ArrayList<>();
        acceptingStates = new ArrayList<>();

        currentState = null;

        tapes = new ArrayList<>();
        symbols = new ArrayList<>();
    }

    public Transition addTransition(Integer input, Integer output){
        Transition a = new Transition(this, input, output);
        outputTransitions.get(input).add(a);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_TRANSITION, this, a);
        return a;
    }

    public void removeTransition(Transition a){
        Integer input = a.getInput();
        List<Transition> transitions = outputTransitions.get(input);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_TRANSITION, this, a);
        transitions.remove(a);
    }

    public int getNbStates() {
        return nbStates;
    }

    public int addState(String name){
        nbStates++;
        statesNames.add(name);
        outputTransitions.add(new ArrayList<>());
        finalStates.add(false);
        acceptingStates.add(false);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_STATE, this, nbStates - 1);
        return nbStates - 1;
    }

    public String getStateName(int state){
        return statesNames.get(state);
    }

    public void removeState(int state){
        nbStates--;
        statesNames.remove(state);
        outputTransitions.remove(state);
        finalStates.remove(state);
        acceptingStates.remove(state);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_STATE, this, state);
    }

    public void setInitialState(Integer state) {
        initialStates.add(state);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_SET_INITIAL_STATE, this, state);
    }

    public void unsetInitialState(Integer state) {
        initialStates.remove(state);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_UNSET_INITIAL_STATE, this, state);
    }

    public boolean isInitial(Integer state){ return initialStates.contains(state);}

    public Integer getCurrentState() {
        return currentState;
    }

    private void setCurrentState(Integer currentState, boolean log) {
        this.currentState = currentState;
        if(log)
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_CURRENT_STATE_CHANGED, this, currentState);
    }

    public void reinitDeterministic(){
        setCurrentState(initialStates.iterator().next(), false);
        for(Tape tape: tapes)
            tape.reinit();
    }

    public void reinitNonDeterministic(){
        for(Tape tape: tapes)
            tape.reinit();
    }

    public Tape addTape(){
        Tape tape = new Tape(this);
        tapes.add(tape);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_TAPE, this, tape);
        return tape;
    }

    public Tape getTape(int i){
        return tapes.get(i);
    }

    public void removeTape(int i){
        Tape tape = tapes.remove(i);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_TAPE, this, tape);
    }

    public void addSymbol(String symbol){
        symbols.add(symbol);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_SYMBOL, this, symbol);
    }

    public void removeSymbol(String symbol){
        symbols.remove(symbol);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_SYMBOL, this, symbol);
    }

    public List<String> getSymbols(){
        return symbols;
    }

    public void setFinalState(int state){
        finalStates.set(state, true);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_SET_FINAL_STATE, this, state);
    }

    public void setAcceptingState(int state){
        acceptingStates.set(state, true);
        setFinalState(state);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_SET_ACCEPTING_STATE, this, state);
    }

    public void unsetFinalState(int state){
        finalStates.set(state, false);
        unsetAcceptingState(state);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_UNSET_FINAL_STATE, this, state);
    }

    public void unsetAcceptingState(int state){
        acceptingStates.set(state, false);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_UNSET_ACCEPTING_STATE, this, state);
    }

    public boolean isFinal(int state){
        return finalStates.get(state);
    }

    public boolean isAccepting(int state){
        return acceptingStates.get(state);
    }

    private boolean isTerminated(){
        return isFinal(currentState);
    }

    private boolean isAccepting(){
        return isAccepting(currentState);
    }

    private void tickDeterministic(){
        for(Transition transition : outputTransitions.get(currentState))
            if(transition.isCurrentlyValid()) {
                transition.fire(true);
                setCurrentState(transition.getOutput(), true);
                break;
            }
    }

    private void executeDeterministic(){
        Subscriber.broadcast(SUBSCRIBER_MSG_COMPUTE_START, this);
        reinitDeterministic();
        while(!isTerminated()) {
            tickDeterministic();
        }
        Subscriber.broadcast(SUBSCRIBER_MSG_COMPUTE_END, this);
    }

    private List<Transition> currentValidArcs(){
        List<Transition> transitions = new LinkedList<>();
        for(Transition transition : outputTransitions.get(currentState)) {
            if(transition.isCurrentlyValid())
                transitions.add(transition);
        }
        return transitions;
    }

    private Configuration saveConfiguration(){
        Map<Tape, TapeConfiguration> tapeConfigurations = new HashMap<>();
        for(Tape tape: tapes)
            tapeConfigurations.put(tape, tape.saveConfiguration());
        return new Configuration(currentState, tapeConfigurations);
    }

    private void loadConfiguration(Configuration configuration){
        for(Map.Entry<Tape, TapeConfiguration> entry: configuration.tapeConfigurations.entrySet())
            entry.getKey().loadConfiguration(entry.getValue());
        this.currentState = configuration.state;
    }

    private boolean isFinalConfiguration(Configuration configuration){
        return isFinal(configuration.state);
    }

    private boolean isAcceptingConfiguration(Configuration configuration){
        return isAccepting(configuration.state);
    }

    private Map<Configuration, Transition> explore(Configuration configuration){
        this.loadConfiguration(configuration);
        Map<Configuration, Transition> children = new HashMap<>();
        List<Transition> outputs = outputTransitions.get(currentState);

        Configuration childConfiguration;
        for(Transition transition : outputs)
            if(transition.isCurrentlyValid()) {
                transition.fire(false);
                setCurrentState(transition.getOutput(), false);
                childConfiguration = this.saveConfiguration();
                children.put(childConfiguration, transition);
                this.loadConfiguration(configuration);
            }
        return children;
    }

    private Pair<Configuration, Iterator<Transition>> exploreNonDeterministic(Set<Configuration> initialConfigurations){
        LinkedList<Configuration> toExplore = new LinkedList<>();
        Map<Configuration, Configuration> fathers = new HashMap<>();
        Map<Configuration, Transition> arcFathers = new HashMap<>();

        toExplore.addAll(initialConfigurations);
        Configuration configuration = null;

        Configuration firstFinalConfiguration = null;
        boolean accepting = false;

        int iteration = 0;
        while(!toExplore.isEmpty() && iteration < MAXIMUM_NON_DETERMINISTIC_SEARCH){
            iteration++;
            configuration = toExplore.pollFirst();

            if(this.isAcceptingConfiguration(configuration)) {
                accepting = true;
                break;
            }
            if(this.isFinalConfiguration(configuration)) {
                if(firstFinalConfiguration == null)
                    firstFinalConfiguration = configuration;
                continue;
            }

            Map<Configuration, Transition> children = this.explore(configuration);
            for(Map.Entry<Configuration, Transition> entry: children.entrySet()){
                fathers.put(entry.getKey(), configuration);
                toExplore.add(entry.getKey());
            }
            arcFathers.putAll(children);

        }

        if(!accepting) {
            if (firstFinalConfiguration == null)
                return null;
            else
                configuration = firstFinalConfiguration;
        }
        LinkedList<Transition> toReturn = new LinkedList<>();
        while(!initialConfigurations.contains(configuration)){
            toReturn.addFirst(arcFathers.get(configuration));
            configuration = fathers.get(configuration);
        }
        return new Pair<>(configuration, toReturn.iterator());

    }

    private void executeNonDeterministic(){
        Subscriber.broadcast(SUBSCRIBER_MSG_COMPUTE_START, this);
        reinitNonDeterministic();


        Subscriber.broadcast(SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START, this);

        HashSet<Configuration> initialConfigurations = new HashSet<>();

        for(Integer state : initialStates) {
            this.setCurrentState(state, false);
            initialConfigurations.add(saveConfiguration());
        }
        Pair<Configuration, Iterator<Transition>> pair = this.exploreNonDeterministic(initialConfigurations);

        Subscriber.broadcast(SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END, this);

        if(pair == null)
            return;

        Configuration initialConfiguration = pair.first;
        Iterator<Transition> it = pair.second;

        this.loadConfiguration(initialConfiguration);
        while(it.hasNext()){
            Transition transition = it.next();
            transition.fire(true);
            setCurrentState(transition.getOutput(), true);

        }
        Subscriber.broadcast(SUBSCRIBER_MSG_COMPUTE_END, this);
    }

    public void execute(){
        if(!isValid())
            return;
        if(isDeterministic())
            executeDeterministic();
        else
            executeNonDeterministic();
    }

    private boolean isDeterministic(int state){
        HashSet<List<List<Object>>> allSymbols = new HashSet<>();
        List<Transition> transitions = outputTransitions.get(state);

        for(Transition transition : transitions){
            List<List<List<Object>>> allSymbolsOfTransition = new ArrayList<>();

            Iterator<ReadSymbol> readSymbolIt = transition.getReadSymbols();
            while(readSymbolIt.hasNext()){
                ReadSymbol readSymbol = readSymbolIt.next();
                Tape tape = readSymbol.getTape();
                Integer head = readSymbol.getHead();

                Iterator<String> symbolsIt = readSymbol.getSymbols();
                List<List<Object>> readSymbolList = new LinkedList<>();
                while(symbolsIt.hasNext()){
                    String symbol = symbolsIt.next();
                    List<Object> l = new LinkedList<>();
                    l.add(tape);
                    l.add(head);
                    l.add(symbol);
                    readSymbolList.add(l);
                }
                allSymbolsOfTransition.add(readSymbolList);
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
                if(!allSymbols.add(current))
                    return false;

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

    public boolean isDeterministic(){
        if(initialStates.size() > 1)
            return false;
        for(int state = 0; state < nbStates; state++){
            if(!isDeterministic(state))
                return false;
        }
        return true;
    }

    public boolean isValid(){
        if(initialStates.size() == 0)
            return false;
        for(int state = 0; state < nbStates; state++){
            if(isFinal(state))
                return true;
        }
        return false;
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

        Tape tape1 = t.getTape(0);
        tape1.addHead();

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

        t.setInitialState(a);

        Transition t1 = t.addTransition(a, a);
        t1.addReadSymbol(new ReadSymbol(tape1, 1, "1", "0"));
        t1.addAction(new MoveAction(tape1, 1, Direction.RIGHT));

        Transition t2 = t.addTransition(a, b);
        t2.addReadSymbol(new ReadSymbol(tape1, 1, (String)null, "0"));
        t2.addAction(new MoveAction(tape1, 1, Direction.LEFT));

        Transition t3 = t.addTransition(b, b);
        t3.addReadSymbol(new ReadSymbol(tape1, 0, "1"));
        t3.addReadSymbol(new ReadSymbol(tape1, 1, "1"));
        t3.addAction(new WriteAction(tape1, 0, null));
        t3.addAction(new WriteAction(tape1, 1, null));
        t3.addAction(new MoveAction(tape1, 0, Direction.RIGHT));
        t3.addAction(new MoveAction(tape1, 1, Direction.LEFT));

        Transition t4 = t.addTransition(b, b);
        t4.addReadSymbol(new ReadSymbol(tape1, 0, "0"));
        t4.addReadSymbol(new ReadSymbol(tape1, 1, "0"));
        t4.addAction(new WriteAction(tape1, 0, null));
        t4.addAction(new WriteAction(tape1, 1, null));
        t4.addAction(new MoveAction(tape1, 0, Direction.RIGHT));
        t4.addAction(new MoveAction(tape1, 1, Direction.LEFT));

        Transition t5 = t.addTransition(b, n);
        t5.addReadSymbol(new ReadSymbol(tape1, 0, "0"));
        t5.addReadSymbol(new ReadSymbol(tape1, 1, "1"));

        Transition t6 = t.addTransition(b, n);
        t6.addReadSymbol(new ReadSymbol(tape1, 0, "1"));
        t6.addReadSymbol(new ReadSymbol(tape1, 1, "0"));

        Transition t7 = t.addTransition(b, n);
        t7.addReadSymbol(new ReadSymbol(tape1, 0, (String)null));
        t7.addReadSymbol(new ReadSymbol(tape1, 1, "1"));

        Transition t8 = t.addTransition(b, n);
        t8.addReadSymbol(new ReadSymbol(tape1, 0, "1"));
        t8.addReadSymbol(new ReadSymbol(tape1, 1, (String)null));

        Transition t9 = t.addTransition(b, n);
        t9.addReadSymbol(new ReadSymbol(tape1, 0, (String)null));
        t9.addReadSymbol(new ReadSymbol(tape1, 1, "0"));

        Transition t10 = t.addTransition(b, n);
        t10.addReadSymbol(new ReadSymbol(tape1, 0, "0"));
        t10.addReadSymbol(new ReadSymbol(tape1, 1, (String)null));

        Transition t11 = t.addTransition(b, y);
        t11.addReadSymbol(new ReadSymbol(tape1, 0, (String)null));
        t11.addReadSymbol(new ReadSymbol(tape1, 1, (String)null));


        t.setAcceptingState(y);
        t.setFinalState(n);
        t.reinitDeterministic();
        Subscriber s = new Subscriber() {
            @Override
            public void read(String msg, Object... parameters) {
                if(msg.equals(TuringMachine.SUBSCRIBER_MSG_CURRENT_STATE_CHANGED))
                    System.out.println(t.getStateName(t.getCurrentState()));
                else if(msg.equals(TuringMachine.SUBSCRIBER_MSG_FIRED_TRANSITION))
                    System.out.println(parameters[1]);
                else if(msg.equals(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED))
                    System.out.println(parameters[1]);
                else if(msg.equals(TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE))
                    System.out.println(parameters[1]);
                else if(msg.equals(TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START))
                    System.out.println("Explore Start");
                else if(msg.equals(TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END))
                    System.out.println("Explore End");
                else if(msg.equals(TuringMachine.SUBSCRIBER_MSG_COMPUTE_START))
                    System.out.println("Compute Start");
                else if(msg.equals(TuringMachine.SUBSCRIBER_MSG_COMPUTE_END))
                    System.out.println("Compute End");
            }
        };

        s.subscribe(TuringMachine.SUBSCRIBER_MSG_CURRENT_STATE_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_FIRED_TRANSITION);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_COMPUTE_START);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_COMPUTE_END);

        t.execute();
    }

    static void testNonDeterministic(){
        TuringMachine t = new TuringMachine();

        t.addSymbol("0");
        t.addSymbol("1");
        t.addSymbol("2");

        Tape tape1 = t.getTape(0);
        tape1.setBottomBound(0);
        tape1.setTopBound(2);
        tape1.setLeftBound(0);
        tape1.setRightBound(2);

        tape1.setInitialHeadColumn(0, 0);
        tape1.setInitialHeadLine(0, 0);

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
        t1.addReadSymbol(new ReadSymbol(tape1, 0, "1"));
        t1.addAction(new MoveAction(tape1, 0, Direction.RIGHT));

        Transition t2 = t.addTransition(a, a);
        t2.addReadSymbol(new ReadSymbol(tape1, 0, "1"));
        t2.addAction(new MoveAction(tape1, 0, Direction.TOP));

        Transition t3 = t.addTransition(a, n);
        t3.addReadSymbol(new ReadSymbol(tape1, 0, "2"));

        Transition t4 = t.addTransition(a, y);
        t4.addReadSymbol(new ReadSymbol(tape1, 0, "0"));

        t.setInitialState(a);
        t.setInitialState(y);
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
                        System.out.println(parameters[1]);
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE:
                        System.out.println(parameters[1]);
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START:
                        System.out.println("Explore Start");
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END:
                        System.out.println("Explore End");
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_COMPUTE_START:
                        System.out.println("Compute Start");
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_COMPUTE_END:
                        System.out.println("Compute End");
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
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_COMPUTE_START);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_COMPUTE_END);

        t.execute();
    }

    public static void main(String[] args){
        testNonDeterministic();
    }
}

class Configuration {
    int state;
    Map<Tape, TapeConfiguration> tapeConfigurations;

    Configuration(int state, Map<Tape, TapeConfiguration> tapeConfigurations) {
        this.state = state;
        this.tapeConfigurations = tapeConfigurations;
    }

}


