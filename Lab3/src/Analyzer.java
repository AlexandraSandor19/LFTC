import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Analyzer {
    private final SymbolTable identifierTable;
    private final SymbolTable constantTable;
    private final TokenClassifier tokenClassifier;
    private final List<AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<Integer, Integer>>> PIF;
    private final List<String> errors;

    public Analyzer(int size) throws IOException {
        this.identifierTable = new SymbolTable(size);
        this.constantTable = new SymbolTable(size);
        this.tokenClassifier = new TokenClassifier();
        this.PIF = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public List<String> getErrors() {
        return errors;
    }

    public void lineToTokens(String line) {
        String delimiters = ":|,| |;|\\(|\\)|\\{|\\}";
        var listOfTokens = Arrays.stream(line.split("(?=" + delimiters + ")|(?<=" + delimiters + ")")).collect(Collectors.toList());
        listOfTokens.forEach(token -> {
            if (tokenClassifier.isSeparator(token)) {
                PIF.add(new AbstractMap.SimpleEntry<>(token, new AbstractMap.SimpleEntry<>(-1, -1)));
            }
            else
            if (tokenClassifier.isOperator(token)) {
                PIF.add(new AbstractMap.SimpleEntry<>(token, new AbstractMap.SimpleEntry<>(-1, -1)));
            }
            else
            if (tokenClassifier.isKeyword(token)) {
                PIF.add(new AbstractMap.SimpleEntry<>(token, new AbstractMap.SimpleEntry<>(-1, -1)));
            }
            else
            if (tokenClassifier.isIdentifier(token)) {
                var position = identifierTable.addToken(token);
                PIF.add(new AbstractMap.SimpleEntry<>(token, position));
            }
            else
            if (tokenClassifier.isConstant(token)) {
                var position = constantTable.addToken(token);
                PIF.add(new AbstractMap.SimpleEntry<>(token, position));
            }
            else {
                var message = "Lexical Error at line: " + line + "\n   > unvalid token: " + token;
                errors.add(message);
            }
        });
    }

    public void writeInSymbolTable() {
        try {
            BufferedWriter out = new BufferedWriter(
                    new FileWriter("src/output/symtab.out", true));
            // writing the symbol table
            out.write("-----------------------------SYMBOL TABLE-----------------------------\n");
            out.write("> IDENTIFIERS\n");
            out.write(identifierTable.toString());
            out.write("\n> CONSTANTS\n");
            out.write(constantTable.toString());
            out.close();
        }
        catch (IOException e) {
            System.out.println("Error occurred while ");
        }
    }

    public String stringifyPIF() {
        StringBuilder string = new StringBuilder();
        PIF.forEach(entry -> {
            string.append(entry.getKey() + " -> " + entry.getValue() + "\n");
        });
        return string.toString();
    }

    public void writeInPIF() {
        try {
            BufferedWriter out = new BufferedWriter(
                    new FileWriter("src/output/PIF.out", true));
            // writing the symbol table
            out.write("-----------------------------PIF-----------------------------\n");
            out.write(stringifyPIF());
            out.close();
        }
        catch (IOException e) {
            System.out.println("Error occurred while ");
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
