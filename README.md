# TuringMachinesEditor V 0.9

This application is a graphical Turing Machine editor written in Java8 with JavaFX. For more informations on Turing machine, see https://en.wikipedia.org/wiki/Turing_machine 

## What can I do with this application?

With this application, you can create, edit, save, debug and execute a Turing machine. It works with deterministic and non deterministic machines.

* In case of a deterministic machine, the application runs the machine until an accepting or a refusing state is reached except if the execution goes on during more than a maximum number of iterations, in which case an error is displayed.
* In case of a non deterministic machine, the machine interprets the problem it solves as a decision problem (for which the answer is either YES or NO). The machine thus recognizes two types of final states : the accepting states and the refusing states. When executing a non deterministic machine, the application explores all the possible executions until a accepting state is reached or until a maximum number of iterations is reached or until all the possible executions were explored. If an accepting path is found, it is returned. Otherwise, if a refusing execution path is found, it is returned. Otherwise, an error is returned.

The machines can be restricted to usual basic machines (a single 1D-tape, a single tape and two symbols 1 and 0). But the editor allows some improvments that make the developments of the machines more easier: multiple tapes, 2D finite/semi-infinite,/infinite tapes, multiple heads, 36 symbols (one per letter and figure), some options to reduce the number of transitions of the machine (by defining more than one reading symbol and more than one action per transition).

## Who is this application for?

This application is for
* students and teachers of Computational complexity theory courses (especially teachers who work with an interactive whiteboard or students who want to practice).
* people who love Turing machines and want a great graphical editor.

## How to install and run it?
 
Coming soon
 
## Quick start

### Starting screen 

When you run the application, you start with the following window.

![Starting screen.](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart01.png)

### The machine

We are going to make a machine which accepts palindromes. It works with a 1D infinite tape and two heads. In order to simplify the machine, we assume the size of the input word is even.

Each head is placed at an extremity of the word. If the two heads read the same symbol, they delete it by writing a BLANK symbol and move the the next symbol (one head move to the right and the other to the left). If the two heads do not read the same symbol, the machine answers NO. If the two heads read a BLANK, the machine return YES.

The machine has then three states A, B and C. The state A is initial, the state B is final refusing and the state C is final accepting. The machine has the following transition table:

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

Do that until you write a complete input word. If you make a mistake, you can erase a symbol of a cell by clicking on that cell and by selecting another symbol. The symbol &#x2205; is the BLANK symbol.

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

You can now click three time on the white screen to add three nodes A, B and C. You can drag and drop the nodes to move them. If you add other nodes by accident, you can ignore them for now, we will remove them later.

![Click on the white screen to add nodes](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart13.png)

Click from node A to node B to add a transition from A to B.

![Add a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart14.png)
![Add a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart15.png)

Then click on the transition. You can edit the shape of the transition by dragging the green circles. 

![Edit the shape of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart16.png)
![Edit the shape of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart17.png)

You can now add four other transitions : two from A to A, one from A to B and one from A to C. Once this is done, you can click again on the leftmost icon of the menu to quit the "Add/Remove nodes/transitions" mode.

![Complete the graph](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart18.png)

If you add other transitions by accident, you can ignore them for now, we will remove them later.
 
### Initial and final states

Long click on the state A, until its color is darkened, to display the settings of that state. 

![Settings of a state](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart19.png)
![Settings of a state](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart20.png)

Note : you can remove any node you added by accident by opening, for each of those states, this menu and by clicking on the bottom right icon. This only works if you first enter the "Add/Remove nodes/transitions" mode.

Click on the top right icon to make the state A as an initial state. Open the same menu for B and click on the top left icon to make B as a final state. By default, if the machine solves a decision problem, a final state corresponds to the answer NO.  Open the menu for C and click on the top center icon to make C as a final accepting state corresponding to the answer YES.

![Settings of a state](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart21.png)
 
### Transitions table

We now have to edit the transitions in order to include the informations of the transitions table: the read symbols of the tape and the actions.

Long click on one of the transitions from A to A, until its color is darkened, to display the settings of the transition.

![Settings of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart22.png)
![Settings of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart23.png)

Note : you can remove any transition you added by accident by opening, for each of those transitions, this menu and by clicking on the top right icon. This only works if you first enter the "Add/Remove nodes/transitions" mode.

We want this transition to be fired if the black head and the red head read a "1". Click on the symbol 1. Then click on the red rectangle to select the red head and finally again on the symbol 1. You should see a black 1 and a red 1 meaning that the two heads should read a 1. 

![Read symbols of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart24.png)
![Read symbols of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart25.png)
![Read symbols of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart26.png)

If this transition is fired, we want the black head to delete the symbol on its current position and to move to the right and we want the red head to delete its symbol and to move to the left.

Click on the top center symbol to quit the submenu related to the read symbols and enter the submenu related to the actions.

![Read symbols of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart27.png)

Now click on the black rectangle to select the black head, drag the arrow symbols to the left and click on the BLANK symbol (&#x2205;). This means that the black head will write a BLANK symbol if this transition is fired. Then click on the right arrow, meaning that the black head will move to the right. Then select the red head and click again on the BLANK symbol and on the left arrow. If you make a mistake, you can remove the last action of the list by clicking on the trash icon (to the left of the symbols). 

![Actions of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart28.png)
![Actions of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart29.png)
![Actions of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart30.png)
![Actions of a transition](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart31.png)

You can now complete the transitions table of the graph:

![Complete the transitions](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart32.png)

### Save the machine

Click on the "Save" icon to save the machine.

![Complete the transitions](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart33.png)

On the left of this icon is the "Open" icon to load a saved machine. On the left again is the "New" icon to clear the screen and start a new machine.

### Execute the machine

When the machine is complete, you can click on the Gear wheel icon to enter the "Automatic firing" mode. The menu change. Click again on the same icon to return to the main menu. 

![Complete the transitions](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart34.png)
![Complete the transitions](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart35.png)

The initial state becomes blue, meaning it is the current state of the machine at the begining of the execution. Click on the play icon in the menu to run the machine. The execution is animated. By clicking on the stop icon, you return to the first configuration of the machine, you can then start the execution again. 

![Complete the transitions](https://raw.githubusercontent.com/mouton5000/TuringMachinesEditor/master/doc/quickstart36.png)

If you want to change the input word of the machine or change the states or the transitions of the graph, you have to quit the "Automatic firing" mode by clicking again on the Gear wheel icon.

### More...

Full help is displayed if you click on the "?" icon of the menu. 
