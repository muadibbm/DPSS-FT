package replicaA;

import system.AbstractGameServer;

public class GameServer extends AbstractGameServer
{

	@Override
	public String createPlayerAccount(String pFirstName, String pLastName, int pAge, String pUsername, String pPassword, String pIPAddress) 
	{
		return null;
	}

	@Override
	public String playerSignIn(String pUsername, String pPassword, String pIPAddress) 
	{
		return null;
	}

	@Override
	public String playerSignOut(String pUsername, String pIPAddress) 
	{
		return null;
	}

	@Override
	public String transferAccount(String pUsername, String pPassword, String pOldIPAddress, String pNewIPAddress) 
	{
		return null;
	}

	@Override
	public String getPlayerStatus(String pAdminUsername, String pAdminPassword, String pIPAddress) 
	{
		return null;
	}

	@Override
	public String suspendAccount(String pAdminUsername, String pAdminPassword, String pIPAddress, String pUsernameToSuspend) 
	{
		return null;
	}

}
