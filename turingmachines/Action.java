package turingmachines;

public abstract class Action {
    protected Tape tape;
    protected int head;

    public Action(Tape tape, int head) {
        this.tape = tape;
        this.head = head;
    }

    public abstract void doAction(boolean log);
}


