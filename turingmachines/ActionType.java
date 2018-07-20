/*
 * Copyright (c) 2018 Dimitri Watel
 */

package turingmachines;

/**
 * Represente the different types of action that can be executed while a transition is fired.
 */
public enum ActionType {
    /**
     * Type of a move action consisting in moving a head.
     */
    MOVE,

    /**
     * Type of a write action consisting in writing on a tape.
     */
    WRITE
}
