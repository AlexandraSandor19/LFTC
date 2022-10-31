import java.util.AbstractMap;

public class Main {
    public static void main(String[] args) {
        int size = 5;
        var symbolTable = new SymbolTable(size);

        symbolTable.addIdentifier("abc");
        symbolTable.addIdentifier("variable");
        symbolTable.addIdentifier("hello1");
        symbolTable.addConstant("21");
        symbolTable.addConstant("string");

        try {
            AbstractMap.SimpleImmutableEntry<Integer, Integer> pos1 = symbolTable.getPositionOfIdentifier("abc");
            AbstractMap.SimpleImmutableEntry<Integer, Integer> pos2 = symbolTable.getPositionOfConstant("string");
            System.out.println("Identifier abc: position " + pos1.getKey() + " in the array; position " + pos1.getValue() + " in the corresponding linked list.");
            System.out.println("Constant string: position " + pos2.getKey() + " in the array; position " + pos2.getValue() + " in the corresponding linked list.");
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        System.out.println(symbolTable);
    }
}
