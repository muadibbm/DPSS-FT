package system;


public abstract class UDP extends Thread
{
	private int aPort;
	private String aServerName;
	private AbstractGameServer aGameServer;
	
	public UDP (int pPort, String pServerName) 
	{
		aPort = pPort;
		aServerName = pServerName;
	}
	
	public abstract boolean startGameServer();
	public abstract boolean stopGameServer();
	public abstract void handleCommunication();
	@Override
	public void run ()
	{
		startGameServer();
		while (true)
			handleCommunication();
	}
}
