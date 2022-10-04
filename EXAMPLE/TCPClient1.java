package EXAMPLE;
import java.io.*;
import java.net.*;

public class TCPClient1 {
    public static void main(String[] args){
        String sentence;
        String modifiedSentence;
        String serverIP="localhost";
        int nPort=8080;
        try{
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket(serverIP,nPort);

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        sentence=inFromUser.readLine();
        outToServer.writeBytes(sentence+'\n');
        modifiedSentence=inFromServer.readLine();
        System.out.println("From Server:"+modifiedSentence);
        clientSocket.close();

        }catch(Exception e){
            System.out.println("Error:"+e.getMessage());
        }
    } 
    
}
