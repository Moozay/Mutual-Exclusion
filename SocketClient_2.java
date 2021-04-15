import java.io.*;
import java.net.Socket;
import java.net.Socket.*;
import javax.swing.*;
import javax.swing.JOptionPane.*;

public class SocketClient_2 {
    public static void main(String[] args) throws IOException{
        String serverAddress = JOptionPane.showInputDialog(null, "Input server Ip");
        String ServerPort = JOptionPane.showInputDialog(null, "Input server Port");
        /*String serverAddress = "172.20.10.2";*/
        /*int serverPort = 9090;*/
        int serverPort = Integer.parseInt(ServerPort);
        Socket s = new Socket(serverAddress, serverPort);
        /*Retrieve input and output stream of server. This allows client to send/receive from server*/
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        InputStream in;
        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String msg ="";
        msg = input.readLine();
        System.out.println(msg);
        String myName = "Moozay";
        while (true) {
            String toServer = JOptionPane.showInputDialog(null, "Send message to server");
            out.println("From " + myName + ": " + toServer);
            msg = input.readLine();
            System.out.println(msg);
            if(toServer.equals("kill")) break;
        }

        out.close();
        input.close();
        s.close();
        System.exit(0);
    }
}
