
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class kr {
	public static HashMap<String,String> map = new HashMap<>();

	public static void main(String args[]) {

        map.put("gov.kr","12.7.8.8");
        map.put("gachon.ac.kr","7.7.7.7");
	    
		try {
			ExecutorService thread = Executors.newFixedThreadPool(10);
			DatagramSocket ds = new DatagramSocket(2828);

			while(true){
				System.out.println("Waiting for a packet reception..");	
				byte[] received = new byte[300];
				DatagramPacket dp = new DatagramPacket(received,  received.length);
				ds.receive(dp);
				thread.execute(new dns_kr(received,dp));
			}
			
		} catch(IOException e) {}
	}

    private static class dns_kr implements Runnable{

        private DatagramPacket dp;
        private byte[] bf;
    
    
        dns_kr(byte[] bf,DatagramPacket dp){
            this.bf=bf;
            this.dp=dp;
        }
    
        @Override
        public void run(){
    
            
            String rs1 = new String(bf);
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
                System.out.println("no address found");
                bf="NoAddressFound".getBytes();
                DatagramPacket dp_send=new DatagramPacket(bf,bf.length,clientip,clientport);
                ds.send(dp_send);
            }else{
                System.out.println("address found");
                String ip=map.get(rs2);
                bf2=ip.getBytes();
                DatagramPacket dp_send= new DatagramPacket(bf2,bf2.length,clientip,clientport);
                ds.send(dp_send);
                    }
            }catch(Exception e){
                System.out.println(e);
            }
            
        }
    }



}



