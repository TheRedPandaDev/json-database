package server;

import java.util.Arrays;

public class Database {
    private final String[] databaseArray;

    public Database() {
        this.databaseArray = new String[1000];
        Arrays.fill(databaseArray, "");
    }

    public void setCell(int cellNum, String text) throws ArrayIndexOutOfBoundsException {
        databaseArray[cellNum] = text;
    }

    public String getCell(int cellNum) throws ArrayIndexOutOfBoundsException {
        if ("".equals(databaseArray[cellNum])) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return databaseArray[cellNum];
    }

    public void deleteCell(int cellNum) throws ArrayIndexOutOfBoundsException {
        databaseArray[cellNum] = "";
    }
}
