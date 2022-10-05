package Capitalize_example;
import java.io.*;
import java.net.*;
import java.util.*;

public class CaplitalizeClient {
    public static void main(String[] args){
        try{
            Socket socket = new Socket("localhost", 8080);
            var in = new Scanner(socket.getInputStream());
            var out = new PrintWriter(socket.getOutputStream(), true);
            var console = new Scanner(System.in);
            while(true){
                System.out.println("Enter a line to be capitalized:");
                String line = console.nextLine();
                out.println(line);
                if(line.trim().equals("BYE")){
                    socket.close();
                    console.close();
                    break;
                }
                System.out.println("Server response: " + in.nextLine());
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
        }
    

