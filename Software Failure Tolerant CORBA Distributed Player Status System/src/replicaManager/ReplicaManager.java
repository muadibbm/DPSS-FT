package replicaManager;

import java.io.*;
import java.net.*;

class ReplicaManager 
{

	private static int replicaAcounter = 0 , replicaBCounter = 0;

	private static DatagramSocket aSocket = null;
	private static boolean waitForConnection = true;
	private static String serverIPAddress;
	static int IPaddress = 0;
	static int serverPort;
	static String dataRecieved = null;
	static String [] messageArray;
	static int parserPosition = 0;

	// private constructor
	private ReplicaManager()
	{
		//Initialize the system by sending 3 UDP messages to 3 server groups
		startServerGroup(Parameters.UDP_PORT_REPLICA_LEAD);
		startServerGroup(Parameters.UDP_PORT_REPLICA_A);
		startServerGroup(Parameters.UDP_PORT_REPLICA_B);

		System.out.println ("Replica Manager sent request to run all servers!");
		startServerListener(Parameters.UDP_PORT_REPLICA_MANAGER);
	}

	public static void main (String [] args) 
	{
		new ReplicaManager();
	}

	protected static void startServerListener (int portNumber) {
		String requestServerInitials = null;

		try {

			aSocket = new DatagramSocket(Parameters.UDP_PORT_REPLICA_MANAGER);
			byte [] buffer = new byte [Parameters.UDP_BUFFER_SIZE];

			//always listen for new messages on the specified port
			while (waitForConnection) {							

				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);

				//get the data from the request and check
				dataRecieved = new String(request.getData());
				//dataRecieved.toUpperCase();
				messageArray = dataRecieved.split(Parameters.UDP_PARSER);

				//parserPosition = dataRecieved.indexOf(Parameters.UDP_PARSER);
				System.out.println(dataRecieved);

				if(messageArray[0].equals(Parameters.LR_NAME))
				{
					//check the message from the Replica A
					if (messageArray[1].contains(Parameters.RA_NAME)) 
					{
						replicaAcounter ++;
						if(replicaAcounter >= 3) 
						{
							replicaAcounter = 0;
							stopServer(Parameters.UDP_PORT_REPLICA_A);
						}
					} //check the message from the Replica B
					else if (messageArray[1].contains(Parameters.RB_NAME)) 
					{
						replicaBCounter ++;
						if(replicaBCounter >= 3) 
						{
							replicaBCounter = 0;
							stopServer(Parameters.UDP_PORT_REPLICA_B);
						}
					}
				}
			}
		}
			catch (Exception e) {e.printStackTrace();} 
		}

	protected static void startServerGroup(int portNumber){
		int UDPcommunicationPort = portNumber;
		DatagramSocket aSocket = null;
		String RMInitMessage = Parameters.RM_NAME + "/" + Parameters.METHOD_CODE.RESTART_REPLICA.name();
		boolean ackRecieved = true;

		try {
			aSocket = new DatagramSocket();
			byte [] m = RMInitMessage.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(m,m.length, aHost, UDPcommunicationPort);
			aSocket.send(request);

		}
		catch (SocketException e){
			System.out.println("Socket " + e.getMessage());
			ackRecieved = false;
		}
		catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
			ackRecieved = false;
		}

		finally {
			if (aSocket != null) {
				aSocket.close();
			}
		}
	}



	protected static boolean stopServer (int portNumber) {
		int stopServerPort = portNumber;
		DatagramSocket aSocket = null;
		String RMInitMessage = Parameters.RM_NAME + "/" + Parameters.METHOD_CODE.RESTART_REPLICA.name();
		boolean ackRecieved = true;

		try {
			aSocket = new DatagramSocket();
			byte [] m = RMInitMessage.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(m,RMInitMessage.length(), aHost, stopServerPort);
			aSocket.send(request);

		}
		catch (SocketException e){
			System.out.println("Socket " + e.getMessage());
			ackRecieved = false;
		}
		catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
			ackRecieved = false;
		}

		finally {
			if (aSocket != null) {
				aSocket.close();
			}
		}
		return ackRecieved;

	}
}