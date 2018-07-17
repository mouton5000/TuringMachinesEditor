/**
 * @author Dimitri Watel
 *
 * This package contains classes that simulate a Turing machine.
 *
 * It works with either deterministic and non deterministic machines.
 * <ul>
 *     <li>In case of a deterministic machine, the application runs the machine except if the execution goes on
 *     during more than a maximum number of iterations, in which case an error is returned. </li>
 *     <li>In case of a non deterministic machine, the application searches for an accepting path until a maximum
 *     number of iterations is reached or until all the possible executions were explored. If an accepting path is
 *     found, it is returned. Otherwise, if a refusing path is found, it is returned. Otherwise, if the exploration
 *     ends before the maximum number of iteration is reached, nothing is returned. In the last case, and error is
 *     returned. </li>
 * </ul>
 * (In order to simplify the implementation, the same class and methods are used for the two cases, which means that
 * the deterministic machine is seen as a non deterministic machine with only one possible computational path.)
 *
 * In order to simplify the machines (and their production/development), some usual improvement of the machines were
 * implemented:
 * <ul>
 *     <li>A machine may have any number of tapes, heads and symbols.</li>
 *     <li>Each tape is a 2 dimensional grid of any (finite or infinite) size. In each dimension, the tape may be
 *     finite, infinite or semi-infinite. if a head tries to move out of tape, the move is simply cancelled.</li>
 *     <li> Each transition specifies, for each head, any number of symbols that can be read by that head in order to
 *     fire the transition.
 *     For instance, if the machine has two heads and if the classical symbols are used (BLANK, 0 and 1),
 *     the transition may ask, in order to be fired, for the first head to read a 1 or a BLANK and for the second
 *     head to read a 1. If the first head reads a 0 or if the second head reads a 0 or a BLANK, the transition is
 *     not fired.
 *     </li>
 *     <li> Each transition specifies, for each head, an ordered list of number of actions that are done while the
 *     transition is fired (move a head, write on the tape with some head). All the actions are done one by one in the
 *     same order as the one provided by the list. </li>
 *     <li> A machine may have multiple input states, in which case it is necessarily non deterministic. </li>
 *     <li> If a non deterministic machine solves a decision problem, it must differ the accepting and the refusing
 *     states corresponding respectively to the YES and NO answers of the machine. </li>
 * </ul>
 *
 * Except for the last case, all those improvements are optional, which means you can, if you prefer, restrict the
 * machine to traditionnal cases.
 *
 *
 *
 * The useful classes:
 * <ul>
 *     <li>{@link turingmachines.TuringMachine} : main class, it simulates a (deterministic or not) Turing machine. </li>
 *     <li>{@link turingmachines.Transition} : it represents a transition of the graph of the machine. </li>
 *     <li>{@link turingmachines.Tape} : it represents the tapes of the machine. </li>
 * </ul>
 *
 * The package also contains the classes {@link turingmachines.Action}, {@link turingmachines.ActionType},
 * {@link turingmachines.MoveAction} , {@link turingmachines.Direction} and {@link turingmachines.WriteAction}
 * in order to represent the possible actions the machine can do during the firing of a transition (moving a head and
 * writing on a cell).
 *
 * Example of a non deterministic Turing machine searching for a 0 in a matrix:
 * <pre>
 *    // Build the machine
 *    TuringMachine t = new TuringMachine();
 *
 *    // Three symbols, 0, 1 and 2 (and the BLANK symbol)
 *    t.addSymbol("0");
 *    t.addSymbol("1");
 *    t.addSymbol("2");
 *
 *    // One finite size tape, a 3x3 grid.
 *    Tape tape1 = t.addTape();
 *    tape1.setBottomBound(0);
 *    tape1.setTopBound(2);
 *    tape1.setLeftBound(0);
 *    tape1.setRightBound(2);
 *
 *    // One head in position (0, 0)
 *    tape1.addHead();
 *    tape1.setInitialHeadColumn(0, 0);
 *    tape1.setInitialHeadLine(0, 0);
 *
 *    // The input of the machine
 *    tape1.writeInput(0, 0, "1");
 *    tape1.writeInput(0, 1, "1");
 *    tape1.writeInput(0, 2, "1");
 *    tape1.writeInput(1, 0, "1");
 *    tape1.writeInput(1, 1, "2");
 *    tape1.writeInput(1, 2, "2");
 *    tape1.writeInput(2, 0, "1");
 *    tape1.writeInput(2, 1, "1");
 *    tape1.writeInput(2, 2, "0");
 *
 *    // Three states
 *    int a = t.addState("A");
 *    int y = t.addState("Y");
 *    int n = t.addState("N");
 *
 *    // Four transitions
 *
 *    // The first one goes from state A to A and is fired if the head reads a 1, in which case the heads is moved to
 *    // the right.
 *    Transition t1 = t.addTransition(a, a);
 *    t1.addReadSymbols(tape1, 0, "1");
 *    t1.addAction(new MoveAction(tape1, 0, Direction.RIGHT));
 *
 *    // The second one goes from state A to A and is fired also if the head reads a 1, in which case the heads is
 *    // moved to the top. The machine is non deterministic.
 *    Transition t2 = t.addTransition(a, a);
 *    t2.addReadSymbols(tape1, 0, "1");
 *    t2.addAction(new MoveAction(tape1, 0, Direction.UP));
 *
 *    // The third one goes from state A to N and is fired if the head reads a 2. No action is done during the firing.
 *    Transition t3 = t.addTransition(a, n);
 *    t3.addReadSymbols(tape1, 0, "2");
 *
 *    // The last one goes from state A to Y and is fired if the head reads a 0. No action is done during the firing.
 *    Transition t4 = t.addTransition(a, y);
 *    t4.addReadSymbols(tape1, 0, "0");
 *
 *    // A is an initial state, Y and N are final. Y is accepting while N is refusing.
 *    t.setInitialState(a);
 *    t.setAcceptingState(y); // An accepting state is implicitely final.
 *    t.setFinalState(n); // A final state which is not accepting is refusing.
 *
 *    // This class is used as a simply publish-subscribe listener in order to print output when events occur.
 *    Subscriber s = new Subscriber() {
 *        @Override
 *        public void read(String msg, Object... parameters) {
 *            switch (msg) {
 *                case TuringMachine.SUBSCRIBER_MSG_CURRENT_STATE_CHANGED:
 *                    System.out.println(t.getStateName(t.getCurrentState()));
 *                    break;
 *                case TuringMachine.SUBSCRIBER_MSG_FIRED_TRANSITION:
 *                    System.out.println(parameters[1]);
 *                    break;
 *                case TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED:
 *                    System.out.println(((Tape)parameters[1]).print());
 *                    break;
 *                case TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE:
 *                    System.out.println(((Tape)parameters[1]).print());
 *                    break;
 *                case TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START:
 *                    System.out.println("Explore Start");
 *                    break;
 *                case TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END:
 *                    System.out.println("Explore End");
 *                    break;
 *            }
 *        }
 *    };
 *
 *    s.subscribe(TuringMachine.SUBSCRIBER_MSG_CURRENT_STATE_CHANGED);
 *    s.subscribe(TuringMachine.SUBSCRIBER_MSG_FIRED_TRANSITION);
 *    s.subscribe(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED);
 *    s.subscribe(TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE);
 *    s.subscribe(TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_START);
 *    s.subscribe(TuringMachine.SUBSCRIBER_MSG_NON_DETERMINISTIC_EXPLORE_END);
 *
 *    // Run the machine.
 *    t.execute();
 * </pre>
 *
 * A graphical interface is given in the {@link gui} package.
 * The {@link util} package contains useful classes (inclusing the {@link util.Subscriber} class).
 *
 * @see gui
 * @see turingmachines.TuringMachine
 * @see turingmachines.Transition
 * @see turingmachines.Tape
 * @see util.Subscriber
 * @see <a href="https://en.wikipedia.org/wiki/Turing_machine">Turing machines on wikipedia</a>
 */
package turingmachines;