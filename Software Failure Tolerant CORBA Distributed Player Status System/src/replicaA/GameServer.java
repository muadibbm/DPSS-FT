package replicaA;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * This class is the GameServer class for replicaA
 * @author Mehrdad Dehdashti
 */
class GameServer extends interfaceIDLPOA implements Runnable
{
	private String aServerName;
	private static Hashtable<String, List<Account>> aDatabase;
	private static Logger aLog;
	private ORBThread aORBThread;
	private UDPThread aUDPThread;
	private int aPortUDP;
	
	protected GameServer(String pServerName, String [] pArgs, int pPortUDP)
	{
		aServerName = pServerName;
		aPortUDP = pPortUDP;
		aLog = Log.createLog(aServerName);
		aDatabase = new Hashtable<String, List<Account>>();
		for(char alphabet = 'A'; alphabet <= 'Z'; alphabet++)
			aDatabase.put(String.valueOf(alphabet), new ArrayList <Account>());
		aLog.info("Database initialized\n" + aDatabase.toString());
		aLog.info("Database is now up to date with most recent changes");
		aORBThread = new ORBThread(createORB(pArgs));
		aORBThread.start();
		aLog.info("ORB Running");
		aUDPThread = new UDPThread(this, aPortUDP);
		aUDPThread.start();
		aLog.info("UDP Running");
	}
	
	private GameServer()
	{
		// Constructor used for creating ORB interface
	}
	
	/* Clears the database and frees any resources allocated for the game server */
	protected void freeServerResources()
	{
		aDatabase.clear();
	}
	
	protected String getName()
	{
		return aServerName;
	}
	
	@Override
	public void run() 
	{
		if(aUDPThread.hasCrashed())
		{
			aLog.info("Crash detected in UDP Thread, restarting UDP");
			aUDPThread = new UDPThread(this, aPortUDP);
			aUDPThread.start();
		}
	}
	
	/* Creates the ORB object for the replica A and writes it to the file 
	 * @return the created ORB object */
	private ORB createORB(String[] pArgs)
	{
		ORB orb = null;
		try {
			// Initialize the ORB object
			orb = ORB.init(pArgs, null);
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			GameServer aInterface = new GameServer();
			byte [] id = rootPOA.activate_object(aInterface);
			// Obtain reference to CORBA object
			org.omg.CORBA.Object reference_CORBA = rootPOA.id_to_reference(id);
			// Write the CORBA object to a file
			String stringORB = orb.object_to_string(reference_CORBA);
			PrintWriter file = new PrintWriter(aServerName + "_IOR.txt");
			file.print(stringORB);
			file.close();
			rootPOA.the_POAManager().activate();
			aLog.info("ORB init completed with file " + aServerName + "_IOR.txt");
		} catch (InvalidName | ServantAlreadyActive | WrongPolicy | 
				ObjectNotActive | FileNotFoundException | AdapterInactive e) {
			aLog.info("ORB Creation Error: " + e.getMessage());
		}
		return orb;
	}
	
	/**
	 * This method when invoked from a PlayerClient will create a player account with the given parameters
	 * and returns the confirmation
	 * @param pFirstName
	 * @param pLastName
	 * @param pAge
	 * @param pUsername a minimum length of 6 characters and a maximum length of 15 characters
	 * @param pPassword a minimum length of 6 characters
	 * @param pIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @return confirmation in form of a boolean
	 */
	@Override
	public boolean createPlayerAccount(String pFirstName, String pLastName, int pAge, String pUsername, String pPassword, String pIPAddress) 
	{
			if(pAge <= 0)
			{
				aLog.info("Error Creating Player Account : Invalid Age");
				return false;
			}
			if(pUsername.length() < 6)
			{
				aLog.info("Error Creating Player Account : Username has to be at least 6 characters");
				return false;
			}
			if(pUsername.length() > 15)
			{
				aLog.info("Error Creating Player Account : Username has to be at most 15 characters");
				return false;
			}
			if((pUsername.charAt(0) > 'Z' && pUsername.charAt(0) < 'a') || pUsername.charAt(0) < 'A' || pUsername.charAt(0) > 'z')
			{
				aLog.info("Error Creating Player Account : Username cannot start with " + pUsername.charAt(0));
				return false;
			}
			if(getAccount(pUsername) != null)
			{
				aLog.info("Error Creating Player Account : Username " + pUsername + " Already Exists");
				return false;
			}
			if(pPassword.length() < 6)
			{
				aLog.info("Error Creating Player Account : Password has to be at least 6 characters");
				return false;
			}
			if(!validate(pIPAddress))
			{
				aLog.info("Error Creating Player Account : Invalid IP-address");
				return false;
			}
			if(IPAddressExists(pIPAddress))
			{
				aLog.info("Error Creating Player Account : IP-address Already Exists");
				return false;
			}
			synchronized(this)
			{
				aDatabase.get(String.valueOf(pUsername.toUpperCase().charAt(0))).add(new Account(pFirstName, pLastName, pAge, pUsername, pPassword, pIPAddress));
			}
			aLog.info("Player Account created :\nFirstName \"" +  pFirstName +  "\", LastName \"" +  pLastName + 
					"\", Age \"" +  pAge +  "\", Username \"" +  pUsername +  "\", Password \"" + pPassword + "\", IP-address \"" +
					pIPAddress + "\", Player is currently Offline");
			return true;
	}

