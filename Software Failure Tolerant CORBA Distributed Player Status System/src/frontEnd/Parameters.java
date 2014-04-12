package frontEnd;

/**
 * This class contains all the static variables used throughout the system.
 */
class Parameters 
{
	protected static int UDP_PORT_REPLICA_LEAD = 4000;
	
	protected static int UDP_PORT_FE = 6000;
	
	protected static enum METHOD_CODE 
	{
	    CREATE_ACCOUNT, PLAYER_SIGN_IN, PLAYER_SIGN_OUT, TRANSFER_ACCOUNT,
	    GET_PLAYER_STATUS, SUSPEND_ACCOUNT,
	    START_REPLICA, STOP_REPLICA;
	}
	
	protected static int UDP_BUFFER_SIZE = 10000;
	
	protected static String UDP_PARSER = "/";
	
	protected static String FE_NAME = "FE";
	protected static String LR_NAME = "LR";
}
