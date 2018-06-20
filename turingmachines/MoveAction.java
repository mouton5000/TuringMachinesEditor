package turingmachines;

public class MoveAction extends Action{

    private Direction direction;

    public MoveAction(Tape tape, int head, Direction direction) {
        super(tape, head);
        this.direction = direction;
    }

    @Override
    void doAction(boolean log) {
        tape.moveHead(head, direction, log);
    }

    @Override
    ActionType getType() {
        return ActionType.MOVE;
    }

    @Override
    Object value() {
        return direction;
    }
}
