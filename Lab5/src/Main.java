import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            var grammar = new Grammar();
            grammar.printSetOfTerminals();
            System.out.println(grammar.cfgCheck());
        }
        catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
