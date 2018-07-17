package turingmachines;

/**
 * Class representing an action : either moving a head or writing on a tape.
 * This abstract class should not be instanciated. The concrete class {@link MoveAction} and {@link WriteAction} should
 * be used instead.
 *
 * Each action is associated with a head (on which the action is done), represented by its tape and the index of
 * the head in the list of heads of the tape.
 */
public abstract class Action {

    /**
     * Tape of the head on which the action is done.
     */
    Tape tape;

    /**
     * Index of the head on which the action is done in the list of heads of the tape of the head.
     */
    int head;

    Action(Tape tape, int head) {
        this.tape = tape;
        this.head = head;
    }

    /**
     * Execute the action. If log is true, a message (depending on the type of action) is broadcast to tell that a
     * head has moved ({@link TuringMachine#SUBSCRIBER_MSG_HEAD_MOVED}) or that a symbol is written
     * ({@link TuringMachine#SUBSCRIBER_MSG_SYMBOL_WRITTEN}).
     * @param log
     */
    abstract void doAction(boolean log);

    /**
     * @return the type of action ({@link ActionType#MOVE} if the action is a move action and
     * ({@link ActionType#WRITE} otherwise.
     */
    abstract ActionType getType();

    /**
     * @return the parameter of the action (the direction if it is a move action and the written symbol otherwise).
     */
    abstract Object value();
}


