package turingmachines;

public class MoveAction extends Action{

    private Direction direction;

    public MoveAction(Tape tape, int head, Direction direction) {
        super(tape, head);
        this.direction = direction;
    }

    @Override
    public void doAction(boolean log) {
        tape.moveHead(head, direction, log);
    }
}
