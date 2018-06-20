package turingmachines;

import util.Subscriber;

import java.util.*;

public class Tape{

    // Tape limits
    // Classic tape : 0, 0, -Inf, Inf
    // k-height tape : 0, k - 1, -Inf, Inf
    // Semi infinite tape : 0, 0, 0, Inf
    // 2D tape : -Inf, Inf, -Inf, Inf
    // Inf = -Inf = null
    private Integer tapeBottomBound;
    private Integer tapeTopBound;
    private Integer tapeLeftBound;
    private Integer tapeRightBound;

    private Map<Integer, Map<Integer, String>> cells;
    private Map<Integer, Map<Integer, String>> inputCells;

    private Integer nbHeads;

    // Coordinates at the beginning of the computation
    private List<Integer> initialHeadsColumn;
    private List<Integer> initialHeadsLine;

    // Current coordinates
    private List<Integer> headsColumn;
    private List<Integer> headsLine;

    private TuringMachine machine;

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

    private int initColumn(){
        if(tapeLeftBound == null && tapeRightBound == null)
            return 0;
        if(tapeLeftBound == null)
            return tapeRightBound;
        if(tapeRightBound == null)
            return tapeLeftBound;
        return (tapeLeftBound + tapeRightBound) / 2;
    }

    private int initLine(){
        if(tapeTopBound == null && tapeBottomBound == null)
            return 0;
        if(tapeTopBound == null)
            return tapeBottomBound;
        if(tapeBottomBound == null)
            return tapeTopBound;
        return (tapeTopBound + tapeBottomBound) / 2;
    }

    public Integer getNbHeads() {
        return nbHeads;
    }

    public void addHead(){
        int column = initColumn();
        int line = initLine();
        addHead(line, column);
    }

    public void addHead(int line, int column){
        nbHeads++;
        initialHeadsColumn.add(column);
        initialHeadsLine.add(line);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_ADD_HEAD, this.machine, this, nbHeads - 1, line, column);
    }

    public void removeHead(int head){
        machine.removeHeadFromTransitions(this, head);
        nbHeads--;
        initialHeadsColumn.remove(head);
        initialHeadsLine.remove(head);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_REMOVE_HEAD, this.machine, this, head);
    }

    public void setLeftBound(Integer left){
        if(left != null && tapeRightBound != null && tapeRightBound < left)
            tapeLeftBound = tapeRightBound;
        else
            tapeLeftBound = left;

        checkHeadsColumns();
        checkInput(true, false);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_TAPE_LEFT_CHANGED, this.machine, this, tapeLeftBound);
    }

    public void setRightBound(Integer right){
        if(right != null && tapeLeftBound != null && tapeLeftBound > right)
            tapeRightBound = tapeLeftBound;
        else
            tapeRightBound = right;

        checkHeadsColumns();
        checkInput(true, false);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_TAPE_RIGHT_CHANGED, this.machine, this, tapeRightBound);
    }

    public void setBottomBound(Integer bottom){
        if(bottom != null && tapeTopBound != null && tapeTopBound < bottom)
            tapeBottomBound = tapeTopBound;
        else
            tapeBottomBound = bottom;

        checkHeadsLines();
        checkInput(false, true);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_TAPE_BOTTOM_CHANGED, this.machine, this, tapeBottomBound);
    }

    public void setTopBound(Integer top){
        if(top != null && tapeBottomBound != null && tapeBottomBound > top)
            tapeTopBound = tapeBottomBound;
        else
            tapeTopBound = top;

        checkHeadsLines();
        checkInput(false, true);
        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_TAPE_TOP_CHANGED, this.machine, this, tapeTopBound);
    }

    Integer getInitialHeadColumn(int head){
        return initialHeadsColumn.get(head);
    }

    Integer getInitialHeadLine(int head){
        return initialHeadsLine.get(head);
    }

    public void setInitialHeadColumn(int head, int column) {
        if ((tapeLeftBound == null || column >= tapeLeftBound)
                && (tapeRightBound == null || column <= tapeRightBound)) {
            initialHeadsColumn.set(head, column);
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED, this.machine, this, head, initialHeadsLine.get(head), column);
        }
    }

    private void checkHeadsColumns(){
        for(int i = 0; i < nbHeads; i++) {
            int column = initialHeadsColumn.get(i);
            if (tapeLeftBound != null && tapeLeftBound > column)
                setInitialHeadColumn(i, tapeLeftBound);
            if (tapeRightBound != null && tapeRightBound < column)
                setInitialHeadColumn(i, tapeRightBound);
        }
    }

    public void setInitialHeadLine(int head, int line) {
        if ((tapeBottomBound == null || line >= tapeBottomBound)
                && (tapeTopBound == null || line <= tapeTopBound)) {
            initialHeadsLine.set(head, line);
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED, this.machine, this, head, line, initialHeadsColumn.get(head));

        }
    }

    private void checkHeadsLines(){
        for(int i = 0; i < nbHeads; i++) {
            int line = initialHeadsLine.get(i);
            if (tapeBottomBound != null && tapeBottomBound > line)
                setInitialHeadLine(i, tapeBottomBound);
            if (tapeTopBound != null && tapeTopBound < line)
                setInitialHeadLine(i, tapeTopBound);
        }
    }

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

    String read(int head){

        Integer column = headsColumn.get(head);
        Integer line = headsLine.get(head);

        return this.getSymbolAt(line, column, false);
    }

    String getSymbolAt(Integer line, Integer column, boolean input){

        Map<Integer, Map<Integer, String>> cells = (input?this.inputCells:this.cells);

        Map<Integer, String> columnCells = cells.get(column);
        if(columnCells == null) // All the column is white
            return null;
        return columnCells.get(line);

    }

    public void writeInput(int line, int column, String symbol){
        write(line, column, symbol, true);
    }

    void write(int head, String symbol, boolean log){

        Integer column = headsColumn.get(head);
        Integer line = headsLine.get(head);

        this.write(line, column, symbol, false);
        if(log)
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE, this.machine, this, head, symbol);

    }

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

        if(input)
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_INPUT_CHANGED, this.machine, this, line, column, symbol);
    }

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

    TapeConfiguration saveConfiguration(){
        Map<Integer, Map<Integer, String>> cells = new HashMap<>();
        for(Map.Entry<Integer, Map<Integer, String>> entry: this.cells.entrySet())
            cells.put(entry.getKey(), new HashMap<>(entry.getValue()));

        List<Integer> headsColumn = new ArrayList<>(this.headsColumn);
        List<Integer> headsLine = new ArrayList<>(this.headsLine);

        return new TapeConfiguration(cells, headsColumn, headsLine);
    }

    void loadConfiguration(TapeConfiguration configuration){
        this.headsColumn.clear();
        this.headsLine.clear();
        headsColumn.addAll(configuration.headsColumn);
        headsLine.addAll(configuration.headsLine);

//        Useless with GC?
//        for(Map.Entry<Integer, Map<Integer, String>> entry: cells.entrySet())
//            entry.getValue().clear();

        cells.clear();

        for(Map.Entry<Integer, Map<Integer, String>> entry: configuration.cells.entrySet())
            cells.put(entry.getKey(), new HashMap<>(entry.getValue()));

    }

    public String print() {
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

class TapeConfiguration {

    Map<Integer, Map<Integer, String>> cells;
    List<Integer> headsColumn;
    List<Integer> headsLine;

    TapeConfiguration(Map<Integer, Map<Integer, String>> cells, List<Integer> headsColumn, List<Integer> headsLine) {
        this.cells = cells;
        this.headsColumn = headsColumn;
        this.headsLine = headsLine;
    }
}
