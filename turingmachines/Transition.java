package turingmachines;

import util.Subscriber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Transition {
    private Integer input;
    private Integer output;
    private List<ReadSymbol> readSymbols;
    private List<Action> actions;
    private TuringMachine machine;

    public Transition(TuringMachine machine, Integer input, Integer output) {
        this.machine = machine;

        this.input = input;
        this.output = output;

        readSymbols = new ArrayList<>();
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
    }

    public void setAction(int i, Action action){
        actions.set(i, action);
    }

    public void removeAction(int i){
        actions.remove(i);
    }

    public Iterator<ReadSymbol> getReadSymbols(){
        return readSymbols.iterator();
    }

    public void addReadSymbol(ReadSymbol readSymbol){
        readSymbols.add(readSymbol);
    }

    public void setReadSymbol(int i, ReadSymbol readSymbol){
        readSymbols.set(i, readSymbol);
    }

    public void removeReadSymbol(int i){
        readSymbols.remove(i);
    }

    boolean isCurrentlyValid(){
        Iterator<ReadSymbol> readSymbolIt = getReadSymbols();

        while(readSymbolIt.hasNext())
            if(!readSymbolIt.next().isCurrentlyRead())
                return false;
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

