import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    private List<String> nonTerminals = new ArrayList<>();
    private List<String> terminals = new ArrayList<>();
    private String startSymbol = "";
    private final List<Map.Entry<String, List<List<String>>>> productions = new ArrayList<>();

    public Grammar() throws IOException {
        readGrammar();
    }

    private void readGrammar() throws IOException {
        FileInputStream fstream = new FileInputStream("src/grammars/g1.in");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine;

        String contentType = "";

        while ((strLine = br.readLine()) != null) {
            if (strLine.startsWith("V =")) {
                var nonTerminalRead = strLine.replace("V = ", "");
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
                var lhs = prodRule[0].trim();
                var rhs = prodRule[1].split("\\|");
                List<List<String>> allLists = new ArrayList<>();
                Arrays.stream(rhs).forEach(els -> {
                    List<String> list = Arrays.stream(els.trim().split("\\s+")).collect(Collectors.toList());
                    allLists.add(list);
                });
                productions.add(Map.entry(lhs, allLists));
            }
            if (strLine.startsWith("P = ")) {
                contentType = "P";
            }
        }
    }

    public void printSetOfNonTerminals() {
        var builder = new StringBuilder();
        builder.append("Non-Terminals: ");
        nonTerminals.forEach(nt -> builder.append(nt).append(", "));
        System.out.println(builder);
    }

    public void printSetOfTerminals() {
        var builder = new StringBuilder();
        builder.append("Terminals: ");
        terminals.forEach(t -> builder.append(t).append(", "));
        System.out.println(builder);
    }

    public String productionStringBuilder(Map.Entry<String, List<List<String>>> entry) {
        var builder = new StringBuilder();
        var lhs = entry.getKey();
        var rhs = entry.getValue();
        var rhsBuilder = new StringBuilder();
        for (int i = 0; i < rhs.size(); i++) {
            var list = rhs.get(i);
            list.forEach(rhsBuilder::append);
            if (i != rhs.size() - 1) {
                rhsBuilder.append(" | ");
            }
        }
        builder.append(lhs).append(" -> ").append(rhsBuilder).append("\n");
        return builder.toString();
    }

    public void printSetOfProductions() {
        var builder = new StringBuilder();
        builder.append("Productions:\n");
        productions.forEach(entry -> {
            builder.append(productionStringBuilder(entry));
        });
        System.out.println(builder);
    }

    public List<Map.Entry<String, List<List<String>>>> getProductionsForNonTerminal(String nonTerminal) {
        var builder = new StringBuilder();
        builder.append("List of productions for " + nonTerminal + ":\n");
        var productionsForNT = new ArrayList<Map.Entry<String, List<List<String>>>>();
        productions.forEach(entry -> {
            if (nonTerminal.equals(entry.getKey())) {
                productionsForNT.add(entry);
                builder.append(productionStringBuilder(entry));
            }
        });
        System.out.println(builder);
        return productionsForNT;
    }

    public boolean cfgCheck() {
        boolean foundStartSymbol = false;

        for (int i = 0; i < productions.size(); i++) {
            var lhs = productions.get(i).getKey();
            var rhs = productions.get(i).getValue();
            if (lhs.equals(startSymbol)) {
                foundStartSymbol = true;
            }
            if (!nonTerminals.contains(lhs)) {
                System.out.println("Lhs is not a non terminal!");
                return false;
            }
            if (!checkRHSOfProduction(rhs)) {
                System.out.println("Rhs failed!");
                return false;
            }
        }

        return foundStartSymbol;
    }

    public boolean checkRHSOfProduction(List<List<String>> rhs) {
        for (List<String> seq : rhs) {
            for (String character : seq) {
                if (!character.equals("ℇ")) {
                    if (!nonTerminals.contains(character) && !terminals.contains(character)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
