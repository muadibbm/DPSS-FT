package system;

/**
 * This parent class provides the UDP architecture every UDP thread has to follow
 * @author Mehrdad Dehdashti
 */
public abstract class AbstractUDP extends Thread
{
	public int aPort;
	
	public AbstractUDP (int pPort) 
	{
		aPort = pPort;
	}
	
	/** Handles all UDP communication */
	protected abstract void handleCommunication();
	
	/**
	 * The run method inherited from Thread start when the thread starts
	 * Note: do not override this method in your own UDP thread class
	 */
	@Override
	public void run ()
	{
		while (true)
			handleCommunication();
	}
}