package replicaA;

/**
 * This class contains all the static variables used throughout the system.
 */
class Parameters 
{
	protected static int UDP_PORT_REPLICA_A = 2000;
	protected static int UDP_PORT_REPLICA_A_NA = 2100;
	protected static int UDP_PORT_REPLICA_A_EU = 2200;
	protected static int UDP_PORT_REPLICA_A_AS = 2300;	
	
	protected static int UDP_PORT_REPLICA_LEAD = 4000;
	
	protected static int UDP_PORT_REPLICA_LEAD_MULTICAST = 4500;
	
	protected static String UDP_ADDR_REPLICA_COMMUNICATION_MULTICAST = "224.0.0.2";
	
	protected static int UDP_PORT_REPLICA_MANAGER = 5000;
	
	protected static int UDP_PORT_FE = 6000;
	
	protected static enum METHOD_CODE 
	{
	    CREATE_ACCOUNT, PLAYER_SIGN_IN, PLAYER_SIGN_OUT, TRANSFER_ACCOUNT,
	    GET_PLAYER_STATUS, SUSPEND_ACCOUNT,
	    RESTART_REPLICA;
	}
	
	protected static int UDP_BUFFER_SIZE = 10000;
	
	protected static String UDP_PARSER = "/";
	
	protected static String LR_NAME = "LR";
	protected static String RM_NAME = "RM";
	protected static String RA_NAME = "RA";
	protected static String RA_NA_NAME = "ReplicaA_NA";
	protected static String RA_EU_NAME = "ReplicaA_EU";
	protected static String RA_AS_NAME = "ReplicaA_AS";
	
	protected static String GeoLocationOfGameServerNA = "132";
	protected static String GeoLocationOfGameServerEU = "93";
	protected static String GeoLocationOfGameServerAS = "182";
	
	// For validating IPAddress using regualr expressions
	public static final String PATTERN_FOR_IPADDRESS_VALIDATION = 
		        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
}