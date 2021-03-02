package client;

import com.beust.jcommander.JCommander;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34522;

    public static void main(String[] args) {
        System.out.println("Client started!");
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                ) {
            CommandArgs commandArgs = new CommandArgs();
            JCommander jCommander = JCommander.newBuilder()
                    .addObject(commandArgs)
                    .build();
            jCommander.parse(args);

            String msg;

            if ("set".equals(commandArgs.getRequestType())) {
                msg = "set " + commandArgs.getCellIndex() + " " + commandArgs.getInputValue();
            } else if ("exit".equals(commandArgs.getRequestType())) {
                msg = "exit";
            } else {
                msg = commandArgs.getRequestType() + " " + commandArgs.getCellIndex();
            }

            outputStream.writeUTF(msg);
            System.out.println("Sent: " + msg);

            String receivedMsg = inputStream.readUTF();
            System.out.println("Received: " + receivedMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
