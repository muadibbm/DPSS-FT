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
	
	protected FrontEndUDPThread(int pPort) 
	{
		aPort = pPort;
		bCrashed = false;
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
	
	@Override
	public void run ()
	{
		while(true)
			handleCommunication();
	}

	/*  Handles the messages received from the replica leader */
	private void handleCommunication() 
	{
		try {
			request = new DatagramPacket(buffer, buffer.length);
			aDatagramSocket.receive(request);
			messageArray = (new String(request.getData())).split(Parameters.UDP_PARSER);
			if(messageArray[0].equals(Parameters.LR_NAME))
			{
				if(messageArray.length > 2) // get Player Status
				{
					messageArray[1] = messageArray[1].trim();
					FrontEndORBThread.setResponse(messageArray[1]);
				}
				else
					{
					switch(Integer.parseInt(messageArray[1].substring(0, 1)))
					{
						case 0 : FrontEndORBThread.setConfimation(false); break;
						case 1 : FrontEndORBThread.setConfimation(true); break;
					}
				}
				FrontEndORBThread.setLeaderResponded(true);
			}
		} catch (IOException e) {
			aDatagramSocket.close();
			bCrashed = true;
		}
	}	
}