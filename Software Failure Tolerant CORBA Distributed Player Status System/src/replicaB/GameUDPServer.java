package replicaB;

import java.io.*;
import java.net.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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
	
	
	static String [] messageArray;
	/**
	  * Constructor for the Game Servers
	  * @param portNumber - the port that the server will listen to
	  */
	public GameUDPServer ( int portNumber)
	   {

		   this.serverPort = portNumber;
			
	   }
	
	
	public static void main ( String  [] args){
		System.out.println("Main UDP server is up and running ...");
		//multicastListener();
		
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
				
				System.out.println("-----------------------------------------------------------");
				System.out.println("Method call for: " + requestMethodCode + " for server : " + requestServerInitials);
				System.out.println("-----------------------------------------------------------");
				
				if (requestMethodCode.contains(Parameters.METHOD_CODE.START_REPLICA.toString())) {
					//run the 3 UDP servers
					startServers();
					
				}
				else if (requestMethodCode.contains(Parameters.METHOD_CODE.STOP_REPLICA.toString())) {
					//stop the 3 servers
					stopServers();
				}
				else {
					
					int tempIP;
					//
					
					if (requestMethodCode.contains(Parameters.METHOD_CODE.CREATE_ACCOUNT.toString())) {
					//System.out.println("There is a method call for CREATE ACCOUNT");
					tempIP = extractIP(messageArray[7]);
					
					//check the IP if the method is CREATE ACCOUNT
					switch (tempIP) {
					
						case Parameters.NA_IP_ADDRESS : 
							forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_NA, dataRecieved);
							break;
						
						case Parameters.EU_IP_ADDRESS : 
							forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_EU,dataRecieved);
							break;
						
						case Parameters.AS_IP_ADDRESS : 
							forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_AS, dataRecieved);
							break;
							
						default : System.out.println("The IP is not registered in our system");
						break;
					
					}
					
					//the requested method is PLAYER SIGN IN
					if (requestMethodCode.contains(Parameters.METHOD_CODE.PLAYER_SIGN_IN.toString())) {
						tempIP = extractIP(messageArray[4]);
						
						//check the IP if the method is PLAYER SIGN IN
						switch (tempIP) {
						
							case Parameters.NA_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_NA, dataRecieved);
								break;
							
							case Parameters.EU_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_EU, dataRecieved);
								break;
							
							case Parameters.AS_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_AS, dataRecieved);
								break;
								
							default : System.out.println("The IP is not registered in our system");
							break;
						
						}
					}
					
					
					//the requested method is PLAYER SIGN OUT
					if (requestMethodCode.contains(Parameters.METHOD_CODE.PLAYER_SIGN_OUT.toString())) {
						tempIP = extractIP(messageArray[3]);
						
						//check the IP if the method is PLAYER SIGN OUT
						switch (tempIP) {
						
							case Parameters.NA_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_NA, dataRecieved);
								break;
							
							case Parameters.EU_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_EU, dataRecieved);
								break;
							
							case Parameters.AS_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_AS,dataRecieved);
								break;
							
							default : System.out.println("The IP is not registered in our system");
							break;
						
						}
					}
					
					
//the requested method is SUSPEND ACCOUNT
					if (requestMethodCode.contains(Parameters.METHOD_CODE.SUSPEND_ACCOUNT.toString())) {
						tempIP = extractIP(messageArray[4]);
						
						//check the IP if the method is SUSPEND ACCOUNT
						switch (tempIP) {
						
							case Parameters.NA_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_NA, dataRecieved);
								break;
							
							case Parameters.EU_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_EU, dataRecieved);
								break;
							
							case Parameters.AS_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_AS, dataRecieved);
								break;
							
							default : System.out.println("The IP is not registered in our system");
							break;
						
						}
					}

					
					//the requested method is TRANSFER ACCOUNT
					if (requestMethodCode.contains(Parameters.METHOD_CODE.TRANSFER_ACCOUNT.toString())) {
						tempIP = extractIP(messageArray[4]);
						
						//check the IP if the method is TRANSFER ACCOUNT
						switch (tempIP) {
						
							case Parameters.NA_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_NA, dataRecieved);
								break;
							
							case Parameters.EU_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_EU,dataRecieved);
								break;
							
							case Parameters.AS_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_AS,dataRecieved);
								break;
						
							default : System.out.println("The IP is not registered in our system");
							break;
						}
					}
					
					
					
					//the requested method is GET PLAYER STATUS
					if (requestMethodCode.contains(Parameters.METHOD_CODE.GET_PLAYER_STATUS.toString())) {
						tempIP = extractIP(messageArray[4]);
						
						//check the IP if the method is  GET PLAYER STATUS
						switch (tempIP) {
						
							case Parameters.NA_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_NA,dataRecieved);
								break;
							
							case Parameters.EU_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_EU, dataRecieved);
								break;
							
							case Parameters.AS_IP_ADDRESS : 
								forwardUDPMessage(Parameters.UDP_PORT_REPLICA_B_AS,dataRecieved);
								break;
								
							default : System.out.println("The IP is not registered in our system");
							break;
						
						}
					}
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
	

	
	public static void forwardUDPMessage (int portNumber, String messageToForward) {
	
		//Forward by UDP from the main udp game server to the specific geolocation server
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte [] m = messageToForward.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			int serverPort = portNumber;
			DatagramPacket request = new DatagramPacket(m,messageToForward.length(), aHost, serverPort);
			aSocket.send(request);
			
			
		//wait for the replay from the geolocation server	
			byte [] buffer = new byte [Parameters.UDP_BUFFER_SIZE];
			DatagramPacket replay = new DatagramPacket(buffer, buffer.length);
			
		//should be true or false and String in case of status is requested	
			aSocket.receive(replay);
			System.out.println("Replay: " + new String(replay.getData()));
		}
		catch (SocketException e){
			System.out.println("Socket " + e.getMessage());
		}
		catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}

		finally {
			if (aSocket != null) {
				aSocket.close();
			}
			}
	
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
