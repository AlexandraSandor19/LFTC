import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class TokenClassifier {

    private final List<String> keywords;
    private final List<String> operators;
    private final List<String> separators;
    private final String TOKEN_FILE = "src/input/tokens.in";

    public TokenClassifier() throws IOException {
        keywords = new ArrayList<>();
        operators = new ArrayList<>();
        separators = new ArrayList<>();
        readAndStoreTokens(TOKEN_FILE);
    }

    private void readAndStoreTokens(String fileName) throws IOException {
        FileInputStream fstream = new FileInputStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine;
        String storingIn = "";

        while ((strLine = br.readLine()) != null)   {
            switch (strLine) {
                case "Operators:" -> storingIn = "operators";
                case "Separators:" -> storingIn = "separators";
                case "Keywords:" -> storingIn = "keywords";
                default -> {
                    switch (storingIn) {
                        case "operators" -> operators.add(strLine);
                        case "separators" -> separators.add(strLine);
                        case "keywords" -> keywords.add(strLine);
                    }
                }
            }
        }
        separators.add(" "); //also include space
        fstream.close();
    }

    public boolean isConstant(String constant) {
        String integerConstRE = "(-*[1-9]+[0-9]*)|0";
        String charConstRE = "\'[(a-zA-Z)|(0-9)]\'";
        String stringConstRE = "\"[a-zA-Z0-9]+\"";
        return constant.matches(integerConstRE) || constant.matches(charConstRE) || constant.matches(stringConstRE);
    }

    public boolean isIdentifier(String identifier) {
        String identifierRE = "[a-zA-Z]+[0-9]*";
        return identifier.matches(identifierRE);
    }

    public boolean isKeyword(String keyword) {
        return keywords.contains(keyword);
    }

    public boolean isOperator(String operator) {
        return operators.contains(operator);
    }

    public boolean isSeparator(String separator) {
        return separators.contains(separator);
    }
}
