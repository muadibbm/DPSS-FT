package replicaA;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import system.AbstractGameServer;

/**
 * This class is the GameServer class for replicaA
 * @author Mehrdad Dehdashti
 */
class GameServer extends AbstractGameServer
{
	private String aServerName;
	private Hashtable<String, List<Account>> aDatabase;
	private Logger aLog;
	
	protected GameServer(String pServerName)
	{
		aServerName = pServerName;
		aLog = system.Log.createLog(aServerName);
		aDatabase = new Hashtable<String, List<Account>>();
		for(char alphabet = 'A'; alphabet <= 'Z'; alphabet++)
			aDatabase.put(String.valueOf(alphabet), new ArrayList <Account>());
		aLog.info("Database initialized\n" + aDatabase.toString());
	}
	
	protected void stopServer()
	{
		
	}
	
	@Override
	protected boolean createPlayerAccount(String pFirstName, String pLastName, int pAge, String pUsername, String pPassword, String pIPAddress) 
	{
		return false;
	}

	@Override
	protected boolean playerSignIn(String pUsername, String pPassword, String pIPAddress) 
	{
		return false;
	}

	@Override
	protected boolean playerSignOut(String pUsername, String pIPAddress) 
	{
		return false;
	}

	@Override
	protected boolean transferAccount(String pUsername, String pPassword, String pOldIPAddress, String pNewIPAddress) 
	{
		return false;
	}

	@Override
	protected boolean getPlayerStatus(String pAdminUsername, String pAdminPassword, String pIPAddress) 
	{
		return false;
	}

	@Override
	protected boolean suspendAccount(String pAdminUsername, String pAdminPassword, String pIPAddress, String pUsernameToSuspend) 
	{
		return false;
	}
}