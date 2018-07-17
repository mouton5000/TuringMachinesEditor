package turingmachines;

/**
 * Represent an action which consists in moving a head on a tape. The head is moved in one of the four directions Up,
 * Right, Left, Down by one cell.
 *
 * @see Action
 * @see Direction
 */
public class MoveAction extends Action{

    /**
     * Direction of the move
     */
    private Direction direction;

    /**
     * Build a new Move Action consisting in moving the given head of the given tape in the given direction.
     * @param tape
     * @param head Index of the head in the list of heads of the given tape.
     * @param direction
     */
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
