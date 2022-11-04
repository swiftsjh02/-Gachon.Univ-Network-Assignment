package UDP;
import java.net.*;
import java.util.Scanner;
import java.io.*;

public class client {
	public static void main(String args[]) {
		try {
			DatagramSocket ds = new DatagramSocket();
			InetAddress ia = InetAddress.getByName("localhost");
            Scanner sc= new Scanner(System.in);
			String msg = sc.nextLine();
			byte[] bf = msg.getBytes();					
			DatagramPacket dp = new DatagramPacket(bf, bf.length, ia, 9999);
			ds.send(dp);
            sc.close();
		} catch(IOException e) {}
	}
}
