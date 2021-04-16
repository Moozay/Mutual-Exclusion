import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) throws IOException{
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);

        ServerConnection serverConnection = new ServerConnection(socket);

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        new Thread(serverConnection).start();
        while (true) {

            System.out.println(" --- OPTIONS --- ");
            System.out.println(" 1. 'sc' to enter critical section\n 2. 'quit' to disconnect");
            String command = keyboard.readLine();

            out.println(command);

            if (command.equals("quit")) break;

        }

        socket.close();
        System.exit(0);
    }
}

