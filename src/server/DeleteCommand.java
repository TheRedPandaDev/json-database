package server;

public class DeleteCommand implements Command {
    private final Database database;
    private final int cellNum;
    private String result;

    public DeleteCommand(Database database, int cellNum) {
        this.database = database;
        this.cellNum = cellNum;
    }

    @Override
    public void execute() {
        try {
            database.deleteCell(cellNum);
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
