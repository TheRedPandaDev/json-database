package server.commands;

import server.WorkingToggle;

public class ExitCommand implements Command {
    WorkingToggle workingToggle;
    Result result;

    public ExitCommand(WorkingToggle workingToggle) {
        this.workingToggle = workingToggle;
    }

    @Override
    public void execute() {
        workingToggle.stopWorking();
        result = new Result("OK");
    }

    @Override
    public Result getResult() {
        return result;
    }
}
