package turingmachines;

class MoveAction extends Action{

    private Direction direction;

    MoveAction(Tape tape, int head, Direction direction) {
        super(tape, head);
        this.direction = direction;
    }

    @Override
    void doAction(boolean log) {
        tape.moveHead(head, direction, log);
    }
}
