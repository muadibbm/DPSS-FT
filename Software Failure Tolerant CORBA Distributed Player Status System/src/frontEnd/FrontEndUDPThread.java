package frontEnd;

import system.AbstractUDP;

/**
 * This is the thread that handles the running of the UDP communication of the front end
 * @author Mehrdad Dehdashti
 */
class FrontEndUDPThread extends AbstractUDP
{
	private boolean bCrashed;
	
	protected FrontEndUDPThread(int pPort) 
	{
		super(pPort);
		bCrashed = false;
	}

	@Override
	protected void handleCommunication() 
	{
		
	}
	
	protected boolean hasCrashed()
	{
		return bCrashed;
	}

}
