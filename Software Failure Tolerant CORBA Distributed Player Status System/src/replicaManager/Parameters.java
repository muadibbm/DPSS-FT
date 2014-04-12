package replicaManager;

public class Parameters 
{
	protected static int UDP_PORT_REPLICA_A = 2000;
	
	protected static int UDP_PORT_REPLICA_B = 3000;
	
	protected static int UDP_PORT_REPLICA_LEAD = 4000;
	
	protected static int UDP_PORT_REPLICA_MANAGER = 5000;
	
	protected static enum METHOD_CODE 
	{
	    START_REPLICA, STOP_REPLICA;
	}
	
	protected static int UDP_BUFFER_SIZE = 10000;
	
	protected static String UDP_PARSER = "/";
}
