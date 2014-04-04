package replicaA;

import system.Parameters;

/*
 * This class consists of three UDP threads each running a game server
 * @author Mehrdad Dehdashti
 */
public class UDPManager 
{
	private static UDPThread aUDPThreadForGameServerNA;
	private static UDPThread aUDPThreadForGameServerEU;
	private static UDPThread aUDPThreadForGameServerAS;
	
	public static void main(String[] args) 
	{
		// Create UDP Threads
		aUDPThreadForGameServerNA = new UDPThread(Parameters.UDP_PORT_REPLICA_A_NA);
		aUDPThreadForGameServerEU = new UDPThread(Parameters.UDP_PORT_REPLICA_A_EU);
		aUDPThreadForGameServerAS = new UDPThread(Parameters.UDP_PORT_REPLICA_A_AS);
		// Create Game Servers within each thread
		aUDPThreadForGameServerNA.startGameServer("NA");
		aUDPThreadForGameServerEU.startGameServer("EU");
		aUDPThreadForGameServerAS.startGameServer("AS");
		// Start the communication
		aUDPThreadForGameServerNA.start();
		aUDPThreadForGameServerEU.start();
		aUDPThreadForGameServerAS.start();
	}
}