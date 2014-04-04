package replicaA;

import system.UDP;

public class UDPThread extends UDP
{

	public UDPThread(int pPort, String pServerName) 
	{
		super(pPort, pServerName);
	}

	@Override
	public boolean startGameServer() 
	{
		return false;
	}

	@Override
	public boolean stopGameServer() 
	{
		return false;
	}

	@Override
	public void handleCommunication() 
	{
		
	}

}
