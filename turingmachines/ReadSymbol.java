package turingmachines;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadSymbol {
    private List<String> symbols;
    private Tape tape;
    private int head;

    public ReadSymbol(Tape tape, int head, List<String> symbols) {
        this.tape = tape;
        this.head = head;
        this.symbols = new ArrayList<>();
        this.symbols.addAll(symbols);
    }

    public ReadSymbol(Tape tape, int head, String... symbols) {
        this.tape = tape;
        this.head = head;
        this.symbols = new ArrayList<>();
        for(String symbol: symbols)
            this.symbols.add(symbol);
    }

    public Iterator<String> getSymbols(){
        return symbols.iterator();
    }

    public int getNbSymbols(){
        return symbols.size();
    }

    public void addSymbol(String symbol){
        symbols.add(symbol);
    }

    public String getSymbol(int i) {
        return symbols.get(i);
    }

    public void setSymbol(int i, String symbol) {
        symbols.set(i, symbol);
    }

    public void removeSymbol(int i){
        symbols.remove(i);
    }

    public Tape getTape() {
        return tape;
    }

    public void setTape(Tape tape) {
        this.tape = tape;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public boolean isCurrentlyRead(){
        return symbols.contains(tape.read(head));
    }
}
