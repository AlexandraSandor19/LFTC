import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Grammar {
    private List<String> nonTerminals = new ArrayList<>();
    private List<String> terminals = new ArrayList<>();
    private String startSymbol = "";
    private final HashMap<String, String> productions = new HashMap<>();

    public Grammar() throws IOException {
        readGrammar();
    }

    private void readGrammar() throws IOException {
        FileInputStream fstream = new FileInputStream("src/grammars/g1.in");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine;

        String contentType = "";

        while ((strLine = br.readLine()) != null) {
            if (strLine.startsWith("N =")) {
                var nonTerminalRead = strLine.replace("N = ", "");
                nonTerminalRead = nonTerminalRead.replaceAll("[{}]", "");
                nonTerminals = Arrays.stream(nonTerminalRead.split(",")).toList();
            }
            if (strLine.startsWith("Sigma =")) {
                var terminalRead = strLine.replace("Sigma = ", "");
                terminalRead = terminalRead.replaceAll("[{}]", "");
                terminals = Arrays.stream(terminalRead.split(",")).toList();
            }
            if (strLine.startsWith("S =")) {
                startSymbol = strLine.replace("S = ", "");
            }
            if (contentType.equals("P")) {
                if (strLine.equals("}")) {
                    break;
                }
                var prodRule = strLine.split("->");
                productions.put(prodRule[0], prodRule[1]);
            }
            if (strLine.startsWith("P = ")) {
                contentType = "P";
            }
        }
    }
}
