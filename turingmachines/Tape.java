package turingmachines;

import util.Subscriber;

import java.util.*;

/**
 * Class representing a tape of a Turing machine.
 *
 * Such a tape is a 2 dimensional grid of any (finite or infinite) size. In each dimension, the tape may be
 * finite, infinite or semi-infinite. Each tape is associated with four Integer bounds corresponding to the four
 * directions (left, right, bottom and top). A bound is null if the tape is infinite in the corresponding direction.
 * The left bound is always lower or equal to the right bound. The same occurs for the bottom and the top bounds.
 *
 * Each tape may contain any number of heads. A head is identified with an integer which can be seen as the index of
 * the head in the list of heads of the tape. Be aware that this identifier may change when a head is removed.
 *
 * A transition cannot be instanciated. The method {@link TuringMachine#addTape()} should be used
 * instead.
 */
public class Tape{

    /**
     * Left bound of the tape : the lowest column of a cell in the tape. If the tape is infinite to the left,
     * this value is null.
     * @see #tapeRightBound
     * @see #tapeBottomBound
     * @see #tapeTopBound
     */
    private Integer tapeLeftBound;

    /**
     * Right bound of the tape : the highest column of a cell in the tape. If the tape is infinite to the right,
     * this value is null.
     * @see #tapeLeftBound
     * @see #tapeBottomBound
     * @see #tapeTopBound
     */
    private Integer tapeRightBound;

    /**
     * Bottom bound of the tape : the lowest line of a cell in the tape. If the tape is infinite to the bottom,
     * this value is null.
     * @see #tapeLeftBound
     * @see #tapeRightBound
     * @see #tapeTopBound
     */
    private Integer tapeBottomBound;

    /**
     * Top bound of the tape : the highest line of a cell in the tape. If the tape is infinite to the top,
     * this value is null.
     * @see #tapeLeftBound
     * @see #tapeRightBound
     * @see #tapeBottomBound
     */
    private Integer tapeTopBound;

    /**
     * Input word of the machine written on the tape. For each line x and each column y,
     * <pre>cells.get(x).get(y)</pre> contains a String corresponding to the symbol written on the tape at line x and
     * column y at the beginning of an execution of the machine. If no symbol is written (i.e. the BLANK symbol is
     * written), no string is stored in the map.
     * @see #cells
     */
    private Map<Integer, Map<Integer, String>> inputCells;

    /**
     * Set of symbols written on the tape during an execution of the machine. For each line x and each column y,
     * <pre>cells.get(x).get(y)</pre> contains a String corresponding to the symbol written on the tape at line x and
     * column y. If no symbol is written (i.e. the BLANK symbol is written), no string is stored in the map.
     * @see #inputCells
     */
    private Map<Integer, Map<Integer, String>> cells;

    /**
     * Number of heads of the tape
     */
    private Integer nbHeads;

    /**
     * For each head i, this list contains (at index i) the column where this head is at the beggining of an execution
     * of the machine.
     * @see #initialHeadsLine
     * @see #headsColumn
     * @see #headsLine
     */
    private List<Integer> initialHeadsColumn;

    /**
     * For each head i, this list contains (at index i) the line where this head is at the beggining of an execution
     * of the machine.
     * @see #initialHeadsColumn
     * @see #headsColumn
     * @see #headsLine
     */
    private List<Integer> initialHeadsLine;

    /**
     * For each head i, this list contains (at index i) the column where this head is during an execution
     * of the machine.
     * @see #initialHeadsColumn
     * @see #initialHeadsLine
     * @see #headsLine
     */
    private List<Integer> headsColumn;

    /**
     * For each head i, this list contains (at index i) the line where this head is during an execution
     * of the machine.
     * @see #initialHeadsColumn
     * @see #initialHeadsLine
     * @see #headsColumn
     */
    private List<Integer> headsLine;

    /**
     * Machine containing this tape.
     */
    private TuringMachine machine;

