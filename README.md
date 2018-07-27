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

### The machine

We are going to make a machine which accepts palindroms. It works with a 1D infinite tape and two heads. 

Each head is placed at an extremity of the word. If the two heads read the same symbol, they delete it by writing a BLANK symbol and move the the next symbol (one head move to the right and the other to the left). If the two heads do not read the same symbol, the machine answers NO. If the two heads read a BLANK, the machine return YES.

The machine has then three states A, B and C. The state B is final refusing and the state C is final accepting. The machine has the following transition table:

| Input | Output | First head symbol | Second head symbol | First head actions | Second head actions |
|-------|--------|-------------------|--------------------|--------------------|---------------------|
| A     | A      | 0                 | 0                  | B &#8594;          | B &#8592;           |
| A     | A      | 1                 | 1                  | B &#8594;          | B &#8592;           |
| A     | B      | 1                 | 0                  |                    |                     |
| A     | B      | 0                 | 1                  |                    |                     |
| A     | C      | B                 | B                  |                    |                     |


### Build the input word, the tape and the heads of the machine

First click on the cells of the tape to write a symbol on that tape. 

![Click on a cell.](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart02.png) ![Click on a symbol to write it in the cell.](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart03.png)

Do that until you write a complete input word. 

![Complete input](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart04.png) 

Now we are going to move the black head on the first cell of the word. Click on the first cell and click on the black rectangle to move the black head. 

![Click on a cell.](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart05.png) ![Click on the black rectangle to move the black head.](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart06.png)

And, finally, we are going to add a new red head on the last cell of the word. Click on the last cell, click on the rectangle containing a "+" and choose the color red.

![Click on the last cell and click on the rectangle with a "+".](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart07.png)

![Click on the color red to add a head.](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart08.png)

### Build the graph of the machine

Above the tape is a white screen. This screen will contain the graph. First resize the tape screen and the white screen by dragging the horizontal separating line. 

![Resize the screens](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart09.png)

Then click on the arrow on the top-right corner to display the main menu.

![Main menu](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart10.png)
![Main menu](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart11.png)

Click on the leftmost icon to enter the "Add/Remove nodes/transitions" mode. Whithout this mode, you cannot add or remove a node or a transition. (This is a safety so that you do not add or remove a node or a transition by accident.)

![Edit graph mode](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart12.png)

You can now click three time on the white screen to add three nodes A, B and C. You can drag and drop the nodes to move them. If you added a fourth node by accident, you can ignore it for now.

![Click on the white screen to add nodes](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart13.png)

Click from node A to node B to add a transition from A to B.

![Add a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart14.png)
![Add a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart15.png)

Then click on the transition. You can edit the shape of the transition by dragging the green circles. 

![Edit the shape of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart16.png)
![Edit the shape of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart17.png)

You can now add another transition from A to B and a transition from A to C to complete the graph.

![Complete the graph](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart18.png)


### Execute the machine

Coming soon
