package replicaA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;

/**
 * This is the UDP thread that handles communication with the replica manager
 * @author Mehrdad Dehdashti
 */
class ReplicaManagerUDPListener extends Thread
{
	private int aPort;
	private boolean bCrashed;
	private boolean bShouldRestart;
	private DatagramSocket aDatagramSocket;
	private DatagramPacket requestFromReplicaManager;
	private byte [] buffer;
	private String [] messageArray;
	
	protected ReplicaManagerUDPListener(int pPort) throws SocketException
	{
		aPort = pPort;
		bCrashed = false;
		bShouldRestart = false;
		aDatagramSocket = new DatagramSocket(aPort);
		buffer = new byte [Parameters.UDP_BUFFER_SIZE];
	}
	
	protected boolean hasCrashed()
	{
		return bCrashed;
	}
	
	protected boolean shouldRestart()
	{
		return bShouldRestart;
	}
	
	protected void resetShouldRestart()
	{
		bShouldRestart = false;
	}
	
	@Override
	public void run ()
	{
		while(true)
			handleCommunication();
	}
	
	/* Handles communication with the replica manager */
	public void handleCommunication()
	{
		try
		{
			requestFromReplicaManager = new DatagramPacket(buffer, buffer.length);
			aDatagramSocket.receive(requestFromReplicaManager);
			messageArray = (new String(requestFromReplicaManager.getData())).split(Parameters.UDP_PARSER);
			if(messageArray[0].equals(Parameters.RM_NAME))
			{
				messageArray[1] = messageArray[1].trim();
				if(messageArray[1].equals(Parameters.METHOD_CODE.RESTART_REPLICA.name()))
				{
					bShouldRestart = true;
					System.out.println(bShouldRestart);
				}
			}
		}
		catch (IOException e)
		{
			aDatagramSocket.close();
			bCrashed = true;
		}
	}
}

/**
 * This is the UDP thread that handles all the communication for the contained game server within
 * @author Mehrdad Dehdashti
 */
class ReplicaAUDPThread extends Thread
{
	private static ReplicaAUDPThread replicaA;
	protected Logger aLog;
	private interfaceIDL aInterfaceIDL;
	private GameServer aNAGameServer;
	private GameServer aEUGameServer;
	private GameServer aASGameServer;
	private Thread aNAThread;
	private Thread aEUThread;
	private Thread aASThread;
	// For receiving from replica leader multicast
	private static MulticastSocket aMulticastSocket;
	private DatagramPacket requestFromLeaderPacket;
	private byte [] buffer;
	private String [] messageArray;
	// For sending replies to replica leader UDP
	private DatagramSocket aSendSocket;
	private DatagramPacket replyToLeaderPacket;
	private String data;
	// For receiving from replica manager UDP
	protected ReplicaManagerUDPListener replicaManagerListener;
	
	// Main method which runs the UDP thread for replica A
	public static void main(String[] args) 
	{
		replicaA = new ReplicaAUDPThread(Parameters.UDP_PORT_REPLICA_A, args);
		while (true)
		{
			if(replicaA.replicaManagerListener.shouldRestart())
			{
				replicaA.stopServers();
				replicaA.startServers();
				replicaA.replicaManagerListener.resetShouldRestart();
			}
			if(replicaA.replicaManagerListener.hasCrashed())
			{
				replicaA.aLog.info("Crash detected in ReplicaA Replica Manager UDP Thread, restarting UDP");
				try {
					replicaA.replicaManagerListener = new ReplicaManagerUDPListener(Parameters.UDP_PORT_REPLICA_A);
				} catch (SocketException e) {
					replicaA.aLog.info("ReplicaA Replica Manager creating failed");
				}
				replicaA.replicaManagerListener.start();
			}
		}
	}
	
