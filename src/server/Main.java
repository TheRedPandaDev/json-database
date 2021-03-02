package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static final int PORT = 34522;
    private static final Database database = new Database();
    private static final Controller controller = new Controller();
    private static final WorkingToggle workingToggle = new WorkingToggle();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            while (workingToggle.isWorking()) {
                Session session = new Session(serverSocket.accept());
                session.start();
                session.join(); // Temporary measure for exiting
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Session extends Thread {
        private final Socket socket;

        public Session(Socket socketForClient) {
            super();
            this.socket = socketForClient;
        }

        @Override
        public void run() {
            try (
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            ) {
                String msg = inputStream.readUTF();
                System.out.println("Received: " + msg);
                String[] input;
                if ("set".equals(msg.substring(0, 3))) {
                    input = new String[3];
                    input[0] = "set";
                    String secondPart = msg.substring(msg.indexOf(" ") + 1);
                    input[1] = secondPart.substring(0, secondPart.indexOf(" "));
                    input[2] = secondPart.substring(secondPart.indexOf(" ") + 1);
                } else {
                    input = msg.split(" ");
                }
                Command command = parseInput(input);
                synchronized (controller) {
                    controller.setCommand(command);
                    controller.executeCommand();
                }
                String msgOut = command.getResult();
                outputStream.writeUTF(msgOut);
                System.out.println("Sent: " + msgOut);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Command parseInput(String[] input) {
            if ("exit".equals(input[0])) {
                return new ExitCommand(workingToggle);
            } else if (("set".equals(input[0])) && (isInteger(input[1]))) {
                return new SetCommand(database, Integer.parseInt(input[1]) - 1, input[2]);
            } else if (("get".equals(input[0])) && (isInteger(input[1]))) {
                return new GetCommand(database, Integer.parseInt(input[1]) - 1);
            } else if (("delete".equals(input[0])) && (isInteger(input[1]))) {
                return new DeleteCommand(database, Integer.parseInt(input[1]) - 1);
            }
            return null;
        }

        private static boolean isInteger(String str) {
            if (str == null) {
                return false;
            }
            int length = str.length();
            if (length == 0) {
                return false;
            }
            int i = 0;
            if (str.charAt(0) == '-') {
                if (length == 1) {
                    return false;
                }
                i = 1;
            }
            for (; i < length; i++) {
                char c = str.charAt(i);
                if (c < '0' || c > '9') {
                    return false;
                }
            }
            return true;
        }
    }
}

class WorkingToggle {
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


