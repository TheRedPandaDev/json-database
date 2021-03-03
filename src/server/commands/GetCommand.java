package server.commands;

import server.Database;

public class GetCommand implements Command {
    private final Database database;
    private final String key;
    private Result result;

    public GetCommand(Database database, String key) {
        this.database = database;
        this.key = key;
    }

    @Override
    public void execute() {
        try {
            result = new Result("OK", database.getCell(key));
        } catch (IllegalArgumentException | NullPointerException e) {
            result = new Result("ERROR", null, e.getMessage());
        }
    }

    @Override
    public Result getResult() {
        return result;
    }
}
