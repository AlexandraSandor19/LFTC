import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            var grammar = new Grammar();
            Parser parser = new Parser(grammar);
            grammar.printSetOfTerminals();
            //System.out.println(grammar.cfgCheck());
            System.out.println(parser.getParseTable());

        }
        catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
