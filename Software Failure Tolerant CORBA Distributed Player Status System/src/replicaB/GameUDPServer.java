package replicaB;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class GameUDPServer {
	
	static final int REPLICAB_PORT = 9999;
	
	public static int UDP_PORT_REPLICA_MANAGER_NA = 5000;
	public static int UDP_PORT_REPLICA_MANAGER_EU = 5100;
	public static int UDP_PORT_REPLICA_MANAGER_AS = 5200;
	
	public static int UDP_PORT_REPLICA_B_NA = 3000;
	public static int UDP_PORT_REPLICA_B_EU = 3100;
	public static int UDP_PORT_REPLICA_B_AS = 3200;
	
	static final int NA_IP_ADDRESS = 132;
	static final int EU_IP_ADDRESS = 93;
	static final int AS_IP_ADDRESS = 182;

	static DatagramSocket aSocket = null;
	static boolean waitForConnection = true;
	String serverIPAddress;
	int serverPort;
	static CharSequence  startServerMessage = "start";
	static CharSequence  stopServerMessage = "stop";
	
	static String dataRecieved = null;
	static int indexCode = 0;
	
	static ServerReplica replicaNA = null;
	static ServerReplica replicaEU = null;
	static ServerReplica replicaAS = null;
	
	/**
	  * Constructor for the Game Servers
	  * @param portNumber - the port that the server will listen to
	  */
	public GameUDPServer ( int portNumber)
	   {

		   this.serverPort = portNumber;
			
	   }
	
	
	public static void main ( String  [] args){
		
		
	 try {
			
			aSocket = new DatagramSocket(REPLICAB_PORT);
			byte [] buffer = new byte [1000];
			
			//always listen for new messages on the specified port
			while (waitForConnection) {							
				
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);

				//get the data from the request and check
				dataRecieved = new String(request.getData());
				dataRecieved.toLowerCase();
	
				if (dataRecieved.contains(startServerMessage)) {
					//run the 3 UDP servers
					startServers();
				}
				else if (dataRecieved.contains(stopServerMessage)) {
					//stop the 3 servers
					stopServers();
				}
				else {
					System.out.println("The UDP message was not read correctly! Please resend the message");
				}

			}	
			
	 }
		catch (Exception e) {e.printStackTrace();}

	}
	
	
	
	public static void startServers () {
		replicaNA = new ServerReplica(NA_IP_ADDRESS);
		replicaEU = new ServerReplica(EU_IP_ADDRESS);
		replicaAS = new ServerReplica(AS_IP_ADDRESS);
	}
	
	
	public static void stopServers () {
		replicaNA = null;
		replicaEU = null;
		replicaAS = null;
	}
	
}
