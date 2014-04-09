package frontEnd;

import java.net.DatagramSocket;
import java.net.SocketException;

import system.AbstractUDP;

/**
 * This is the thread that handles the running of the UDP communication of the front end
 * @author Mehrdad Dehdashti
 */
class FrontEndUDPThread extends AbstractUDP
{
	private DatagramSocket aDatagramSocket;
	
	protected FrontEndUDPThread(int pPort) 
	{
		super(pPort);
		try {
			aDatagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			setCrashed();
		}
	}

	@Override
	protected void handleCommunication() 
	{
		
	}
}
