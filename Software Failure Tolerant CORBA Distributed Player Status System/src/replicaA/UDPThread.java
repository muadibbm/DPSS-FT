package replicaA;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Logger;

import system.AbstractGameServer;
import system.AbstractUDP;

/**
 * This is the UDP thread that handles all the communication for the contained game server within
 * @author Mehrdad Dehdashti
 */
class UDPThread extends AbstractUDP
{
	private AbstractGameServer aGameServer;
	private DatagramSocket aDatagramSocket;
	private Logger aLog;
	
	protected UDPThread(int pPort)
	{
		super(pPort);
		try {
			aDatagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			setCrashed();
		}
	}

	/**
	 * This method creates the game server object contained within the UDP thread
	 * @param pServerName is the name of the server to be created
	 * @return true if the server was successfully created
	 */
	protected boolean startGameServer(String pServerName) 
	{
		try
		{
			aGameServer = new GameServer(pServerName);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	/*
	 * stops the game server
	 * @return true if the server was successfully stopped
	 */
	private boolean stopGameServer() 
	{
		try
		{
			((GameServer) aGameServer).stopServer();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	protected void handleCommunication() 
	{
		
	}
}
