package system;

/**
 * This parent class provides the UDP architecture every UDP thread has to follow
 * @author Mehrdad Dehdashti
 */
public abstract class AbstractUDP extends Thread
{
	private int aPort;
	private boolean bCrashed;
	
	public AbstractUDP (int pPort) 
	{
		aPort = pPort;
		bCrashed = false;
	}
	
	/** Handles all UDP communication */
	protected abstract void handleCommunication();
	
	/** Sets the UDP crash flag to true
	 * Note: do NOT override this method in your own UDP thread class
	 */
	public void setCrashed()
	{
		bCrashed = true;
	}
	
	/** Checks if the UDP has crashed 
	 * Note: do NOT override this method in your own UDP thread class
	 */
	public boolean hasCrashed()
	{
		return bCrashed;
	}
	
	/** Returns the port number of the UDP socket 
	  * Note: do NOT override this method in your own UDP thread class
	  */
	public int getPort()
	{
		return aPort;
	}
	
	/**
	 * The run method inherited from Thread start when the thread starts
	 * Note: do NOT override this method in your own UDP thread class
	 */
	@Override
	public void run ()
	{
		while (true)
			handleCommunication();
	}
}