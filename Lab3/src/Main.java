
public class Main {
    public static void main(String[] args) throws Exception {
        var scanner = new Analyzer(10);
        try {
            scanner.scanProgram("src/input/pr1.in");
            if (scanner.getErrors().isEmpty()) {
                System.out.println("Lexically correct!");
                scanner.writeInSymbolTable();
                scanner.writeInPIF();
            }
            else {
                scanner.getErrors().forEach(System.err::println);
            }
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}
