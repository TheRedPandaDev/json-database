package server;

public class ExitCommand implements Command {
    WorkingToggle workingToggle;

    public ExitCommand(WorkingToggle workingToggle) {
        this.workingToggle = workingToggle;
    }

    @Override
    public void execute() {
        workingToggle.stopWorking();
    }

    @Override
    public String getResult() {
        return "OK";
    }
}
