package turingmachines;

abstract class Action {
    Tape tape;
    int head;

    Action(Tape tape, int head) {
        this.tape = tape;
        this.head = head;
    }

    abstract void doAction(boolean log);
}


