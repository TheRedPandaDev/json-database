package server;

import java.util.Map;
import java.util.TreeMap;

public class Database {
    private final Map<String, String> databaseMap;

    public Database() {
        databaseMap = new TreeMap<>();
    }

    public void setCell(String key, String value) throws IllegalArgumentException, NullPointerException {
        databaseMap.put(key, value);
    }

    public String getCell(String key) throws IllegalArgumentException, NullPointerException {
        String value = databaseMap.get(key);
        if (value == null) {
            throw new IllegalArgumentException("No such key");
        }
        return value;
    }

    public void deleteCell(String key) throws IllegalArgumentException, NullPointerException {
        String value = databaseMap.remove(key);
        if (value == null) {
            throw new IllegalArgumentException("No such key");
        }
    }
}
