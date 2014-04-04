package replicaA;

import system.AbstractGameServer;
import system.UDP;

public class UDPThread extends UDP
{
	private AbstractGameServer aGameServer;
	
	public UDPThread(int pPort) 
	{
		super(pPort);
	}

	@Override
	public boolean startGameServer(String pServerName) 
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

	@Override
	public boolean stopGameServer() 
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
	public void handleCommunication() 
	{
		
	}
}
