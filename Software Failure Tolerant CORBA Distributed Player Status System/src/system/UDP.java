package system;

public abstract class UDP extends Thread
{
	public int aPort;
	
	public UDP (int pPort) 
	{
		aPort = pPort;
	}
	
	public abstract boolean startGameServer(String pServerName);
	public abstract boolean stopGameServer();
	public abstract void handleCommunication();
	@Override
	public void run ()
	{
		while (true)
			handleCommunication();
	}
}