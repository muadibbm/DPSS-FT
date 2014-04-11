package replicaLeader;

public class Parameters 
{
	protected static int UDP_PORT_REPLICA_A = 2000;
	
	protected static int UDP_PORT_REPLICA_B = 3000;
	
	protected static int UDP_PORT_REPLICA_LEAD = 4000;
	
	protected static int UDP_PORT_REPLICA_MANAGER = 5000;
	
	protected static int UDP_PORT_FE = 6000;
	
	protected static enum METHOD_CODE 
	{
	    CREATE_ACCOUNT, PLAYER_SIGN_IN, PLAYER_SIGN_OUT, TRANSFER_ACCOUNT,
	    GET_PLAYER_STATUS, SUSPEND_ACCOUNT,
	    START_REPLICA, STOP_REPLICA;
	}
	
	protected static char UDP_PARSER = '.';
	
	protected static String GeoLocationOfGameServerNA = "132";
	protected static String GeoLocationOfGameServerEU = "93";
	protected static String GeoLocationOfGameServerAS = "182";
}
