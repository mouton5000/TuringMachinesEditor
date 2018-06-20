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

    public void addAction(Action action){
        actions.add(action);

        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_ACTION,
                this.machine, this, action.tape, action.head, action.getType(), action.value());
    }

    public void removeAction(int i){
        if(i < 0)
            return;
        actions.remove(i);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_ACTION, this.machine, this, i);
    }

    void removeAllActions(Tape tape, int head) {
        for(int i = getNbActions() - 1; i >= 0; i--){
            Action action = actions.get(i);
            if(action.tape == tape && action.head == head)
                removeAction(i);
        }
    }

    public int getNbActions() {
        return actions.size();
    }

    Iterator<Map.Entry<Tape, List<Set<String>>>> getReadSymbols(){
        return readSymbols.entrySet().iterator();
    }


    public Iterator<String> getReadSymbols(Tape tape, int head) {
        List<Set<String>> list = this.readSymbols.get(tape);
        if(list == null)
            return new HashSet<String>().iterator();
        if(list.size() <= head)
            return new HashSet<String>().iterator();
        return readSymbols.get(tape).get(head).iterator();
    }

    public void addReadSymbols(Tape tape, int head, String... readSymbols){
        List<Set<String>> list = this.readSymbols.computeIfAbsent(tape, k -> new ArrayList<>());
        while(list.size() <= head)
            list.add(new HashSet<>());

        Set<String> set = list.get(head);
        for(String s : readSymbols)
            if(set.add(s))
                Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_READ_SYMBOL,
                        this.machine, this, tape, head, s);
    }

    public void removeReadSymbols(Tape tape, int head, String... readSymbols){
        List<Set<String>> list = this.readSymbols.get(tape);
        if(list == null)
            return;

        if(list.size() <= head)
            return;

        Set<String> set = list.get(head);

        for(String s : readSymbols)
            if(set.remove(s))
                Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_READ_SYMBOL,
                        this.machine, this, tape, head, s);
    }

    void removeAllReadSymbols(Tape tape, int head) {
        List<Set<String>> list = this.readSymbols.get(tape);
        if(list == null)
            return;

        if(list.size() <= head)
            return;

        Set<String> readSymbols = list.remove(head);
        for(String s : readSymbols)
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_READ_SYMBOL,
                    this.machine, this, tape, head, s);
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

