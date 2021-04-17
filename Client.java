import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) throws IOException{
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);

        ServerConnection serverConnection = new ServerConnection(socket);

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        new Thread(serverConnection).start();
        while (true) {

            String command = keyboard.readLine();

            out.println(command);

            String serverMsg = in.readLine();

            if (serverMsg.contains("enter")) {
                out.println("ok");
                System.out.println("Hello");
            }

            if (command.equals("quit")) break;

        }

        socket.close();
        System.exit(0);
    }
}

