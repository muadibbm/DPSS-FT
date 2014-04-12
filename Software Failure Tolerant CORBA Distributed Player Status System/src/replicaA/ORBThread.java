package replicaA;

import org.omg.CORBA.ORB;

/**
 * This is the thread that handles the running of the ORB object of replica A
 * @author Mehrdad Dehdashti
 */
class ORBThread extends Thread
{
	private ORB orb;

	protected ORBThread(ORB pOrb)
	{
		orb = pOrb;
	}

	@Override
	public void run()
	{
		orb.run();
	}
}
