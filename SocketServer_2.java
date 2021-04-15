import java.io.*;
import java.net.*;


public class SocketServer_2 {
    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(9090);
        System.out.println("server is running");
        while (true){
            Socket socket1 = listener.accept();
            PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
            BufferedReader input1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
            out1.println("connected to server");
            Socket socket2 = listener.accept();
            PrintWriter out2 = new PrintWriter(socket2.getOutputStream(), true);
            BufferedReader input2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
            out2.println("connected to server");
            String msg1 = "";
            String msg2 = "";
            while (true){
                msg1 = input1.readLine();
                if(!msg1.equals("")){
                    out2.println(msg1);
                }
                msg2 = input2.readLine();
                if(!msg2.equals("")){
                    out1.println(msg2);
                }
                if(msg1.equals("#") || msg2.equals("#")) break;
            }
            out1.close();
            input1.close();
            socket1.close();
            out2.close();
            input2.close();
            socket2.close();
        }
    }
}