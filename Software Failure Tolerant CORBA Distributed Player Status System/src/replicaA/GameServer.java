package replicaA;

import system.AbstractGameServer;

/**
 * This class is the GameServer class for replicaA
 * @author Mehrdad Dehdashti
 */
class GameServer extends AbstractGameServer
{
	private String aServerName;
	
	protected GameServer(String pServerName)
	{
		aServerName = pServerName;
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