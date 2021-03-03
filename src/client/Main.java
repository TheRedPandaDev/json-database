package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34522;

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

        Gson gson = new Gson();

        String msgOut = gson.toJson(commandArgs);

        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
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