    /**
     * Build a new tape of the given machine. A default tape is one dimensionnal and contains no head. No symbol is
     * written on it.
     * @param machine
     */
    Tape(TuringMachine machine){
        this.machine = machine;

        tapeTopBound = 0;
        tapeBottomBound = 0;
        tapeLeftBound = null;
        tapeRightBound = null;

        nbHeads = 0;
        initialHeadsColumn = new ArrayList<>();
        initialHeadsLine = new ArrayList<>();
        headsColumn = new ArrayList<>();
        headsLine = new ArrayList<>();

        cells = new HashMap<>();
        inputCells = new HashMap<>();
    }

    /**
     * @return the default column of a head added to the tape: the center column if the tape is not
     * vertically semi-infinite and the finite bound otherwise.
     */
    private int initColumn(){
        if(tapeLeftBound == null && tapeRightBound == null)
            return 0;
        if(tapeLeftBound == null)
            return tapeRightBound;
        if(tapeRightBound == null)
            return tapeLeftBound;
        return (tapeLeftBound + tapeRightBound) / 2;
    }
    /**
     * @return the default line of a head added to the tape: the center line if the tape is not
     * horizontally semi-infinite and the finite bound otherwise.
     */
    private int initLine(){
        if(tapeTopBound == null && tapeBottomBound == null)
            return 0;
        if(tapeTopBound == null)
            return tapeBottomBound;
        if(tapeBottomBound == null)
            return tapeTopBound;
        return (tapeTopBound + tapeBottomBound) / 2;
    }

    /**
     * @return the number of heads of the tape.
     */
    public Integer getNbHeads() {
        return nbHeads;
    }

    /**
     * Add a new head to the tape at the position given by {@link #initColumn()} and {@link #initLine()}.
     */
    public void addHead(){
        int column = initColumn();
        int line = initLine();
        addHead(line, column);
    }

