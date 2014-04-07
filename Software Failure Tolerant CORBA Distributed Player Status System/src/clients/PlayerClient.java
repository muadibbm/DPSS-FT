package clients;

import java.io.BufferedReader;
import java.io.FileReader;

import org.omg.CORBA.ORB;

import system.Parameters;
import dpss.interfaceIDL;
import dpss.interfaceIDLHelper;

/**
 * This is the PlayerClient class which operates the player
 * @author Mehrdad Dehdashti
 */
public class PlayerClient extends Thread
{
	private interfaceIDL aInterfaceIDL;
	
	public PlayerClient (String pName, String[] pArgs)
	{
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
			// TODO : log
		} catch (Exception e) {
			// TODO : log error
		}
	}
	
	/** Client invocation of FE operation createPlayerAccount using CORBA */
	public void createPlayerAccount(String pFirstName, String pLastName, int pAge, String pUsername, String pPassword, String pIPAddress)
	{
		aInterfaceIDL.createPlayerAccount(pFirstName, pLastName, pAge, pUsername, pPassword, pIPAddress);
		// TODO : if log else log
	}
	
	/** Client invocation of FE operation playerSignIn using CORBA */
	public void playerSignIn(String pUsername, String pPassword, String pIPAddress)
	{
		aInterfaceIDL.playerSignIn(pUsername, pPassword, pIPAddress);
		// TODO : if log else log
	}
	
	/** Client invocation of FE operation playerSignOut using CORBA  */
	public void playerSignOut(String pUsername, String pIPAddress)
	{
		aInterfaceIDL.playerSignOut(pUsername, pIPAddress);
		// TODO : if log else log
	}
	
	/** Client invocation of FE operation transferAccount using CORBA  */
	public void transferAccount(String pUsername,  String pPassword, String pOldIPAddress, String pNewIPAddress)
	{
		aInterfaceIDL.transferAccount(pUsername, pPassword, pOldIPAddress, pNewIPAddress);
		// TODO : if log else log
	}
}
