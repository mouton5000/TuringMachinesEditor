package turingmachines;

public class WriteAction extends Action{

    private String symbol;

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
