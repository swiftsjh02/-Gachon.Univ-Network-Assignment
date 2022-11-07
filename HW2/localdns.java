package HW2;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class localdns {
	
	public static HashMap<String,String> map = new HashMap<>();
	public static void main(String args[]) {
	    
		try {
			ExecutorService thread = Executors.newFixedThreadPool(10);
			DatagramSocket ds = new DatagramSocket(6060);
			map.put("google.com","8.8.8.8");

			while(true){
				System.out.println("Waiting for a packet reception..");	
				byte[] received = new byte[300];
				DatagramPacket dp = new DatagramPacket(received,  received.length);
				ds.receive(dp);
				thread.execute(new dns(received,dp));
			}
			
		} catch(IOException e) {}
	}

	private static class dns implements Runnable{

		//status code 0: OK, 1:Fail,2:Request,
	public static byte[] Protocol_To_Byte(String msg,byte status,String ip){
		byte[] msg_byte = msg.getBytes();
		byte[] ip_byte = ip.getBytes();
		byte[] add ={status,(byte)msg_byte.length};
		int add_len=add.length;
		int reuslt2_len=msg_byte.length+add_len+ip_byte.length;
		byte[] result2=new byte[reuslt2_len];
		
		for(int i=0;i<add_len;i++){
			result2[i]=add[i];
		}
		for(int i=0;i<msg_byte.length;i++){
			result2[i+add_len]=msg_byte[i];
		}
		for(int i=0;i<ip_byte.length;i++){
			result2[i+add_len+msg_byte.length]=ip_byte[i];
		}
		

 		
		return result2;
	}

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


		private DatagramPacket dp;
		private byte[] bf;
		static public int rootport = 25597;
		static public String rootip = "localhost";
	
		dns(byte[] bf,DatagramPacket dp){
			this.bf=bf;
			this.dp=dp;
		}
	
		@Override
		public void run(){
			
			byte status=bf[0];
			byte lenofquery=bf[1];
			byte[] query_byte=new byte[lenofquery];
			for(int i=2; i<lenofquery+2; i++){
				query_byte[i-2]=bf[i];
			}
			
			String rs1 = new String(query_byte);
			String rs2 = rs1.trim();
			byte[] bf2= new byte[300];
			try{
			DatagramSocket ds= new DatagramSocket();
			InetAddress clientip=dp.getAddress();
			int clientport=dp.getPort();
			System.out.println("IP:" + clientip + "  Port#:"+ clientport);
			System.out.println("message: " + rs2);
			if(map.get(rs2)==null){
				byte[] bf3= new byte[300];
				System.out.println("Cache miss");
				System.out.println("Sending request to rootdns");
				bf=Protocol_To_Byte(rs2,(byte)2);
				DatagramPacket dp_send=new DatagramPacket(bf,bf.length,InetAddress.getByName(rootip),rootport);
				DatagramPacket dp_from_tld=new DatagramPacket(bf3,bf3.length);
				ds.send(dp_send);
				ds.receive(dp_from_tld);
				String ipport=new String(dp_from_tld.getData());
				String tldip[]=ipport.split(":");
				System.out.println("Received response from rootdns");
				System.out.println("tldip: "+tldip[0]);
				System.out.println("tldport: "+tldip[1]);
				byte[] finalip=new byte[300];
				DatagramPacket dp_to_tld=new DatagramPacket(bf,bf.length,InetAddress.getByName(tldip[0]),Integer.parseInt(tldip[1].trim()));
				System.out.println("sending request to tlddns");
				ds.send(dp_to_tld);
				DatagramPacket final_from_tld= new DatagramPacket(finalip,finalip.length);
				ds.receive(final_from_tld);
				String final_ip=new String(final_from_tld.getData());
				final_ip=final_ip.trim();
				System.out.println("Cached newly received ip");
				map.put(rs2, final_ip);
				DatagramPacket dp_local_to_client_final=new DatagramPacket(Protocol_To_Byte(rs2, (byte)0, final_ip),Protocol_To_Byte(rs2, (byte)0, final_ip).length,clientip,clientport);
				System.out.println("Sending response to client");
				ds.send(dp_local_to_client_final);
			}else{
				System.out.println("Cache hit");
				String ip=map.get(rs2);
				bf2=Protocol_To_Byte(rs2,(byte)0,ip);
				DatagramPacket dp_send= new DatagramPacket(bf2,bf2.length,clientip,clientport);
				ds.send(dp_send);
				System.out.println("Succesully sent response to client");
					}
			}catch(Exception e){
				System.out.println(e);
			}
			
		}
	}
	
}



