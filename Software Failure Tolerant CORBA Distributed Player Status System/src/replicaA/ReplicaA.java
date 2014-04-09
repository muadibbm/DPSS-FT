package replicaA;

import java.net.SocketException;

import system.Parameters;

/**
 * This class consists of three UDP threads each running a game server
 * @author Mehrdad Dehdashti
 */
public class ReplicaA 
{
	private static UDPThread aUDPThreadForGameServerNA;
	private static UDPThread aUDPThreadForGameServerEU;
	private static UDPThread aUDPThreadForGameServerAS;
	
	public ReplicaA()
	{
		// Create UDP Threads
		aUDPThreadForGameServerNA = new UDPThread(Parameters.UDP_PORT_REPLICA_A_NA);
		aUDPThreadForGameServerEU = new UDPThread(Parameters.UDP_PORT_REPLICA_A_EU);
		aUDPThreadForGameServerAS = new UDPThread(Parameters.UDP_PORT_REPLICA_A_AS);
		// Create Game Servers within each thread
		aUDPThreadForGameServerNA.startGameServer("ReplicaA_NA");
		aUDPThreadForGameServerEU.startGameServer("ReplicaA_EU");
		aUDPThreadForGameServerAS.startGameServer("ReplicaA_AS");
		// Start the communication
		aUDPThreadForGameServerNA.start();
		aUDPThreadForGameServerEU.start();
		aUDPThreadForGameServerAS.start();
	}
}