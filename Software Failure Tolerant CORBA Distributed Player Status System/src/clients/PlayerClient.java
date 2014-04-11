package clients;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;

import dpss.interfaceIDL;
import dpss.interfaceIDLHelper;

/**
 * This is the PlayerClient class which operates the player
 * @author Mehrdad Dehdashti
 */
class PlayerClient extends Thread
{
	private Logger aLog;
	private interfaceIDL aInterfaceIDL;
	
	protected PlayerClient (String pName, String[] pArgs)
	{
		aLog = Log.createLog(pName);
		try 
		{
			ORB orb = ORB.init(pArgs, null);
			// Get the reference to the CORBA object from the file
			BufferedReader bufferedReader = new BufferedReader(new FileReader(Parameters.FE_NAME + "_IOR.txt"));
			String stringORB = bufferedReader.readLine();
			bufferedReader.close();
			// Transform the reference string to CORBA object
			org.omg.CORBA.Object reference_CORBA = orb.string_to_object(stringORB);
			aInterfaceIDL = interfaceIDLHelper.narrow(reference_CORBA);
			aLog.info("ORB IOR read from file");
		} catch (Exception e) {
			aLog.info("Acquiring ORB failed");
		}
	}
	
	/** Client invocation of FE operation createPlayerAccount using CORBA */
	protected void createPlayerAccount(String pFirstName, String pLastName, int pAge, String pUsername, String pPassword, String pIPAddress)
	{
		if(aInterfaceIDL.createPlayerAccount(pFirstName, pLastName, pAge, pUsername, pPassword, pIPAddress))
			aLog.info("Player Account created :\nFirstName \"" +  pFirstName +  "\", LastName \"" +  pLastName + 
					"\", Age \"" +  pAge +  "\", Username \"" +  pUsername +  "\", Password \"" + pPassword + "\", IP-address \"" + 
					pIPAddress + "\", Player is currently Offline");
		else
			aLog.info("CreatePlayerAccount Failed");
	}
	
	/** Client invocation of FE operation playerSignIn using CORBA */
	protected void playerSignIn(String pUsername, String pPassword, String pIPAddress)
	{
		if(aInterfaceIDL.playerSignIn(pUsername, pPassword, pIPAddress))
			aLog.info("Player Sign in : Player " +  pUsername + " Is Now Online");
		else
			aLog.info("PlayerSignIn Failed");
	}
	
	/** Client invocation of FE operation playerSignOut using CORBA  */
	protected void playerSignOut(String pUsername, String pIPAddress)
	{
		if(aInterfaceIDL.playerSignOut(pUsername, pIPAddress))
			aLog.info("Player Sign out : Player " +  pUsername + " Is Now Offline");
		else
			aLog.info("PlayerSignOut Failed");
	}
	
	/** Client invocation of FE operation transferAccount using CORBA  */
	protected void transferAccount(String pUsername,  String pPassword, String pOldIPAddress, String pNewIPAddress)
	{
		if(aInterfaceIDL.transferAccount(pUsername, pPassword, pOldIPAddress, pNewIPAddress))
			aLog.info("Player Account Transferred :\nUsername \"" +  pUsername +  "\", Password \"" + pPassword + "\", IP-address \"" + pNewIPAddress + "\"");
		else
			aLog.info("TransferAccount Failed");
	}
}
