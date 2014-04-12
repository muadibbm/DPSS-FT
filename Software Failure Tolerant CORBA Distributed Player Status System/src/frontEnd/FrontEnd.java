package frontEnd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
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

/**
 * This is the CORBA front end which handles all relay request from the clients to the 
 * replica lead and vice-versa using a queue data structure
 * @author Mehrdad Dehdashti
 */
public class FrontEnd extends interfaceIDLPOA implements Runnable
{
	private static Logger aLog;
	private static Queue <List<Object>> aQueue;
	private static boolean bMethodBeingProcessed; // Used to check if the leader has sent back the result
	private static boolean bConfirmation; // Used to check if the method invoked was a success or failure
	private FrontEndORBThread aORBThread;
	private FrontEndUDPThread aUDPThread;
	
	// Used for UDP communication between front end and replica leader
	private DatagramSocket sendSocket;
	private DatagramPacket requestToReplicaLeader;
	private byte [] message;
	private String data;
	private InetAddress host;
	
	// Main method which runs the front end
	public static void main(String[] args) 
	{
		new Thread(new FrontEnd(args)).start();
	}
	
	private FrontEnd(String[] pArgs)
	{
		aLog = Log.createLog(Parameters.FE_NAME);
		aQueue = new LinkedList <List<Object>>();
		aLog.info("Queue initialized");
		bMethodBeingProcessed = false;
		bConfirmation = false;
		try {
			sendSocket = new DatagramSocket();
			host = InetAddress.getByName("localhost");
		} catch (SocketException | UnknownHostException e) {
			aLog.info("Error creating send socket: " + e.getMessage());
		}
		aORBThread = new FrontEndORBThread(createORB(pArgs));
		aORBThread.start();
		aLog.info("ORB Running");
		aUDPThread = new FrontEndUDPThread(Parameters.UDP_PORT_FE);
		aUDPThread.start();
		aLog.info("UDP Running");
	}
	
	private FrontEnd()
	{
		// Constructor used for creating ORB interface
	}
	
	@Override
	public void run() 
	{
		handleFrontEndCommunication();
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
			aLog.info("FE ORB init completed with file " + Parameters.FE_NAME + "_IOR.txt");
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
		while(true)
		{
			if(!aQueue.isEmpty()) // Process requests one at a time
			{
				bMethodBeingProcessed = true;
				tmpList = aQueue.remove();
				sendRequestToReplicaLeader(tmpList);
			}

			if(aUDPThread.hasLeaderResponded())
				bMethodBeingProcessed = false;
			
			bConfirmation = aUDPThread.getLeaderConfirmation(); // Get response from replica leader
			
			if(aUDPThread.hasCrashed())
			{
				aLog.info("Crash detected in UDP Thread, restarting UDP");
				aUDPThread = new FrontEndUDPThread(Parameters.UDP_PORT_FE);
				aUDPThread.start();
			}
		}
	}
	
