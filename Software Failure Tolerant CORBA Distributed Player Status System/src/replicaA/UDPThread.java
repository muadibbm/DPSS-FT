package replicaA;

import system.AbstractGameServer;
import system.AbstractUDP;

/**
 * This is the UDP thread that handles all the communication for the contained game server within
 * @author Mehrdad Dehdashti
 */
class UDPThread extends AbstractUDP
{
	private AbstractGameServer aGameServer;
	
	protected UDPThread(int pPort) 
	{
		super(pPort);
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
		// TODO
	}
}
