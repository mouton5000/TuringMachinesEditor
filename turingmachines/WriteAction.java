package turingmachines;

class WriteAction extends Action{

    private String symbol;

    WriteAction(Tape tape, int head, String symbol) {
        super(tape, head);
        this.symbol = symbol;
    }

    @Override
    void doAction(boolean log) {
        tape.write(head, symbol, log);
    }
}
