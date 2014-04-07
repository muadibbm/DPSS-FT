package clients;

import java.io.BufferedReader;
import java.io.FileReader;

import org.omg.CORBA.ORB;

import system.Parameters;
import dpss.interfaceIDL;
import dpss.interfaceIDLHelper;

/**
 * This is the AdministratorClient class which operates the administrator
 * @author Mehrdad Dehdashti
 */
public class AdministratorClient extends Thread
{
	private interfaceIDL aInterfaceIDL;
	
	public AdministratorClient (String pName, String[] pArgs)
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
	
	/** Client invocation of FE operation getPlayerStatus using CORBA  */
	public void getPlayerStatus(String pAdminUsername, String pAdminPassword, String pIPAddress)
	{
		aInterfaceIDL.getPlayerStatus(pAdminUsername, pAdminPassword, pIPAddress);
		// TODO : if log else log
	}
	
	/** Client invocation of FE operation suspendAccount using CORBA  */
	public void suspendAccount(String pAdminUsername, String pAdminPassword, String pIPAddress, String pUsernameToSuspend)
	{
		aInterfaceIDL.suspendAccount(pAdminUsername, pAdminPassword, pIPAddress, pUsernameToSuspend);
		// TODO : if log else log
	}
}
