# TuringMachinesEditor V 0.9

This application is a graphical Turing Machine editor written in Java8 with JavaFX. For more informations on Turing machine, see https://en.wikipedia.org/wiki/Turing_machine 

## What can I do with this application?

With this application, you can create, edit, save, debug and execute a Turing machine. It works with deterministic and non deterministic machines.

* In case of a deterministic machine, the application runs the machine until an accepting or a refusing state is reached except if the execution goes on during more than a maximum number of iterations, in which case an error is displayed.
* In case of a non deterministic machine, the machine interprets the problem it solves as a decision problem (for which the answer is either YES or NO). The machine thus recognizes two types of final states : the accepting states and the refusing states. When executing a non deterministic machine, the application explores all the possible executions until a accepting state is reached or until a maximum number of iterations is reached or until all the possible executions were explored. If an accepting path is found, it is returned. Otherwise, if a refusing execution path is found, it is returned. Otherwise, an error is returned.

The machines can be restricted to usual basic machines (a single 1D-tape, a single tape and two symbols 1 and 0). But the editor allows some improvments that make the developments of the machines more easier: multiple tapes, 2D finite/semi-infinite,/infinite tapes, multiple heads, 36 symbols (one per letter and figure), some options to reduce the number of transitions of the machine (by defining more than one reading symbol and more than one action per transition).

## Who is this application for?

This application is for
* students and teachers for Computational complexity theory courses (especially teachers who work with an interactive whiteboard).
* people who love Turing machines and want a great graphical editor.

## How to install and run it?
 
Coming soon
 
## Quick start

### Starting screen 

When you run the application, you start with the following window. 

![Starting screen.](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart01.png)

### Build the input word, the tape and the heads of the machine

We are going to make a machine which accepts palindroms. It works with a 1D infinite tape and two heads. First click on the cells of the tape to write a symbol on that tape. 

![Click on a cell.](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart02.png) ![Click on a symbol to write it in the cell.](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart03.png)

Do that until you write a complete input word. 

![Complete input](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart04.png) 

Now we are going to move the black head on the first cell of the word. Click on the first cell and click on the black rectangle to move the black head. 

![Click on a cell.](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart05.png) ![Click on the black rectangle to move the black head.](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart06.png)

And, finally, we are going to add a new red head on the last cell of the word. Click on the last cell, click on the rectangle containing a "+" and choose the color red.

![Click on the last cell and click on the rectangle with a "+".](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart07.png)

![Click on the color red to add a head.](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart08.png)

### Build the graph of the machine

Coming soon

### Execute the machine

Coming soon
