package turingmachines;

public class WriteAction extends Action{

    private String symbol;

    public WriteAction(Tape tape, int head, String symbol) {
        super(tape, head);
        this.symbol = symbol;
    }

    @Override
    public void doAction(boolean log) {
        tape.write(head, symbol, log);
    }
}
