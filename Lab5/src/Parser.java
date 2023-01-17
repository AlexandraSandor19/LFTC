import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

public class Parser {
  private Grammar grammar;
  private Map<String, List<String>> followSet = new HashMap<>();
  private Map<String, List<String>> firstSet = new HashMap<>();
  private static Stack<List<String>> rules = new Stack<>();
  private Map<Pair<String, List<String>>, Integer> productionsNumbered = new HashMap<>();
  private ParseTable parseTable = new ParseTable();

  public Parser(Grammar grammar) {
    this.grammar = grammar;
    for (String nonTerminal : grammar.getNonTerminals()) {
      firstSet.put(nonTerminal, this.firstOf(nonTerminal));
    }
    for (String nonTerminal : grammar.getNonTerminals()) {
      followSet.put(nonTerminal, this.followOf(nonTerminal, nonTerminal));
    }
    createParseTable();
  }

  public ParseTable getParseTable(){
    return this.parseTable;
  }

  private List<String> firstOf(String nonTerminal){
    if (firstSet.containsKey(nonTerminal)) {
      return firstSet.get(nonTerminal);
    }

    List<String> temp = new ArrayList<>();
    List<String> terminals = grammar.getTerminals();
    for (Entry<String, List<List<String>>> production : grammar.getProductionsForNonTerminal(nonTerminal))
      for (List<String> rule : production.getValue()) {
        String firstSymbol = rule.get(0);
        //If x-> Є, is a production rule, then add Є to FIRST(x).
        if (firstSymbol.equals("ε"))
          temp.add("ε");
        else if (terminals.contains(firstSymbol))
          temp.add(firstSymbol);
        else
          // x is a production -> add all
          temp.addAll(firstOf(firstSymbol));
      }
    return temp;
  }

  private void generateFollowSet() {
    for (String nonTerminal : grammar.getNonTerminals()) {
      followSet.put(nonTerminal, this.followOf(nonTerminal, nonTerminal));
    }
  }

  private List<String> followOf(String nonTerminal, String initialNonTerminal) {
    if (followSet.containsKey(nonTerminal))
      return followSet.get(nonTerminal);

    List<String> temp = new ArrayList<>();
    List<String> terminals = grammar.getTerminals();

    // rule 1
    if (nonTerminal.equals(grammar.getStartSymbol()))
      temp.add("$");

    for (Entry<String, List<List<String>>> production : grammar.getProductionsForNonTerminal(nonTerminal)) {
      String productionStart = production.getKey();
      for (List<String> rule : production.getValue()){
        List<String> ruleConflict = new ArrayList<>();
        ruleConflict.add(nonTerminal);
        ruleConflict.addAll(rule);
        if (rule.contains(nonTerminal) && !rules.contains(ruleConflict)) {
          rules.push(ruleConflict);
          int indexNonTerminal = rule.indexOf(nonTerminal);
          // For any production rule A → αB, Follow(B) = Follow(A)
          temp.addAll(followOperation(nonTerminal, temp, terminals, productionStart, rule, indexNonTerminal, initialNonTerminal));
           // For cases like: N -> E 36 E, when E is the nonTerminal so we have 2 possibilities: 36 goes in follow(E) and also follow(N)
          List<String> sublist = rule.subList(indexNonTerminal + 1, rule.size());
          //For any production rule A → αBβ
          //If ∈ ∉ First(β), then Follow(B) = First(β)
          //If ∈ ∈ First(β), then Follow(B) = { First(β) – ∈ } ∪ Follow(A)
          if (sublist.contains(nonTerminal))
            temp.addAll(followOperation(nonTerminal, temp, terminals, productionStart, rule, indexNonTerminal + 1 + sublist.indexOf(nonTerminal), initialNonTerminal));

          rules.pop();
        }
      }
    }
    return temp;
  }

  private List<String> followOperation(String nonTerminal, List<String> temp, List<String> terminals, String productionStart, List<String> rule, int indexNonTerminal, String initialNonTerminal) {
    if (indexNonTerminal == rule.size() - 1) {
      if (productionStart.equals(nonTerminal))
        return temp;
      if (!initialNonTerminal.equals(productionStart)){
        temp.addAll(followOf(productionStart, initialNonTerminal));
      }
    }
    else
    {
      String nextSymbol = rule.get(indexNonTerminal + 1);
      if (terminals.contains(nextSymbol))
        temp.add(nextSymbol);
      else{
        if (!initialNonTerminal.equals(nextSymbol)) {
          Set<String> fists = new HashSet<>(firstSet.get(nextSymbol));
          if (fists.contains("ε")) {
            temp.addAll(followOf(nextSymbol, initialNonTerminal));
            fists.remove("ε");
          }
          temp.addAll(fists);
        }
      }
    }
    return temp;
  }

  private void numberingProductions() {
    int index = 1;
    for (Map.Entry<String, List<List<String>>> production: grammar.getProductions())
      for (List<String> rule: production.getValue())
        productionsNumbered.put(new Pair<>(production.getKey(), rule), index++);
  }


  private void createParseTable() {
    numberingProductions();

    List<String> columnSymbols = new LinkedList<>(grammar.getTerminals());
    columnSymbols.add("$");

    // M(a, a) = pop
    // M($, $) = acc

    parseTable.put(new Pair<>("$", "$"), new Pair<>(Collections.singletonList("acc"), -1));
    for (String terminal: grammar.getTerminals())
      parseTable.put(new Pair<>(terminal, terminal), new Pair<>(Collections.singletonList("pop"), -1));



//        1) M(A, a) = (α, i), if:
//            a) a ∈ first(α)
//            b) a != ε
//            c) A -> α production with index i
//
//        2) M(A, b) = (α, i), if:
//            a) ε ∈ first(α)
//            b) whichever b ∈ follow(A)
//            c) A -> α production with index i
    productionsNumbered.forEach((key, value) -> {
      String rowSymbol = key.getKey();
      List<String> rule = key.getValue();
      Pair<List<String>, Integer> parseTableValue = new Pair<>(rule, value);

      for (String columnSymbol : columnSymbols) {
        Pair<String, String> parseTableKey = new Pair<>(rowSymbol, columnSymbol);

        // if our column-terminal is exactly first of rule
        if (rule.get(0).equals(columnSymbol) && !columnSymbol.equals("ε"))
          parseTable.put(parseTableKey, parseTableValue);

          // if the first symbol is a non-terminal and it's first contain our column-terminal
        else if (grammar.getNonTerminals().contains(rule.get(0)) && firstSet.get(rule.get(0)).contains(columnSymbol)) {
          if (!parseTable.containsKey(parseTableKey)) {
            parseTable.put(parseTableKey, parseTableValue);
          }
        }
        else {
          // if the first symbol is ε then everything if FOLLOW(rowSymbol) will be in parse table
          if (rule.get(0).equals("ε")) {
            for (String b : followSet.get(rowSymbol))
              parseTable.put(new Pair<>(rowSymbol, b), parseTableValue);

            // if ε is in FIRST(rule)
          } else {
            Set<String> firsts = new HashSet<>();
            for (String symbol : rule)
              if (grammar.getNonTerminals().contains(symbol))
                firsts.addAll(firstSet.get(symbol));
            if (firsts.contains("ε")) {
              for (String b : firstSet.get(rowSymbol)) {
                if (b.equals("ε"))
                  b = "$";
                parseTableKey = new Pair<>(rowSymbol, b);
                if (!parseTable.containsKey(parseTableKey)) {
                  parseTable.put(parseTableKey, parseTableValue);
                }
              }
            }
          }
        }
      }
    });
  }

}
