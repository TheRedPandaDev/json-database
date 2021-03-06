package server.commands;

import server.Database;

import java.io.IOException;

public class SetCommand implements Command {
    private final Database database;
    private final String key;
    private final String value;
    private Result result;

    public SetCommand(Database database, String key, String value) {
        this.database = database;
        this.key = key;
        this.value = value;
    }

    @Override
    public void execute() {
        try {
            database.setCell(key, value);
            result = new Result("OK");
        } catch (IllegalArgumentException | NullPointerException | IOException e) {
            result = new Result("ERROR", null, e.getMessage());
        }

    }

    @Override
    public Result getResult() {
        return result;
    }
}
