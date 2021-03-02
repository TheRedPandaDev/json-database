package server;

public class SetCommand implements Command {
    private final Database database;
    private final int cellNum;
    private final String text;
    private String result;

    public SetCommand(Database database, int cellNum, String text) {
        this.database = database;
        this.cellNum = cellNum;
        this.text = text;
    }

    @Override
    public void execute() {
        try {
            database.setCell(cellNum, text);
            this.result = "OK";
        } catch (ArrayIndexOutOfBoundsException e) {
            this.result = "ERROR";
        }

    }

    @Override
    public String getResult() {
        return result;
    }
}
