package replicaB;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class TestUDPClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DatagramSocket aSocket = null;
		//String message = "RM/RESTART_REPLICA";
		//String message = "LR/CREATE_ACCOUNT/Desy/Andree/22/est/test/93.22";
		//String message = "LR/CREATE_ACCOUNT/Desy/Andree/22/est/test/182.22";
		//String message = "LD/PLAYER_SIGN_IN/est/test/93.22";
		//String message = "LD/PLAYER_SIGN_OUT/est/93.22";
		String message = "LD/SUSPEND_ACCOUNT/Admin/Admin/93.22/est";
				
		//String message = "LR/CREATE_ACCOUNT/Desy/Andree/22/desy123/desy123/132.22";
		//String message = "LD/PLAYER_SIGN_IN/desy123/desy123/132.22";
		//String message = "LD/PLAYER_SIGN_OUT/desy123/132.22";
		//String message = "LD/SUSPEND_ACCOUNT/Admin/Admin/132.22/desy123";
		//String message = "RM/NO";
		
		//String message = "LD/GET_PLAYER_STATUS/Admin/Admin/182.22";
		
		try {
			aSocket = new DatagramSocket();
			byte [] m = message.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			int serverPort = Parameters.UDP_PORT_REPLICA_B;
			//int serverPort = Parameters.UDP_PORT_REPLICA_B_NA;
			DatagramPacket request = new DatagramPacket(m,message.length(), aHost, serverPort);
			aSocket.send(request);
			byte [] buffer = new byte [1000];
			DatagramPacket replay = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(replay);
			System.out.println("Replay: " + new String(replay.getData()));
		}
		catch (SocketException e){
			System.out.println("Socket " + e.getMessage());
		}
		catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}

	}
	

	
	}


