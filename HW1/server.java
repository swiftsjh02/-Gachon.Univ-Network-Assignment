package HW1;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


class ClientThread extends Thread
{
    Socket socket;
    int id;
    
    ClientThread (Socket socket, int id)
    {
        this.socket = socket;
        this.id = id;
    }
    
    @Override
    public void run ()
    {
        try
        {
            while (true)
            {   
                OutputStream os=socket.getOutputStream();
                InputStream IS = socket.getInputStream();
                byte[] bt = new byte[256];
                int size = IS.read(bt);
                
                String output = new String(bt, 0, size, "UTF-8");
                String[] command= output.split(" ");
              
                if(command.length<2){
                    if(command[0].equals("bye")){
                        os.write("connetion closed by client's request".getBytes());
                        System.out.println("    Thread " + id + " is closed. ");
                        os.flush();
                        break;
                    }
                    else{
                        os.write("wrong command".getBytes());
                        os.flush();
                        continue;
                    }
                }
                else if(command.length>2){
                    os.write("Too many arguments ".getBytes());
                    os.flush();
                    continue;
                }
                String[] args=command[1].split(",");
                if(args.length>2){
                    os.write("Too many arguments ".getBytes());
                    os.flush();
                    continue;
                }
                if(args.length<2){
                    os.write("need 2 arguments not 1".getBytes());
                    os.flush();
                    continue;
                }
                double result=0;
                if(command[0].toUpperCase().equals("ADD")){
                    result=Integer.parseInt(args[0])+Integer.parseInt(args[1]);
                }
                else if(command[0].toUpperCase().equals("MINUS")){
                    result=Integer.parseInt(args[0])-Integer.parseInt(args[1]);
                }
                else if(command[0].toUpperCase().equals("DIV")){
                    try{
                    result=Double.parseDouble(args[0])/Integer.parseInt(args[1]);
                    }
                    catch(Exception e){
                        os.write("Error: Divide by zero".getBytes());
                        os.flush();
                        continue;
                    }
                }
                else if(command[0].toUpperCase().equals("MUL")){
                    result=Integer.parseInt(args[0])*Integer.parseInt(args[1]);
                }
                else{
                    os.write("Error: Wrong command".getBytes());
                    os.flush();
                    continue;
                }
                String result_string=Double.toString(result);
                System.out.println("Thread " + id + " >  result:" + result);
                os.write(result_string.getBytes());
                os.flush();
            }
        } catch (IOException e)
        {
            System.out.println("    Thread " + id + " is closed. ");
        }
    }
}

class ConnectThread extends Thread
{
    ServerSocket serverSocket;
    int count = 1;
    
    ConnectThread (ServerSocket serverSocket)
    {
        System.out.println(server.getTime() + " Server opened");
        this.serverSocket = serverSocket;
    }
    
    @Override
    public void run ()
    {
        try
        {
            while (true)
            {
                Socket socket = serverSocket.accept();
                System.out.println("    Thread " + count + " is started.");
                ClientThread clientThread = new ClientThread(socket, count);
                clientThread.start();
                count++;
            }
        } catch (IOException e)
        {
            System.out.println("    SERVER CLOSE    ");
        }
    }
}

public class server
{
    public static void main (String[] args)
    {
        Scanner input = new Scanner(System.in);
        ServerSocket serverSocket = null;
        try
        {   // 서버소켓을 생성, 8080 포트와 binding
            serverSocket = new ServerSocket(8080); // 생성자 내부에 bind()가 있고, bind() 내부에 listen() 있음
            ConnectThread connectThread = new ConnectThread(serverSocket);
            connectThread.start();
            
            int temp = input.nextInt(); // 스레드 생성 전에 숫자를 입력하면 바로 SERVER CLOSE!
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            serverSocket.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }
    
    static String getTime ()
    {
        SimpleDateFormat f = new SimpleDateFormat("[hh : mm : ss ]");
        return f.format(new Date());
    }
}