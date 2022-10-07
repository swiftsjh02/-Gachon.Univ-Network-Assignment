package HW1;
import java.io.*;
import java.net.*;


public class client {

	public static void recvMsg(InputStream is){
		byte[] data = new byte[256]; //서버로 부터 받은 byte 배열 공간 생성
		try{
			is.read(data); //서버로 부터 받은 byte 배열을 data에 저장
		}
		catch(Exception e){
			System.out.println(e);
		}
		byte sizeofcode=data[0]; //sizeofcode에 data의 첫번째 byte 저장
		byte sizeofresult=data[sizeofcode+1]; //sizeofresult에 data[sizeofcode+1]에 있는 결과값의 길이 저장
		String code=new String(data,1,sizeofcode); //code에 data의 1번째 byte부터 sizeofcode만큼의 byte를 String으로 변환하여 저장
		System.out.println("Status: "+code);
		if(sizeofresult!=0){
			String result=new String(data,sizeofcode+2,sizeofresult); //result에 data의 sizeofcode+2번째 byte부터 sizeofresult만큼의 byte를 String으로 변환하여 저장
			System.out.println("Result: "+result);
		}
		
	}
    public static void main(String[] args){
        String ip="localhost"; // 기본 서버 주소와 포트를 하드코드방식으로 지정
        String port="8080";

        InputStream is=null; // 서버로부터 받은 메시지를 읽어들이기 위한 InputStream
		BufferedReader stin = null; // 키보드로부터 입력받기 위한 BufferedReader
		BufferedWriter toserver = null; // 서버로 메시지를 보내기 위한 BufferedWriter

		Socket socket = null; // 서버와 통신하기 위한 소켓

        try{
            File file= new File("HW1/server_info.dat"); // 서버 주소와 포트를 저장한 파일을 읽어들이기 위한 File 객체
            FileReader filereader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(filereader);
            String line="";
            line=bufReader.readLine();
            ip=line;
            line=bufReader.readLine();
            port=line;
            bufReader.close();


        }
        catch(FileNotFoundException e){ // 파일이 없을 경우
            System.out.println("no file found, Run Program as default ip and port");
        }
        catch(Exception e){ //그 외의 에러 발생시
            System.out.println(e);
        }

        try {
			socket = new Socket(ip, Integer.parseInt(port));  // 서버와 통신을 위한 소켓 생성

			is=socket.getInputStream(); // 서버로부터 받은 메시지를 읽어들이기 위한 InputStream
			stin = new BufferedReader(new InputStreamReader(System.in)); // 키보드로부터 입력받기 위한 BufferedReader

			toserver = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // 서버로 메시지를 보내기 위한 BufferedWriter

			String outputMessage; // 서버로 보낼 메시지를 저장할 변수
			while (true) {
				outputMessage = stin.readLine(); // 키보드로부터 입력받은 메시지를 outputMessage에 저장
				if (outputMessage.equalsIgnoreCase("bye")) {  // bye를 입력받으면 
					toserver.write(outputMessage); //서버에게 bye를 보냄
					toserver.flush(); // 버퍼를 비움
					recvMsg(is); // 서버로부터 받은 메시지를 출력
					break; //소켓 연결 종료
				}
				toserver.write(outputMessage);  // bye가 아닌 다른 메시지를 입력받으면 서버로 메시지를 보냄
				toserver.flush(); // 버퍼를 비움

				recvMsg(is); // 서버로부터 받은 결과를 recvMsg 함수를 통해 출력
				
				
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				socket.close(); 
			} catch (IOException e) {
				System.out.println("비정상적 종료");
			}
		}

    }
}
