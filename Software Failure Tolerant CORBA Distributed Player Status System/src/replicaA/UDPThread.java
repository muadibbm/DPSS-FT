package replicaA;

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
