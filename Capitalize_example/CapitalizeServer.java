package Capitalize_example;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.net.*;

class Capitalizer implements Runnable{
    private Socket socket;

    Capitalizer(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try{
            var in = new Scanner((socket.getInputStream()));
            var out= new PrintWriter(socket.getOutputStream(), true);
            while(in.hasNextLine()){
                out.println(in.nextLine().toUpperCase());
            }
        }catch(Exception e){
            System.out.println("Error:" + socket);
        }finally{
            try{
                socket.close();
            }catch(IOException e){}
            System.out.println("Closed:" + socket);
        }
    }
}


public class CapitalizeServer {
    public static void main(String[] args) throws Exception{
        try{
        ServerSocket listner = new ServerSocket(8080);
        System.out.println("The Capitalize Server is running...");
        
        
        ExecutorService pool = Executors.newFixedThreadPool(20);
        while(true){
            Socket socket = listner.accept();
            pool.execute(new Capitalizer(socket));
        }

    }catch(Exception e){
        System.out.println(e);
    }
    }
}