    /**
     * Add a new head to the tape at the given line and column.
     * A {@link TuringMachine#SUBSCRIBER_MSG_ADD_HEAD} message is broadcast to the class {@link util.Subscriber}.
     * @param line
     * @param column
     * @see util.Subscriber
     */
    public void addHead(int line, int column){
        if(tapeLeftBound != null && column < tapeLeftBound)
            return;
        if(tapeRightBound != null && column > tapeRightBound)
            return;
        if(tapeBottomBound != null && line < tapeBottomBound)
            return;
        if(tapeTopBound != null && line > tapeTopBound)
            return;
        
        nbHeads++;
        initialHeadsColumn.add(column);
        initialHeadsLine.add(line);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_HEAD, this.machine, this, nbHeads - 1, line, column);
    }

    /**
     * Remove the given head from the tape. Be aware that every head with a greater index identifier will see their
     * index decreased by one.
     * A {@link TuringMachine#SUBSCRIBER_MSG_ADD_HEAD} message is broadcast to the class {@link util.Subscriber}.
     * @param head index of the head in the list of heads of this tape
     * @see util.Subscriber
     */
    public void removeHead(int head){
        machine.removeHeadFromTransitions(this, head);
        nbHeads--;
        initialHeadsColumn.remove(head);
        initialHeadsLine.remove(head);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_HEAD, this.machine, this, head);
    }

    /**
     * Set the left bound of the tape to the given value. A null value means that the tape is infinite to the left.
     * If the right bound is lower than the given value, the left bound if set to the right bound instead.
     * Every input symbol not anymore on the tape after the change is removed.
     * Every head not anymore on the tape after the change is moved to the left bound.
     *
     * A {@link TuringMachine#SUBSCRIBER_MSG_TAPE_LEFT_CHANGED} message is broadcast to the class
     * {@link util.Subscriber}. {@link TuringMachine#SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED} and
     * {@link TuringMachine#SUBSCRIBER_MSG_INPUT_CHANGED} may be broadcast to tell that heads were moved and that
     * symbols were removed from the tape.
     * @param left
     * @see util.Subscriber
     */
    public void setLeftBound(Integer left){
        if(left != null && tapeRightBound != null && tapeRightBound < left)
            tapeLeftBound = tapeRightBound;
        else
            tapeLeftBound = left;

        checkHeadsColumns();
        checkInput(true, false);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_TAPE_LEFT_CHANGED, this.machine, this, tapeLeftBound);
    }

    /**
     * Set the right bound of the tape to the given value. A null value means that the tape is infinite to the right.
     * If the left bound is greater than the given value, the right bound if set to the left bound instead.
     * Every input symbol not anymore on the tape after the change is removed.
     * Every head not anymore on the tape after the change is moved to the right bound.
     *
     * A {@link TuringMachine#SUBSCRIBER_MSG_TAPE_RIGHT_CHANGED} message is broadcast to the class
     * {@link util.Subscriber}. {@link TuringMachine#SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED} and
     * {@link TuringMachine#SUBSCRIBER_MSG_INPUT_CHANGED} may be broadcast to tell that heads were moved and that
     * symbols were removed from the tape.
     * @param right
     * @see util.Subscriber
     */
    public void setRightBound(Integer right){
        if(right != null && tapeLeftBound != null && tapeLeftBound > right)
            tapeRightBound = tapeLeftBound;
        else
            tapeRightBound = right;

        checkHeadsColumns();
        checkInput(true, false);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_TAPE_RIGHT_CHANGED, this.machine, this, tapeRightBound);
    }

    /**
     * Set the bottom bound of the tape to the given value. A null value means that the tape is infinite to the bottom.
     * If the top bound is lower than the given value, the bottom bound if set to the top bound instead.
     * Every input symbol not anymore on the tape after the change is removed.
     * Every head not anymore on the tape after the change is moved to the bottom bound.
     *
     * A {@link TuringMachine#SUBSCRIBER_MSG_TAPE_BOTTOM_CHANGED} message is broadcast to the class
     * {@link util.Subscriber}. {@link TuringMachine#SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED} and
     * {@link TuringMachine#SUBSCRIBER_MSG_INPUT_CHANGED} may be broadcast to tell that heads were moved and that
     * symbols were removed from the tape.
     * @param bottom
     * @see util.Subscriber
     */
    public void setBottomBound(Integer bottom){
        if(bottom != null && tapeTopBound != null && tapeTopBound < bottom)
            tapeBottomBound = tapeTopBound;
        else
            tapeBottomBound = bottom;

        checkHeadsLines();
        checkInput(false, true);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_TAPE_BOTTOM_CHANGED, this.machine, this, tapeBottomBound);
    }

    /**
     * Set the top bound of the tape to the given value. A null value means that the tape is infinite to the top.
     * If the bottom bound is greater than the given value, the top bound if set to the bottom bound instead.
     * Every input symbol not anymore on the tape after the change is removed.
     * Every head not anymore on the tape after the change is moved to the top bound.
     *
     * A {@link TuringMachine#SUBSCRIBER_MSG_TAPE_TOP_CHANGED} message is broadcast to the class
     * {@link util.Subscriber}. {@link TuringMachine#SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED} and
     * {@link TuringMachine#SUBSCRIBER_MSG_INPUT_CHANGED} may be broadcast to tell that heads were moved and that
     * symbols were removed from the tape.
     * @param top
     * @see util.Subscriber
     */
    public void setTopBound(Integer top){
        if(top != null && tapeBottomBound != null && tapeBottomBound > top)
            tapeTopBound = tapeBottomBound;
        else
            tapeTopBound = top;

        checkHeadsLines();
        checkInput(false, true);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_TAPE_TOP_CHANGED, this.machine, this, tapeTopBound);
    }

    /**
     * @param head index of the head in the list of heads of the machine.
     * @return the column of the given head at the beggining of an execution of the machine.
     * @see #getInitialHeadLine(int)
     * @see #setInitialHeadColumn(int, int)
     * @see #setInitialHeadLine(int, int)
     */
    Integer getInitialHeadColumn(int head){
        return initialHeadsColumn.get(head);
    }

    /**
     * @param head index of the head in the list of heads of the machine.
     * @return the line of the given head at the beggining of an execution of the machine.
     * @see #getInitialHeadColumn(int)
     * @see #setInitialHeadColumn(int, int)
     * @see #setInitialHeadLine(int, int)
     */
    Integer getInitialHeadLine(int head){
        return initialHeadsLine.get(head);
    }

    /**
     * Set the column of the given head at the beggining of an execution of the machine to the given column.
     * A {@link TuringMachine#SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED} message is broadcast to the class
     * {@link util.Subscriber}.
     *
     * @param head index of the head in the list of heads of the machine.
     * @param column
     * @see #getInitialHeadLine(int)
     * @see #getInitialHeadColumn(int)
     * @see #setInitialHeadLine(int, int)
     * @see util.Subscriber
     */
    public void setInitialHeadColumn(int head, int column) {
        if ((tapeLeftBound == null || column >= tapeLeftBound)
                && (tapeRightBound == null || column <= tapeRightBound)) {
            initialHeadsColumn.set(head, column);
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED, this.machine, this, head, initialHeadsLine.get(head), column);
        }
    }

    /**
     * Check, for each head, if that head is still on a column of the tape after the bounds were changed. Otherwise,
     * the head is moved to the closest column of the tape.
     * {@link TuringMachine#SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED} messages may be broadcast to the class
     * {@link util.Subscriber} to tell that heads were moved.
     *
     * @see util.Subscriber
     */
    private void checkHeadsColumns(){
        for(int i = 0; i < nbHeads; i++) {
            int column = initialHeadsColumn.get(i);
            if (tapeLeftBound != null && tapeLeftBound > column)
                setInitialHeadColumn(i, tapeLeftBound);
            if (tapeRightBound != null && tapeRightBound < column)
                setInitialHeadColumn(i, tapeRightBound);
        }
    }

    /**
     * Set the line of the given head at the beggining of an execution of the machine to the given line.
     * A {@link TuringMachine#SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED} message is broadcast to the class
     * {@link util.Subscriber}.
     *
     * @param head index of the head in the list of heads of the machine.
     * @param line
     * @see #getInitialHeadLine(int)
     * @see #getInitialHeadColumn(int)
     * @see #setInitialHeadColumn(int, int)
     * @see util.Subscriber
     */
    public void setInitialHeadLine(int head, int line) {
        if ((tapeBottomBound == null || line >= tapeBottomBound)
                && (tapeTopBound == null || line <= tapeTopBound)) {
            initialHeadsLine.set(head, line);
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED, this.machine, this, head, line, initialHeadsColumn.get(head));

        }
    }

    /**
     * Check, for each head, if that head is still on a line of the tape after the bounds were changed. Otherwise,
     * the head is moved to the closest line of the tape.
     * {@link TuringMachine#SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED} messages may be broadcast to the class
     * {@link util.Subscriber} to tell that heads were moved.
     *
     * @see util.Subscriber
     */
    private void checkHeadsLines(){
        for(int i = 0; i < nbHeads; i++) {
            int line = initialHeadsLine.get(i);
            if (tapeBottomBound != null && tapeBottomBound > line)
                setInitialHeadLine(i, tapeBottomBound);
            if (tapeTopBound != null && tapeTopBound < line)
                setInitialHeadLine(i, tapeTopBound);
        }
    }

    /**
     * Clear the tape.
     */
    void reinit(){
        headsColumn.clear();
        headsColumn.addAll(initialHeadsColumn);
        headsLine.clear();
        headsLine.addAll(initialHeadsLine);

        cells.clear();
        for(Map.Entry<Integer, Map<Integer, String>> pair: inputCells.entrySet()){
            Integer column = pair.getKey();
            Map<Integer, String> columnCells = pair.getValue();
            Map<Integer, String> columnCells2 = new HashMap<>(columnCells);
            cells.put(column, columnCells2);
        }
    }

    /**
     * Move a head in the given direction (left, right, down or up) by one cell. This function is called during the
     * execution of the machine.
     *
     * If log is true, a {@link TuringMachine#SUBSCRIBER_MSG_HEAD_MOVED} message is broadcast to
     * the class {@link util.Subscriber}.
     * @param head index of the head in the list of heads of the machine.
     * @param direction
     * @param log
     * @see util.Subscriber
     */
    void moveHead(int head, Direction direction, boolean log){
        Integer column = headsColumn.get(head);
        Integer line = headsLine.get(head);

        switch (direction){
            case DOWN:
                if(!line.equals(tapeBottomBound)) {
                    headsLine.set(head, line - 1);
                    if(log)
                        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED, this.machine, this, head, line - 1, column);
                }
                break;
            case UP:
                if(!line.equals(tapeTopBound)) {
                    headsLine.set(head, line + 1);
                    if(log)
                        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED, this.machine, this, head, line + 1, column);
                }
                break;
            case LEFT:
                if(!column.equals(tapeLeftBound)) {
                    headsColumn.set(head, column - 1);
                    if(log)
                        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED, this.machine, this, head, line, column - 1);
                }
                break;
            case RIGHT:
                if(!column.equals(tapeRightBound)){
                    headsColumn.set(head, column + 1);
                    if(log)
                        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED, this.machine, this, head, line, column + 1);
                }
                break;
        }
    }

    /**
     * @param head index of the head in the list of heads of the machine.
     * @return the symbol written in the cell of the given head (null if the symbol is BLANK) during the current
     * execution of the machine.
     */
    String read(int head){

        Integer column = headsColumn.get(head);
        Integer line = headsLine.get(head);

        return this.getSymbolAt(line, column, false);
    }

    /**
     *
     * @param line
     * @param column
     * @param input
     * @return the symbol written in the cell at the given line and column (or null if the symbol is BLANK). If input
     * is true, the returned symbol is the one of the input word of the machine, otherwise it is the symbol written
     * during the current execution of the machine.
     */
    String getSymbolAt(Integer line, Integer column, boolean input){

        Map<Integer, Map<Integer, String>> cells = (input?this.inputCells:this.cells);

        Map<Integer, String> columnCells = cells.get(column);
        if(columnCells == null) // All the column is white
            return null;
        return columnCells.get(line);

    }

    /**
     * Write the given symbol (null if the symbol is BLANK) at the given line and column of the input word of the
     * machine.
     *
     * A {@link TuringMachine#SUBSCRIBER_MSG_INPUT_CHANGED} message is broadcast to the class {@link util.Subscriber}.
     * @param line
     * @param column
     * @param symbol
     * @see util.Subscriber
     */
    public void writeInput(int line, int column, String symbol){
        write(line, column, symbol, true);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_INPUT_CHANGED, this.machine, this, line, column, symbol);
    }

    /**
     * Write the given symbol (null if the symbol is BLANK) at the position of the given head during the current
     * execution of the machine (it does not affect the input word of the machine).
     * If log is true, {@link TuringMachine#SUBSCRIBER_MSG_HEAD_WRITE} and
     * {@link TuringMachine#SUBSCRIBER_MSG_SYMBOL_WRITTEN} messages are broadcast to the class {@link util.Subscriber}.
     * @param head index of the head in the list of heads of the machine.
     * @param symbol
     * @param log
     * @see util.Subscriber
     */
    void write(int head, String symbol, boolean log){

        Integer column = headsColumn.get(head);
        Integer line = headsLine.get(head);

        this.write(line, column, symbol, false);
        if(log) {
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE, this.machine, this, head);
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_SYMBOL_WRITTEN, this.machine, this,
                    line, column, symbol);
        }

    }

    /**
     * Write the given symbol (null if the symbol is BLANK) at the given line and column of the tape. If input is
     * true, this symbol is written on the input word of the machine, otherwise it is written in the given execution
     * (and does not affect the input word of the machine).
     * @param line
     * @param column
     * @param symbol
     * @param input
     */
    private void write(Integer line, Integer column, String symbol, boolean input){

        Map<Integer, Map<Integer, String>> cells = (input?this.inputCells:this.cells);

        Map<Integer, String> columnCells = cells.get(column);
        if(symbol == null) { // White symbol
            if (columnCells == null) // All the column is white
                return;
            String cell = columnCells.get(line);
            if (cell != null) {
                columnCells.remove(line);
                if (columnCells.size() == 0)
                    cells.remove(column);
            }
        }
        else{
            if (columnCells == null) {
                columnCells = new HashMap<>();
                cells.put(column, columnCells);
            }
            columnCells.put(line, symbol);
        }
    }

    /**
     * For each symbol of the input word written on the tape, check if the symbol is still on the tape after the bounds
     * were changed.
     * Otherwise the symbol is removed from the tape. horizontalChange (respectively verticalChange) is
     * true if and only if the left and right (respectively bottom and top) bounds of the tape were changed.
     *
     * {@link TuringMachine#SUBSCRIBER_MSG_INPUT_CHANGED} messages may be broadcast to the class
     * {@link util.Subscriber} to tell that input word was changed.
     *
     * @param horizontalChange
     * @param verticalChange
     * @see util.Subscriber
     */
    private void checkInput(boolean horizontalChange, boolean verticalChange){
        Iterator<Map.Entry<Integer, Map<Integer, String>>> it1 = inputCells.entrySet().iterator();
        while(it1.hasNext()){
            Map.Entry<Integer, Map<Integer, String>> entry1 = it1.next();
            Integer column = entry1.getKey();
            if(horizontalChange &&
                    ((tapeLeftBound != null && column < tapeLeftBound) ||
                            (tapeRightBound != null && column > tapeRightBound))
                    ) {
                for(Integer line : entry1.getValue().keySet())
                    Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_INPUT_CHANGED, this.machine, this, line, column, null);
                it1.remove();
            }
            else if(verticalChange){
                Iterator<Map.Entry<Integer, String>> it2 = entry1.getValue().entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry<Integer, String> entry2 = it2.next();
                    Integer line = entry2.getKey();
                    if ((tapeBottomBound != null && line < tapeBottomBound) ||
                            (tapeTopBound != null && line > tapeTopBound)) {
                        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_INPUT_CHANGED, this.machine, this, line, column, null);
                        it2.remove();
                    }
                }
                if (entry1.getValue().isEmpty())
                    it1.remove();
            }
        }
    }

    /**
     * @return a snapshot of the tape (position of the heads and word currently written (not necessarily the input word)
     * on
     * the
     * tape)
     */
    TapeConfiguration saveConfiguration(){
        Map<Integer, Map<Integer, String>> cells = new HashMap<>();
        for(Map.Entry<Integer, Map<Integer, String>> entry: this.cells.entrySet())
            cells.put(entry.getKey(), new HashMap<>(entry.getValue()));

        List<Integer> headsColumn = new ArrayList<>(this.headsColumn);
        List<Integer> headsLine = new ArrayList<>(this.headsLine);

        return new TapeConfiguration(cells, headsColumn, headsLine);
    }

    /**
     * Load the given configuration (set the position of the heads and the word currently written (not necessarily the
     * input word) on the tape.
     * @param configuration
     */
    void loadConfiguration(TapeConfiguration configuration){
        this.loadConfiguration(configuration, false);
    }

    /**
     * Load the given configuration (set the position of the heads and the word currently written (not necessarily the
     * input word) on the tape.
     * If log is true, {@link TuringMachine#SUBSCRIBER_MSG_HEAD_MOVED} and
     * {@link TuringMachine#SUBSCRIBER_MSG_INPUT_CHANGED} messages may be broadcast to the class
     * {@link util.Subscriber} to tell that heads were moved and that the word currently written on the tape is changed.
     * @param configuration
     */
    void loadConfiguration(TapeConfiguration configuration, boolean log){
        headsColumn.clear();
        headsLine.clear();

        headsColumn.addAll(configuration.headsColumn);
        headsLine.addAll(configuration.headsLine);

        if(log){
            for(int head = 0; head < nbHeads; head++){
                Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED, this.machine, this, head,
                        headsLine.get(head), headsColumn.get(head));
            }
        }

        if(log) {
            for (Map.Entry<Integer, Map<Integer, String>> entry : cells.entrySet()) {
                for (Map.Entry<Integer, String> entry2 : entry.getValue().entrySet()) {
                    Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_SYMBOL_WRITTEN, this.machine, this,
                            entry2.getKey(), entry.getKey(), null);
                }
                entry.getValue().clear();
            }
        }

        cells.clear();

        for(Map.Entry<Integer, Map<Integer, String>> entry: configuration.cells.entrySet()) {
            cells.put(entry.getKey(), new HashMap<>(entry.getValue()));
            if(log)
                for (Map.Entry<Integer, String> entry2 : entry.getValue().entrySet()) {
                    Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_SYMBOL_WRITTEN, this.machine, this,
                            entry2.getKey(), entry.getKey(), entry2.getValue());
                }
        }

    }

    /**
     * @return a 2D representation of the tape as a String.
     */
    public String print() {
        if(cells.isEmpty() && nbHeads == 0)
            return "--";

        StringBuilder s = new StringBuilder();

        Integer minColumn = Integer.MAX_VALUE;
        Integer maxColumn = Integer.MIN_VALUE;
        Integer minLine = Integer.MAX_VALUE;
        Integer maxLine = Integer.MIN_VALUE;

        for(Integer column: cells.keySet()){
            minColumn = Math.min(minColumn, column);
            maxColumn = Math.max(maxColumn, column);
            for(Integer line : cells.get(column).keySet()){
                minLine = Math.min(minLine, line);
                maxLine = Math.max(maxLine, line);
            }
        }

        for(int i = 0; i < nbHeads; i++){
            Integer column = headsColumn.get(i);
            Integer line = headsLine.get(i);
            minColumn = Math.min(minColumn, column);
            maxColumn = Math.max(maxColumn, column);
            minLine = Math.min(minLine, line);
            maxLine = Math.max(maxLine, line);
        }

        int headDigits = (int)Math.log10(nbHeads) + 1;
        String headFormat = "H%"+headDigits+"d";

        for(int line = maxLine; line >= minLine; line--){
            for(int column = minColumn; column <= maxColumn; column++){
                boolean head = false;
                for(int i = 0; i < nbHeads; i++){
                    if(column == headsColumn.get(i) && line == headsLine.get(i)) {
                        s.append(String.format(headFormat, i));
                        head = true;
                        break;
                    }
                }
                if(!head)
                    for(int i = 0; i < headDigits + 1; i++)
                        s.append(" ");

                String symbol = this.getSymbolAt(line, column, false);
                s.append(" ");
                s.append((symbol == null?"":symbol));
                s.append(" | ");
            }
            s.append("\n");
        }

        return s.toString();
    }
}

