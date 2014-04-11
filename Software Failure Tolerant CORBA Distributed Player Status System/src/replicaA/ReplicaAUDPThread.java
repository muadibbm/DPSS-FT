package replicaA;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Logger;

/**
 * This is the UDP thread that handles all the communication for the contained game server within
 * @author Mehrdad Dehdashti
 */
class ReplicaAUDPThread extends Thread
{
	private GameServer aNAGameServer;
	private GameServer aEUGameServer;
	private GameServer aASGameServer;
	private Thread aNAThread;
	private Thread aEUThread;
	private Thread aASThread;
	private DatagramSocket aDatagramSocket;
	private Logger aLog;
	private int aPort;
	
	// Main method which runs the UDP thread for replica A
	public static void main(String[] args) 
	{
		new ReplicaAUDPThread(Parameters.UDP_PORT_REPLICA_A);
	}
	
	private ReplicaAUDPThread(int pPort)
	{
		aPort = pPort;
		try {
			aDatagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO : log
		}
		// Create Game Servers within each thread
		aNAGameServer = new GameServer("ReplicaA_NA");
		aEUGameServer = new GameServer("ReplicaA_EU");
		aASGameServer = new GameServer("ReplicaA_AS");
		// Start the Threads running each runnable Game Server
		aNAThread = new Thread(aNAGameServer);
		aEUThread = new Thread(aEUGameServer);
		aASThread = new Thread(aASGameServer);
		aNAThread.start();
		aEUThread.start();
		aASThread.start();
		// Start the UDP communication for replica A
		start();
	}

	@Override
	public void run ()
	{
		while(true)
			handleCommunication();
	}

	private void handleCommunication() 
	{
		// TODO : UDP listen to incoming requests from Leader and replica manager
	}
}
