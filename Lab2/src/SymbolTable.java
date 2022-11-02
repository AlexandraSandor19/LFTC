import java.util.AbstractMap;

public class SymbolTable {
    private final HashTable table;
    private final int m;  // size of table

    public SymbolTable(int m) {
        this.table = new HashTable(m);
        this.m = m;
    }

    public int getSize() {
        return m;
    }

    public AbstractMap.SimpleEntry<Integer, Integer> addToken(String identifier) throws Exception {
        return this.table.put(identifier);
    }

    public AbstractMap.SimpleEntry<Integer, Integer> getPosition(String token) throws Exception {
        return this.table.getPositionInTable(token);
    }

    @Override
    public String toString() {
        return "SymbolTable: [\n" + table + "\n]";
    }
}
