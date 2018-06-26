package turingmachines;

import util.Pair;
import util.Subscriber;

import java.util.*;

public class TuringMachine {

    private static final int MAXIMUM_NON_DETERMINISTIC_SEARCH = 10000;

    public static final String SUBSCRIBER_MSG_FIRED_TRANSITION = "TMFireTransition";
    public static final String SUBSCRIBER_MSG_HEAD_WRITE = "TMHeadWrite";
    public static final String SUBSCRIBER_MSG_SYMBOL_WRITTEN = "TMSymolWritten";
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
    public static final String SUBSCRIBER_MSG_ADD_READ_SYMBOL = "TMAddReadSymbol";
    public static final String SUBSCRIBER_MSG_REMOVE_READ_SYMBOL = "TMRemoveReadSymbol";
    public static final String SUBSCRIBER_MSG_ADD_ACTION = "TMAddAction";
    public static final String SUBSCRIBER_MSG_REMOVE_ACTION = "TMRemoveAction";

    public static final String SUBSCRIBER_MSG_ADD_TAPE = "TMHeadAddTape";
    public static final String SUBSCRIBER_MSG_REMOVE_TAPE = "TMRemoveTape";
    public static final String SUBSCRIBER_MSG_TAPE_LEFT_CHANGED = "TMTapeLeftChanged";
    public static final String SUBSCRIBER_MSG_TAPE_RIGHT_CHANGED = "TMTapeRightChanged";
    public static final String SUBSCRIBER_MSG_TAPE_BOTTOM_CHANGED = "TMTapeBottomChanged";
    public static final String SUBSCRIBER_MSG_TAPE_TOP_CHANGED = "TMTapeTopChanged";

    public static final String SUBSCRIBER_MSG_ADD_SYMBOL = "TMAddSymbol";
    public static final String SUBSCRIBER_MSG_EDIT_SYMBOL = "TMEditSymbol";
    public static final String SUBSCRIBER_MSG_REMOVE_SYMBOL = "TMRemoveSymbol";

    public static final String SUBSCRIBER_MSG_INPUT_CHANGED= "TMInputChanged";

    public static final String SUBSCRIBER_MSG_ADD_HEAD = "TMHeadAddHead";
    public static final String SUBSCRIBER_MSG_REMOVE_HEAD = "TMRemoveHead";
    public static final String SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED = "TMHeadInitialPositionChanged";

    public static final String SUBSCRIBER_MSG_ERROR = "TMError";

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

    private Pair<List<Configuration>, List<Transition>> builtPath;
    private Pair<Integer, Integer> builtIndex;
    private Configuration manualInitialConfiguration;


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

        builtPath = null;
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
        Set<Transition> transitions = new HashSet<>(outputTransitions.get(state));
        for(int state2 = 0; state2 < getNbStates(); state2++){
            for(Transition transition : outputTransitions.get(state2)) {
                if(state == transition.getOutput())
                    transitions.add(transition);
                if(state < transition.getInput())
                    transition.setInput(transition.getInput() - 1);
                if(state < transition.getOutput())
                    transition.setOutput(transition.getOutput() - 1);
            }
        }
        for(Transition transition : transitions)
            this.removeTransition(transition);


