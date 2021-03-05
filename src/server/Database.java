package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {
    private volatile Map<String, String> databaseMap;
    private final File dbFile;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Gson gson;

    public Database(String pathToFile) {
        gson = new GsonBuilder().create();
        dbFile = new File(pathToFile);
        readDBFile();
    }

    public void setCell(String key, String value) {
        readWriteLock.writeLock().lock();
        try {
            databaseMap.put(key, value);
            writeDBFile();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public String getCell(String key) throws IllegalArgumentException {
        readWriteLock.readLock().lock();
        String value;
        try {
            value = databaseMap.get(key);
        } finally {
            readWriteLock.readLock().unlock();
        }

        if (value == null) {
            throw new IllegalArgumentException("No such key");
        }
        return value;
    }

    public void deleteCell(String key) throws IllegalArgumentException {
        readWriteLock.writeLock().lock();
        String value;
        try {
            value = databaseMap.remove(key);
            writeDBFile();
        } finally {
            readWriteLock.writeLock().unlock();
        }
        if (value == null) {
            throw new IllegalArgumentException("No such key");
        }
    }

    private void readDBFile() {
        StringBuilder dbJSONstrBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(dbFile))) {
            String inputLine = bufferedReader.readLine();
            while (inputLine != null) {
                dbJSONstrBuilder.append(inputLine);
                inputLine = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String dbJSONstr = dbJSONstrBuilder.toString();

        if (dbJSONstr.length() == 0) {
            databaseMap = new TreeMap<>();
        } else {
            Type typeOfTreeMap = new TypeToken<TreeMap<String, String>>() { }.getType();

            databaseMap = gson.fromJson(dbJSONstr, typeOfTreeMap);
        }
    }

    private void writeDBFile() {
        String dbJSONstr = gson.toJson(databaseMap);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dbFile))) {
            bufferedWriter.write(dbJSONstr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
