package replicaA;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * This class is the GameServer class for replicaA
 * @author Mehrdad Dehdashti
 */
class GameServer extends interfaceIDLPOA implements Runnable
{
	private String aServerName;
	private Hashtable<String, List<Account>> aDatabase;
	private Logger aLog;
	private ORBThread aORBThread;
	private UDPThread aUDPThread;
	private int aPortUDP;
	
	protected GameServer(String pServerName, String [] pArgs, int pPortUDP)
	{
		aServerName = pServerName;
		aPortUDP = pPortUDP;
		aLog = Log.createLog(aServerName);
		aDatabase = new Hashtable<String, List<Account>>();
		for(char alphabet = 'A'; alphabet <= 'Z'; alphabet++)
			aDatabase.put(String.valueOf(alphabet), new ArrayList <Account>());
		aLog.info("Database initialized\n" + aDatabase.toString());
		updateDatabase();
		aLog.info("Database is now up to date with most recent changes");
		aORBThread = new ORBThread(createORB(pArgs));
		aORBThread.start();
		aLog.info("ORB Running");
		aUDPThread = new UDPThread(aPortUDP);
		aUDPThread.start();
		aLog.info("UDP Running");
	}
	
	private GameServer()
	{
		// Constructor used for creating ORB interface
	}
	
	/* Reads from file containing the most recent player accounts */
	private void updateDatabase()
	{
		// TODO
	}
	
	/* Clears the database and frees any resources allocated for the game server */
	protected void freeServerResources()
	{
		// TODO
	}
	
	@Override
	public void run() 
	{
		if(aUDPThread.hasCrashed())
		{
			aLog.info("Crash detected in UDP Thread, restarting UDP");
			aUDPThread = new UDPThread(aPortUDP);
			aUDPThread.start();
		}
	}
	
	/* Creates the ORB object for the replica A and writes it to the file 
	 * @return the created ORB object */
	private ORB createORB(String[] pArgs)
	{
		ORB orb = null;
		try {
			// Initialize the ORB object
			orb = ORB.init(pArgs, null);
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			GameServer aInterface = new GameServer();
			byte [] id = rootPOA.activate_object(aInterface);
			// Obtain reference to CORBA object
			org.omg.CORBA.Object reference_CORBA = rootPOA.id_to_reference(id);
			// Write the CORBA object to a file
			String stringORB = orb.object_to_string(reference_CORBA);
			PrintWriter file = new PrintWriter(aServerName + "_IOR.txt");
			file.print(stringORB);
			file.close();
			rootPOA.the_POAManager().activate();
			aLog.info("ORB init completed with file " + aServerName + "_IOR.txt");
		} catch (InvalidName | ServantAlreadyActive | WrongPolicy | 
				ObjectNotActive | FileNotFoundException | AdapterInactive e) {
			aLog.info("ORB Creation Error: " + e.getMessage());
		}
		return orb;
	}
	
	/**
	 * This method when invoked from a PlayerClient will create a player account with the given parameters
	 * and returns the confirmation
	 * @param pFirstName
	 * @param pLastName
	 * @param pAge
	 * @param pUsername a minimum length of 6 characters and a maximum length of 15 characters
	 * @param pPassword a minimum length of 6 characters
	 * @param pIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @return confirmation in form of a boolean
	 */
	@Override
	public boolean createPlayerAccount(String pFirstName, String pLastName, int pAge, String pUsername, String pPassword, String pIPAddress) 
	{
		return false;
	}

	/**
	 * This method when invoked from a PlayerClient will set a player online if conditions are met 
	 * with the given parameters and returns the confirmation
	 * @param pUsername a minimum length of 6 characters and a maximum length of 15 characters
	 * @param pPassword a minimum length of 6 characters
	 * @param pIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @return confirmation in form of a boolean
	 */
	@Override
	public boolean playerSignIn(String pUsername, String pPassword, String pIPAddress) 
	{
		return false;
	}

	/**
	 * This method when invoked from a PlayerClient will set this player off-line if conditions are met
	 * with the given parameters and returns the confirmation
	 * @param pUsername a minimum length of 6 characters and a maximum length of 15 characters
	 * @param pIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @return confirmation in form of a boolean
	 */
	@Override
	public boolean playerSignOut(String pUsername, String pIPAddress) 
	{
		return false;
	}

	/**
	 * This method when invoked from a PlayerClient will transfer this player's account to the given server
	 * if conditions are met with the given parameters and returns the confirmation
	 * @param pUsername a minimum length of 6 characters and a maximum length of 15 characters
	 * @param pOldIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @param pNewIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @return confirmation in form of a boolean
	 */
	@Override
	public boolean transferAccount(String pUsername, String pPassword, String pOldIPAddress, String pNewIPAddress) 
	{
		return false;
	}

	/**
	 * This method when invoked from a AdministratorClient will return the number of players online and off-line
	 * @param pAdminUsername by default all administrators have username "Admin"
	 * @param pAdminPassword by default all administrators have password "Admin"
	 * @param pIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @return Confirmation in form of a boolean
	 */
	@Override
	public String getPlayerStatus(String pAdminUsername, String pAdminPassword, String pIPAddress) 
	{
		return "";
	}

	/**
	 * This method when invoked from a AdministratorClient will suspend the player account given
	 * @param pAdminUsername by default all administrators have username "Admin"
	 * @param pAdminPassword by default all administrators have password "Admin"
	 * @param pIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @param pUsernameToSuspend a minimum length of 6 characters and a maximum length of 15 characters
	 * @return Confirmation in form of a boolean
	 */
	@Override
	public boolean suspendAccount(String pAdminUsername, String pAdminPassword, String pIPAddress, String pUsernameToSuspend) 
	{
		return false;
	}
}