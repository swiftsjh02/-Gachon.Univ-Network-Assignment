package HW2;
import java.util.*;
import java.net.*;
import java.util.Scanner;
import java.io.*;


public class client {
	
	//status code 0: OK, 1:Fail,2:Request,
	public static byte[] Protocol_To_Byte(String msg,byte status){
		byte[] msg_byte = msg.getBytes();
		byte[] add ={status,(byte)msg_byte.length};
		int add_len=add.length;
		int reuslt2_len=msg_byte.length+add_len;
		byte[] result2=new byte[reuslt2_len+1];
		for(int i=0;i<add_len;i++){
			result2[i]=add[i];
		}
		for(int i=0;i<msg_byte.length;i++){
			result2[i+add_len]=msg_byte[i];
		}
		result2[reuslt2_len]=(byte)0;

 		
		return result2;
	}
	

	public static void main(String args[]) {
		try {
			DatagramSocket ds = new DatagramSocket(8080);
            Scanner sc= new Scanner(System.in);
			String msg = sc.nextLine(); //도메인 입력받기

			byte[] bf = Protocol_To_Byte(msg,(byte)2);			
			DatagramPacket dp = new DatagramPacket(bf, bf.length,InetAddress.getByName("localhost"),6090);
			
			bf= new byte[300];
			DatagramPacket dp_recv= new DatagramPacket(bf,bf.length);

			ds.send(dp);
            sc.close();

			
			ds.receive(dp_recv);
			ds.close();
			int status = bf[0];
			int query_len=bf[1];
			int ip_len=bf[query_len+2];
			System.out.print("IP:");
			for(int i=2+query_len;i<2+query_len+ip_len;i++){
				System.out.print((char)bf[i]);
			}
			System.out.println();
			
			
		

		} catch(IOException e) {}

		
	}




	
}
