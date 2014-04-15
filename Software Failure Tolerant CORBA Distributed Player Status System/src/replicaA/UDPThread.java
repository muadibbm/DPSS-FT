package replicaA;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * UDP Thread used for intercommunication between the game servers
 * @author Mehrdad Dehdashti
 */
class UDPThread extends Thread
{
	private GameServer aServerNameRef;
	private int aPort;
	private boolean bCrashed;
	private DatagramSocket aDatagramSocket;
	private byte [] buffer;
	private String [] messageArray;
	private DatagramPacket request;
	private DatagramPacket reply;
	private String data;
	private byte [] message;
	
	protected UDPThread(GameServer pGameServerRef, int pPort)
	{
		aServerNameRef = pGameServerRef;
		aPort = pPort;
		bCrashed = false;
		buffer = new byte [Parameters.UDP_BUFFER_SIZE];
		try {
			aDatagramSocket = new DatagramSocket(aPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	protected boolean hasCrashed()
	{
		return bCrashed;
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			try {
				request = new DatagramPacket(buffer, buffer.length);
				aDatagramSocket.receive(request);
				messageArray = (new String(request.getData())).split(Parameters.UDP_PARSER);
				messageArray[0] = messageArray[0].trim();
				if(messageArray[0].equals(Parameters.METHOD_CODE.TRANSFER_ACCOUNT.toString()))
				{
					messageArray[6] = messageArray[6].trim();
					aServerNameRef.createPlayerAccount(messageArray[1], messageArray[2], Integer.parseInt(messageArray[3]),
													   messageArray[4], messageArray[5], messageArray[6]);
				}
				else if(messageArray[0].equals(Parameters.METHOD_CODE.GET_PLAYER_STATUS.toString()))
				{
					data = aServerNameRef.getName() + Parameters.UDP_PARSER + 
							aServerNameRef.getNumberOfOnlinePlayer() + Parameters.UDP_PARSER +
							aServerNameRef.getNumberOfOfflinePlayer();
					message = data.getBytes();
					reply = new DatagramPacket(message, message.length, InetAddress.getByName("localhost"), request.getPort());
					aDatagramSocket.send(reply);
				}
			} catch (IOException e) {
				aDatagramSocket.close();
				bCrashed = true;
			}
		}
	}
}