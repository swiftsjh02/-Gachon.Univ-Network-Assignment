package HW1;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;


class MsgSend{ //메시지를 보내는 클래스 , 2개의 메소드를 포함 
    //1. sendMsg : 정상적인 메시지를 보내는 메소드
    public int sendMsg(OutputStream os,String status,String result){
        try{
            byte[] code = status.getBytes();
            byte[] data = result.getBytes();
            int sizeofdata=data.length;
            int sizeofcode=code.length;
            byte[] packet= new byte[sizeofdata+sizeofcode+2];
            packet[0]=(byte)sizeofcode;
            for(int i=1; i<sizeofcode+1; i++){
                packet[i]=code[i-1];
            }
            packet[sizeofcode+1]=(byte)sizeofdata;
            for(int i=sizeofcode+2; i<sizeofdata+sizeofcode+2; i++){
                packet[i]=data[i-sizeofcode-2];
            }

            for(int i=0; i<packet.length; i++){
                System.out.println(packet[i]);
            }
            os.write(packet);
            os.flush();

            
        }
        catch(Exception e){
            System.out.println(e);
        }

        return 0;
    }
    //2. sendMsg : 결과값을 포함하지 않는 비정상적 메세지를 보내는 메소드
    public int sendMsg(OutputStream os,String status){
        try{
            byte[] code = status.getBytes();
            
            int sizeofdata=0;
            int sizeofcode=code.length;
            byte[] packet= new byte[sizeofdata+sizeofcode+2];
            packet[0]=(byte)sizeofcode;
            for(int i=1; i<sizeofcode+1; i++){
                packet[i]=code[i-1];
            }
            packet[sizeofcode+1]=(byte)sizeofdata;

            for(int i=0; i<packet.length; i++){
                System.out.println(packet[i]);
            }
            os.write(packet);
            os.flush();

            
        }
        catch(Exception e){
            System.out.println(e);
        }

        return 0;
    }
}


class ClientThread extends Thread
{
    Socket socket;
    int id;
    
    //생성자를 통해 입력받은 소켓과 클라이언트(쓰레드)의 id를 저장
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
            //메세지를 보내주는 객체 생성
            MsgSend msgsend = new MsgSend();
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
                        msgsend.sendMsg(os,"ENDBYCLIENT");
                        System.out.println("    Thread " + id + " is closed. ");
                        break;
                    }
                    else{
                        msgsend.sendMsg(os,"WCMD");
                        continue;
                    }
                }
                else if(command.length>2){
                    msgsend.sendMsg(os,"TooMany");
                    continue;
                }
                String[] args=command[1].split(",");
                if(args.length>2){
                    msgsend.sendMsg(os,"TooMany");
                    continue;
                }
                if(args.length<2){
                    msgsend.sendMsg(os,"TooFew");
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
                    if(Integer.parseInt(args[1])==0){
                        msgsend.sendMsg(os,"DIVZERO");
                        continue;
                    }
                    result=Double.parseDouble(args[0])/Integer.parseInt(args[1]);
                }
                else if(command[0].toUpperCase().equals("MUL")){
                    result=Integer.parseInt(args[0])*Integer.parseInt(args[1]);
                }
                else{
                    msgsend.sendMsg(os,"WCMD");
                    continue;
                }
                String result_string;
                if(result!=(int)result){
                    result_string=Double.toString(result);
                }else{
                    result_string=Integer.toString((int)result);
                }
               
                System.out.println("Thread " + id + " >  result:" + result);
                msgsend.sendMsg(os, "OK", result_string);
                
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
            System.out.println(e);
            System.out.println("    SERVER CLOSE    ");
        }
    }
}

public class server
{
    public static void main (String[] args)
    {
        
        ServerSocket serverSocket = null;
        try
        {   // 서버소켓을 생성, 8080 포트와 binding
            serverSocket = new ServerSocket(8080); // 생성자 내부에 bind()가 있고, bind() 내부에 listen() 있음
            ConnectThread connectThread = new ConnectThread(serverSocket);
            connectThread.start();
            
            
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
    }
    
    static String getTime ()
    {
        SimpleDateFormat f = new SimpleDateFormat("[hh : mm : ss ]");
        return f.format(new Date());
    }
}