package replicaA;

import system.Parameters;

public class UDPManager 
{
	private static UDPThread aUDPThreadForGameServerNA;
	private static UDPThread aUDPThreadForGameServerEU;
	private static UDPThread aUDPThreadForGameServerAS;
	
	public static void main(String[] args) 
	{
		aUDPThreadForGameServerNA = new UDPThread(Parameters.UDP_PORT_REPLICA_A_NA, "NA");
		aUDPThreadForGameServerEU = new UDPThread(Parameters.UDP_PORT_REPLICA_A_EU, "EU");
		aUDPThreadForGameServerAS = new UDPThread(Parameters.UDP_PORT_REPLICA_A_AS, "AS");
		
		aUDPThreadForGameServerNA.start();
		aUDPThreadForGameServerEU.start();
		aUDPThreadForGameServerAS.start();
	}
}