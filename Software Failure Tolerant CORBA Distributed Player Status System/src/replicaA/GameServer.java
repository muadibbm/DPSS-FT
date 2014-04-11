package replicaA;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import dpss.interfaceIDLPOA;

/**
 * This class is the GameServer class for replicaA
 * @author Mehrdad Dehdashti
 */
class GameServer extends interfaceIDLPOA implements Runnable
{
	private String aServerName;
	private Hashtable<String, List<Account>> aDatabase;
	private Logger aLog;
	
	protected GameServer(String pServerName)
	{
		aServerName = pServerName;
		aLog = Log.createLog(aServerName);
		aDatabase = new Hashtable<String, List<Account>>();
		for(char alphabet = 'A'; alphabet <= 'Z'; alphabet++)
			aDatabase.put(String.valueOf(alphabet), new ArrayList <Account>());
		aLog.info("Database initialized\n" + aDatabase.toString());
	}
	
	protected void freeServerResources()
	{
		
	}
	
	@Override
	public void run() 
	{
		
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
	public boolean getPlayerStatus(String pAdminUsername, String pAdminPassword, String pIPAddress) 
	{
		return false;
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