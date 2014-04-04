package system;

/**
 * This class contains all the static variables used throughout the system.
 */
public class Parameters 
{
	public static int UDP_PORT_REPLICA_A_NA = 2000;
	public static int UDP_PORT_REPLICA_A_EU = 2100;
	public static int UDP_PORT_REPLICA_A_AS = 2200;
	
	public static int UDP_PORT_REPLICA_B_NA = 3000;
	public static int UDP_PORT_REPLICA_B_EU = 3100;
	public static int UDP_PORT_REPLICA_B_AS = 3200;
	
	public static int UDP_PORT_REPLICA_LEAD_NA = 4000;
	public static int UDP_PORT_REPLICA_LEAD_EU = 4100;
	public static int UDP_PORT_REPLICA_LEAD_AS = 4200;
	
	public static int UDP_PORT_REPLICA_MANAGER_NA = 5000;
	public static int UDP_PORT_REPLICA_MANAGER_EU = 5100;
	public static int UDP_PORT_REPLICA_MANAGER_AS = 5200;
	
	public static enum METHOD_CODE 
	{
	    CREATE_ACCOUNT, PLAYER_SIGN_IN, PLAYER_SIGN_OUT, TRANSFER_ACCOUNT,
	    GET_PLAYER_STATUS, SUSPEND_ACCOUNT,
	    START_REPLICA, STOP_REPLICA;
	}
	
	public static char UDP_PARSER = '.';
}
