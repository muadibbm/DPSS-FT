package replicaManager;

import java.io.*;
import java.net.*;

public class ReplicaManager 
{

	static int leaderCounter = 0 , replicaAcounter = 0 , replicaBCounter = 0;

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
		//Initialize the system by sending 3 UDP messages to 3 server groups
		startServerGroup(Parameters.UDP_PORT_REPLICA_LEAD);
		startServerGroup(Parameters.UDP_PORT_REPLICA_A);
		startServerGroup(Parameters.UDP_PORT_REPLICA_B);

		startServerListener(Parameters.UDP_PORT_REPLICA_MANAGER);

	}

	public static void startServerListener (int portNumber) {
		String requestServerInitials = null;
		String requestMethodCode = null;

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

				//get who is initiating the request : RM, Leader, Replica1 or Replica2
				requestServerInitials = messageArray[0];



				//check the message from the leader
				if ( requestServerInitials.contains(Parameters.LR_NAME)) {

					if (Integer.parseInt(messageArray[1]) == 0) {
						if (leaderCounter != 0 ) {
							leaderCounter ++;
							if(leaderCounter>=3) {
								stopServer(Parameters.UDP_PORT_REPLICA_LEAD);
							}
						} else leaderCounter++;
					}
					else if (Integer.parseInt(messageArray[1]) == 1) {
						leaderCounter = 0;
					}
				}
					//check the message from the Replica A
					if ( requestServerInitials.contains(Parameters.RA_NAME)) {

						if (Integer.parseInt(messageArray[1]) == 0) {
							if (replicaAcounter != 0 ) {
								replicaAcounter ++;
								if(replicaAcounter>=3) {
									stopServer(Parameters.UDP_PORT_REPLICA_A);
								}
							} else replicaAcounter++;
						}
						else if (Integer.parseInt(messageArray[1]) == 1) {
							replicaAcounter = 0;
						}
					}

						//check the message from the Replica B
						if ( requestServerInitials.contains(Parameters.RB_NAME)) {

							if (Integer.parseInt(messageArray[1]) == 0) {
								if (replicaBCounter != 0 ) {
									replicaBCounter ++;
									if(replicaBCounter>=3) {
										stopServer(Parameters.UDP_PORT_REPLICA_B);
									}
								} else replicaBCounter++;
							}
							else if (Integer.parseInt(messageArray[1]) == 1) {
								replicaBCounter = 0;
							}
						}

				}
				//requestMethodCode = messageArray[1]; 

		}
			catch (Exception e) {e.printStackTrace();} 
		}

	public static void startServerGroup(int portNumber){
		int UDPcommunicationPort = portNumber;
		DatagramSocket aSocket = null;
		String RMInitMessage = Parameters.RM_NAME + "/" + Parameters.METHOD_CODE.RESTART_REPLICA.name();
		boolean ackRecieved = false;
		String dataFromReplay = null;

		try {
			aSocket = new DatagramSocket();
			byte [] m = RMInitMessage.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(m,RMInitMessage.length(), aHost, UDPcommunicationPort);
			aSocket.send(request);

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



	public static boolean stopServer (int portNumber) {
		int stopServerPort = portNumber;
		DatagramSocket aSocket = null;
		String RMInitMessage = Parameters.RM_NAME + "/" + Parameters.METHOD_CODE.RESTART_REPLICA.name();
		boolean ackRecieved = false;
		String dataFromReplay = null;

		try {
			aSocket = new DatagramSocket();
			byte [] m = RMInitMessage.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(m,RMInitMessage.length(), aHost, stopServerPort);
			aSocket.send(request);

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
		return ackRecieved;

	}
}