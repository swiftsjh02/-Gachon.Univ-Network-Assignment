package HW2;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class client {
	

	public static void main(String args[]) {
		try {
			DatagramSocket ds = new DatagramSocket(8080);
            Scanner sc= new Scanner(System.in);
			String msg = sc.nextLine();
			byte[] bf = msg.getBytes();					
			DatagramPacket dp = new DatagramPacket(bf, bf.length,InetAddress.getByName("localhost"),6060);
			
			bf= new byte[300];
			DatagramPacket dp_recv= new DatagramPacket(bf,bf.length);

			ds.send(dp);
            sc.close();
			ds.receive(dp_recv);
			String ip= new String(bf);
			ip=ip.trim();
			System.out.println("IP: "+ip);
			ds.close();
		

		} catch(IOException e) {}
	}
}
