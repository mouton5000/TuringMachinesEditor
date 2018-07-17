package turingmachines;

/**
 * Represent an action which consists in writing a symbol on a tape on the position given by a head.
 *
 * @see Action
 */
public class WriteAction extends Action{

    /**
     * Symbol that should be written on the tape.
     */
    private String symbol;

    /**
     * Build a new Write Action consisting in writing the given symbol on the given tape at the position of the given
     * head.
     * @param tape
     * @param head Index of the head in the list of heads of the given tape.
     * @param symbol
     */
    public WriteAction(Tape tape, int head, String symbol) {
        super(tape, head);
        this.symbol = symbol;
    }

    @Override
    void doAction(boolean log) {
        tape.write(head, symbol, log);
    }

    @Override
    ActionType getType() {
        return ActionType.WRITE;
    }

    @Override
    Object value() {
        return symbol;
    }
}
