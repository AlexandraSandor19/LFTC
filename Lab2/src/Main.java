import java.util.AbstractMap;

public class Main {
    public static void main(String[] args) throws Exception {
        int size = 5;
        var symbolTable = new SymbolTable(size);

        symbolTable.addToken("abc");
        symbolTable.addToken("string");

        try {
            AbstractMap.SimpleEntry<Integer, Integer> pos1 = symbolTable.getPosition("abc");
            AbstractMap.SimpleEntry<Integer, Integer> pos2 = symbolTable.getPosition("string");
            System.out.println("Identifier abc: position " + pos1.getKey() + " in the array; position " + pos1.getValue() + " in the corresponding linked list.");
            System.out.println("Constant string: position " + pos2.getKey() + " in the array; position " + pos2.getValue() + " in the corresponding linked list.");
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}
