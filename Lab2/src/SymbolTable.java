import java.util.AbstractMap;

public class SymbolTable {
    private final HashTable identifierTable;
    private final HashTable constantTable;
    private final int m;  // size of table

    public SymbolTable(int m) {
        this.identifierTable = new HashTable(m);
        this.constantTable = new HashTable(m);
        this.m = m;
    }

    public int addIdentifier(String identifier) {
        return this.identifierTable.put(identifier);
    }

    public int addConstant(String constant) {
        return this.constantTable.put(constant);
    }

    public AbstractMap.SimpleImmutableEntry<Integer, Integer> getPositionOfIdentifier(String identifier) throws Exception {
        return this.identifierTable.getPositionInTable(identifier);
    }

    public AbstractMap.SimpleImmutableEntry<Integer, Integer> getPositionOfConstant(String constant) throws Exception {
        return this.constantTable.getPositionInTable(constant);
    }

    @Override
    public String toString() {
        return "SymbolTable [\n" +
                "Identifier Table:\n" + identifierTable +
                "\nConstantTable:\n" + constantTable + "\n]";
    }
}
