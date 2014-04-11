package frontEnd;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * This is the thread that handles the running of the UDP communication of the front end
 * @author Mehrdad Dehdashti
 */
class FrontEndUDPThread extends Thread
{
	private DatagramSocket aDatagramSocket;
	private int aPort;
	boolean bCrashed;
	
	protected FrontEndUDPThread(int pPort) 
	{
		aPort = pPort;
		bCrashed = false;
		try {
			aDatagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			bCrashed = true;
		}
	}
	
	protected boolean hasCrashed()
	{
		return bCrashed;
	}
	
	@Override
	public void run ()
	{
		while(true)
			handleCommunication();
	}

	private void handleCommunication() 
	{
		// TODO : UDP listen to Leader
	}	
}