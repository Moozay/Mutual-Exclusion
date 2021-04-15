import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;

    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients) throws IOException {
        this.client = clientSocket;
        this.clients = clients;
        InputStream in1;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    public void run() {
        try {
            while (true) {
                String request = in.readLine();
                if(request.contains("id")) {
                    out.println(Server.getRandomName());
                    System.out.println("[SERVER] Name sent. Closing...");
                } else if (request.startsWith("say")) {
                    int firstSpace = request.indexOf(" ");
                    if (firstSpace != -1) {
                        outToAll(request.substring(firstSpace+1));
                    }
                }
                else if ((request == null) || (request.equals("quit"))){
                   leaving("[SERVER] Client disconnected....");
                   break;
                }
                else {
                    out.println("Choose a valid option");
                }
            }
        } catch(IOException e) {
            System.err.println("IO Exception in client handler");
            System.err.println(e.getStackTrace());
        } finally {
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void outToAll(String broadcastMessage) {
        if (clients == null) System.out.println("Hello");
        for (ClientHandler aClient : clients) {
            aClient.out.println((broadcastMessage));
        }
    }

    private void leaving(String msg){
        outToAll(msg);
        System.out.println(msg);
    }
}
