import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

public class Parser {
  private Grammar grammar;
  private Map<String, List<String>> followSet;
  private Map<String, List<String>> firstSet;
  private static Stack<List<String>> rules = new Stack<>();


  public Parser(Grammar grammar) {
    this.grammar = grammar;
    for (String nonTerminal : grammar.getNonTerminals()) {
      firstSet.put(nonTerminal, this.firstOf(nonTerminal));
    }
  }
  private List<String> firstOf(String nonTerminal){

    if (firstSet.containsKey(nonTerminal))
      return firstSet.get(nonTerminal);

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
          temp.addAll(followOperation(nonTerminal, temp, terminals, productionStart, rule, indexNonTerminal, initialNonTerminal));

//                    // For cases like: N -> E 36 E, when E is the nonTerminal so we have 2 possibilities: 36 goes in follow(E) and also follow(N)
          List<String> sublist = rule.subList(indexNonTerminal + 1, rule.size());
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

}
