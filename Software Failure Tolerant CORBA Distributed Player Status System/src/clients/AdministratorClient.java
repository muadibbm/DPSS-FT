package clients;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;

import replicaA.interfaceIDL;
import replicaA.interfaceIDLHelper;

/**
 * This is the AdministratorClient class which operates the administrator
 * @author Mehrdad Dehdashti
 */
class AdministratorClient extends Thread
{
	private Logger aLog;
	private interfaceIDL aInterfaceIDL;
	
	protected AdministratorClient (String pName, String[] pArgs)
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
	
	/** Client invocation of FE operation getPlayerStatus using CORBA  */
	protected void getPlayerStatus(String pAdminUsername, String pAdminPassword, String pIPAddress)
	{
		aLog.info(aInterfaceIDL.getPlayerStatus(pAdminUsername, pAdminPassword, pIPAddress));
	}
	
	/** Client invocation of FE operation suspendAccount using CORBA  */
	protected void suspendAccount(String pAdminUsername, String pAdminPassword, String pIPAddress, String pUsernameToSuspend)
	{
		if(aInterfaceIDL.suspendAccount(pAdminUsername, pAdminPassword, pIPAddress, pUsernameToSuspend))
			aLog.info("Player Account suspended :\nUsername \"" +  pUsernameToSuspend);
		else
			aLog.info("SuspendAccount Failed");
	}
}