package server;

public class WorkingToggle {
    private boolean working;

    public WorkingToggle() {
        this.working = true;
    }

    public void stopWorking() {
        this.working = false;
    }

    public boolean isWorking() {
        return working;
    }
}
