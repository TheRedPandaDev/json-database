package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class Main {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34522;
    private static final String pathToFile = "src/client/data/";

    public static void main(String[] args) {
        CommandArgs commandArgs = new CommandArgs();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(commandArgs)
                .build();
        jCommander.parse(args);

        if (commandArgs.isHelp()) {
            jCommander.usage();
            return;
        }

        System.out.println("Client started!");

        String msgOut = null;

        if (commandArgs.getFileName() != null) {
            StringBuilder msgOutBuilder = new StringBuilder();
            File requestFile = new File(pathToFile.concat(commandArgs.getFileName()));
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(requestFile))) {
                String input = bufferedReader.readLine();
                while (input != null) {
                    msgOutBuilder.append(input);
                    input = bufferedReader.readLine();
                }
                msgOut = msgOutBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Gson gson = new Gson();
            msgOut = gson.toJson(commandArgs);
        }

        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
                ) {
            outputStream.writeUTF(msgOut);
            System.out.println("Sent: " + msgOut);

            String receivedMsg = inputStream.readUTF();
            System.out.println("Received: " + receivedMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
