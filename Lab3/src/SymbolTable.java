import java.util.AbstractMap;

public class SymbolTable {
    private final HashTable table;
    private final int m;  // size of table

    public SymbolTable(int m) {
        this.table = new HashTable(m);
        this.m = m;
    }

    public AbstractMap.SimpleEntry<Integer, Integer> addToken(String token) {
        return this.table.put(token);
    }

    public AbstractMap.SimpleEntry<Integer, Integer> getPosition(String token) throws Exception {
        return this.table.getPositionInTable(token);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        table.getTable().forEach(list -> {
            if(!list.isEmpty()) {
              list.forEach(key -> {
                  try {
                      string.append(key).append(" -> ").append(getPosition(key)).append("\n");
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              });
            }
        });
        return string.toString();
    }
}
