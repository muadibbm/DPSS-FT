package replicaLeader;

import java.net.*;
import java.io.*;
public class UDPPeer extends Thread
{
	private int m_PortNumber;
	private GameServerImpl m_gameServer = null;
	public static String m_PacketData = null;
	
	public void run()
	{
		try 
		{
			System.out.println("UDP Server Going OnLine @ " + m_PortNumber);
			receivePacket(m_PortNumber, m_gameServer);
		} 
		catch (Exception e) 
		{
			System.out.println("Server thread interrubpted.");
		}
     //System.out.println("Exiting child thread.");
	}
	
	UDPPeer(int p_PortNumber, GameServerImpl p_gameserver)
	{
		m_PortNumber = p_PortNumber;
		m_gameServer = p_gameserver;
	}
	
	public void receivePacket(int p_portNumber, GameServerImpl p_gameserver)
	{	
		DatagramSocket aSocket = null;
		
		int SenderPortNumber = 0;
		boolean SendReply = false;
		String DatatoSend = "";
		
		try
		{
	    	aSocket = new DatagramSocket(p_portNumber);
	    	// create socket at agreed port
			byte[] buffer = new byte[1000];
 			while(true)
 			{  
 				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
  				aSocket.receive(request);     
  				   
    			String result = new String(request.getData(), "UTF-8");
    			System.out.println("printing result: " + result);
  				
  				if(result.substring(0, 1).matches("P"))
  				{
  					SenderPortNumber = Integer.parseInt(result.substring(1, 5));
  					System.out.println("UDP Server @ "+p_gameserver.m_Location+" : Message Received from: " + SenderPortNumber);
  					  					
  					if(p_gameserver.m_Location == "NorthAmerica")
  	  				{
  						m_PacketData = null;
  	  					m_PacketData = p_gameserver.m_Location + "(" + p_gameserver.GetServerDetails() + ")";
  	  				}
  	  				else if(p_gameserver.m_Location == "Asia")
  	  				{
  	  					m_PacketData = null;
	  					m_PacketData = p_gameserver.m_Location + "(" + p_gameserver.GetServerDetails() + ")";
  	  				}
  	  				else if(p_gameserver.m_Location == "Europe")
  	  				{
  	  					m_PacketData = null;
	  					m_PacketData = p_gameserver.m_Location + "(" + p_gameserver.GetServerDetails() + ")";
  	  				}
  	  				else
  	  				{
  	  					m_PacketData = null;
  	  				}
  					
  					DatatoSend = m_PacketData;
  					SendReply = true;
  				}
  				
  				else if(result.substring(0, 1).matches("T"))
  				{
  					SenderPortNumber = Integer.parseInt(result.substring(1, 5));
  					String UserData = result.substring(5, result.length());
  					String Segments[] = UserData.split("/");
  					String newAccStatus = p_gameserver.createPlayerAccount(Segments[0],Segments[1],Segments[2],Segments[3], Segments[4], Segments[5]);
  					System.out.println("UDP Server @ "+p_gameserver.m_Location+" : " + newAccStatus);
  					m_PacketData = newAccStatus;
  					DatatoSend = m_PacketData;
  					SendReply = true;  					
  				}
  				
  				else
  				{
  					m_PacketData = result;
  					System.out.println("UDP Server @ "+p_gameserver.m_Location+" : Data set is: " + m_PacketData);
  				}
  				  				
  				if(SendReply == true && SenderPortNumber != 0)
  				{
  					UDPSendRequestforData(DatatoSend, SenderPortNumber);
  					//DatagramPacket reply = new DatagramPacket(UDPPeer.m_PacketData.getBytes(), UDPPeer.m_PacketData.length(), request.getAddress(), request.getPort());
  		    		//aSocket.send(reply);
  					System.out.println("UDP Server @ "+p_gameserver.m_Location+" : Message Sent to: " + SenderPortNumber);
  					SendReply = false;
  					DatatoSend = "";
  				}
    		}
		}
		catch (SocketException e)
		{
			System.out.println("Socket: " + e.getMessage());
		}
		catch (IOException e) 
		{
			System.out.println("IO: " + e.getMessage());
		}
		finally 
		{
			System.out.println("Closing Socket");
			if(aSocket != null) aSocket.close();
		}
    }
	
	public boolean UDPSendRequestforData(String p_Data, int p_portNumber)
	{
		DatagramSocket aSocket = null;
		try 
		{
			aSocket = new DatagramSocket();    
			byte [] m = p_Data.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			int serverPort = p_portNumber;		                                                 
			DatagramPacket request = new DatagramPacket(m,  p_Data.length(), aHost, serverPort);
			aSocket.send(request);			                        
			return true;
		}
		catch (SocketException e)
		{
			System.out.println("Socket: " + e.getMessage());
		}
		catch (IOException e)
		{
			System.out.println("IO: " + e.getMessage());
		}
		finally 
		{
			if(aSocket != null) aSocket.close();
		}
		
		return false;
	}
}
