package server.commands;

import server.Database;

public class DeleteCommand implements Command {
    private final Database database;
    private final String key;
    private Result result;

    public DeleteCommand(Database database, String key) {
        this.database = database;
        this.key = key;
    }

    @Override
    public void execute() {
        try {
            database.deleteCell(key);
            result = new Result("OK");
        } catch (IllegalArgumentException | NullPointerException e) {
            result = new Result("ERROR", null, e.getMessage());
        }
    }

    @Override
    public Result getResult() {
        return result;
    }
}
