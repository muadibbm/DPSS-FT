package frontEnd;

import org.omg.CORBA.ORB;

/**
 * This is the thread that handles the running of the ORB object of the front end
 * @author Mehrdad Dehdashti
 */
class FrontEndORBThread extends Thread
{
	private ORB orb;
	private static boolean bLeaderResponded;
	private static boolean bConfirmation;

	protected FrontEndORBThread(ORB pOrb)
	{
		orb = pOrb;
		bLeaderResponded = false;
		bConfirmation = false;
	}

	protected static void setLeaderResponded(boolean pResponded)
	{
		bLeaderResponded = pResponded;
	}
	
	// Used to check if the leader has sent back the result
	protected static boolean hasLeaderResponded()
	{
		return bLeaderResponded;
	}
	
	protected static void setConfimation(boolean pConfirmation)
	{
		bConfirmation = pConfirmation;
	}
	
	// Used to check if the method invoked was a success or failure
	protected static boolean getConfirmation()
	{
		return bConfirmation;
	}
	
	
	public void run()
	{
		orb.run();
	}
}