	/**
	 * This method when invoked from a PlayerClient will set a player online if conditions are met 
	 * with the given parameters and returns the confirmation
	 * @param pUsername a minimum length of 6 characters and a maximum length of 15 characters
	 * @param pPassword a minimum length of 6 characters
	 * @param pIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @return confirmation in form of a boolean
	 */
	@Override
	public boolean playerSignIn(String pUsername, String pPassword, String pIPAddress) 
	{
		if((pUsername.charAt(0) > 'Z' && pUsername.charAt(0) < 'a') || pUsername.charAt(0) < 'A' || pUsername.charAt(0) > 'z')
		{
			aLog.info("Error Player Sign in : Username cannot start with " + pUsername.charAt(0));
			return false;
		}
		Account tmpAccount = getAccount(pUsername);
		if(tmpAccount == null)
		{
			aLog.info("Error Player Sign in : Username Was Not Found");
			tmpAccount = null;
			return false;
		}
		if(!tmpAccount.getPassword().equals(pPassword))
		{
			aLog.info("Error Player Sign in : Incorrect Password");
			tmpAccount = null;
			return false;
		}
		if(!validate(pIPAddress))
		{
			aLog.info("Error Player Sign in : Invalid IP-address");
			tmpAccount = null;
			return false;
		}
		if(!tmpAccount.getIPAddress().equals(pIPAddress))
		{
			aLog.info("Error Player Sign in : Nonmatching IP-address");
			tmpAccount = null;
			return false;
		}
		if(!tmpAccount.isOnline())
		{
			tmpAccount.setOnline(true);
			tmpAccount = null;
			aLog.info("Player Sign in : Player " +  pUsername + " Is Now Online");
			return true;
		}
		else
		{
			aLog.info("Error Player Sign in : Player " + pUsername + " Is Already Online");
			tmpAccount = null;
			return false;
		}
	}

	/**
	 * This method when invoked from a PlayerClient will set this player off-line if conditions are met
	 * with the given parameters and returns the confirmation
	 * @param pUsername a minimum length of 6 characters and a maximum length of 15 characters
	 * @param pIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @return confirmation in form of a boolean
	 */
	@Override
	public boolean playerSignOut(String pUsername, String pIPAddress) 
	{
		if((pUsername.charAt(0) > 'Z' && pUsername.charAt(0) < 'a') || pUsername.charAt(0) < 'A' || pUsername.charAt(0) > 'z')
		{
			aLog.info("Error Player Sign out : Username cannot start with " + pUsername.charAt(0));
			return false;
		}
		Account tmpAccount = getAccount(pUsername);
		if(tmpAccount == null)
		{
			aLog.info("Error Player Sign out : Username Was Not Found");
			tmpAccount = null;
			return false;
		}
		if(!validate(pIPAddress))
		{
			aLog.info("Error Player Sign out : Invalid IP-address");
			tmpAccount = null;
			return false;
		}
		if(!tmpAccount.getIPAddress().equals(pIPAddress))
		{
			aLog.info("Error Player Sign out : Nonmatching IP-address");
			tmpAccount = null;
			return false;
		}
		if(tmpAccount.isOnline())
		{
			tmpAccount.setOnline(false);
			tmpAccount = null;
			aLog.info("Player Sign out : Player " +  pUsername + " Is Now Offline");
			return true;
		}
		else
		{
			aLog.info("Error Player Sign out : Player " + pUsername + " Is Already Offline");
			tmpAccount = null;
			return false;
		}
	}

