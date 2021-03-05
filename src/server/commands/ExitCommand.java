package server.commands;

import server.WorkingToggle;

import java.io.IOException;
import java.net.ServerSocket;

public class ExitCommand implements Command {
    WorkingToggle workingToggle;
    ServerSocket serverSocket;
    Result result;

    public ExitCommand(WorkingToggle workingToggle, ServerSocket serverSocket) {
        this.workingToggle = workingToggle;
        this.serverSocket = serverSocket;
    }

    @Override
    public void execute() {
        workingToggle.stopWorking();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        result = new Result("OK");
    }

    @Override
    public Result getResult() {
        return result;
    }
}
