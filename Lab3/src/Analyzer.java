import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Analyzer {
    private final SymbolTable identifierTable;
    private final SymbolTable constantTable;
    private final PIF PIF;
    private final TokenClassifier tokenClassifier;
    private final List<String> errors;

    public Analyzer(int size) throws IOException {
        this.identifierTable = new SymbolTable(size);
        this.constantTable = new SymbolTable(size);
        this.tokenClassifier = new TokenClassifier();
        this.PIF = new PIF();
        this.errors = new ArrayList<>();
    }

    public PIF getPIF() {
        return PIF;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void lineToTokens(String line) {
        String delimiters = ":|,| |;|\\(|\\)|\\{|\\}";
        var listOfTokens = Arrays.stream(line.split("(?=" + delimiters + ")|(?<=" + delimiters + ")")).collect(Collectors.toList());
        listOfTokens.forEach(token -> {
            if (tokenClassifier.isSeparator(token)) {
                PIF.put(new AbstractMap.SimpleEntry<>(token, new AbstractMap.SimpleEntry<>(-1, -1)));
            }
            else
            if (tokenClassifier.isOperator(token)) {
                PIF.put(new AbstractMap.SimpleEntry<>(token, new AbstractMap.SimpleEntry<>(-1, -1)));
            }
            else
            if (tokenClassifier.isKeyword(token)) {
                PIF.put(new AbstractMap.SimpleEntry<>(token, new AbstractMap.SimpleEntry<>(-1, -1)));
            }
            else
            if (tokenClassifier.isIdentifier(token)) {
                var position = identifierTable.addToken(token);
                PIF.put(new AbstractMap.SimpleEntry<>(token, position));
            }
            else
            if (tokenClassifier.isConstant(token)) {
                var position = constantTable.addToken(token);
                PIF.put(new AbstractMap.SimpleEntry<>(token, position));
            }
            else {
                var message = "Lexical Error at line: " + line + "\n   > unvalid token: " + token;
                errors.add(message);
            }
        });
    }

    public void writeInSymbolTable() {
        try {
            // first, we delete all the existing content of the file
            PrintWriter writer = new PrintWriter("src/output/symtab.out");
            writer.print("");
            writer.close();

            // writing the symbol table file
            BufferedWriter out = new BufferedWriter(
                    new FileWriter("src/output/symtab.out", true));
            out.write("-----------------------------SYMBOL TABLE-----------------------------\n");
            out.write("> IDENTIFIERS\n");
            out.write(identifierTable.toString());
            out.write("\n> CONSTANTS\n");
            out.write(constantTable.toString());
            out.close();
        }
        catch (IOException e) {
            System.out.println("Error occurred while writing into the Symbol Table file!");
        }
    }

    public void scanProgram(String fileName) throws IOException {
        FileInputStream fstream = new FileInputStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine;

        while ((strLine = br.readLine()) != null)   {
            this.lineToTokens(strLine);
        }
        fstream.close();
    }

}
