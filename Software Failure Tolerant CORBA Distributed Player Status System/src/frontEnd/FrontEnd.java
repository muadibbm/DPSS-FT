package frontEnd;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

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
	private Logger aLog;
	private Queue <List<Object>> aQueue;
	private FrontEndORBThread aORBThread;
	private FrontEndUDPThread aUDPThread;
	
	public FrontEnd(String[] pArgs)
	{
		aLog = system.Log.createLog(Parameters.FE_NAME);
		aQueue = new LinkedList <List<Object>>();
		aLog.info("Queue initialized");
		aORBThread = new FrontEndORBThread(createORB(pArgs));
		aORBThread.start();
		aLog.info("ORB Running");
		aUDPThread = new FrontEndUDPThread(Parameters.UDP_PORT_FE);
		aUDPThread.start();
		aLog.info("UDP Running");
		handleFrontEndCommunication();
	}
	
	private FrontEnd()
	{
		// Private constructor used for creating ORB interface
	}
	
	/* Creates the ORB object for the front end and writes it to the file 
	 * @return the created ORB object */
	private ORB createORB(String[] pArgs)
	{
		ORB orb = null;
		try {
			// Initialize the ORB object
			orb = ORB.init(pArgs, null);
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			FrontEnd aInterface = new FrontEnd();
			byte [] id = rootPOA.activate_object(aInterface);
			// Obtain reference to CORBA object
			org.omg.CORBA.Object reference_CORBA = rootPOA.id_to_reference(id);
			// Write the CORBA object to a file
			String stringORB = orb.object_to_string(reference_CORBA);
			PrintWriter file = new PrintWriter(Parameters.FE_NAME + "_IOR.txt");
			file.print(stringORB);
			file.close();
			rootPOA.the_POAManager().activate();
			aLog.info("FE ORB init completed with file " + Parameters.FE_NAME + ".txt");
		} catch (InvalidName | ServantAlreadyActive | WrongPolicy | 
				ObjectNotActive | FileNotFoundException | AdapterInactive e) {
			aLog.info("ORB Creation Error: " + e.getMessage());
		}
		return orb;
	}
	
	/* Monitors the UDP object for crashes and in a case of a crash, restarts it
	 * Handles the CORBA requests from the clients and replica leader */
	private void handleFrontEndCommunication()
	{
		List<Object> tmpList;
		String IPAddress;
		while(true)
		{
			if(!aQueue.isEmpty())
			{
				tmpList = aQueue.remove();
				IPAddress = ((String)tmpList.get(6));
				if(IPAddress.length() >= 3 && IPAddress.substring(0,3).equals(Parameters.GeoLocationOfGameServerNA))
				{
					sendRequestToReplicaLeader(Parameters.UDP_PORT_REPLICA_LEAD_NA, tmpList);
				}
				else if(IPAddress.length() >= 2 && IPAddress.substring(0,2).equals(Parameters.GeoLocationOfGameServerEU))
				{
					sendRequestToReplicaLeader(Parameters.UDP_PORT_REPLICA_LEAD_EU, tmpList);
				}
				else if(IPAddress.length() >= 3 && IPAddress.substring(0,3).equals(Parameters.GeoLocationOfGameServerAS))
				{
					sendRequestToReplicaLeader(Parameters.UDP_PORT_REPLICA_LEAD_AS, tmpList);
				}
				else
				{
					aLog.info("Invalid GeoLocation");
					//TODO : return false; how to return for the client ?
				}
			}
			if(aUDPThread.hasCrashed())
			{
				aLog.info("Crash detected in UDP Thread, restarting UDP");
				aUDPThread = new FrontEndUDPThread(Parameters.UDP_PORT_FE);
				aUDPThread.start();
			}
		}
	}
	
	/* Sends pArguments through UDP using the given aPort */
	private void sendRequestToReplicaLeader(int aPort, List <Object> pArguments)
	{
		Parameters.METHOD_CODE methodCode = (Parameters.METHOD_CODE)pArguments.get(0);
		if(methodCode == Parameters.METHOD_CODE.CREATE_ACCOUNT)
		{
			// TODO
		}
		else if(methodCode == Parameters.METHOD_CODE.PLAYER_SIGN_IN)
		{
			// TODO
		}
		else if(methodCode == Parameters.METHOD_CODE.PLAYER_SIGN_OUT)
		{
			// TODO
		}
		else if(methodCode == Parameters.METHOD_CODE.TRANSFER_ACCOUNT)
		{
			// TODO
		}
		else if(methodCode == Parameters.METHOD_CODE.GET_PLAYER_STATUS)
		{
			// TODO
		}
		else if(methodCode == Parameters.METHOD_CODE.SUSPEND_ACCOUNT)
		{
			// TODO
		}
		methodCode = null;
	}

	@Override
	public boolean createPlayerAccount(String pFirstName, String pLastName, int pAge, String pUsername, String pPassword, String pIPAddress) 
	{
		List<Object> tmpList = new ArrayList<Object>();
		tmpList.add(Parameters.METHOD_CODE.CREATE_ACCOUNT); // index = 0
		tmpList.add(pFirstName); // index = 1
		tmpList.add(pLastName); // index = 2
		tmpList.add(pAge); // index = 3
		tmpList.add(pUsername); // index = 4
		tmpList.add(pPassword); // index = 5
		tmpList.add(pIPAddress); // index = 6
		aQueue.add(tmpList);
		tmpList = null;
		return false;
	}

	@Override
	public boolean playerSignIn(String pUsername, String pPassword, String pIPAddress) 
	{
		List<Object> tmpList = new ArrayList<Object>();
		tmpList.add(Parameters.METHOD_CODE.PLAYER_SIGN_IN); // index = 0
		tmpList.add(pUsername); // index = 1
		tmpList.add(pPassword); // index = 2
		tmpList.add(pIPAddress); // index = 3
		aQueue.add(tmpList);
		tmpList = null;
		return false;
	}

	@Override
	public boolean playerSignOut(String pUsername, String pIPAddress) 
	{
		List<Object> tmpList = new ArrayList<Object>();
		tmpList.add(Parameters.METHOD_CODE.PLAYER_SIGN_OUT); // index = 0
		tmpList.add(pUsername); // index = 1
		tmpList.add(pIPAddress); // index = 2
		aQueue.add(tmpList);
		tmpList = null;
		return false;
	}

	@Override
	public boolean transferAccount(String pUsername, String pPassword, String pOldIPAddress, String pNewIPAddress) 
	{
		List<Object> tmpList = new ArrayList<Object>();
		tmpList.add(Parameters.METHOD_CODE.TRANSFER_ACCOUNT); // index = 0
		tmpList.add(pUsername); // index = 1
		tmpList.add(pPassword); // index = 2
		tmpList.add(pOldIPAddress); // index = 3
		tmpList.add(pNewIPAddress); // index = 4
		aQueue.add(tmpList);
		tmpList = null;
		return false;
	}

	@Override
	public boolean getPlayerStatus(String pAdminUsername, String pAdminPassword, String pIPAddress) 
	{
		List<Object> tmpList = new ArrayList<Object>();
		tmpList.add(Parameters.METHOD_CODE.GET_PLAYER_STATUS); // index = 0
		tmpList.add(pAdminUsername); // index = 1
		tmpList.add(pAdminPassword); // index = 2
		tmpList.add(pIPAddress); // index = 3
		aQueue.add(tmpList);
		tmpList = null;
		return false;
	}

	@Override
	public boolean suspendAccount(String pAdminUsername, String pAdminPassword, String pIPAddress, String pUsernameToSuspend) 
	{
		List<Object> tmpList = new ArrayList<Object>();
		tmpList.add(Parameters.METHOD_CODE.SUSPEND_ACCOUNT ); // index = 0
		tmpList.add(pAdminUsername); // index = 1
		tmpList.add(pAdminPassword); // index = 2
		tmpList.add(pIPAddress); // index = 3
		tmpList.add(pUsernameToSuspend); // index = 4
		aQueue.add(tmpList);
		tmpList = null;
		return false;
	}
}
