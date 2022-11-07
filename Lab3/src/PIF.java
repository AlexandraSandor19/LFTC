import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class PIF {

    private final List<AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<Integer, Integer>>> PIF;

    public PIF() {
        PIF = new ArrayList<>();
    }

    public void put(AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<Integer, Integer>> entry) {
        PIF.add(entry);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        PIF.forEach(entry -> {
            string.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
        });
        return string.toString();
    }

    public void writeInPIF() {
        try {
            // first, we delete all the existing content of the file
            PrintWriter writer = new PrintWriter("src/output/PIF.out");
            writer.print("");
            writer.close();

            // writing in pif file
            BufferedWriter out = new BufferedWriter(
                    new FileWriter("src/output/PIF.out", true));
            out.write("-----------------------------PIF-----------------------------\n");
            out.write(this.toString());
            out.close();
        }
        catch (IOException e) {
            System.out.println("Error occurred while writing into the PIF file!");
        }
    }
}
