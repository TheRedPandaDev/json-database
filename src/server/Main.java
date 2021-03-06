package server;

import com.google.gson.*;
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
    private static final String pathToFile = "./src/server/data/db.json";
    private static final Database database = new Database(pathToFile);
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
        private final Controller controller;
        private final Gson gson;

        public Session(Socket socketForClient, ServerSocket serverSocket) {
            super();
            this.socket = socketForClient;
            this.serverSocket = serverSocket;
            controller = new Controller();
            gson = new Gson();
        }

        @Override
        public void run() {
            try (
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
            ) {
                String receivedMsg = inputStream.readUTF();
                System.out.println("Received: " + receivedMsg);
                JsonObject request = gson.fromJson(receivedMsg, JsonObject.class);
                Command command = parseInput(request);
                String msgOut;
                if (command != null) {
                    controller.setCommand(command);
                    controller.executeCommand();
                    Result result = controller.getCommandResult();
                    msgOut = createAnswerJson(result);
                } else {
                    msgOut = new Result("ERROR", null, "Wrong type").toString();
                }
                outputStream.writeUTF(msgOut);
                System.out.println("Sent: " + msgOut);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        private Command parseInput(JsonObject request) {
            String typeSrt;
            JsonElement tmpJsonElement;
            CmdTypeSet type;
            String key;
            String value;

            typeSrt = request.get(KeySet.type.name()).getAsString();

            type = typeSrt == null ? CmdTypeSet.error :
                    (isValidCmd(typeSrt) ? CmdTypeSet.valueOf(typeSrt) : CmdTypeSet.error);

            tmpJsonElement = request.get(KeySet.key.name());
            if (tmpJsonElement == null) {
                key = "";
            } else {
                if (tmpJsonElement.isJsonArray()) {
                    key = tmpJsonElement.getAsJsonArray().toString();
                } else if (tmpJsonElement.isJsonPrimitive()) {
                    key = tmpJsonElement.getAsString();
                } else {
                    key = "";
                    type = CmdTypeSet.error;
                }
            }

            tmpJsonElement = request.get(KeySet.value.name());
            if (tmpJsonElement == null) {
                value = "";
            } else {
                if (tmpJsonElement.isJsonObject()) {
                    value = tmpJsonElement.getAsJsonObject().toString();
                } else if (tmpJsonElement.isJsonPrimitive()) {
                    value = tmpJsonElement.getAsJsonPrimitive().toString();
                } else {
                    value = "";
                    type = CmdTypeSet.error;
                }
            }

            switch (type) {
                case exit:
                    return new ExitCommand(workingToggle, serverSocket);
                case set:
                    return new SetCommand(database, key, value);
                case get:
                    return new GetCommand(database, key);
                case delete:
                    return new DeleteCommand(database, key);
                default:
                    return null;
            }
        }

        private boolean isValidCmd(final String cmd) {
            for (CmdTypeSet cmdType : CmdTypeSet.values()) {
                if (cmdType.name().equals(cmd)) {
                    return true;
                }
            }
            return false;
        }

        private String createAnswerJson(Result result) {
            JsonObject answer = new JsonObject();

            answer.addProperty("response", result.getResponse());

            if ("ERROR".equals(result.getResponse())) {
                answer.addProperty("reason", result.getReason());
            } else if (result.getValue() != null && "OK".equals(result.getResponse())) {
                boolean isJson = false;
                JsonElement jEl = new JsonObject();

                try {
                    jEl = JsonParser.parseString(result.getValue());
                    isJson = true;
                } catch (JsonSyntaxException ignored) {
                }

                if (isJson) {
                    answer.add("value", jEl);
                } else {
                    answer.addProperty("value", result.getValue());
                }
            }
            return gson.toJson(answer);
        }
    }
}
