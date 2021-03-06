package server;

import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {
    private volatile JsonObject database;
    private final Path DB_PATH;
    private final ReadWriteLock readWriteLock;
    private final Gson gson;

    public Database(String dbPath) {
        this.DB_PATH = Path.of(dbPath);
        try {
            createDbFileIfNotExists(DB_PATH);
            this.database = readDbFromFile(DB_PATH).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        readWriteLock = new ReentrantReadWriteLock();
        gson = new Gson();
    }

    public void setCell(String key, String value) throws IOException {
        readWriteLock.writeLock().lock();
        try {
            JsonElement keyJ = JsonParser.parseString(key);
            if (keyJ.isJsonPrimitive()) {
                setValue(database, keyJ.getAsString(), value);
                writeDbToFile(database, DB_PATH);
            } else if (keyJ.isJsonArray()) {
                setDeep(database, (JsonArray) keyJ, value);
                writeDbToFile(database, DB_PATH);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public String getCell(String key) throws IllegalArgumentException {
        JsonElement jeKey = gson.fromJson(key, JsonElement.class);

        readWriteLock.readLock().lock();
        String value;
        try {
            KeyObject keyObject = findKeyObject(database, jeKey);
            value = getEntity(keyObject);
        } finally {
            readWriteLock.readLock().unlock();
        }

        if (value == null) {
            throw new IllegalArgumentException("No such key");
        }
        return value;
    }

    public void deleteCell(String key) throws IllegalArgumentException {
        JsonElement jeKey = gson.fromJson(key, JsonElement.class);

        readWriteLock.writeLock().lock();
        KeyObject keyObject = findKeyObject(database, jeKey);

        JsonObject newJsonObject = deleteEntity(database, keyObject);
        if (newJsonObject == null) {
            readWriteLock.writeLock().unlock();
            throw new IllegalArgumentException("No such key");
        }

        try {
            writeDbToFile(newJsonObject, DB_PATH);
            this.database = readDbFromFile(DB_PATH).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void setDeep(JsonObject rootJson, JsonArray keyJ, String value) {
        JsonArray keys = keyJ.getAsJsonArray();

        JsonObject currentJObject = rootJson;
        JsonObject newJObject;
        String k = "";
        boolean branchIsCreated = false;
        for (int i = 0; i < keys.size(); i++) {
            k = keys.get(i).getAsString();
            if (!branchIsCreated && currentJObject.keySet().contains(k)) {
                JsonElement newJObj = currentJObject.get(k);
                if (newJObj.isJsonObject()) {
                    newJObject = newJObj.getAsJsonObject();

                    if (i < keys.size() - 1) {
                        currentJObject = newJObject;
                    }
                }
            } else {
                if (!branchIsCreated) {
                    branchIsCreated = true;
                }

                newJObject = new JsonObject();
                currentJObject.add(k, newJObject);

                if (i < keys.size() - 1) {
                    currentJObject = newJObject;
                }
            }
        }
        setValue(currentJObject, k, value);
    }

    private void setValue(JsonObject currentJObject, String key, String value) {
        JsonElement valueJ = JsonParser.parseString(value);

        if (valueJ.isJsonObject()) {
            currentJObject.add(key, valueJ.getAsJsonObject());
        } else if (valueJ.isJsonPrimitive()) {
            currentJObject.addProperty(key, valueJ.getAsString());
        }
    }

    private String getEntity(KeyObject keyObject) {
        JsonElement jsonEl = keyObject.getCurrentJObject().get(keyObject.k);
        if (jsonEl == null) {
            return null;
        }
        if (jsonEl.isJsonObject()) return gson.toJson(jsonEl);
        else if (jsonEl.isJsonPrimitive()) {
            return jsonEl.getAsString();
        }
        return null;
    }

    private JsonObject deleteEntity(final JsonObject rootJObject, final KeyObject keyObject) {

        if (keyObject.currentJObject.keySet().contains(keyObject.k)) {
            keyObject.currentJObject.remove(keyObject.k);
            return rootJObject;
        }
        return null;
    }

    private KeyObject findKeyObject(final JsonObject currentJObject, final JsonElement jeKey) {
        JsonObject newJObject = null;
        KeyObject keyObject = new KeyObject(currentJObject, "");

        if (jeKey.isJsonArray()) {
            JsonArray keys = jeKey.getAsJsonArray();
            for (int i = 0; i < keys.size(); i++) {
                keyObject.k = keys.get(i).getAsString();
                if (keyObject.currentJObject.keySet().contains(keyObject.k)) {
                    JsonElement jElem = keyObject.currentJObject.get(keyObject.k);
                    if (jElem.isJsonObject()) {
                        newJObject = jElem.getAsJsonObject();
                    } else if (jElem.isJsonPrimitive()) {
                        if (i < keys.size() - 1) {
                            break;
                        }
                    }
                    if (i < keys.size() - 1) {
                        keyObject.currentJObject = newJObject;
                    }
                } else {
                    break;
                }
            }
        } else if (jeKey.isJsonPrimitive()) {
            keyObject.k = jeKey.getAsString();
        }
        return keyObject;
    }

    public static JsonElement readDbFromFile(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return JsonParser.parseReader(reader);
        }
    }

    public static void writeDbToFile(JsonObject db, Path path) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            new Gson().toJson(db, writer);
        }
    }

    public static void createDbFileIfNotExists(Path path) throws IOException {
        if (Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        if (Files.notExists(path)) {
            Files.writeString(path, "{}");
        }
    }

    private static class KeyObject {
        JsonObject currentJObject;
        String k;

        public KeyObject(JsonObject currentJObject, String k) {
            this.currentJObject = currentJObject;
            this.k = k;
        }

        public JsonObject getCurrentJObject() {
            return currentJObject;
        }
    }
}
