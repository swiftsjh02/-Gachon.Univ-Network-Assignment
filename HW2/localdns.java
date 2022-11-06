package HW2;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class localdns {
	

	public static void main(String args[]) {
	    
		try {
			ExecutorService thread = Executors.newFixedThreadPool(10);
			DatagramSocket ds = new DatagramSocket(6060);

			while(true){
				System.out.println("Waiting for a packet reception..");	
				byte[] received = new byte[300];
				DatagramPacket dp = new DatagramPacket(received,  received.length);
				ds.receive(dp);
				thread.execute(new dns(received,dp));
			}
			
		} catch(IOException e) {}
	}
}


class dns implements Runnable{

	private DatagramPacket dp;
	private byte[] bf;
	public HashMap<String,String> map = new HashMap<>();
    static public int rootport = 25584;
	static public String rootip = "localhost";

	dns(byte[] bf,DatagramPacket dp){
		this.bf=bf;
		this.dp=dp;
	}

	@Override
	public void run(){
		if(map.get("www.google.com") == null){
			map.put("www.google.com","8.8.8.8");
		}

		
		String rs1 = new String(bf);
		String rs2 = rs1.trim();
		byte[] bf2= new byte[300];
		try{
		DatagramSocket ds= new DatagramSocket();
		Inet4Address clientip=(Inet4Address)dp.getAddress();
		int clientport=dp.getPort();
		System.out.println("IP:" + clientip + "  Port#:"+ clientport);
		System.out.println("message: " + rs2);
		if(map.get(rs2)==null){
            byte[] bf3= new byte[300];
			System.out.println("Cache missed");
			System.out.println("Sending request to rootdns");
			bf=rs2.getBytes();
			DatagramPacket dp_send=new DatagramPacket(bf,bf.length,InetAddress.getByName(rootip),rootport);
            DatagramPacket dp_from_tld=new DatagramPacket(bf3,bf3.length);
			ds.send(dp_send);
            ds.receive(dp_from_tld);
            String ipport=new String(dp_from_tld.getData());
            System.out.println(ipport);
		}else{
			System.out.println("Cache hit");
			String ip=map.get(rs2);
			bf2=ip.getBytes();
			DatagramPacket dp_send= new DatagramPacket(bf2,bf2.length,clientip,8080);
			ds.send(dp_send);
				}
		}catch(Exception e){
			System.out.println(e);
		}
		
	}
}