	/**
	 * This method when invoked from a PlayerClient will transfer this player's account to the given server
	 * if conditions are met with the given parameters and returns the confirmation
	 * @param pUsername a minimum length of 6 characters and a maximum length of 15 characters
	 * @param pOldIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @param pNewIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @return confirmation in form of a boolean
	 */
	@Override
	public boolean transferAccount(String pUsername, String pPassword, String pOldIPAddress, String pNewIPAddress) 
	{
		if((pUsername.charAt(0) > 'Z' && pUsername.charAt(0) < 'a') || pUsername.charAt(0) < 'A' || pUsername.charAt(0) > 'z')
		{
			aLog.info("Error Transferring Account : Username cannot start with " + pUsername.charAt(0));
			return false;
		}
		Account tmpAccount = getAccount(pUsername);
		if(tmpAccount == null)
		{
			aLog.info("Error Transferring Account : Username Was Not Found");
			tmpAccount = null;
			return false;
		}
		if(!tmpAccount.getPassword().equals(pPassword))
		{
			aLog.info("Error Transferring Account : Incorrect Password");
			tmpAccount = null;
			return false;
		}
		if(!validate(pOldIPAddress))
		{
			aLog.info("Error Transferring Account : Invalid IP-address");
			tmpAccount = null;
			return false;
		}
		if(!tmpAccount.getIPAddress().equals(pOldIPAddress))
		{
			aLog.info("Error Transferring Account : Nonmatching IP-address");
			tmpAccount = null;
			return false;
		}
		if(!validate(pNewIPAddress))
		{
			aLog.info("Error Transferring Account : Invalid New IP-address");
			tmpAccount = null;
			return false;
		}
			synchronized(this)
			{
				if(aDatabase.get(String.valueOf(pUsername.toUpperCase().charAt(0))).remove(tmpAccount))
				{
					aLog.info("Player Account Deleted :\nUsername \"" +  pUsername +  "\", Password \"" + pPassword + "\", IP-address \"" + pOldIPAddress + "\"");
					DatagramSocket datagramSocket;
					DatagramPacket requestToTransfer;
					byte [] message;
					String data;
					int aPortNumber;
					// Send the request to transfer to the geo location specified by the given pNewIPAddress
					if(pNewIPAddress.length() >= 3 && pNewIPAddress.substring(0,3).equals(Parameters.GeoLocationOfGameServerNA))
						aPortNumber = Parameters.UDP_PORT_REPLICA_A_NA;
					else if(pNewIPAddress.length() >= 2 && pNewIPAddress.substring(0,2).equals(Parameters.GeoLocationOfGameServerEU))
						aPortNumber = Parameters.UDP_PORT_REPLICA_A_EU;
					else if(pNewIPAddress.length() >= 3 && pNewIPAddress.substring(0,3).equals(Parameters.GeoLocationOfGameServerAS))
						aPortNumber = Parameters.UDP_PORT_REPLICA_A_AS;
					else
					{
						aLog.info("Error Transferring Account : Invalid GeoLocation");
						return false;
					}
					try 
					{
						datagramSocket = new DatagramSocket();
						data = Parameters.METHOD_CODE.TRANSFER_ACCOUNT.name() + Parameters.UDP_PARSER +
									tmpAccount.getFirstName() + Parameters.UDP_PARSER +
									tmpAccount.getLastName() + Parameters.UDP_PARSER +
									Integer.toString(tmpAccount.getAge()) + Parameters.UDP_PARSER +
									pUsername + Parameters.UDP_PARSER +
									pPassword + Parameters.UDP_PARSER +
									pNewIPAddress;
						message = data.getBytes();
						requestToTransfer = new DatagramPacket(message, message.length, InetAddress.getByName("localhost"), aPortNumber);
						datagramSocket.send(requestToTransfer);
					} catch (SocketException e) {
						aLog.info("Error Transferring Account : removal faild");
						return false;
					} catch (IOException e) {
						aLog.info("Error Transferring Account : removal faild");
						return false;
					}
				}
			}		
			aLog.info("Player Account Transferred :\nUsername \"" +  pUsername +  "\", Password \"" + pPassword + "\", IP-address \"" + pNewIPAddress + "\"");
			return true;
	}

