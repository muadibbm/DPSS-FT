package replicaB;

public class Parameters 
{
	protected static int UDP_PORT_REPLICA_A = 2000;
	
	protected static int UDP_PORT_REPLICA_B = 3000;
	
	protected static int UDP_PORT_REPLICA_LEAD = 4000;
	
	protected static int UDP_PORT_REPLICA_LEAD_MULTICAST = 4446;
	
	protected static String UDP_ADDR_REPLICA_COMMUNICATION_MULTICAST = "224.0.0.2";
	
	protected static int UDP_PORT_REPLICA_MANAGER = 5000;
	
	protected static int UDP_PORT_FE = 6000;
	
	protected static enum METHOD_CODE 
	{
	    CREATE_ACCOUNT
	    , PLAYER_SIGN_IN
	    , PLAYER_SIGN_OUT
	    , TRANSFER_ACCOUNT
	    , GET_PLAYER_STATUS
	    , SUSPEND_ACCOUNT
	    , RESTART_REPLICA;
	}
	
	protected static String UDP_PARSER = "/";
	
	protected static int UDP_BUFFER_SIZE = 10000;
	
	protected static String FE_NAME = "FE";
	protected static String RM_NAME = "RM"; 
	protected static String RA_NAME = "RA";
	protected static String RB_NAME = "RB";
	protected static String LR_NAME = "LR";
	
	protected static String GeoLocationOfGameServerNA = "132";
	protected static String GeoLocationOfGameServerEU = "93";
	protected static String GeoLocationOfGameServerAS = "182";
	
	protected static final int NA_IP_ADDRESS = 132;
	protected static final int EU_IP_ADDRESS = 93;
	protected static final int AS_IP_ADDRESS = 182;
	
	
	protected static int UDP_PORT_REPLICA_B_NA = 3001;
	protected static int UDP_PORT_REPLICA_B_EU = 3100;
	protected static int UDP_PORT_REPLICA_B_AS = 3200;
	
	protected static String SERVER_LOG_EXTENSION = "_REPLICA_B_LOG.txt";
	
	protected static String NA_SERVER_ACRO = "NA";
	protected static String EU_SERVER_ACRO = "EU";
	protected static String AS_SERVER_ACRO = "AS";
}
