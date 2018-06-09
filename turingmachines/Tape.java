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
    private List<Integer> initialHeadsX;
    private List<Integer> initialHeadsY;

    // Current coordinates
    private List<Integer> headsX;
    private List<Integer> headsY;

    private TuringMachine machine;

    Tape(TuringMachine machine){
        this.machine = machine;

        tapeTopBound = 0;
        tapeBottomBound = 0;
        tapeLeftBound = null;
        tapeRightBound = null;

        nbHeads = 0;
        initialHeadsX = new ArrayList<>();
        initialHeadsY = new ArrayList<>();
        headsX = new ArrayList<>();
        headsY = new ArrayList<>();
        addHead();

        cells = new HashMap<>();
        inputCells = new HashMap<>();
    }

    private int initX(){
        if(tapeLeftBound == null && tapeRightBound == null)
            return 0;
        if(tapeLeftBound == null)
            return tapeRightBound;
        if(tapeRightBound == null)
            return tapeLeftBound;
        return (tapeLeftBound + tapeRightBound) / 2;
    }

    private int initY(){
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
        nbHeads++;
        initialHeadsX.add(initX());
        initialHeadsY.add(initY());
    }

    public void removeHead(int head){
        nbHeads--;
        initialHeadsX.remove(head);
        initialHeadsY.remove(head);
    }

    public void setLeftBound(Integer left){
        if(left != null && tapeRightBound != null && tapeRightBound < left)
            tapeLeftBound = tapeRightBound;
        else
            tapeLeftBound = left;

        for(int i = 0; i < nbHeads; i++) {
            int x = initialHeadsX.get(i);
            if (tapeLeftBound != null && tapeLeftBound > x)
                initialHeadsX.set(i, tapeLeftBound);
        }

        Iterator<Map.Entry<Integer, Map<Integer, String>>> it = inputCells.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Integer, Map<Integer, String>> entry = it.next();
            Integer x = entry.getKey();
            if(left != null && x < left)
                it.remove();
        }
    }

    public void setRightBound(Integer right){
        if(right != null && tapeRightBound != null && tapeLeftBound > right)
            tapeRightBound = tapeLeftBound;
        else
            tapeRightBound = right;

        for(int i = 0; i < nbHeads; i++) {
            int x = initialHeadsX.get(i);
            if (tapeRightBound != null && tapeRightBound < x)
                initialHeadsX.set(i, tapeRightBound);
        }

        Iterator<Map.Entry<Integer, Map<Integer, String>>> it = inputCells.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Integer, Map<Integer, String>> entry = it.next();
            Integer x = entry.getKey();
            if(right != null && x > right)
                it.remove();
        }
    }

    public void setBottomBound(Integer bottom){
        if(bottom != null && tapeTopBound != null && tapeTopBound < bottom)
            tapeBottomBound = tapeTopBound;
        else
            tapeBottomBound = bottom;

        for(int i = 0; i < nbHeads; i++) {
            int y = initialHeadsY.get(i);
            if (tapeBottomBound != null && tapeBottomBound > y)
                initialHeadsY.set(i, tapeBottomBound);
        }

        Iterator<Map.Entry<Integer, Map<Integer, String>>> it1 = inputCells.entrySet().iterator();
        while(it1.hasNext()){
            Map.Entry<Integer, Map<Integer, String>> entry1 = it1.next();
            Iterator<Map.Entry<Integer, String>> it2 = entry1.getValue().entrySet().iterator();
            while (it2.hasNext()) {
                Map.Entry<Integer, String> entry2 = it2.next();
                Integer y = entry2.getKey();
                if (bottom != null && y < bottom)
                    it2.remove();
            }
            if(entry1.getValue().isEmpty())
                it1.remove();
        }
    }

    public void setTopBound(Integer top){
        if(top != null && tapeTopBound != null && tapeBottomBound > top)
            tapeTopBound = tapeBottomBound;
        else
            tapeTopBound = top;

        for(int i = 0; i < nbHeads; i++) {
            int x = initialHeadsY.get(i);
            if (tapeTopBound != null && tapeTopBound < x)
                initialHeadsY.set(i, tapeTopBound);
        }

        Iterator<Map.Entry<Integer, Map<Integer, String>>> it1 = inputCells.entrySet().iterator();
        while(it1.hasNext()){
            Map.Entry<Integer, Map<Integer, String>> entry1 = it1.next();
            Iterator<Map.Entry<Integer, String>> it2 = entry1.getValue().entrySet().iterator();
            while (it2.hasNext()) {
                Map.Entry<Integer, String> entry2 = it2.next();
                Integer y = entry2.getKey();
                if (top != null && y > top)
                    it2.remove();
            }
            if(entry1.getValue().isEmpty())
                it1.remove();
        }
    }

    public Integer getInitialHeadX(int head){
        return initialHeadsX.get(head);
    }

    public Integer getInitialHeadY(int head){
        return initialHeadsY.get(head);
    }

    public void setInitialHeadX(int head, int x){
        if((tapeLeftBound == null || x >= tapeLeftBound)
                && (tapeRightBound == null || x <= tapeRightBound) )
            initialHeadsX.set(head, x);
    }

    public void setInitialHeadY(int head, int y){
        if((tapeBottomBound == null || y >= tapeBottomBound)
                && (tapeTopBound == null || y <= tapeTopBound) )
            initialHeadsY.set(head, y);
    }

    void reinit(){
        headsX.clear();
        headsX.addAll(initialHeadsX);
        headsY.clear();
        headsY.addAll(initialHeadsY);

        cells.clear();
        for(Map.Entry<Integer, Map<Integer, String>> pair: inputCells.entrySet()){
            Integer x = pair.getKey();
            Map<Integer, String> column = pair.getValue();
            Map<Integer, String> column2 = new HashMap<>(column);
            cells.put(x, column2);
        }
    }

    public void moveHead(int head, Direction direction, boolean log){
        Integer x = headsX.get(head);
        Integer y = headsY.get(head);

        switch (direction){
            case BOTTOM:
                if(!y.equals(tapeBottomBound)) {
                    headsY.set(head, y - 1);
                    if(log)
                        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED, this.machine, this, head, x, y - 1);
                }
                break;
            case TOP:
                if(!y.equals(tapeTopBound)) {
                    headsY.set(head, y + 1);
                    if(log)
                        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED, this.machine, this, head, x, y + 1);
                }
                break;
            case LEFT:
                if(!x.equals(tapeLeftBound)) {
                    headsX.set(head, x - 1);
                    if(log)
                        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED, this.machine, this, head, x - 1, y);
                }
                break;
            case RIGHT:
                if(!x.equals(tapeRightBound)){
                    headsX.set(head, x + 1);
                    if(log)
                        Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_MOVED, this.machine, this, head, x + 1, y);
                }
                break;
        }
    }

    public String read(int head){

        Integer x = headsX.get(head);
        Integer y = headsY.get(head);

        return this.getSymbolAt(x, y, false);
    }

    public String getSymbolAt(Integer x, Integer y, boolean input){

        Map<Integer, Map<Integer, String>> cells = (input?this.inputCells:this.cells);

        Map<Integer, String> column = cells.get(x);
        if(column == null) // All the column is white
            return null;
        return column.get(y);

    }

    public void writeInput(int x, int y, String symbol){
        write(x, y, symbol, true);
    }

    void write(int head, String symbol, boolean log){

        Integer x = headsX.get(head);
        Integer y = headsY.get(head);

        this.write(x, y, symbol, false);
        if(log)
            Subscriber.broadcast(TuringMachine.SUBSCRIBER_MSG_HEAD_WRITE, this.machine, this, head, symbol);

    }

    void write(Integer x, Integer y, String symbol, boolean input){

        Map<Integer, Map<Integer, String>> cells = (input?this.inputCells:this.cells);

        Map<Integer, String> column = cells.get(x);
        if(symbol == null) { // White symbol
            if (column == null) // All the column is white
                return;
            String cell = column.get(y);
            if (cell != null) {
                column.remove(y);
                if (column.size() == 0)
                    cells.remove(x);
            }
        }
        else{
            if (column == null) {
                column = new HashMap<>();
                cells.put(x, column);
            }
            column.put(y, symbol);
        }
    }

    TapeConfiguration saveConfiguration(){
        Map<Integer, Map<Integer, String>> cells = new HashMap<>();
        for(Map.Entry<Integer, Map<Integer, String>> entry: this.cells.entrySet())
            cells.put(entry.getKey(), new HashMap<>(entry.getValue()));

        List<Integer> headsX = new ArrayList<>(this.headsX);
        List<Integer> headsY = new ArrayList<>(this.headsY);

        return new TapeConfiguration(cells, headsX, headsY);
    }

    void loadConfiguration(TapeConfiguration configuration){
        this.headsX.clear();
        this.headsY.clear();
        headsX.addAll(configuration.headsX);
        headsY.addAll(configuration.headsY);

//        Useless with GC?
//        for(Map.Entry<Integer, Map<Integer, String>> entry: cells.entrySet())
//            entry.getValue().clear();

        cells.clear();

        for(Map.Entry<Integer, Map<Integer, String>> entry: configuration.cells.entrySet())
            cells.put(entry.getKey(), new HashMap<>(entry.getValue()));

    }

    @Override
    public String toString() {
        if(cells.isEmpty() && nbHeads == 0)
            return "--";

        StringBuilder s = new StringBuilder();

        Integer minX = Integer.MAX_VALUE;
        Integer maxX = Integer.MIN_VALUE;
        Integer minY = Integer.MAX_VALUE;
        Integer maxY = Integer.MIN_VALUE;

        for(Integer x : cells.keySet()){
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            for(Integer y : cells.get(x).keySet()){
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }
        }

        for(int i = 0; i < nbHeads; i++){
            Integer x = headsX.get(i);
            Integer y = headsY.get(i);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        int headDigits = (int)Math.log10(nbHeads) + 1;
        String headFormat = "H%"+headDigits+"d";

        for(int y = maxY; y >= minY; y--){
            for(int x = minX; x <= maxX; x++){
                boolean head = false;
                for(int i = 0; i < nbHeads; i++){
                    if(x == headsX.get(i) && y == headsY.get(i)) {
                        s.append(String.format(headFormat, i));
                        head = true;
                        break;
                    }
                }
                if(!head)
                    for(int i = 0; i < headDigits + 1; i++)
                        s.append(" ");

                String symbol = this.getSymbolAt(x, y, false);
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
    List<Integer> headsX;
    List<Integer> headsY;

    TapeConfiguration(Map<Integer, Map<Integer, String>> cells, List<Integer> headsX, List<Integer> headsY) {
        this.cells = cells;
        this.headsX = headsX;
        this.headsY = headsY;
    }
}