	/**
	 * This method when invoked from a AdministratorClient will return the number of players online and off-line
	 * @param pAdminUsername by default all administrators have username "Admin"
	 * @param pAdminPassword by default all administrators have password "Admin"
	 * @param pIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @return Confirmation in form of a boolean
	 */
	@Override
	public String getPlayerStatus(String pAdminUsername, String pAdminPassword, String pIPAddress) 
	{
		if(!pAdminUsername.equals("Admin"))
		{
			aLog.info("Error Getting Player Status : Incorrect Administrator Username");
			return "0";
		}
		if(!pAdminPassword.equals("Admin"))
		{
			aLog.info("Error Getting Player Status : Incorrect Administrator Password");
			return "0";
		}
		if(!validate(pIPAddress))
		{
			aLog.info("Error Getting Player Status : Invalid IP-address");
			return "0";
		}
		int portNumber1 = 0;
		int portNumber2 = 0;
		if(aServerName.equals(Parameters.RA_NA_NAME))
		{
			portNumber1 = Parameters.UDP_PORT_REPLICA_A_EU;
			portNumber2 = Parameters.UDP_PORT_REPLICA_A_AS;
		}
		else if(aServerName.equals(Parameters.RA_EU_NAME))
		{
			portNumber1 = Parameters.UDP_PORT_REPLICA_A_NA;
			portNumber2 = Parameters.UDP_PORT_REPLICA_A_AS;	
		}
		else if(aServerName.equals(Parameters.RA_AS_NAME))
		{
			portNumber1 = Parameters.UDP_PORT_REPLICA_A_EU;
			portNumber2 = Parameters.UDP_PORT_REPLICA_A_NA;	
		}
		DatagramSocket datagramSocket;
		DatagramPacket requestToGetPlayerStatus;
		DatagramPacket reply;
		String repliedMessage1;
		String repliedMessage2;
		byte [] message;
		String data;
		InetAddress host;
		try 
		{
			datagramSocket = new DatagramSocket();
			host = InetAddress.getByName("localhost");
			data = Parameters.METHOD_CODE.GET_PLAYER_STATUS.name();
			message = data.getBytes();
			requestToGetPlayerStatus = new DatagramPacket(message, message.length, host, portNumber1);
			datagramSocket.send(requestToGetPlayerStatus);
			
			message = new byte [1000];
			reply = new DatagramPacket(message, message.length);
			datagramSocket.receive(reply);
			repliedMessage1 = new String(reply.getData());
				
			datagramSocket = new DatagramSocket();
			host = InetAddress.getByName("localhost");
			data = Parameters.METHOD_CODE.GET_PLAYER_STATUS.name();
			message = data.getBytes();
			requestToGetPlayerStatus = new DatagramPacket(message, message.length, host, portNumber2);
			datagramSocket.send(requestToGetPlayerStatus);
				
			message = new byte [1000];
			reply = new DatagramPacket(message, message.length);
			datagramSocket.receive(reply);
			repliedMessage2 = new String(reply.getData());
			
		} catch (SocketException e) {
			return "0";
		} catch (IOException e) {
			return "0";
		}
		aLog.info("Get Player Status : " + aServerName + ": " + getNumberOfOnlinePlayer() + " online, " + getNumberOfOfflinePlayer() + 
				" offline. \n" + repliedMessage1 + "\n" + repliedMessage2);
		return "1" + Parameters.UDP_PARSER +
				aServerName + Parameters.UDP_PARSER + getNumberOfOnlinePlayer() + Parameters.UDP_PARSER + getNumberOfOfflinePlayer() + Parameters.UDP_PARSER +
				repliedMessage1 + Parameters.UDP_PARSER + 
				repliedMessage2;
	}

