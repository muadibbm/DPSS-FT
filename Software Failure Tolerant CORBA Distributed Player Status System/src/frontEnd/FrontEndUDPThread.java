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
				if(messageArray.length > 3) // get Player Status
				{
					//messageArray[1] is confirmation
					FrontEndORBThread.setResponse(messageArray[2] + " Online " + messageArray[3] + " Offline " + messageArray[4] + "\n" +
							messageArray[5] + " Online " + messageArray[6] + " Offline " + messageArray[7] + "\n" +
							messageArray[8] + " Online " + messageArray[9] + " Offline " + messageArray[10]);
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