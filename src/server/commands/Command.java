package server.commands;

public interface Command {
    void execute();
    Result getResult();
}
