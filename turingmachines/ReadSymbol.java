package turingmachines;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ReadSymbol {
    private List<String> symbols;
    private Tape tape;
    private int head;

    ReadSymbol(Tape tape, int head, List<String> symbols) {
        this.tape = tape;
        this.head = head;
        this.symbols = new ArrayList<>();
        this.symbols.addAll(symbols);
    }

    ReadSymbol(Tape tape, int head, String... symbols) {
        this.tape = tape;
        this.head = head;
        this.symbols = new ArrayList<>();
        for(String symbol: symbols)
            this.symbols.add(symbol);
    }

    Iterator<String> getSymbols(){
        return symbols.iterator();
    }

    int getNbSymbols(){
        return symbols.size();
    }

    void addSymbol(String symbol){
        symbols.add(symbol);
    }

    String getSymbol(int i) {
        return symbols.get(i);
    }

    void setSymbol(int i, String symbol) {
        symbols.set(i, symbol);
    }

    void removeSymbol(int i){
        symbols.remove(i);
    }

    Tape getTape() {
        return tape;
    }

    void setTape(Tape tape) {
        this.tape = tape;
    }

    int getHead() {
        return head;
    }

    void setHead(int head) {
        this.head = head;
    }

    boolean isCurrentlyRead(){
        return symbols.contains(tape.read(head));
    }
}
