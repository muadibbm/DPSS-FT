package replicaA;

/**
 * UDP Thread used for intercommunication between the game servers
 * @author Mehrdad Dehdashti
 */
class UDPThread extends Thread
{
	private int aPort;
	private boolean bCrashed;
	
	protected UDPThread(int pPort)
	{
		aPort = pPort;
		bCrashed = false;
	}
	
	protected boolean hasCrashed()
	{
		return bCrashed;
	}
	
	@Override
	public void run()
	{
		// TODO : handle interserver communication
	}
}
