/*
 * Copyright (c) 2018 Dimitri Watel
 */

package turingmachines;

import util.Subscriber;

import java.util.*;

/**
 * Class representing a transition of the graph of a Turing machine.
 *
 * A transition consists in :
 * <ul>
 *     <li>A input and an ouput states linked by this transition</li>
 *     <li>A set of read symbols</li>
 *     <li>A list of actions</li>
 * </ul>
 *
 * The transition may be fired if and only if
 * <ul>
 *     <li>The current state pointed by the state register is the input state.</li>
 *     <li>The set of read symbols is consistent with what is written on the tapes</li>
 * </ul>
 * In that case, if the transition is fired, all the actions are executed in the order given by the list of actions.
 *
 * A read symbol is a tuple of a tape, an index of a head on that tape and a set of symbols. This read symbol is
 * consistent with the tape if the symbol on the cell where the head is placed is in the set of symbols of the read
 * symbol.
 * If there are multiple read symbols, they should all be consistent with their associated tape and head.
 *
 * A transition cannot be instanciated. The method {@link TuringMachine#addTransition(Integer, Integer)} should be used
 * instead.
 */
public class Transition {

    /**
     * The input state of the transition
     */
    private int input;

    /**
     * The output state of the transition
     */
    private int output;

    /**
     * Set of read symbols of the transition. For each tape and each head (represented by its index in the list of
     * heads of the tape), this map contains a set of String (the symbols the head must read so that the transtiion
     * may be fired).
     */
    private Map<Tape, List<Set<String>>> readSymbols;

    /**
     * List of actions that are executed if the transition is fired.
     */
    private List<Action> actions;

    /**
     * The Turing machine of the transition.
     */
    private TuringMachine machine;

    /**
     * Build a new transition on the given machine from the given input state to the given output state with no read
     * symbol neither an action.
     * @param machine
     * @param input
     * @param output
     */
    Transition(TuringMachine machine, Integer input, Integer output) {
        this.machine = machine;

        this.input = input;
        this.output = output;

        readSymbols = new HashMap<>();
        actions = new ArrayList<>();
    }

    /**
     * Change the input state of the transition, used when the indentifier of the state change due to the removing of
     * another state.
     * @param input
     * @see TuringMachine#removeState(int)
     */
    void setInput(int input) {
        this.input = input;
    }

    /**
     * Change the output state of the transition, used when the indentifier of the state change due to the removing of
     * another state.
     * @param output
     * @see TuringMachine#removeState(int)
     */
    void setOutput(int output) {
        this.output = output;
    }

    /**
     * @return the input state of the transition
     */
    public int getInput() {
        return input;
    }

    /**
     * @return the output state of the transition
     */
    public int getOutput() {
        return output;
    }

    /**
     * @return an iterator on the list of actions of the transition.
     */
    public Iterator<Action> getActions(){
        return actions.iterator();
    }