	/**
	 * This method when invoked from a AdministratorClient will suspend the player account given
	 * @param pAdminUsername by default all administrators have username "Admin"
	 * @param pAdminPassword by default all administrators have password "Admin"
	 * @param pIPAddress 132.xxx.xxx.xxx for NA, 93.xxx.xxx.xxx for EU, 182.xxx.xxx.xxx for AS
	 * @param pUsernameToSuspend a minimum length of 6 characters and a maximum length of 15 characters
	 * @return Confirmation in form of a boolean
	 */
	@Override
	public boolean suspendAccount(String pAdminUsername, String pAdminPassword, String pIPAddress, String pUsernameToSuspend) 
	{
		if(!pAdminUsername.equals("Admin"))
		{
			aLog.info("Error Suspending Account : Incorrect Administrator Username");
			return false;
		}
		if(!pAdminPassword.equals("Admin"))
		{
			aLog.info("Error Suspending Account : Incorrect Administrator Password");
			return false;
		}
		if(!validate(pIPAddress))
		{
			aLog.info("Error Suspending Account : Invalid IP-address");
			return false;
		}
		if((pUsernameToSuspend.charAt(0) > 'Z' && pUsernameToSuspend.charAt(0) < 'a') || pUsernameToSuspend.charAt(0) < 'A' || pUsernameToSuspend.charAt(0) > 'z')
		{
			aLog.info("Error Suspending Account : Username cannot start with " + pUsernameToSuspend.charAt(0));
			return false;
		}
		Account tmpAccount = getAccount(pUsernameToSuspend);
		if(tmpAccount == null)
		{
			aLog.info("Error Suspending Account : Username Was Not Found");
			tmpAccount = null;
			return false;
		}

		boolean bSuccess = false;
		synchronized(this)
		{
			bSuccess = aDatabase.get(String.valueOf(pUsernameToSuspend.toUpperCase().charAt(0))).remove(tmpAccount);
		}
		if(bSuccess)
		{
			aLog.info("Player Account suspended :\nUsername \"" +  pUsernameToSuspend +  "\", Password \"" + tmpAccount.getPassword() + "\", IP-address \"" + tmpAccount.getIPAddress() + "\"");
			return true;
		}
		else
		{
			aLog.info("Error Suspending Account : suspension faild");
			return false;
		}
	}
	
	// returns the number of current online players in this server
	protected int getNumberOfOnlinePlayer()
	{
		List <Account> tmpList;
		Enumeration<List<Account>> enumeration = aDatabase.elements();
		int numberOfOnlinePlayers = 0;
		while(enumeration.hasMoreElements())
		{
			tmpList = enumeration.nextElement();
			for (int index = 0; index < tmpList.size(); index++)
				if(tmpList.get(index).isOnline())
					numberOfOnlinePlayers++;
		}
		return numberOfOnlinePlayers;
	}

	// returns the number of current offline players in this server
	protected int getNumberOfOfflinePlayer()
	{
		List <Account> tmpList;
		Enumeration<List<Account>> enumeration = aDatabase.elements();
		int numberOfOfflinePlayers = 0;
		while(enumeration.hasMoreElements())
		{
			tmpList = enumeration.nextElement();
			for (int index = 0; index < tmpList.size(); index++)
				if(!tmpList.get(index).isOnline())
					numberOfOfflinePlayers++;
		}
		return numberOfOfflinePlayers;
	}

		// Returns the account with the given pUsername, returns null if pUsername does not exist
		private Account getAccount(String pUsername)
		{
			String tmpUpperCaseUsername = pUsername.toUpperCase();
			List <Account> tmpList = aDatabase.get(String.valueOf(tmpUpperCaseUsername.charAt(0)));
			for (int index = 0; index < tmpList.size(); index++)
			{
				if(tmpList.get(index).getUserName().equals(pUsername))
				{
					tmpUpperCaseUsername = null;
					return tmpList.get(index);
				}
			}
			tmpUpperCaseUsername = null;
			tmpList = null;
			return null;
		}

		// Checks if the given pUsername exists in the database
		private boolean IPAddressExists(String pIPAddress)
		{
			List <Account> tmpList;
			Enumeration<List<Account>> enumeration = aDatabase.elements();
			while(enumeration.hasMoreElements())
			{
				tmpList = enumeration.nextElement();
				for (int index = 0; index < tmpList.size(); index++)
				{
					if(tmpList.get(index).getIPAddress().equals(pIPAddress))
					{
						tmpList = null;
						enumeration = null;
						return true;
					}
				}
			}
			tmpList = null;
			enumeration = null;
			return false;
		}

		// Checks for the validity of the given pIPAddress
		private static boolean validate(final String pIPAddress) 
		{          
		      Pattern pattern = Pattern.compile(Parameters.PATTERN_FOR_IPADDRESS_VALIDATION);
		      Matcher matcher = pattern.matcher(pIPAddress);
		      pattern = null;
		      return matcher.matches();             
		}
}