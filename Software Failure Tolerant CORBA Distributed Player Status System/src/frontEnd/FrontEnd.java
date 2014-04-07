package frontEnd;

import java.io.FileNotFoundException; 
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import dpss.interfaceIDLPOA;
import system.Parameters;

/**
 * This is the CORBA front end which handles all relay request from the clients to the 
 * replica lead and vice-versa using a queue data structure
 * @author Mehrdad Dehdashti
 */
public class FrontEnd extends interfaceIDLPOA
{
	private static FrontEnd aFE;
	
	private String aServerName;
	private Queue <Integer> aQueue;
	private FrontEndORBThread aORBThread;
	private FrontEndUDPThread aUDPThread; 
	
	private FrontEnd(String pServerName, String[] pArgs)
	{
		aServerName = pServerName;
		aQueue = new LinkedList <Integer>();
		aORBThread = new FrontEndORBThread(createORB(pArgs));
		aUDPThread = new FrontEndUDPThread(Parameters.UDP_PORT_FE);
		aUDPThread.start();
		handleUDPThread();
	}
		
	public static void main (String [] args)
	{
		aFE = new FrontEnd("FE", args);
	}
	
	/* Creates the ORB object for the front end and writes it to the file 
	 * @return the created ORB object */
	private ORB createORB(String[] pArgs)
	{
		try {
			// Initialize the ORB object
			ORB orb = ORB.init(pArgs, null);
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			FrontEnd aSampleInterface = new FrontEnd(aServerName, pArgs);
			byte [] id = rootPOA.activate_object(aSampleInterface);
			// Obtain reference to CORBA object
			org.omg.CORBA.Object reference_CORBA = rootPOA.id_to_reference(id);
			// Write the CORBA object to a file
			String stringORB = orb.object_to_string(reference_CORBA);
			PrintWriter file = new PrintWriter(aServerName + "_IOR.txt");
			file.print(stringORB);
			file.close();
			rootPOA.the_POAManager().activate();
			// TODO log.info("Server ORB init completed with file " + aServerName + ".txt");
			return orb;
		} catch (InvalidName | ServantAlreadyActive | WrongPolicy | 
				ObjectNotActive | FileNotFoundException | AdapterInactive e) {
			// TODO log.info("ORB Creation Error: " + e.getMessage());
			return null;
		}
	}
	
	/* Monitors the UDP object for crashes and in a case of a crash, restarts it */
	private void handleUDPThread()
	{
		while(true)
		{
			if(aUDPThread.hasCrashed())
			{
				// TODO : log the crash from FE
				aUDPThread = new FrontEndUDPThread(Parameters.UDP_PORT_FE);
				aUDPThread.start();
			}
		}
	}

	@Override
	public boolean createPlayerAccount(String pFirstName, String pLastName, int pAge, String pUsername, String pPassword, String pIPAddress) 
	{
		return false;
	}

	@Override
	public boolean playerSignIn(String pUsername, String pPassword, String pIPAddress) 
	{
		return false;
	}

	@Override
	public boolean playerSignOut(String pUsername, String pIPAddress) 
	{
		return false;
	}

	@Override
	public boolean transferAccount(String pUsername, String pPassword, String pOldIPAddress, String pNewIPAddress) 
	{
		return false;
	}

	@Override
	public boolean getPlayerStatus(String pAdminUsername, String pAdminPassword, String pIPAddress) 
	{
		return false;
	}

	@Override
	public boolean suspendAccount(String pAdminUsername, String pAdminPassword, String pIPAddress, String pUsernameToSuspend) 
	{
		return false;
	}
}
