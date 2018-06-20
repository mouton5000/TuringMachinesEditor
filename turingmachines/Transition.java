package turingmachines;

import util.Subscriber;

import java.util.*;

public class Transition {
    private Integer input;
    private Integer output;
    private Map<Tape, List<Set<String>>> readSymbols;
    private List<Action> actions;
    private TuringMachine machine;

    Transition(TuringMachine machine, Integer input, Integer output) {
        this.machine = machine;

        this.input = input;
        this.output = output;

        readSymbols = new HashMap<>();
        actions = new ArrayList<>();
    }

    public Integer getInput() {
        return input;
    }

    public Integer getOutput() {
        return output;
    }

    public Iterator<Action> getActions(){
        return actions.iterator();
    }

    void addAction(Action action){
        actions.add(action);
    }

    void setAction(int i, Action action){
        actions.set(i, action);
    }

    void removeAction(int i){
        actions.remove(i);
    }

    Iterator<Map.Entry<Tape, List<Set<String>>>> getReadSymbols(){
        return readSymbols.entrySet().iterator();
    }

    Set<String> getReadSymbols(Tape tape, int head){
        List<Set<java.lang.String>> list = this.readSymbols.get(tape);
        if(list == null)
            return new HashSet<>();

        if(list.size() <= head)
            return new HashSet<>();

        return list.get(head);
    }

    void addReadSymbols(Tape tape, int head, String... readSymbols){
        List<Set<String>> list = this.readSymbols.computeIfAbsent(tape, k -> new ArrayList<>());
        while(list.size() <= head)
            list.add(new HashSet<>());
        list.get(head).addAll(Arrays.asList(readSymbols));
    }

    void removeReadSymbol(Tape tape, int head, String... readSymbols){
        List<Set<String>> list = this.readSymbols.get(tape);
        if(list == null)
            return;

        if(list.size() <= head)
            return;

        list.get(head).removeAll(Arrays.asList(readSymbols));
    }

    boolean isCurrentlyValid(){
        for(Map.Entry<Tape, List<Set<String>>> entry : readSymbols.entrySet()){
            Tape tape = entry.getKey();
            int head = 0;
            for(Set<String> symbols : entry.getValue()){
                if(!symbols.isEmpty() && !symbols.contains(tape.read(head)))
                    return false;
                head++;
            }
        }
        return true;
    }

    void fire(boolean log){
        if(log)
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_FIRED_TRANSITION, this.machine, this);

        Iterator<Action> actionsIt = getActions();
        while(actionsIt.hasNext())
            actionsIt.next().doAction(log);
    }

    @Override
    public String toString() {
        return this.machine.getStateName(input) + " --> " + this.machine.getStateName(output);
    }
}