/**
 * Represent a configuration of a tape, consisting in a snapshot of the state of the tape:
 * <ul>
 *     <li>Where are the heads of the tape.</li>
 *     <li>What is written on the tape.</li>
 * </ul>
 */
class TapeConfiguration {

    Map<Integer, Map<Integer, String>> cells;
    List<Integer> headsColumn;
    List<Integer> headsLine;

    TapeConfiguration(Map<Integer, Map<Integer, String>> cells, List<Integer> headsColumn, List<Integer> headsLine) {
        this.cells = cells;
        this.headsColumn = headsColumn;
        this.headsLine = headsLine;
    }

    public String toString() {
        int nbHeads = headsColumn.size();

        if(cells.isEmpty() && nbHeads == 0)
            return "--";

        StringBuilder s = new StringBuilder();

        Integer minColumn = Integer.MAX_VALUE;
        Integer maxColumn = Integer.MIN_VALUE;
        Integer minLine = Integer.MAX_VALUE;
        Integer maxLine = Integer.MIN_VALUE;

        for(Integer column : cells.keySet()){
            minColumn = Math.min(minColumn, column);
            maxColumn = Math.max(maxColumn, column);
            for(Integer line : cells.get(column).keySet()){
                minLine = Math.min(minLine, line);
                maxLine = Math.max(maxLine, line);
            }
        }

        for(int i = 0; i < nbHeads; i++){
            Integer column = headsColumn.get(i);
            Integer line = headsLine.get(i);
            minColumn = Math.min(minColumn, column);
            maxColumn = Math.max(maxColumn, column);
            minLine = Math.min(minLine, line);
            maxLine = Math.max(maxLine, line);
        }

        int headDigits = (int)Math.log10(nbHeads) + 1;
        String headFormat = "H%"+headDigits+"d";

        for(int line = maxLine; line >= minLine; line--){
            for(int column = minColumn; column <= maxColumn; column++){
                boolean head = false;
                for(int i = 0; i < nbHeads; i++){
                    if(column == headsColumn.get(i) && line == headsLine.get(i)) {
                        s.append(String.format(headFormat, i));
                        head = true;
                        break;
                    }
                }
                if(!head)
                    for(int i = 0; i < headDigits + 1; i++)
                        s.append(" ");

                String symbol;
                try {
                    symbol = this.cells.get(column).get(line);
                }
                catch (NullPointerException e){
                    symbol = null;
                }
                s.append(" ");
                s.append((symbol == null?"":symbol));
                s.append(" | ");
            }
            s.append("\n");
        }

        return s.toString();
    }

}
