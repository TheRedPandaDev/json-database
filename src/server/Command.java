package server;

public interface Command {
    void execute();
    String getResult();
}
