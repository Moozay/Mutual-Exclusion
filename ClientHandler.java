import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;
    private SocketAddress clientId;
    private static boolean isFree = true;
    private static ArrayList<SocketAddress> queue = new ArrayList<>();

    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients, SocketAddress clientId) throws IOException {
        this.client = clientSocket;
        this.clients = clients;
        this.clientId = clientId;
        InputStream in1;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    public void run() {
        try {
            while (true) {
                out.println(" --- OPTIONS --- ");
                out.println(" 1. 'sc' to enter critical section\n 2. 'quit' to disconnect");

                String request = in.readLine();
                if(request.contains("sc")) {
                    if (isFree) {
                        enterSc();
                    }
                    else {
                        out.println("you have been added to the queue, please wait...");
                    }
                }
                else if ((request == null) || (request.equals("quit"))){
                   leaving("client with id: " + this.clientId + " disconnected");
                    clients.remove(client);
                   break;
                }
                else {
                    out.println("Choose a valid option");
                }
            }
        } catch(IOException e) {
            System.err.println("IO Exception  3  in client handler");
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

    private void showAllUsers(){
        out.println("list of current users online");
        for (int i = 0; i< clients.size(); i++){
            out.println(i + ". "+ clients.get(i).clientId);
        }
    }

    private void outToAll(String broadcastMessage) {
        if (clients == null) System.out.println("Hello");
        for (ClientHandler aClient : clients) {
            aClient.out.println((broadcastMessage));
        }
    }

    private void multicast(int[] list, String msg){
        for (int j : list) {
            unicast(j, msg);
        }
    }

    private void unicast(int indexNb, String msg){
        if ((indexNb<0) || (indexNb>clients.size())){
            out.println("please choose a valid option");
        }
        else{
           for (int i = 0; i< clients.size(); i++){
               if(i == indexNb){
                   clients.get(i).out.println("message from " + clientId + " : '" + msg + "'");
               }
           }
        }
    }

    private void leaving(String msg){
        outToAll(msg);
        System.out.println(msg);
    }

    private void flag(){
        isFree = !isFree;
    }

    private void addToQueue(SocketAddress clientId){
        queue.add(clientId);
    }

    private void removeFromQueue(){
        queue.remove(0);
    }

    private void enterSc(){
        if (isFree){
            flag();
            executeSc();
        }
        else {

        }
    }

    private void removeFromList(SocketAddress id){

    }

    private void executeSc(){
        try {
            while (true) {
                out.println("1. 'bc' to broadcast\n" +
                        " 2. 'mc' to send message to group\n " +
                        "3. 'uc' to send message to a user\n" +
                        "4. 'exit' to leave critical section");

                String message = in.readLine();
                if(message.startsWith("bc")) {
                    out.println("input message: ");
                    String msg = in.readLine();
                    outToAll(msg);
                } else if (message.startsWith("mc")) {
                   showAllUsers();
                    out.println("how many users do you want to message: ");
                    String nbUsers = in.readLine();
                    int intNbUsers = Integer.parseInt(nbUsers);
                    int[] listOfUsers = new int[intNbUsers];
                    if((intNbUsers>0) && (intNbUsers<=clients.size())){
                        out.println("Enter the indexes of users to message");
                        for(int i = 0;i<listOfUsers.length;i++){
                            String userIndex = in.readLine();
                            listOfUsers[i] = Integer.parseInt(userIndex);
                        }
                        out.println("input message: ");
                        String msg = in.readLine();
                        multicast(listOfUsers, msg);
                    }
                    else {
                        out.println("this is it");
                    }
                }
                else if (message.startsWith("uc")){
                    out.println("select a user: \n");
                    showAllUsers();
                    String user = in.readLine();
                    int userInt = Integer.parseInt(user);
                    out.println("input message: ");
                    String msg = in.readLine();
                    unicast(userInt,msg);
                }
                else if (message.startsWith("exit")){
                    leaving("client with id: " + this.clientId + " left the critical section");
                    flag();
                    break;
                }
                else {
                    out.println("Choose a valid option");
                }
            }
        } catch(IOException e) {
            System.err.println("IO Exception in client handler");
            System.err.println(e.getStackTrace());
        }
    }
}
