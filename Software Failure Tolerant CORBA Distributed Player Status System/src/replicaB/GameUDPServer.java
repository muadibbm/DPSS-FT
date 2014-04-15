package replicaB;

import java.io.*;
import java.net.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import org.omg.Dynamic.Parameter;

//import replicaA.Parameters;


public class GameUDPServer extends Thread{
	

	static DatagramSocket aSocket = null;
	static boolean waitForConnection = true;
	//String serverIPAddress;
	int serverPort = 0 ;
	
	static String dataRecieved = null;
	static int parserPosition = 0;
	static String requestServerInitials = null;
	static String requestMethodCode = null;
	static String requestIPServer = null;
	
	static ServerReplica replicaNA = null;
	static ServerReplica replicaEU = null;
	static ServerReplica replicaAS = null;
	
	
	static String [] messageArray = new String [10];
	/**
	  * Constructor for the Game Servers
	  * @param portNumber - the port that the server will listen to
	  */
	
	public void run() 
	{
		UDPMulticastListener();
	}
	
	public void UDPMulticastListener () {
		try 
		{
			System.out.println("Multicat UDP Listener for Leader Messages is now Online ");
			
			byte[] buffer = new byte[Parameters.UDP_BUFFER_SIZE];
			MulticastSocket aMulticastSocket;
		
			aMulticastSocket = new MulticastSocket(Parameters.UDP_PORT_REPLICA_LEAD_MULTICAST);
			aMulticastSocket.joinGroup(InetAddress.getByName(Parameters.UDP_ADDR_REPLICA_COMMUNICATION_MULTICAST));
		
			while (true) 
			{
				DatagramPacket requestFromLeaderPacket = new DatagramPacket(buffer, buffer.length);
				aMulticastSocket.receive(requestFromLeaderPacket);
				String l_result = new String(requestFromLeaderPacket.getData(), "UTF-8");
				requestFromLeaderPacket.setLength(buffer.length);
				System.out.println("requestFromLeaderPacket: " +l_result);
				
				sendPacket(l_result, Parameters.UDP_PORT_REPLICA_B);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	public static boolean sendPacket(String p_Data, int p_portNumber)
	{
		DatagramSocket aSocket = null;
		try 
		{
			aSocket = new DatagramSocket();    
			byte [] m = p_Data.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			int serverPort = p_portNumber;		                                                 
			DatagramPacket request = new DatagramPacket(m,  p_Data.length(), aHost, serverPort);
			aSocket.send(request);			                        
			return true;
		}
		catch (SocketException e)
		{
			System.out.println("Socket: " + e.getMessage());
		}
		catch (IOException e)
		{
			System.out.println("IO: " + e.getMessage());
		}
		finally 
		{
			if(aSocket != null) aSocket.close();
		}
		
		return false;
	}
	
	public GameUDPServer ( int portNumber)
	   {

		   this.serverPort = portNumber;
		   for ( int i = 0 ; i<= messageArray.length;i++) {
			   messageArray[i] = null;
		   }
			
	   }
	
	public GameUDPServer()
	{
		
	}
	
	public static void main ( String  [] args)
	{
		System.out.println("Main UDP server is up and running ...");
		//multicastListener();
	
		GameUDPServer l_GameUDPServer = new GameUDPServer();
		l_GameUDPServer.start();
		
	 try {
			
			aSocket = new DatagramSocket(Parameters.UDP_PORT_REPLICA_B);
			byte [] buffer = new byte [Parameters.UDP_BUFFER_SIZE];
			
			//always listen for new messages on the specified port
			while (waitForConnection) {							
				
				for ( int i = 0 ; i< messageArray.length;i++) {
					   messageArray[i] = null;
				  }
				dataRecieved = null;
				
				
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
				
				System.out.println("-----------------------------------------------------------");
				System.out.println("Method call for: " + requestMethodCode + " from server : " + requestServerInitials);
				System.out.println("-----------------------------------------------------------");
				int tempIP;
				
				if ((dataRecieved.trim()).contains(Parameters.RB_NAME)) {
					DatagramPacket replayToLeader = new DatagramPacket(request.getData(), request.getLength(),request.getAddress(),Parameters.UDP_PORT_REPLICA_LEAD);
					aSocket.send(replayToLeader);
					System.out.println ("On Main server sent ACK to leader: " +request.getData().toString());
				}
				
				if (!requestMethodCode.contains("1")) {
				if (requestMethodCode.contains(Parameters.METHOD_CODE.RESTART_REPLICA.toString())) {
					//stop the 3 servers
					if (! (replicaNA == null || replicaNA.equals(null))) {
						stopServers();
						//run the 3 UDP servers
						startServers();
					} else {
					//run the 3 UDP servers
					startServers();
					}
				}
				if (requestMethodCode.equalsIgnoreCase(Parameters.METHOD_CODE.CREATE_ACCOUNT.name())) {
					
					//System.out.println("There is a method call for CREATE ACCOUNT");
					tempIP = extractIP(messageArray[7]);
					
					//check the IP if the method is CREATE ACCOUNT
					switch (tempIP) {
					
						case Parameters.NA_IP_ADDRESS : 
							aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_NA, dataRecieved));
							break;
						
						case Parameters.EU_IP_ADDRESS : 
							aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_EU,dataRecieved));
							break;
						
						case Parameters.AS_IP_ADDRESS : 
							aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_AS, dataRecieved));
							break;
							
						default : System.out.println("The IP is not registered in our system");
						break;
					
					}
					//the requested method is PLAYER SIGN IN
					} 
				
				if (requestMethodCode.contains(Parameters.METHOD_CODE.PLAYER_SIGN_IN.name())) {
						tempIP = extractIP(messageArray[4]);
						//check the IP if the method is PLAYER SIGN IN
						switch (tempIP) {
						
							case Parameters.NA_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_NA, dataRecieved));
								break;
							
							case Parameters.EU_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_EU, dataRecieved));
								break;
							
							case Parameters.AS_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_AS, dataRecieved));
								break;
								
							default : System.out.println("The IP is not registered in our system");
							break;
					
						}
						//the requested method is PLAYER SIGN OUT
					} 
				
						if (requestMethodCode.contains(Parameters.METHOD_CODE.PLAYER_SIGN_OUT.name())) {
						tempIP = extractIP(messageArray[3]);
						
						//check the IP if the method is PLAYER SIGN OUT
						switch (tempIP) {
						
							case Parameters.NA_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_NA, dataRecieved));
								break;
							
							case Parameters.EU_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_EU, dataRecieved));
								break;
							
							case Parameters.AS_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_AS,dataRecieved));
								break;
							
							default : System.out.println("The IP is not registered in our system");
							break;
						
						}
						//the requested method is SUSPEND ACCOUNT
					}else if (requestMethodCode.contains(Parameters.METHOD_CODE.SUSPEND_ACCOUNT.name())) {
						tempIP = extractIP(messageArray[4]);
						
						//check the IP if the method is SUSPEND ACCOUNT
						switch (tempIP) {
						
							case Parameters.NA_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_NA, dataRecieved));
								break;
							
							case Parameters.EU_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_EU, dataRecieved));
								break;
							
							case Parameters.AS_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_AS, dataRecieved));
								break;
							
							default : System.out.println("The IP is not registered in our system");
							break;
						
						}
						//the requested method is TRANSFER ACCOUNT
					} else if (requestMethodCode.contains(Parameters.METHOD_CODE.TRANSFER_ACCOUNT.name())) {
						tempIP = extractIP(messageArray[4]);
						
						//check the IP if the method is TRANSFER ACCOUNT
						switch (tempIP) {
						
							case Parameters.NA_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_NA, dataRecieved));
								break;
							
							case Parameters.EU_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_EU,dataRecieved));
								break;
							
							case Parameters.AS_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_AS,dataRecieved));
								break;
						
							default : System.out.println("The IP is not registered in our system");
							break;
						}
					}
					//the requested method is GET PLAYER STATUS
					else if (requestMethodCode.contains(Parameters.METHOD_CODE.GET_PLAYER_STATUS.name())) {
						tempIP = extractIP(messageArray[4]);
						
						//check the IP if the method is  GET PLAYER STATUS
						switch (tempIP) {
						
							case Parameters.NA_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_NA,dataRecieved));
								break;
							
							case Parameters.EU_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_EU, dataRecieved));
								break;
							
							case Parameters.AS_IP_ADDRESS : 
								aSocket.send(forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_AS,dataRecieved));
								break;
								
							default : System.out.println("The IP is not registered in our system");
							break;
						
						}
					}
					//else {System.out.println("The METHOD CODE is not valid:" + messageArray[1]);}
				
		
						//forward the message to the leader
						if (dataRecieved.contains(Parameters.RB_NAME)) {
							DatagramPacket replay = new DatagramPacket(request.getData(), request.getLength(),request.getAddress(),Parameters.UDP_PORT_REPLICA_B);
							//DatagramPacket replay = new DatagramPacket(request.getData(), request.getLength(),request.getAddress(),Parameters.UDP_PORT_REPLICA_LEAD);
							System.out.println("LR will be getting the followinf confirmation: " + replay.getData().toString());
							aSocket.send(replay);
						}
						 
				
		}
			
	 }
	 }
		catch (Exception e) {e.printStackTrace();}

	}
	
	
	
	public static void startServers () {
		
		replicaNA = new ServerReplica(Parameters.NA_IP_ADDRESS);
		replicaEU = new ServerReplica(Parameters.EU_IP_ADDRESS);
		replicaAS = new ServerReplica(Parameters.AS_IP_ADDRESS);
		replicaNA.start();
		replicaEU.start();
		replicaAS.start();
		
		System.out.println("NA, EU and AS servers are up and running...");
	}
	
	
	public static void stopServers () {
		replicaNA.stop();
		replicaEU.stop();
		replicaAS.stop();
		System.out.println("Reboot servers NA, EU and AS ...");
	}
	

	
	public static DatagramPacket forwardUDPMessage (int portNumber, String messageToForward ) {
		DatagramPacket request = null;
		//Forward by UDP from the main udp game server to the specific geolocation server
		try {
			//aSocket = new DatagramSocket();
			byte [] m = messageToForward.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			int serverPort = portNumber;
			request = new DatagramPacket(m,messageToForward.length(), aHost, serverPort);

		}
		catch (Exception e){
			System.out.println("Socket " + e.getMessage());
		}

		return request;
	
	}
	
	public static int extractIP ( String inputIP ){
		int accessIP=0;
		try
		{
		int index = inputIP.indexOf(".");
		accessIP = Integer.parseInt(inputIP.substring(0, index));
		}
		catch ( Exception e ) {e.printStackTrace();}
		return accessIP;
	}
	
	public static void multicastListener () {
		
		 MulticastSocket socket = null;
		    DatagramPacket inPacket = null;
		    byte[] inBuf = new byte[Parameters.UDP_BUFFER_SIZE];
		    try {
		      //Prepare to join multicast group
		      socket = new MulticastSocket(8888);
		      InetAddress address = InetAddress.getByName("localhost");
		      socket.joinGroup(address);
		 
		      while (true) {
		        inPacket = new DatagramPacket(inBuf, inBuf.length);
		        socket.receive(inPacket);
		        String msg = new String(inBuf, 0, inPacket.getLength());
		        System.out.println("From " + inPacket.getAddress() + " Msg : " + msg);
		      }
		    } catch (IOException ioe) {
		      System.out.println(ioe);
		    }
		  
	}
	
	
	
}