	/* Sends pArguments through UDP using the replica leader port */
	private void sendRequestToReplicaLeader(List <Object> pArguments)
	{
		
		Parameters.METHOD_CODE methodCode = (Parameters.METHOD_CODE)pArguments.get(0);
		if(methodCode == Parameters.METHOD_CODE.CREATE_ACCOUNT)
		{
			aLog.info("Sending request to replica leader for CREATE PLAYER ACCOUNT");
			try {	
				data = Parameters.FE_NAME + Parameters.UDP_PARSER +
						methodCode.toString() + Parameters.UDP_PARSER +
						pArguments.get(1) + Parameters.UDP_PARSER +
						pArguments.get(2) + Parameters.UDP_PARSER +
						pArguments.get(3) + Parameters.UDP_PARSER +
						pArguments.get(4) + Parameters.UDP_PARSER +
						pArguments.get(5) + Parameters.UDP_PARSER +
						pArguments.get(6) + Parameters.UDP_PARSER;
				message = data.getBytes();
				requestToReplicaLeader = new DatagramPacket(message, data.length(), host, Parameters.UDP_PORT_REPLICA_LEAD);
				sendSocket.send(requestToReplicaLeader);
			} catch (SocketException e) {
				aLog.info("SocketException : " + e.getMessage());
			} catch (IOException e) {
				aLog.info("IOException : " + e.getMessage());
			}
		}
		else if(methodCode == Parameters.METHOD_CODE.PLAYER_SIGN_IN)
		{
			aLog.info("Sending request to replica leader for PLAYER SIGN IN");
			try {	
				data = Parameters.FE_NAME + Parameters.UDP_PARSER +
						methodCode.toString() + Parameters.UDP_PARSER +
						pArguments.get(1) + Parameters.UDP_PARSER +
						pArguments.get(2) + Parameters.UDP_PARSER +
						pArguments.get(3) + Parameters.UDP_PARSER;
				message = data.getBytes();
				requestToReplicaLeader = new DatagramPacket(message, data.length(), host, Parameters.UDP_PORT_REPLICA_LEAD);
				sendSocket.send(requestToReplicaLeader);
			} catch (SocketException e) {
				aLog.info("SocketException : " + e.getMessage());
			} catch (IOException e) {
				aLog.info("IOException : " + e.getMessage());
			}
		}
		else if(methodCode == Parameters.METHOD_CODE.PLAYER_SIGN_OUT)
		{
			aLog.info("Sending request to replica leader for PLAYER SIGN OUT");
			try {	
				data = Parameters.FE_NAME + Parameters.UDP_PARSER +
						methodCode.toString() + Parameters.UDP_PARSER +
						pArguments.get(1) + Parameters.UDP_PARSER +
						pArguments.get(2) + Parameters.UDP_PARSER;
				message = data.getBytes();
				requestToReplicaLeader = new DatagramPacket(message, data.length(), host, Parameters.UDP_PORT_REPLICA_LEAD);
				sendSocket.send(requestToReplicaLeader);
			} catch (SocketException e) {
				aLog.info("SocketException : " + e.getMessage());
			} catch (IOException e) {
				aLog.info("IOException : " + e.getMessage());
			}
		}
		else if(methodCode == Parameters.METHOD_CODE.TRANSFER_ACCOUNT)
		{
			aLog.info("Sending request to replica leader for TRANSFER ACCOUNT");
			try {	
				data = Parameters.FE_NAME + Parameters.UDP_PARSER +
						methodCode.toString() + Parameters.UDP_PARSER +
						pArguments.get(1) + Parameters.UDP_PARSER +
						pArguments.get(2) + Parameters.UDP_PARSER +
						pArguments.get(3) + Parameters.UDP_PARSER +
						pArguments.get(4) + Parameters.UDP_PARSER;
				message = data.getBytes();
				requestToReplicaLeader = new DatagramPacket(message, data.length(), host, Parameters.UDP_PORT_REPLICA_LEAD);
				sendSocket.send(requestToReplicaLeader);
			} catch (SocketException e) {
				aLog.info("SocketException : " + e.getMessage());
			} catch (IOException e) {
				aLog.info("IOException : " + e.getMessage());
			}
		}
		else if(methodCode == Parameters.METHOD_CODE.GET_PLAYER_STATUS)
		{
			aLog.info("Sending request to replica leader for GET PLAYER STATUS");
			try {	
				data = Parameters.FE_NAME + Parameters.UDP_PARSER +
						methodCode.toString() + Parameters.UDP_PARSER +
						pArguments.get(1) + Parameters.UDP_PARSER +
						pArguments.get(2) + Parameters.UDP_PARSER +
						pArguments.get(3) + Parameters.UDP_PARSER;
				message = data.getBytes();
				requestToReplicaLeader = new DatagramPacket(message, data.length(), host, Parameters.UDP_PORT_REPLICA_LEAD);
				sendSocket.send(requestToReplicaLeader);
			} catch (SocketException e) {
				aLog.info("SocketException : " + e.getMessage());
			} catch (IOException e) {
				aLog.info("IOException : " + e.getMessage());
			}
		}
		else if(methodCode == Parameters.METHOD_CODE.SUSPEND_ACCOUNT)
		{
			aLog.info("Sending request to replica leader for SUSPEND ACCOUNT");
			try {	
				data = Parameters.FE_NAME + Parameters.UDP_PARSER +
						methodCode.toString() + Parameters.UDP_PARSER +
						pArguments.get(1) + Parameters.UDP_PARSER +
						pArguments.get(2) + Parameters.UDP_PARSER +
						pArguments.get(3) + Parameters.UDP_PARSER +
						pArguments.get(4) + Parameters.UDP_PARSER;
				message = data.getBytes();
				requestToReplicaLeader = new DatagramPacket(message, data.length(), host, Parameters.UDP_PORT_REPLICA_LEAD);
				sendSocket.send(requestToReplicaLeader);
			} catch (SocketException e) {
				aLog.info("SocketException : " + e.getMessage());
			} catch (IOException e) {
				aLog.info("IOException : " + e.getMessage());
			}
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
		while(bMethodBeingProcessed) { /* Wait for the leader to respond */  }
		aLog.info("Confirmation returned to client for CREATE PLAYER ACCOUNT");
		return bConfirmation;
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
		while(bMethodBeingProcessed) { /* Wait for the leader to respond */ }
		aLog.info("Confirmation returned to client for PLAYER SIGN IN");
		return bConfirmation;
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
		while(bMethodBeingProcessed) { /* Wait for the leader to respond */ }
		aLog.info("Confirmation returned to client for PLAYER SIGN OUT");
		return bConfirmation;
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
		while(bMethodBeingProcessed) { /* Wait for the leader to respond */ }
		aLog.info("Confirmation returned to client for TRANSFER ACCOUNT");
		return bConfirmation;
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
		while(bMethodBeingProcessed) { /* Wait for the leader to respond */ }
		aLog.info("Confirmation returned to client for GET PLAYER STATUS");
		return bConfirmation;
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
		while(bMethodBeingProcessed) { /* Wait for the leader to respond */ }
		aLog.info("Confirmation returned to client for SUSPEND ACCOUNT");
		return bConfirmation;
	}
}
