package HW2;

import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class rootdns {

	static public int comport = 7070;
	static public String comip = "localhost";
	

	public static void main(String args[]) {
	    
		try {
			ExecutorService thread = Executors.newFixedThreadPool(10);
			DatagramSocket ds = new DatagramSocket(25597);

			while(true){
				System.out.println("Waiting for a packet reception..");	
				byte[] received = new byte[300];
				DatagramPacket dp = new DatagramPacket(received,  received.length);
				ds.receive(dp);
				thread.execute(new dns_root(received,dp));
			}
			
		} catch(IOException e) {}
	}

	private static class dns_root implements Runnable{


		private DatagramPacket dp;
		private byte[] bf;
		
	
		dns_root(byte[] bf,DatagramPacket dp){
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
			String lastpart = rs2.substring(rs2.lastIndexOf('.')+1);
			try{
			DatagramSocket ds= new DatagramSocket();
			InetAddress clientip=dp.getAddress();
			int clientport=dp.getPort();
			System.out.println("IP:" + clientip + "  Port#:"+ clientport);
			System.out.println("message: " + rs2);
	
			if(lastpart.equals("com")){
				String ipport=comip+":"+String.valueOf(comport);
				byte[] bf3 = new byte[300];
				bf3=ipport.getBytes();
				DatagramPacket dp_send = new DatagramPacket(bf3, bf3.length, clientip, clientport);
				ds.send(dp_send);
			}
		}
		catch(IOException e){}
	
	
			
		}
	}
}




