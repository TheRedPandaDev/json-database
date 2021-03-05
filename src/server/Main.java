package server;

import com.google.gson.Gson;
import server.commands.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final int PORT = 34522;
    private static final String pathToFile = "./src/server/data/";
    private static final Database database = new Database(pathToFile);
    private static final Controller controller = new Controller();
    private static final WorkingToggle workingToggle = new WorkingToggle();

    public static void main(String[] args) {
        ExecutorService executorService = Executors
                .newFixedThreadPool(Runtime.getRuntime()
                        .availableProcessors());
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            while (workingToggle.isWorking()) {
                Session session = new Session(serverSocket.accept(), serverSocket);
                executorService.submit(session);
            }
        } catch (IOException ignored) {
        } finally {
            executorService.shutdown();
        }
    }

    static class Session implements Runnable {
        private final Socket socket;
        private final ServerSocket serverSocket;

        public Session(Socket socketForClient, ServerSocket serverSocket) {
            super();
            this.socket = socketForClient;
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try (
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
            ) {
                String receivedMsg = inputStream.readUTF();
                System.out.println("Received: " + receivedMsg);
                Gson gson = new Gson();
                CommandArgs commandArgs = gson.fromJson(receivedMsg, CommandArgs.class);
                Command command = parseInput(commandArgs);
                Result result;
                synchronized (controller) {
                    controller.setCommand(command);
                    controller.executeCommand();
                    result = controller.getCommandResult();
                }
                String msgOut = gson.toJson(result);
                outputStream.writeUTF(msgOut);
                System.out.println("Sent: " + msgOut);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Command parseInput(CommandArgs commandArgs) {
            if ("exit".equals(commandArgs.getType())) {
                return new ExitCommand(workingToggle, serverSocket);
            } else if (("set".equals(commandArgs.getType()))) {
                return new SetCommand(database, commandArgs.getKey(), commandArgs.getValue());
            } else if (("get".equals(commandArgs.getType()))) {
                return new GetCommand(database, commandArgs.getKey());
            } else if (("delete".equals(commandArgs.getType()))) {
                return new DeleteCommand(database, commandArgs.getKey());
            }
            return null;
        }
    }
}
