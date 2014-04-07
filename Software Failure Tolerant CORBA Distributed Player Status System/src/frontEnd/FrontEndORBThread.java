package frontEnd;

import org.omg.CORBA.ORB;

/**
 * This is the thread that handles the running of the ORB object of the front end
 * @author Mehrdad Dehdashti
 */
class FrontEndORBThread extends Thread
{
	private ORB orb;

	protected FrontEndORBThread(ORB pOrb)
	{
		orb = pOrb;
	}

	public void run()
	{
		orb.run();
	}
}
