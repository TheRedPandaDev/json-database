package server;

public class GetCommand implements Command {
    private final Database database;
    private int cellNum;
    private String result;

    public GetCommand(Database database, int cellNum) {
        this.database = database;
        this.cellNum = cellNum;
    }

    @Override
    public void execute() {
        try {
            this.result = database.getCell(cellNum);
        } catch (ArrayIndexOutOfBoundsException e) {
            this.result = "ERROR";
        }
    }

    @Override
    public String getResult() {
        return result;
    }
}
