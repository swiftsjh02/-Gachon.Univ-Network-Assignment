package HW1;
import java.io.*;
import java.net.*;


public class client {
    public static void main(String[] args){
        String ip="localhost";
        String port="12345";

        InputStream is=null;
		BufferedReader stin = null;
		BufferedWriter toserver = null;

		Socket socket = null;

        try{
            File file= new File("HW1/server_info.dat");
            FileReader filereader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(filereader);
            String line="";
            line=bufReader.readLine();
            ip=line;
            System.out.println(line);
            line=bufReader.readLine();
            port=line;
            System.out.println(line);

            bufReader.close();


        }
        catch(FileNotFoundException e){
            System.out.println("no file found, Run Program as default ip and port");
        }
        catch(Exception e){
            System.out.println(e);
        }

        try {
			socket = new Socket(ip, Integer.parseInt(port)); 

			is=socket.getInputStream();
			stin = new BufferedReader(new InputStreamReader(System.in)); 

			toserver = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			String outputMessage;
			while (true) {
				outputMessage = stin.readLine(); 
				if (outputMessage.equalsIgnoreCase("bye")) { 
					toserver.write(outputMessage);
					toserver.flush();
					byte[] data = new byte[256];
					int n = is.read(data);
					final String result=new String(data,0,n);
					System.out.println(result);
					break;
				}
				toserver.write(outputMessage); 
				toserver.flush();

				byte[] data = new byte[256];
				int n = is.read(data);
				final String result=new String(data,0,n);
				System.out.println("result: "+result);
				
				
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
