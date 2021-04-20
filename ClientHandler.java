import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientHandler implements Runnable {
    private final BufferedReader in;
    private final PrintWriter out;
    private final ArrayList<ClientHandler> clients;
    private final SocketAddress clientId;
    private static boolean isFree = true;

    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients, SocketAddress clientId) throws IOException {
        this.clients = clients;
        this.clientId = clientId;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public void run() {
        try {
            while (true) {
                out.println(" --- OPTIONS --- ");
                out.println(" 1. 'sc' pour demander la section critique\n" +
                            " 2. 'quit' pour se deconnecter du server");

                String request = in.readLine();
                if(request.contains("sc")) {
                    if (isFree) {
                        enterSc();
                    }
                    else {
                        out.println("Client '" + clientId +"' a la section critique, veuillez patienter...");
                    }
                }
                else if (request.equals("quit")){
                   leaving("Client avec id: " + this.clientId + " s'est deconnecte", clientId);
                   break;
                }
                else {
                    out.println("Choose a valid option");
                }
            }
        } catch(IOException e) {
            System.err.println("IO Exception in client handler");
            System.err.println(Arrays.toString(e.getStackTrace()));
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
        out.println("Liste des utilisateurs actuellement connectes");
        for (int i = 0; i< clients.size(); i++){
            out.println(i + ". "+ clients.get(i).clientId);
        }
    }

    private void outToAll(String broadcastMessage, SocketAddress clientId) {
        for (ClientHandler aClient : clients) {
            aClient.out.println("Message de '" + clientId + "' : '" + broadcastMessage + "'");
        }
    }

    private void multicast(int[] list, String msg){
        for (int j : list) {
            unicast(j, msg);
        }
    }

    private void unicast(int indexNb, String msg){
        if ((indexNb<0) || (indexNb>clients.size())){
            out.println("Veuillez choisir une option valide");
        }
        else{
           for (int i = 0; i< clients.size(); i++){
               if(i == indexNb){
                   clients.get(i).out.println("Message de '" + clientId + "' : '" + msg + "'");
               }
           }
        }
    }

    private void leaving(String msg, SocketAddress clientId){
        outToAll(msg, clientId);
        System.out.println(msg);
    }

    private void flag(){
        isFree = !isFree;
    }

    private void enterSc(){
        if (isFree){
            flag();
            executeSc();
        }
    }

    private void executeSc(){
        try {
            while (true) {
                out.println("1. 'bc' pour envoyer un 'broadcast'\n" +
                            "2. 'mc' pour envoyer un 'multicast'\n " +
                            "3. 'uc' pour envoyer un 'unicast'\n" +
                            "4. 'exit' pour sortir de la section critique");

                String message = in.readLine();
                if(message.startsWith("bc")) {
                    out.println("Entrez un message: ");
                    String msg = in.readLine();
                    outToAll(msg, clientId);
                } else if (message.startsWith("mc")) {
                   showAllUsers();
                    out.println("Combien d'utilisateurs voulez-vous envoyer un message: ");
                    String nbUsers = in.readLine();
                    int intNbUsers = Integer.parseInt(nbUsers);
                    int[] listOfUsers = new int[intNbUsers];
                    if((intNbUsers>0) && (intNbUsers<=clients.size())){
                        out.println("Entrez les index des utilisateurs à envoyer un message");
                        for(int i = 0;i<listOfUsers.length;i++){
                            String userIndex = in.readLine();
                            listOfUsers[i] = Integer.parseInt(userIndex);
                        }
                        out.println("Entrez un message: ");
                        String msg = in.readLine();
                        multicast(listOfUsers, msg);
                    }
                    else {
                        out.println("Veuillez saisir un numero valide");
                    }
                }
                else if (message.startsWith("uc")){
                    out.println("Selectionnez un utilisateur: \n");
                    showAllUsers();
                    String user = in.readLine();
                    int userInt = Integer.parseInt(user);
                    out.println("Entrez un message: ");
                    String msg = in.readLine();
                    unicast(userInt,msg);
                }
                else if (message.startsWith("exit")){
                    leaving("Client avec id: " + this.clientId + " a sorti de la section critique", clientId);
                    flag();
                    break;
                }
                else {
                    out.println("Choisissez une option valide");
                }
            }
        } catch(IOException e) {
            System.err.println("IO Exception in client handler");
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
