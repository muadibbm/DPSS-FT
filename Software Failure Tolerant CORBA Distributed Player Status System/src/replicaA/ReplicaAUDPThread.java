package replicaA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;

/**
 * This is the UDP thread that handles all the communication for the contained game server within
 * @author Mehrdad Dehdashti
 */
class ReplicaAUDPThread extends Thread
{
	private interfaceIDL aInterfaceIDL;
	private GameServer aNAGameServer;
	private GameServer aEUGameServer;
	private GameServer aASGameServer;
	private Thread aNAThread;
	private Thread aEUThread;
	private Thread aASThread;
	private int aPort;
	// For receiving from replica leader multicast
	private MulticastSocket aMulticastSocket;
	private DatagramPacket requestFromLeaderPacket;
	private byte [] buffer;
	private String [] messageArray;
	// For sending replies to replica leader UDP
	private InetAddress localhost;
	private DatagramSocket aSendSocket;
	private DatagramPacket replyToLeaderPacket;
	private String data;
	private Logger aLog;
	
	// Main method which runs the UDP thread for replica A
	public static void main(String[] args) 
	{
		new ReplicaAUDPThread(Parameters.UDP_PORT_REPLICA_A, args);
	}
	
	private ReplicaAUDPThread(int pPort, String[] pArgs)
	{
		aLog = Log.createLog("ReplicaA_UDP");
		aPort = pPort;
		buffer = new byte [Parameters.UDP_BUFFER_SIZE];
		try {
			aMulticastSocket = new MulticastSocket(); // TODO : destination port
			// TODO aMulticastSocket.joinGroup(InetAddress.getByName(""));
			localhost = InetAddress.getByName("localhost");
			aSendSocket = new DatagramSocket(aPort);
		} catch (IOException e) {
			aLog.info("UDP Socket creation failed");
		}
		startServers(); // TODO : remove this
		// Start the UDP communication for replica A
		aLog.info("UDP Running");
		start();
	}
	
	/* sets the interfaceIDL reference based on the Geo location in the given pIPAddress 
	 * returns true if successful */
	private boolean setORBreference(String pIPAddress) throws IOException
	{
		ORB orb = ORB.init(new String [1], null);
		BufferedReader bufferedReader;
		// Get the reference to the CORBA objects from the file
		if(pIPAddress.length() >= 3 && pIPAddress.substring(0,3).equals(Parameters.GeoLocationOfGameServerNA))
		{
			bufferedReader = new BufferedReader(new FileReader(Parameters.RA_NA_NAME + "_IOR.txt"));
		}
		else if(pIPAddress.length() >= 2 && pIPAddress.substring(0,2).equals(Parameters.GeoLocationOfGameServerEU))
		{
			bufferedReader = new BufferedReader(new FileReader(Parameters.RA_EU_NAME + "_IOR.txt"));
		}
		else if(pIPAddress.length() >= 3 && pIPAddress.substring(0,3).equals(Parameters.GeoLocationOfGameServerAS))
		{
			bufferedReader = new BufferedReader(new FileReader(Parameters.RA_AS_NAME + "_IOR.txt"));			
		}
		else
		{
			aLog.info("Invalid GeoLocation");
			return false;
		}
		String stringORB = bufferedReader.readLine();
		bufferedReader.close();
		// Transform the reference string to CORBA object
		org.omg.CORBA.Object reference_CORBA = orb.string_to_object(stringORB);
		aInterfaceIDL = interfaceIDLHelper.narrow(reference_CORBA);
		
		orb = null;
		stringORB = null;
		bufferedReader = null;
		
		return true;
	}
	
	private boolean startServers()
	{
		try
		{
			// Create Game Servers within each thread
			aNAGameServer = new GameServer(Parameters.RA_NA_NAME, new String [1], Parameters.UDP_PORT_REPLICA_A_NA);
			aEUGameServer = new GameServer(Parameters.RA_EU_NAME, new String [1], Parameters.UDP_PORT_REPLICA_A_EU);
			aASGameServer = new GameServer(Parameters.RA_AS_NAME, new String [1], Parameters.UDP_PORT_REPLICA_A_AS);
			// Start the Threads running each runnable Game Server
			aNAThread = new Thread(aNAGameServer);
			aEUThread = new Thread(aEUGameServer);
			aASThread = new Thread(aASGameServer);
			aNAThread.start();
			aLog.info("NA Server Running");
			aEUThread.start();
			aLog.info("EU Server Running");
			aASThread.start();
			aLog.info("AS Server Running");
		}
		catch(Exception e)
		{
			aLog.info("Error starting the servers: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean stopServers()
	{
		try
		{
			aNAThread = null;
			aEUThread = null;
			aASThread = null;
			aNAGameServer.freeServerResources();
			aEUGameServer.freeServerResources();
			aASGameServer.freeServerResources();
			aNAGameServer = null;
			aEUGameServer = null;
			aASGameServer = null;
		}
		catch(Exception e)
		{
			aLog.info("Error stopping the servers: " + e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public void run ()
	{
		while(true)
			handleCommunication();
	}

	// Takes care of requests from replica leader multicast and sends replies to the replica leader UDP listener
	private void handleCommunication() 
	{
		try 
		{
			requestFromLeaderPacket = new DatagramPacket(buffer, buffer.length);
			aMulticastSocket.receive(requestFromLeaderPacket);
			messageArray = (new String(requestFromLeaderPacket.getData())).split(Parameters.UDP_PARSER);
			if(messageArray[0].equals(Parameters.LR_NAME)) // Message from replica leader
			{
				if(messageArray[1].equals(Parameters.METHOD_CODE.CREATE_ACCOUNT.name()))
				{
					setORBreference(messageArray[7]);
					if(aInterfaceIDL.createPlayerAccount(messageArray[2], messageArray[3], Integer.parseInt(messageArray[4]),
														 messageArray[5], messageArray[6], messageArray[7]))
					{
						data = Parameters.RA_NAME + Parameters.UDP_PARSER +
								"1";
						buffer = data.getBytes();
						replyToLeaderPacket = new DatagramPacket(buffer, data.length(), localhost, Parameters.UDP_PORT_REPLICA_LEAD);
						aSendSocket.send(replyToLeaderPacket);
					}
					else
					{
						// send fail
					}
				}
			}
			else if(messageArray[0].equals(Parameters.RM_NAME)) // Message from replica manager
			{
				
			}
		}
		catch (IOException e)
		{
			aLog.info("UDP crashed, closing UDP Socket");
			aSendSocket.close();
			aLog.info("UDP crashed, creating new UDP Socket");
			try {
				aSendSocket = new DatagramSocket(aPort);
			} catch (SocketException e1) {
				aLog.info("UDP Socket creation failed");
			}
		}
	}
}
