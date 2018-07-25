# TuringMachinesEditor V 0.9

This application is a graphical Turing Machine editor written in Java8 with JavaFX. For more informations on Turing machine, see https://en.wikipedia.org/wiki/Turing_machine 

## What can I do with this application?

With this application, you can create, edit, save, debug and execute a Turing machine. It works with either deterministic and non deterministic machines.

 * In case of a deterministic machine, the application runs the machine until an accepting or a refusing state is reached except if the execution goes on during more than a maximum number of iterations, in which case an error is displayed.
 * In case of a non deterministic machine, the machine interprets the problem it solves as a decision problem (for which the answer is either YES or NO). The machine thus recognizes two types of final states : the accepting states and the refusing states. When executing a non deterministic machine, the application explores all the possible executions until a accepting state is reached or until a maximum number of iterations is reached or until all the possible executions were explored. If an accepting path is found, it is returned. Otherwise, if a refusing execution path is found, it is returned. Otherwise, an error is returned.
 
 ## How to install and launch it?
 
 Coming soon
