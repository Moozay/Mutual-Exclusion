import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 9090;


    private static final ArrayList<ClientHandler> clients = new ArrayList<>();
    private static final ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);

        //noinspection InfiniteLoopStatement
        while (true) {
            System.out.println("[SERVER] Waiting for client...");
            Socket client = listener.accept();
            SocketAddress clientId = client.getRemoteSocketAddress();
            System.out.println("[SERVER] New client with id: "+ clientId + " connected" );
            ClientHandler clientThread = new ClientHandler(client, clients, clientId);
            clients.add(clientThread);

            pool.execute(clientThread);
        }
    }
}
