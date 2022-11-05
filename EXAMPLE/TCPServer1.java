package EXAMPLE;
import java.io.*;
import java.net.*;

public class TCPServer1 {
    public static void main(String[] args){
        ServerSocket welcomeSocket = null;
        String clientSentence;
        String capitalizedSentence;
        int nPort;

        nPort=8080;
        try{
        welcomeSocket = new ServerSocket(nPort);
        }catch(IOException e){
            System.out.println("Error: " + e);
        }

        System.out.println("Server is listening on port " + nPort);

        while(true){
            try{

            
            Socket connectionSocket = welcomeSocket.accept();

            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            clientSentence=inFromClient.readLine();
            System.out.println("Client:"+clientSentence);
            capitalizedSentence=clientSentence.toUpperCase() + '\n';
            if(capitalizedSentence.equals("BYE")){
                System.out.println("Client has disconnected");
                connectionSocket.close();
                welcomeSocket.close();
                break;
            }
            outToClient.writeBytes(capitalizedSentence);
            }
            catch(Exception e){
                System.out.println("Error:"+e.getMessage());
            }
        }

    }
    
}
