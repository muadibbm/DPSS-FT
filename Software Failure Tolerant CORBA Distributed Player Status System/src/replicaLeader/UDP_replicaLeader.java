package replicaLeader;

import java.net.*;
import java.io.*;

import org.omg.CORBA.ORB;

public class UDP_replicaLeader extends Thread
{
	private String m_UDPDataGram_from_stripped;
	
	@Override
	public void run()
	{		
		try 
		{
			setUDPServerOnline();
		} 
		catch (Exception e) 
		{
			System.out.println("Server thread interrubpted.");
		}
     //System.out.println("Exiting child thread.");
	}
	
	private UDP_replicaLeader()
	{
		
	}
	
	protected void setUDPServerOnline()
	{	
		DatagramSocket aSocket = null;
		try
		{
	    	aSocket = new DatagramSocket(Parameters.UDP_PORT_REPLICA_LEAD);
	    	
			byte[] buffer = new byte[Parameters.UDP_BUFFER_SIZE];
			
			System.out.println("UDP_replicaLeader.setUDPServerOnline: UDP_replicaLeader going online.");
			
 			while(true)
 			{  
 				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
  				aSocket.receive(request);     
  				String l_result = new String(request.getData(), "UTF-8");
  				
  				System.out.println("UDP_replicaLeader.setUDPServerOnline: l_result - " + l_result);
  				
  				if(l_result != null)
  				{
  					String l_senderName = parseSenderName(l_result);
  				
  					// Confirm from where the packet has come
  					if(l_senderName != null)
  					{
  						switch(l_senderName) 
  						{
  				    		case "FE":
  				    				System.out.println("Receiving data from FE.");
  				    				
  				    				// create LocalORBPRocessing Obj
  				    				LocalOrbProcessing l_LocalOrbProcessing = new LocalOrbProcessing();
  				    				
  				    				if(m_UDPDataGram_from_stripped != "")
  				    				{
  				    					String l_invocationResponse = l_LocalOrbProcessing.performRMI(m_UDPDataGram_from_stripped);
  				    					sendPacket(l_invocationResponse, Parameters.UDP_PORT_FE);
  				    				}
  				    				// send data to the orb
  				    				
  				    				// multicast data send to the Replica_A and Replica_B
  				    				break;
  				    
  				    		case "RM":
  				    				// commands me to start the orb and my personal NA, AS and EU Servers 
  				    			break;
  				    		
  				    		case "RA":
  				    				// result of a certain request
  				    			break;
  				    		
  				    		case "RB":
  				    				// result of a certain request
  				    			break;
  				    		
  				    		default:
  				    				System.out.println("Server thread interrubpted.");
  				    				break;
  				        
  						}	
  					
  					}
  				}
  				
  				/*else
  				{
  					System.out.println("UDP_LeaderData Received Cannot be parsed");
  				}*/
  				
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
			if(aSocket != null) aSocket.close();
		}
    }
	
	protected boolean sendPacket(String p_Data, int p_portNumber)
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
	
	// Parse the datagram and extract the senders name into a String Array
	protected String parseSenderName(String p_input)
	{
		String l_segments[] = p_input.split(Parameters.UDP_PARSER);
		if(l_segments != null)
		{
			m_UDPDataGram_from_stripped = p_input.substring(3, p_input.length());
			System.out.println("UDP_replicaLeader.parseSenderName: m_UDPDataGram_from_stripped - " + m_UDPDataGram_from_stripped);
			return l_segments[0];
		}
		System.out.println("UDP_replicaLeader.parseSenderName: failed to parse udp packet data");
		return null;
	}
	
		
	public static void main(String[] args)  
	{
		UDP_replicaLeader m_UDP_replicaLeader = new UDP_replicaLeader();
		m_UDP_replicaLeader.start();
	}

}