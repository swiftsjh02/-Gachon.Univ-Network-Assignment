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
                
                OutputStream os=socket.getOutputStream(); //클라이언트에게 메세지를 보내기 위한 스트림
                InputStream IS = socket.getInputStream(); //클라이언트로부터 메세지를 받기 위한 스트림
                byte[] bt = new byte[256]; //클라이언트로부터 받은 메세지를 저장할 배열
                int size = IS.read(bt); //클라이언트로부터 받은 메세지의 크기를 저장
                
                String output = new String(bt, 0, size, "UTF-8");  //받은 메세지를 String으로 변환
                String[] command= output.split(" ");    //받은 메세지를 공백을 기준으로 나눔
              
                if(command.length<2){
                    if(command[0].equals("bye")){ //bye 명령어를 받으면 종료
                        msgsend.sendMsg(os,"ENDBYCLIENT");
                        System.out.println("    Thread " + id + " is closed. ");
                        break;
                    }
                    else{ //명령어가 2개 이하인 경우 에러메세지를 보냄
                        msgsend.sendMsg(os,"WCMD");
                        continue;
                    }
                }
                else if(command.length>2){
                    msgsend.sendMsg(os,"TooMany");
                    continue;
                }
                String[] args=command[1].split(",");
                if(args.length>2){ //계산의 인자가 2개 초과인 경우 에러메세지를 보냄
                    msgsend.sendMsg(os,"TooMany");
                    continue;
                }
                if(args.length<2){ //계산의 인자가 2개 미만인 경우 에러메세지를 보냄
                    msgsend.sendMsg(os,"TooFew");
                    continue;
                }
                double result=0; //계산 결과를 저장할 변수
                if(command[0].toUpperCase().equals("ADD")){
                    result=Integer.parseInt(args[0])+Integer.parseInt(args[1]);
                }
                else if(command[0].toUpperCase().equals("MINUS")){
                    result=Integer.parseInt(args[0])-Integer.parseInt(args[1]);
                }
                else if(command[0].toUpperCase().equals("DIV")){
                    if(Integer.parseInt(args[1])==0){//나누는 수가 0인 경우 에러메세지를 보냄
                        msgsend.sendMsg(os,"DIVZERO");
                        continue;
                    }
                    result=Double.parseDouble(args[0])/Integer.parseInt(args[1]);
                }
                else if(command[0].toUpperCase().equals("MUL")){
                    result=Integer.parseInt(args[0])*Integer.parseInt(args[1]);
                }
                else{
                    msgsend.sendMsg(os,"WCMD"); //명령어가 존재하지 않는 경우 에러메세지를 보냄
                    continue;
                }
                String result_string; //계산 결과를 문자열로 변환할 변수
                if(result!=(int)result){   //결과가 정수가 아니라면 
                    result_string=Double.toString(result); //Double형으로 변환
                }else{ //결과가 정수라면
                    result_string=Integer.toString((int)result); //Integer형으로 변환
                }
               
                System.out.println("Thread " + id + " >  result:" + result);
                msgsend.sendMsg(os, "OK", result_string); //OK 코드와 계산 결과를 보냄
                
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
    
    ConnectThread (ServerSocket serverSocket) //생성자를 통해 서버소켓을 받음
    {
        System.out.println(server.getTime() + " Server opened"); //서버가 열렸다는 메세지 출력
        this.serverSocket = serverSocket; //서버소켓을 저장
    }
    
    @Override
    public void run ()
    {
        try
        {
            while (true) //계속 새로운 클라이언트의 연결을 수락하고 새 소켓을 cLIENTtHREAD에 넘겨줌
            {
                Socket socket = serverSocket.accept();  //클라이언트의 연결을 수락
                System.out.println("    Thread " + count + " is started.");
                ClientThread clientThread = new ClientThread(socket, count); 
                clientThread.start(); //새로운 클라이언트의 연결을 수락하고 새 소켓을 cLIENTtHREAD에 넘겨줌
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
            ConnectThread connectThread = new ConnectThread(serverSocket); // 서버소켓을 connectThread에 넘겨줌
            connectThread.start(); // connectThread 시작
            
            
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