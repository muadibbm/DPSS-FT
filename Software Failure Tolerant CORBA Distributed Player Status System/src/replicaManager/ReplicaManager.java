package replicaManager;

import java.io.*;
import java.net.*;

public class ReplicaManager 
{
	
	
	
	static DatagramSocket aSocket = null;
	static boolean waitForConnection = true;
	static String serverIPAddress;
	static int IPaddress = 0;
	static int serverPort;
	static String dataRecieved = null;
	static String [] messageArray;
	static int parserPosition = 0;
	
	
	public ReplicaManager()
	{
		
	}
	
	public static void main (String [] args) {
		
		startServer(8888);
		
	}
	
	public static void startServer (int portNumber) {
		String requestServerInitials = null;
		String requestMethodCode = null;
	
		try {
			
			aSocket = new DatagramSocket(Parameters.UDP_PORT_REPLICA_B);
			byte [] buffer = new byte [Parameters.UDP_BUFFER_SIZE];
			
			//always listen for new messages on the specified port
			while (waitForConnection) {							
				
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);

				//get the data from the request and check
				dataRecieved = new String(request.getData());
				//dataRecieved.toUpperCase();
				messageArray = dataRecieved.split(Parameters.UDP_PARSER);
				
				parserPosition = dataRecieved.indexOf(Parameters.UDP_PARSER);
				System.out.println(dataRecieved);
				
				//get who is initiating the request : RM, Leader, Replica1 or Replica2
				requestServerInitials = messageArray[0];
				requestMethodCode = messageArray[1]; 
			}
		}
			catch (Exception e) {e.printStackTrace();} 
		}
		
	
	
	public static void stopServer (int portNumber) {
		
		
	}
}