        outputTransitions.remove(state);
        nbStates--;
        statesNames.remove(state);
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
        removeTape(tapes.get(i));
    }

    public void removeTape(Tape tape){
        for(int head = tape.getNbHeads() - 1; head >= 0; head--)
            tape.removeHead(head);
        tapes.remove(tape);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_TAPE, this, tape);
    }

    public Iterator<Tape> getTapes() {
        return tapes.iterator();
    }

    public int getNbTapes(){
        return tapes.size();
    }

    public void addSymbol(String symbol){
        if(symbols.contains(symbol)) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "The symbol already exists.");
            return;
        }
        symbols.add(symbol);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_SYMBOL, this, symbol);
    }

    public void editSymbol(int i, String symbol){
        if(symbols.contains(symbol)) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "The symbol already exists.");
            return;
        }
        String prevSymbol = symbols.set(i, symbol);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_EDIT_SYMBOL, this, i, prevSymbol, symbol);
    }

    public void removeSymbol(int i){
        String symbol = symbols.remove(i);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_SYMBOL, this, i, symbol);
    }

    public List<String> getSymbols(){
        return symbols;
    }

    public String getSymbol(int i){
        return symbols.get(i);
    }

    public boolean hasSymbol(String symbol) {
        return symbols.contains(symbol);
    }

    public int getNbSymbols(){
        return symbols.size();
    }

    void removeHeadFromTransitions(Tape tape, int head) {
        for(List<Transition> transitions: this.outputTransitions)
            for(Transition transition: transitions) {
                transition.removeAllReadSymbols(tape, head);
                transition.removeAllActions(tape, head);
            }
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

    public boolean isTerminated(){
        return isFinal(currentState);
    }

    public boolean isAccepting(){
        return isAccepting(currentState);
    }

    public void clear() {
        for(int i = this.getNbStates() - 1; i >= 0; i--)
            this.removeState(i);
        for(int i = this.getNbTapes() - 1; i >= 0; i--)
            this.removeTape(i);
        for(int i = this.getNbSymbols() - 1; i >= 0; i--)
            this.removeSymbol(i);
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
        loadConfiguration(configuration, false);
    }

    private void loadConfiguration(Configuration configuration, boolean log){
        for(Map.Entry<Tape, TapeConfiguration> entry: configuration.tapeConfigurations.entrySet())
            entry.getKey().loadConfiguration(entry.getValue(), log);
        this.setCurrentState(configuration.state, log);
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

    private Pair<List<Configuration>, List<Transition>> exploreNonDeterministic(
            Set<Configuration> initialConfigurations){
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

        LinkedList<Configuration> toReturnC = new LinkedList<>();
        LinkedList<Transition> toReturnT = new LinkedList<>();
        while(!initialConfigurations.contains(configuration)){
            toReturnC.addFirst(configuration);
            toReturnT.addFirst(arcFathers.get(configuration));
            configuration = fathers.get(configuration);
        }
        toReturnC.addFirst(configuration);
        return new Pair<>(new ArrayList<>(toReturnC), new ArrayList<>(toReturnT));

    }

    public void build(){
        if(!isValid()) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Invalid machine. No initial and/or final state.");
            return;
        }

        Subscriber.broadcast(SUBSCRIBER_MSG_COMPUTE_START, this);
        for(Tape tape : tapes)
                tape.reinit();

        Subscriber.broadcast(SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START, this);

        HashSet<Configuration> initialConfigurations = new HashSet<>();

        for (Integer state : initialStates) {
            this.setCurrentState(state, false);
            initialConfigurations.add(saveConfiguration());
        }
        builtPath = this.exploreNonDeterministic(initialConfigurations);
        builtIndex = new Pair<>(0, 0);

        if(builtPath == null)
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Cannot end computation.");

        Subscriber.broadcast(SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END, this);

    }

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
        Subscriber.broadcast(SUBSCRIBER_MSG_COMPUTE_END, this);
        return false;
    }

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

        Subscriber.broadcast(SUBSCRIBER_MSG_COMPUTE_START, this);
        return false;
    }

    public void loadFirstConfiguration(){
        if(builtPath == null) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Computation not built. Cannot execute.");
            return;
        }

        builtIndex.first = 0;
        builtIndex.second = 0;
        this.loadConfiguration(builtPath.first.get(builtIndex.first), true);
    }

    public void loadLastConfiguration(){
        if(builtPath == null) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Computation not built. Cannot execute.");
            return;
        }

        builtIndex.first = builtIndex.second = builtPath.first.size() - 1;
        this.loadConfiguration(builtPath.first.get(builtIndex.first), true);
    }

    public void clearBuild(){
        this.builtPath = null;
        this.builtIndex = null;
    }

    public void buildManual(){
        if(!isValid()) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Invalid machine. No initial and/or final state.");
            return;
        }

        Subscriber.broadcast(SUBSCRIBER_MSG_COMPUTE_START, this);
        for(Tape tape : tapes)
            tape.reinit();

        manualSetCurrentState(initialStates.iterator().next());
        manualInitialConfiguration = builtPath.first.get(0);
    }

    public void manualSetCurrentState(Integer state) {
        this.setCurrentState(state, false);
        Configuration configuration = this.saveConfiguration();
        builtPath = new Pair<>(new ArrayList<>(), new ArrayList<>());
        builtPath.first.add(configuration);
        builtIndex = new Pair<>(0, 0);
    }

    public void manualFireTransition(Transition transition){
        if(!transition.getInput().equals(this.currentState)){
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Cannot fire transition, invalid current state.");
            return;
        }
        if(!transition.isCurrentlyValid()){
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ERROR, this, "Cannot fire transition, symbols on tape do not match.");
            return;
        }

        transition.fire(true);
        setCurrentState(transition.getOutput(), true);

        Configuration configuration = this.saveConfiguration();

        builtPath.first.add(configuration);
        builtPath.second.add(transition);
        builtIndex.first++;
        builtIndex.second++;

    }

    public void clearManual(){
        if(manualInitialConfiguration == null)
            return;
        this.loadConfiguration(manualInitialConfiguration, true);
        this.builtPath = null;
        this.builtIndex = null;
        this.manualInitialConfiguration = null;
    }

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
        tape1.addHead();
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

        Tape tape1 = t.addTape();
        tape1.setBottomBound(0);
        tape1.setTopBound(2);
        tape1.setLeftBound(0);
        tape1.setRightBound(2);

        tape1.addHead();

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(state);
        sb.append("\n");
        for(Tape tape : tapeConfigurations.keySet()){
            sb.append(tapeConfigurations.get(tape));
            sb.append("\n");
        }
        return sb.toString();
    }
}


