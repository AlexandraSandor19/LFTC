import java.io.*;
import java.util.*;

public class FiniteAutomata {

    private List<String> states;
    private List<String> alphabet;
    private final Map<Map.Entry<String, String>, String> transitionFunctions;
    private String initialState;
    private List<String> finalStates;
    private boolean validInput;

    public FiniteAutomata() {
        this.states = new ArrayList<>();
        this.alphabet = new ArrayList<>();
        this.transitionFunctions = new HashMap<>();
        this.finalStates = new ArrayList<>();
        this.validInput = true;
        try {
            this.readFromFile();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private boolean isValidState(String state) {
        return states.contains(state);
    }

    private void readFromFile() throws IOException {
        FileInputStream fstream = new FileInputStream("input/FA.in");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine;

        while ((strLine = br.readLine()) != null)   {
            if (strLine.contains("all_states")) {
                var concatStates = strLine.replaceAll("all_states: ", "");
                states = Arrays.stream(concatStates.split(",")).toList();
            }
            if (strLine.contains("input_symbols")) {
                var concatSymbols = strLine.replaceAll("input_symbols: ", "");
                alphabet = Arrays.stream(concatSymbols.split(",")).toList();
            }
            if (strLine.contains("initial_state")) {
                var initState = strLine.replaceAll("initial_state: ", "");
                if (isValidState(initState)) {
                    initialState = initState;
                }
                else {
                    System.out.printf("The initial state %s isn't a valid state!", initState);
                    validInput = false;
                    break;
                }
            }
            if (strLine.contains("final_states")) {
                var concatFinalStates = strLine.replaceAll("final_states: ", "");
                var finStates = Arrays.stream(concatFinalStates.split(",")).toList();
                var listOfFinStates = finStates.stream().filter(this::isValidState).toList();
                if (listOfFinStates.equals(finStates)) {
                    finalStates = listOfFinStates;
                }
                else {
                    System.out.println("The list of final states is invalid!");
                    validInput = false;
                    break;
                }
            }
            if (strLine.contains("transition_function")) {
                var concatTransitionFun = strLine.replaceAll("transition_function: ", "");
                var entries = Arrays.stream(concatTransitionFun.split(";")).toList();
                for (var entry : entries) {
                    var elems = Arrays.stream(entry.split(",")).map(el -> el.replaceAll("[()]", "")).toList();
                    var pair = Map.entry(elems.get(0), elems.get(1));
                    var nextState = elems.get(2);
                    if (isValidState(pair.getKey()) && isValidState(nextState) && alphabet.contains(pair.getValue())) {
                        transitionFunctions.put(pair, nextState);
                    }
                    else {
                        System.out.printf("{Current state:%s, Input symbol:%s, Next state:%s} is not a valid transition function!", pair.getKey(), pair.getValue(), nextState);
                        validInput = false;
                        break;
                    }
                }
            }
        }
        fstream.close();
    }

    public void displayMenu() {
        System.out.println("Commands:");
        System.out.println("> Display:");
        System.out.println("  All states -> (1)\n  Alphabet -> (2)\n  Initial state -> (3)\n  Final state -> (4)\n  Transitions -> (5)");
        System.out.println("> Verify sequence -> (6)");
    }

    private String formatTransitions() {
        var transitions = new StringBuilder();
        transitionFunctions.forEach((key, value) -> transitions.append("(").append(key.getKey()).append(",").append(key.getValue()).append(",").append(value).append(") "));
        return transitions.toString();
    }

    private boolean sequenceChecker(String sequence) {
        String currentState = initialState;
        for (int idx = 0; idx < sequence.length(); idx++) {
            var symbol = sequence.charAt(idx);
            var found = false;
            for (var entry : transitionFunctions.entrySet()) {
                if (currentState.equals(entry.getKey().getKey()) && String.valueOf(symbol).equals(entry.getKey().getValue())) {
                    currentState = entry.getValue();
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return finalStates.contains(currentState);
    }

    private void checkSequence() {
        System.out.println("Enter sequence: ");
        var sc = new Scanner(System.in);
        var seq = sc.next();
        var result = sequenceChecker(seq);
        if (result) {
            System.out.printf("Sequence %s is valid!\n", seq);
        }
        else {
            System.out.printf("Sequence %s is invalid!\n", seq);
        }
    }

    public void start() {
        var stop = false;
        while (!stop && validInput) {
            displayMenu();
            System.out.println("Enter: ");
            var sc = new Scanner(System.in);
            var input = sc.next();
            switch (input){
                case "1" -> System.out.println(states);
                case "2" -> System.out.println(alphabet);
                case "3" -> System.out.println(initialState);
                case "4" -> System.out.println(finalStates);
                case "5" -> System.out.println(formatTransitions());
                case "6" -> checkSequence();
                case "end" -> stop = true;
                default -> System.out.println("Not a valid command!");
            }
        }
    }

}
