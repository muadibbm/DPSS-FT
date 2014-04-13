package replicaB;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;




public class ServerReplica  extends Thread{

	/**
	 * Define constants for the servers the users can access
	 */
	public static UserPlayer playerToTransfered;
	
	//ports used for the TRANSFER ACCOUNT method whit in the server group only
	static final int NA_port = 6666;
	static final int EU_port = 6667;
	static final int AS_port = 6668;
	
	
	static DatagramSocket aSocket = null;
	static boolean waitForConnection = true;
	String serverIPAddress;
	int IPaddress = 0;
	int serverPort;
	
	String messageArray [];
	
	static java.util.Date date= new java.util.Date();
	/**
	 * online and offline variable to keep track for the user activities
	 */
	int online = 0;
	int offline = 0;
	
	
	/**
	 * acronym - used for the name of the server
	 * total records
	 * Structure to keep and allow access to the users - Hashtable with list, 
	 * allowing addition of new records to the hashtable with the same key
	 */
	String acronym;
	int serverListeningPort = 0;
	String dataRecieved = null;
	
	static int totalRecords;
	static Hashtable<Character, List <UserPlayer>> records = new Hashtable<>();

	
	/**
	 * Method to create/edit a log file for the game server
	 * @param str line of text to be added to the log file
	 */
	
	
	public ServerReplica (int IP) {
		if ( IP == Parameters.AS_IP_ADDRESS ){
			acronym = "AS";
			IPaddress = Parameters.AS_IP_ADDRESS;
			serverListeningPort = Parameters.UDP_PORT_REPLICA_B_AS;
		}else if ( IP == Parameters.EU_IP_ADDRESS ){
			acronym = "EU";
			serverListeningPort =Parameters.UDP_PORT_REPLICA_B_EU;
			IPaddress = Parameters.EU_IP_ADDRESS;
		}
		else if ( IP == Parameters.NA_IP_ADDRESS ){
			acronym = "NA";
			serverListeningPort = Parameters.UDP_PORT_REPLICA_B_NA;
			IPaddress = Parameters.NA_IP_ADDRESS;
		}
	}
	
	
	/**
	 * Method to create/edit a log file for the game server
	 * @param str line of text to be added to the log file
	 */
	public synchronized void log (String str) {
		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(acronym + Parameters.SERVER_LOG_EXTENSION,true)));
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
			String formattedDate = sdf.format(date);
			out.println(formattedDate + " : "  + str);
			out.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Method to create user account for the provided server
	 * @param str first name, last name, age, user name, password, IP
	 */
	public boolean createPlayerAccount(String FirstName, String LastName,
			int Age, String Username, String Password, String IPAddress) {
		boolean accountCreated = false;
		UserPlayer temprec = new UserPlayer(FirstName, LastName, Age, Username, Password, IPAddress); 
		//System.out.println(temprec.toString());
		acronym = getServerAcronim(extractIP(IPAddress));
		//if there is no list created add new and put into the Hashtable
		
		char initial = Character.toUpperCase(Username.toCharArray()[0]);
		System.out.println("Account with : " + initial + " : was added to the server.");
		//add syncronize block here
		try {
			
		if(records.get(initial)==null) {
			synchronized (this) {
			List<UserPlayer> tempList = new ArrayList<UserPlayer>();
			tempList.add(temprec);
			records.put(initial, tempList);
			log("New player was added " + Username + " to server " + acronym);
			accountCreated = true;
			}
		}
		//if there is already a list add new node
		else {
			synchronized (this) {
			//check if the user exists
			if(!checkIfUserRegistered(temprec.getUserName(), IPAddress)){
				records.get(initial).add(temprec);
				log("New player was added " + Username + " to server " + acronym);
				accountCreated = true;
			}
			else {
				System.out.println("The account you try to create already exists on the server");
				log("Account " + Username + " already exists on the server " + IPAddress);
				accountCreated = false;
			}
			}
		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println ("Method call to create account, return value from the method : " + accountCreated);
		return accountCreated;

	}
	
	/**
	 * Method to log in the user and change his/her status in the hash table
	 * @param Username
	 * @param Password
	 * @param IPAddress
	 * @return true if the record is found and the user status is changed to true
	 */
	public boolean PlayerSignIn(String Username, String Password,
			String IPAddress) {
		char initial = Character.toUpperCase(Username.toCharArray()[0]);
		boolean recordFound = false;
		//check if there are records for the username's first letter 
		if(records.get(initial)==null) {
			System.out.println("The account you try to access can not be found!");
			log("Player " + Username + " is not registered on server " + acronym);
		}
		//if there is already a list check if the record exists
		else {
			List<UserPlayer> lList = records.get(initial);
			for (int i = 0; i < lList.size(); i++) {
				if (lList.get(i).getUserName().equals(Username)) {
					//if the user is not loged in - log in
					if (!(lList.get(i).getStatus())) {
						synchronized (lList) {
						lList.get(i).changeStatus();	
						}
						log("Player " + Username + " is loged in to server " + acronym);
						System.out.println("Player " + Username + " loged in!");
						recordFound = true;
					}
					else
						{log("Player " + Username + " is already loged in to server " + acronym);
					System.out.println("Player " + Username + " is already loged in!");
					recordFound = true;
						}
				}
				
			}
			
		}
		return recordFound;
	}
	
	
	/**
	 * Method to log out user
	 * @param Username
	 * @param IPAddress
	 * @return true if the account is found and the user's status is changed to logged out
	 */
	public boolean PlayerSignOut(String Username, String IPAddress) {
		char initial = Character.toUpperCase(Username.toCharArray()[0]);
		boolean recordFound = false;
		//check if there are records for the username's first letter 
		if(records.get(initial)==null) {
			System.out.println("The account you try to access can not be found!");
			log("Player " + Username + " can not be found on " + IPAddress);
		}
		//if there is already a list check if the record exists
		else {
			List<UserPlayer> lList = records.get(initial);
			for (int i = 0; i < lList.size(); i++) {
				if (lList.get(i).getUserName().equals(Username)) {
					//if the user is loged in - log out
					if ((lList.get(i).getStatus())) {
						synchronized (lList) {
						lList.get(i).changeStatus();
						}
						log("Player " + Username + " signed out from server " + acronym);
						System.out.println("Player " + Username + " signed out!");
						recordFound = true;
					}
					else
						{log("Player " + Username + " is already signedout from server " + acronym);
					System.out.println("Player " + Username + " is already signed out!");
					recordFound = false;
						}
				}
				
			}
			
		}
		return recordFound;
	}
	
	
	/**
	 * Method to remove user account temporary from a server
	 * @param AdminUsername
	 * @param AdminPassword
	 * @param IPAddress
	 * @param UsernameToSuspend
	 * @return true if the account is found and removed
	 */
	public boolean suspendAccount(String AdminUsername, String AdminPassword,
			String IPAddress, String UsernameToSuspend) {
		char initial = Character.toUpperCase(UsernameToSuspend.toCharArray()[0]);
		boolean recordFound = false;
		//check if there are records for the username's first letter 
		if(records.get(initial)==null) {
			System.out.println("The account you try to access can not be found!");
			log("Account  " + UsernameToSuspend + " is not registered on server " + IPAddress);
		}
		//if there is already a list check if the record exists
		else {
			List<UserPlayer> lList = records.get(initial);
			for (int i = 0; i < lList.size(); i++) {
				if (lList.get(i).getUserName().equals(UsernameToSuspend)) {
					//if the user is found
					synchronized (lList) {
						
					
					lList.remove(lList.get(i));
					log("Account " + UsernameToSuspend + " was removed from server  " + acronym);
					System.out.println("Account " + UsernameToSuspend + " was removed from server  " + acronym);
					recordFound = true;
					//if that is the only record remove the list from the hashtable
					if (lList.isEmpty()){
						records.remove(initial);
					}
					}
				}
			}
			
		}
		return recordFound;
	}

	//initiate UDP connection like Client and transfer the account, the details for the account are in transfer data string
	public boolean transferAccountUDP (String transferData, int transferPort) {
		
		boolean transferSuccess = false;
		int statusCode = 0;
		String statusCodeFromReplay;
		DatagramSocket aSocket = null;
		int indexCode = 0;
		try {
			aSocket = new DatagramSocket();
			byte [] m = transferData.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			int serverPort = transferPort;
			DatagramPacket request = new DatagramPacket(m,transferData.length(), aHost, serverPort);
			aSocket.send(request);
			
			byte [] buffer = new byte [1000];
			DatagramPacket replay = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(replay);
			
			//add the new account to the server
			statusCodeFromReplay = new String( replay.getData());
			indexCode = statusCodeFromReplay.indexOf(".");
			statusCode = Integer.parseInt(statusCodeFromReplay.substring(0, indexCode));
			
			if ( statusCode == 1) {
				System.out.println("Transfer done!");
				log ("Transfer done!");
				transferSuccess = true;
			}
			else {
				System.out.println("The username is already taken. Transfer aborted!");
				log ("The username is already taken. Transfer aborted!");
				transferSuccess = false;
			}

		}
		catch (SocketException e){
			System.out.println("Socket Exception: " + e.getMessage());
		}
		catch (IOException e) {
			System.out.println("IO Exception: " + e.getMessage());
		}

		finally {
			if (aSocket != null) {
				aSocket.close();
			}

	}
		return transferSuccess;
	}
	

	/**
	 * Method to provide info for all servers in the system : users' status across all 3 servers
	 * @param AdminUsername
	 * @param AdminPassword
	 * @param IPAddress
	 * @return string with the status of the transfer
	 */
	public String getPlayerStatus(String AdminUsername, String AdminPassword,
			String IPAddress) {
		String returnMessage = null;
		int onlineUsers = 0;
		int offlineUsers = 0;
		String serverAcro = null;
		String currentServerStat = null;
		
		serverAcro = getServerAcronim(Integer.parseInt(IPAddress));
		
		String letter = "ABCDEFJHIJKLMNOPQRSTUVWXYZ";
		
		for (int i = 0; i < 26; i++) {
			char charLetter = letter.charAt(i);
			if(records.get(charLetter) !=null){
				//System.out.println("\n \n Return users for '" + charLetter + "' : ");
				for ( int j = 0 ; j<records.get(charLetter).size(); j++ ) {					
					
						//if the user is loged in
						if (records.get(charLetter).get(j).getStatus()) {
							onlineUsers++;
						}
						else 
							offlineUsers++;					
				}
					
			}
		}
		currentServerStat = serverAcro + " has " + onlineUsers + " online and " + offlineUsers + " offline.";
		returnMessage = StatusUDP(Integer.parseInt(IPAddress));
		System.out.println("Return message from 2 UDP connections: " + returnMessage);
		
		String tempMessage = currentServerStat + returnMessage;
		log(tempMessage);
		return tempMessage;
	}
	
	
	
	
	
	
	/**
	 * Method to transfer account between servers in the system
	 * @param Username
	 * @param Password
	 * @param OldIPAddress
	 * @param NewIPAddress
	 * @return string with the status of the transfer 
	 */
	public String transferAccount(String Username, String Password,
			String OldIPAddress, String NewIPAddress) {
		
		int transferIP = Integer.parseInt(NewIPAddress);
		int transferPort = 5000;
		boolean transferTest = false;
		if (transferIP == Parameters.NA_IP_ADDRESS) 
			transferPort = NA_port;
		else if ( transferIP == Parameters.EU_IP_ADDRESS) 
			transferPort = EU_port;
		else if ( transferIP == Parameters.AS_IP_ADDRESS)
			transferPort = AS_port;
		else System.out.println("Can not get the port " + NewIPAddress);
		

		String transferStatus = "0.0";
		char initial = Character.toUpperCase(Username.toCharArray()[0]);
	
		
		//check if there are records for the username's first letter 
		if(records.get(initial)==null) {
			System.out.println("The account you try to access can not be found!");
			log("The account " + Username + " was not found on server  " + acronym);
		}
		//if there is already a list check if the record exists
		else {
			List<UserPlayer> lList = records.get(initial);
			for (int i = 0; i < lList.size(); i++) {
				//get the status of the transfer
					if (lList.get(i).getUserName().equals(Username) && lList.get(i).getPassword().equals(Password)) {
						System.out.println("Account " + Username + " requested transfer from  " + acronym);
			
//Create UDP connection with the server the account will be transfered to
						transferStatus = "1." + lList.get(i).getFirstName() + "." + lList.get(i).getLastName() + "." + lList.get(i).getAge() + "." + lList.get(i).getUserName() + "." + lList.get(i).getPassword() + ".";
						System.out.println(transferStatus);
						
						transferTest = transferAccountUDP(transferStatus,transferPort);
						if (transferTest) {
						log("Account " + Username + "  was transfered from  " + acronym);
						System.out.println("Account " + Username + " was transfered from  " + acronym + transferTest);
						} else {
							log("Account " + Username + "  can not be transfered from  " + acronym);
							System.out.println("Account " + Username + " can not be transfered from  " + acronym + transferTest);
							transferStatus = "0.0";	
						}
					}
					//if the account is not found
					else	
					{
						log("Account " + Username + " was not found on server  " + acronym);
						System.out.println("Account " + Username + " was not found on server  " + acronym);
						transferStatus = "0.0";	
					}
	
				
				//the data is sent and the account is added to the new server
					if (transferTest) {
						synchronized (lList) {
						lList.remove(lList.get(i));
						}
						log("Account " + Username + " was transfered from server  " + acronym);

						//if that is the only record remove the list from the hashtable
						if (lList.isEmpty()){
							synchronized (lList) {
							records.remove(initial);
							}
						}
						
					}
					
					
				
			}
		}
		
		return transferStatus;

	}
	

	/**
	 * Method that will connect from the current server to the other 2 and get the statuses for all users
	 * @param accessIP - the current server that got the user request
	 * @return string with the status info from all servers
	 */
	public static String StatusUDP (int accessIP) {
		
		//int serverPortInit = 0;
		int serverPortConn1 = 0;
		int serverPortConn2 = 0;
		
		if(accessIP == Parameters. NA_IP_ADDRESS) {
			//serverPortInit = NA_port;
			serverPortConn1 = EU_port;
			serverPortConn2 = AS_port;
		}else if (accessIP == Parameters.EU_IP_ADDRESS)
		{
			//serverPortInit = EU_port;
			serverPortConn1 = NA_port;
			serverPortConn2 = AS_port;
		}else if (accessIP == Parameters.AS_IP_ADDRESS)
		{
			//serverPortInit = AS_port;
			serverPortConn1 = NA_port;
			serverPortConn2 = EU_port;
		}
		String returnStatus = "";
		
		//boolean transferSuccess = false;
		int statusCode1 = 0;
		int statusCode2 = 0;
		String serverAcro = "";
		String statusCodeFromReplay1, statusCodeFromReplay2;
		String requestCode = "5.0";
		
		DatagramSocket aSocket1 = null;
		DatagramSocket aSocket2 = null;
		int indexCode1 = 0;
		try {
			String returnStatus1;
			String returnStatus2;
			
			aSocket1 = new DatagramSocket();
			byte [] m1 = requestCode.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			int serverPort1 = serverPortConn1;
			DatagramPacket request = new DatagramPacket(m1,m1.length, aHost, serverPort1);
			aSocket1.send(request);
			
			byte [] buffer1 = new byte [1400];
			DatagramPacket replay1 = new DatagramPacket(buffer1, buffer1.length);
			aSocket1.receive(replay1);
			
			if (serverPort1==Parameters.AS_IP_ADDRESS){
				serverAcro = "AS";
			}else if (serverPort1==Parameters.EU_IP_ADDRESS){
				serverAcro = "EU";
			}
			else if (serverPort1==Parameters.NA_IP_ADDRESS){
				serverAcro = "NA";
			}
			
			//add the new account to the server
			statusCodeFromReplay1 = new String(replay1.getData());
			indexCode1 = statusCodeFromReplay1.indexOf(".");
			statusCode1 = Integer.parseInt(statusCodeFromReplay1.substring(0, indexCode1));
			returnStatus1 = (statusCodeFromReplay1.substring(indexCode1+1, statusCodeFromReplay1.length())).trim();
			returnStatus = returnStatus.concat(returnStatus1);
			
			//UDP to the second server
			int indexCode2 = 0;
			aSocket2 = new DatagramSocket();
			byte [] m2 = requestCode.getBytes();
			InetAddress aHost1 = InetAddress.getByName("localhost");
			int serverPort2 = serverPortConn2;
			DatagramPacket request2 = new DatagramPacket(m2,m2.length, aHost1, serverPort2);
			aSocket2.send(request2);
			
			byte [] buffer2 = new byte [1400];
			DatagramPacket replay2 = new DatagramPacket(buffer2, buffer2.length);
			aSocket2.receive(replay2);
			//
			
			if (serverPort2==Parameters.AS_IP_ADDRESS){
				serverAcro = "AS";
			}else if (serverPort2==Parameters.EU_IP_ADDRESS){
				serverAcro = "EU";
			}
			else if (serverPort2==Parameters.NA_IP_ADDRESS){
				serverAcro = "NA";
			}
			
			//add the new account to the server
			statusCodeFromReplay2 = new String(replay2.getData());
			indexCode2 = statusCodeFromReplay2.indexOf(Parameters.UDP_PARSER);
			statusCode2 = Integer.parseInt(statusCodeFromReplay2.substring(0, indexCode2));
			returnStatus2 = (statusCodeFromReplay2.substring(indexCode2+1, statusCodeFromReplay2.length())).trim();
			returnStatus = returnStatus1 + returnStatus2;
			System.out.println("Check status remote server 2: OK ");
			
			
			//returnStatus = returnStatus.concat(returnStatus2);
			System.out.println("UDP 1 request: " + returnStatus1);
			System.out.println("UDP 2 request: " + returnStatus2);
			System.out.println("UDP 1 and 2: " + returnStatus1 + returnStatus2);
		}
		catch (SocketException e){
			System.out.println("Socket Exception: " + e.getMessage());
		}
		catch (IOException e) {
			System.out.println("IO Exception: " + e.getMessage());
		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}

		finally {
			if (aSocket2 != null) {
				aSocket2.close();
			}
			if (aSocket1 != null) {
				aSocket1.close();
			}

	}	
		return returnStatus;
	}
	

	/**
	 * Method to check if the user account is already registered on the server
	 * @param username
	 * @param userIP
	 * @return false if the new record can be added
	 */
	public static boolean checkIfUserRegistered (String username, String userIP) {

		char initial = Character.toUpperCase(username.toCharArray()[0]);
		boolean recordNotFound = true;
		//check if there are records for the username's first letter 
		if(records.get(initial)==null) {
			recordNotFound = false;
		}
		//if there is already a list check if the record exists
		else {
			List<UserPlayer> lList = records.get(initial);
			for (int i = 0; i < lList.size(); i++) {
				if (lList.get(i).getUserName().equals(username)) {
					//if the user already exists
						System.out.println("Username  " + username + " is already registered!");
						recordNotFound = true;
					}
					else
						{
						System.out.println("Username  " + username + " is available for registration");
						recordNotFound = false;
						}
				}
				
			}
		return recordNotFound;
	}
	
	@Override
	public void run()
	{
		startUDPserver();
	}
	
	/**
	 * Runs the server per geolocation called from the main UDP server
	 * @param port
	 * @param IP
	 */
	private void startUDPserver () {
		
		serverListeningPort = this.serverListeningPort;
		IPaddress = this.IPaddress;
		int parserPosition = 0;
		int parserPositionNextOccurence = 0;
		String requestServerInitials = null;
		String requestMethodCode = null;
		String tempDataUDPMessage = null;
		
		String tempFirstName, tempLastName, tempUsername, tempPassword, tempIP;
		int tempAge = 0;
		
		 try {
				
				aSocket = new DatagramSocket(serverListeningPort);
				byte [] buffer = new byte [Parameters.UDP_BUFFER_SIZE];
				System.out.println ("Replica " + IPaddress + " is up and running!");
				//always listen for new messages on the specified port
				while (waitForConnection) {							
					
					DatagramPacket request = new DatagramPacket(buffer, buffer.length);
					aSocket.receive(request);

					//get the data from the request and check
					dataRecieved = new String(request.getData());
					//System.out.println("Message to server : " + IPaddress + " : " + dataRecieved);
					//put all data in an array : [0] - server sent the request, [1] - method code
					messageArray = dataRecieved.split(Parameters.UDP_PARSER);
					
					

					
					//get who is initiating the request : RM, Leader, Replica1 or Replica2
					UDPMethodCall(dataRecieved);
					
					
					//if the message is coming from Leader or Replica Manager
					if ( messageArray[0].equals(Parameters.LR_NAME)) {
					
					
					
					} else if ( messageArray[0].equals(Parameters.RM_NAME)) {
						
						
						
					}
					
					
					//parserPosition = dataRecieved.indexOf(Parameters.UDP_PARSER);
					System.out.println(dataRecieved.toString());
					
					
					


				}	
				
		 }
			catch (Exception e) {e.printStackTrace();}

	}
	
	public String  getServerAcronim (int serverIP) {
		String acro = null;
		if ( serverIP == Parameters.NA_IP_ADDRESS) {
			acro = Parameters.NA_SERVER_ACRO;
		} else if ( serverIP == Parameters.EU_IP_ADDRESS) {
			acro = Parameters.EU_SERVER_ACRO;
		} else if ( serverIP == Parameters.AS_IP_ADDRESS) {
			acro = Parameters.AS_SERVER_ACRO;
		}
		
		
		return acro;
	}
	
	public void UDPMethodCall (String UDPrequest) {
		
		String methodCall = null;
		
		messageArray = UDPrequest.split(Parameters.UDP_PARSER);
		//Extract the method forwarded from the main UDP server
		methodCall = messageArray[1];
		
		if (methodCall.contains(Parameters.METHOD_CODE.CREATE_ACCOUNT.toString())) {
			
			//call create account
			createPlayerAccount(messageArray[2], messageArray[3], Integer.parseInt(messageArray[4]), messageArray[5], messageArray[6], messageArray[7]);
		
		} else if (methodCall.contains(Parameters.METHOD_CODE.PLAYER_SIGN_IN.toString())){
			
			//call player sign in
			PlayerSignIn(messageArray[2], messageArray[3], messageArray[4]);
			
		} else if (methodCall.contains(Parameters.METHOD_CODE.PLAYER_SIGN_OUT.toString())){
			
			//call player sign out
			PlayerSignOut(messageArray[2], messageArray[3]);
			
		} else if (methodCall.contains(Parameters.METHOD_CODE.SUSPEND_ACCOUNT.toString())){
			
			//call suspend account
			suspendAccount(messageArray[2], messageArray[3], messageArray[4], messageArray[5]);
			
		} else if (methodCall.contains(Parameters.METHOD_CODE.TRANSFER_ACCOUNT.toString())){
			
			//call transfer account
			transferAccount(messageArray[2], messageArray[3], messageArray[4], messageArray[5]);
			
		} else if (methodCall.contains(Parameters.METHOD_CODE.GET_PLAYER_STATUS.toString())){
			
			//call get player status
			getPlayerStatus(messageArray[2], messageArray[3], messageArray[4]);
			
		}

		
	}
	

	public static int extractIP ( String inputIP ){
		int accessIP=0;
		try
		{
		int index = inputIP.indexOf(".");
		accessIP = Integer.parseInt(inputIP.substring(0, index));
		}
		catch ( Exception e ) {e.printStackTrace();}
		return accessIP;
	}

}