    /**
     * Add the given action at the end of the list of actions of the transition.
     * A {@link TuringMachine#SUBSCRIBER_MSG_ADD_ACTION} message is broadcast to the class {@link util.Subscriber}.
     * @param action
     * @see util.Subscriber
     */
    public void addAction(Action action){
        actions.add(action);

        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_ACTION,
                this.machine, this, action.tape, action.head, action.getType(), action.value());
    }

    /**
     * Add the action at the given index from the list of actions of the transition if the index is between 0 and the
     * number of actions - 1.
     * A {@link TuringMachine#SUBSCRIBER_MSG_REMOVE_ACTION} message is broadcast to the class {@link util.Subscriber}.
     * @param index index of the removed action.
     * @see util.Subscriber
     */
    public void removeAction(int index){
        if(index < 0 || index >= actions.size())
            return;
        actions.remove(index);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_ACTION, this.machine, this, index);
    }

    /**
     * Remove all the actions associated with the given tape and the given head from the list of actions.
     * {@link TuringMachine#SUBSCRIBER_MSG_REMOVE_ACTION} messages are broadcast to the class {@link util.Subscriber}
     * for each removed action.
     * @param tape
     * @param head
     */
    void removeAllActions(Tape tape, int head) {
        for(int i = getNbActions() - 1; i >= 0; i--){
            Action action = actions.get(i);
            if(action.tape == tape && action.head == head)
                removeAction(i);
        }
    }

    /**
     * @return the number of actions of the transition
     */
    public int getNbActions() {
        return actions.size();
    }

    /**
     * @return an iterator to the list of read symbols.
     */
    Iterator<Map.Entry<Tape, List<Set<String>>>> getReadSymbols(){
        return readSymbols.entrySet().iterator();
    }

    /**
     * @param tape
     * @param head index of the head in the list of heads of the given tape.
     * @return an iterator to all the strings of the read symbol associated with the given tape and the given head.
     */
    public Iterator<String> getReadSymbols(Tape tape, int head) {
        List<Set<String>> list = this.readSymbols.get(tape);
        if(list == null)
            return new HashSet<String>().iterator();
        if(list.size() <= head)
            return new HashSet<String>().iterator();
        return readSymbols.get(tape).get(head).iterator();
    }

    /**
     * Add all the given symbols to the list of symbols of the read symbol associated with the given tape and the
     * given head.
     *
     * {@link TuringMachine#SUBSCRIBER_MSG_ADD_READ_SYMBOL} messages are broadcast to the class
     * {@link util.Subscriber} for each new symbol.
     * @param tape
     * @param head index of the head in the list of heads of the given tape.
     * @param symbols
     * @see util.Subscriber
     */
    public void addReadSymbols(Tape tape, int head, String... symbols){
        List<Set<String>> list = this.readSymbols.computeIfAbsent(tape, k -> new ArrayList<>());
        while(list.size() <= head)
            list.add(new HashSet<>());

        Set<String> set = list.get(head);
        for(String s : symbols)
            if(set.add(s))
                Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_READ_SYMBOL,
                        this.machine, this, tape, head, s);
    }

    /**
     * Remove all the given symbols from the list of symbols of the read symbol associated with the given tape and the
     * given head.
     *
     * {@link TuringMachine#SUBSCRIBER_MSG_REMOVE_READ_SYMBOL} messages are broadcast to the class
     * {@link util.Subscriber} for each removed symbol.
     * @param tape
     * @param head index of the head in the list of heads of the given tape.
     * @param symbols
     * @see util.Subscriber
     */
    public void removeReadSymbols(Tape tape, int head, String... symbols){
        List<Set<String>> list = this.readSymbols.get(tape);
        if(list == null)
            return;

        if(list.size() <= head)
            return;

        Set<String> set = list.get(head);

        for(String s : symbols)
            if(set.remove(s))
                Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_READ_SYMBOL,
                        this.machine, this, tape, head, s);
    }

    /**
     * Remove all the read symbols associated with the given tape and the given head.
     * {@link TuringMachine#SUBSCRIBER_MSG_REMOVE_READ_SYMBOL} messages are broadcast to the class
     * {@link util.Subscriber} for each removed symbol.
     * @param tape
     * @param head index of the head in the list of heads of the given tape.
     */
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

    /**
     * @return true if for each read symbol (consisting in a triplet (tape, head, symbols)), the symbol currently read
     * by the head on the tape is in the list of symbols of the triplet.
     */
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

    /**
     * Fire the transition, meaning that all the actions of the list of actions of the transition are executed in the
     * order given by the list.
     * If log is true, a {@link TuringMachine#SUBSCRIBER_MSG_FIRED_TRANSITION} message is broadcast to the class
     * {@link util.Subscriber} and every action may broadcast messages to tell that a head has moved and that a
     * symbol is written on a tape.
     * @param log
     * @see util.Subscriber
     */
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

