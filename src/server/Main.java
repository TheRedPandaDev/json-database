package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private static final String[] database = new String[100];
    private static final int PORT = 34522;

    public static void main(String[] args) {
//        Arrays.fill(database, "");
//        Scanner scanner = new Scanner(System.in);
//        String[] input = new String[3];
//        input[0] = scanner.next();
//        while (!"exit".equals(input[0])) {
//            input[1] = scanner.next();
//            if ("set".equals(input[0])) {
//                input[2] = scanner.nextLine().trim();
//            }
//            parseInput(input);
//            input[0] = scanner.next();
//        }
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            Session session = new Session(serverSocket.accept());
            session.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseInput(String[] input) {
        if (("set".equals(input[0])) && (isInteger(input[1]))) {
            try {
                setCell(Integer.parseInt(input[1]) - 1, input[2]);
                System.out.println("OK");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("ERROR");
            }
        } else if (("get".equals(input[0])) && (isInteger(input[1]))) {
            try {
                System.out.println(getCell(Integer.parseInt(input[1]) - 1));
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("ERROR");
            }
        } else if (("delete".equals(input[0])) && (isInteger(input[1]))) {
            try {
                deleteCell(Integer.parseInt(input[1]) - 1);
                System.out.println("OK");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("ERROR");
            }
        } else {
            System.out.println("ERROR");
        }
    }

    public static boolean isInteger(String str) {
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

    private static void setCell(int cellNum, String text) throws ArrayIndexOutOfBoundsException {
        database[cellNum] = text;
    }

    private static String getCell(int cellNum) throws ArrayIndexOutOfBoundsException {
        if ("".equals(database[cellNum])) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return database[cellNum];
    }

    private static void deleteCell(int cellNum) throws ArrayIndexOutOfBoundsException {
        database[cellNum] = "";
    }

}

class Session extends Thread {
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
            int recordNum = Integer.parseInt(msg.split(" # ")[1]);
            String msgOut = "A record # " + recordNum + " was sent!";
            outputStream.writeUTF(msgOut);
            System.out.println("Sent: " + msgOut);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
