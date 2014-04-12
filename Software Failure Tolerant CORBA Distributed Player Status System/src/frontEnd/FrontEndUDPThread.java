package frontEnd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * This is the thread that handles the running of the UDP communication of the front end
 * @author Mehrdad Dehdashti
 */
class FrontEndUDPThread extends Thread
{
	private DatagramSocket aDatagramSocket;
	private DatagramPacket request;
	private byte [] buffer;
	private String [] messageArray;
	private int aPort;
	private boolean bCrashed;
	private boolean bLeaderResponded;
	private boolean bConfirmation;
	
	protected FrontEndUDPThread(int pPort) 
	{
		aPort = pPort;
		bCrashed = false;
		bLeaderResponded = false;
		bConfirmation = false;
		buffer = new byte [Parameters.UDP_BUFFER_SIZE];
		try {
			aDatagramSocket = new DatagramSocket(aPort);
		} catch (SocketException e) {
			bCrashed = true;
		}
	}
	
	protected boolean hasCrashed()
	{
		return bCrashed;
	}
	
	protected boolean hasLeaderResponded()
	{
		return bLeaderResponded;
	}
	
	protected boolean getLeaderConfirmation()
	{
		return bConfirmation;
	}
	
	@Override
	public void run ()
	{
		while(true)
			handleCommunication();
	}

	/*  Handles the messages received from the replica leader */
	private void handleCommunication() 
	{
		bLeaderResponded = false;
		try {
			request = new DatagramPacket(buffer, buffer.length);
			aDatagramSocket.receive(request);
			messageArray = (new String(request.getData())).split(Parameters.UDP_PARSER);
			if(messageArray[0].equals(Parameters.LR_NAME))
			{
				switch(Integer.parseInt(messageArray[1]))
				{
					case 0 : bConfirmation = false; break;
					case 1 : bConfirmation = true; break;
				}
				bLeaderResponded = true;
			}
		} catch (IOException e) {
			aDatagramSocket.close();
			bCrashed = true;
		}
	}	
}