	private ReplicaAUDPThread(int pPort, String[] pArgs)
	{
		aLog = Log.createLog("ReplicaA_UDP");
		buffer = new byte [Parameters.UDP_BUFFER_SIZE];
		try {
			aMulticastSocket = new MulticastSocket(Parameters.UDP_PORT_REPLICA_LEAD_MULTICAST);
			aMulticastSocket.joinGroup(InetAddress.getByName(Parameters.UDP_ADDR_REPLICA_COMMUNICATION_MULTICAST));
			aSendSocket = new DatagramSocket();
			replicaManagerListener = new ReplicaManagerUDPListener(Parameters.UDP_PORT_REPLICA_A);
		} catch (IOException e) {
			aLog.info("UDP Socket creation failed");
		}
		// Start the UDP communication for replica A
		aLog.info("UDP Running");
		replicaManagerListener.start();
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
	
	/* Creates and starts the servers */
	protected boolean startServers()
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
			aLog.info(aNAGameServer.getName() + " Server Running");
			aEUThread.start();
			aLog.info(aEUGameServer.getName() + " Server Running");
			aASThread.start();
			aLog.info(aASGameServer.getName() + " Server Running");
		}
		catch(Exception e)
		{
			aLog.info("Error starting the servers: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	/* Stops the servers and clears their resources */
	protected boolean stopServers()
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
			aLog.info("Stopped All Servers");
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
			requestFromLeaderPacket.setLength(buffer.length);
			if(messageArray[0].equals(Parameters.LR_NAME))
			{
				messageArray[1] = messageArray[1].trim();
				aLog.info("Recieived request from replica leader : " + messageArray[1]);
				if(messageArray[1].equals(Parameters.METHOD_CODE.CREATE_ACCOUNT.name()))
				{
					messageArray[7] = messageArray[7].trim();
					setORBreference(messageArray[7]);
					if(aInterfaceIDL.createPlayerAccount(messageArray[2], messageArray[3], Integer.parseInt(messageArray[4]),
														 messageArray[5], messageArray[6], messageArray[7]))
						data = Parameters.RA_NAME + Parameters.UDP_PARSER + "1" + Parameters.UDP_PARSER + Parameters.UDP_END_PARSE;
					else
						data = Parameters.RA_NAME + Parameters.UDP_PARSER + "0" + Parameters.UDP_PARSER + Parameters.UDP_END_PARSE;
				}
				else if(messageArray[1].equals(Parameters.METHOD_CODE.PLAYER_SIGN_IN.name()))
				{
					messageArray[4] = messageArray[4].trim();
					setORBreference(messageArray[4]);
					if(aInterfaceIDL.playerSignIn(messageArray[2], messageArray[3], messageArray[4]))
						data = Parameters.RA_NAME + Parameters.UDP_PARSER + "1" + Parameters.UDP_PARSER + Parameters.UDP_END_PARSE;
					else
						data = Parameters.RA_NAME + Parameters.UDP_PARSER + "0" + Parameters.UDP_PARSER + Parameters.UDP_END_PARSE;
				}
				else if(messageArray[1].equals(Parameters.METHOD_CODE.PLAYER_SIGN_OUT.name()))
				{
					messageArray[3] = messageArray[3].trim();
					setORBreference(messageArray[3]);
					if(aInterfaceIDL.playerSignOut(messageArray[2], messageArray[3]))
						data = Parameters.RA_NAME + Parameters.UDP_PARSER + "1" + Parameters.UDP_PARSER + Parameters.UDP_END_PARSE;
					else
						data = Parameters.RA_NAME + Parameters.UDP_PARSER + "0" + Parameters.UDP_PARSER + Parameters.UDP_END_PARSE;
				}
				else if(messageArray[1].equals(Parameters.METHOD_CODE.TRANSFER_ACCOUNT.name()))
				{
					messageArray[5] = messageArray[5].trim();
					setORBreference(messageArray[4]);
					if(aInterfaceIDL.transferAccount(messageArray[2], messageArray[3], messageArray[4], messageArray[5]))
						data = Parameters.RA_NAME + Parameters.UDP_PARSER + "1" + Parameters.UDP_PARSER + Parameters.UDP_END_PARSE;
					else
						data = Parameters.RA_NAME + Parameters.UDP_PARSER + "0" + Parameters.UDP_PARSER + Parameters.UDP_END_PARSE;
				}
				else if(messageArray[1].equals(Parameters.METHOD_CODE.SUSPEND_ACCOUNT.name()))
				{
					messageArray[5] = messageArray[5].trim();
					setORBreference(messageArray[4]);
					if(aInterfaceIDL.suspendAccount(messageArray[2], messageArray[3], messageArray[4], messageArray[5]))
						data = Parameters.RA_NAME + Parameters.UDP_PARSER + "1" + Parameters.UDP_PARSER + Parameters.UDP_END_PARSE;
					else
						data = Parameters.RA_NAME + Parameters.UDP_PARSER + "0" + Parameters.UDP_PARSER + Parameters.UDP_END_PARSE;
				}
				else if(messageArray[1].equals(Parameters.METHOD_CODE.GET_PLAYER_STATUS.name()))
				{
					messageArray[4] = messageArray[4].trim();
					setORBreference(messageArray[4]);
					data = Parameters.RA_NAME + Parameters.UDP_PARSER + 
							aInterfaceIDL.getPlayerStatus(messageArray[2], messageArray[3], messageArray[4]) +
							Parameters.UDP_PARSER + Parameters.UDP_END_PARSE;
				}
				buffer = new byte [Parameters.UDP_BUFFER_SIZE];
				buffer = data.getBytes();
				replyToLeaderPacket = new DatagramPacket(buffer, data.length(),  InetAddress.getByName("localhost"), Parameters.UDP_PORT_REPLICA_LEAD);
				aSendSocket.send(replyToLeaderPacket);
				aLog.info("Sent back results to replica leader : " + data.toString());
				data = "";
			}
		}
		catch (IOException e)
		{
			aLog.info("UDP crashed, closing UDP Socket");
			aSendSocket.close();
			aLog.info("UDP crashed, creating new UDP Socket");
			try {
				aSendSocket = new DatagramSocket();
			} catch (SocketException e1) {
				aLog.info("UDP Socket creation failed");
			}
		}
	}
}
