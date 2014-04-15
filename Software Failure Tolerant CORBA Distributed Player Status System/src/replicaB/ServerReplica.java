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
	public UserPlayer playerToTransfered;
	
	//ports used for the TRANSFER ACCOUNT method whit in the server group only
	static final int NA_port = Parameters.UDP_PORT_REPLICA_B_NA;
	static final int EU_port = Parameters.UDP_PORT_REPLICA_B_EU;
	static final int AS_port = Parameters.UDP_PORT_REPLICA_B_AS;
	
	protected  boolean methodAcknowledgment = false;
	protected  String methodAcknowledgmentStr = null;
	DatagramSocket aSocket = null;
	boolean waitForConnection = true;
	String serverIPAddress;
	int IPaddress = 0;
	int serverPort;
	
	public static boolean isRunning = true;
	
	String messageArray [] = new String [20];
	
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
	
	int totalRecords;
	Hashtable<Character, List <UserPlayer>> records = new Hashtable<>();

	int LOCAL_CONNECTION_1 = 0;
	int LOCAL_CONNECTION_2 = 0;
	
	
	/**
	 * Method to create/edit a log file for the game server
	 * @param str line of text to be added to the log file
	 */
	
	
	public ServerReplica (int IP) {
		if ( IP == Parameters.AS_IP_ADDRESS ){
			acronym = "AS";
			IPaddress = Parameters.AS_IP_ADDRESS;
			this.serverListeningPort = Parameters.UDP_PORT_REPLICA_B_AS;
			LOCAL_CONNECTION_1 = Parameters.UDP_PORT_REPLICA_B_EU;
			LOCAL_CONNECTION_2 = Parameters.UDP_PORT_REPLICA_B_NA;
			
		}else if ( IP == Parameters.EU_IP_ADDRESS ){
			acronym = "EU";
			this.serverListeningPort =Parameters.UDP_PORT_REPLICA_B_EU;
			IPaddress = Parameters.EU_IP_ADDRESS;
			LOCAL_CONNECTION_1 = Parameters.UDP_PORT_REPLICA_B_AS;
			LOCAL_CONNECTION_2 = Parameters.UDP_PORT_REPLICA_B_NA;
		}
		else if ( IP == Parameters.NA_IP_ADDRESS ){
			acronym = "NA";
			this.serverListeningPort = Parameters.UDP_PORT_REPLICA_B_NA;
			IPaddress = Parameters.NA_IP_ADDRESS;
			LOCAL_CONNECTION_1 = Parameters.UDP_PORT_REPLICA_B_EU;
			LOCAL_CONNECTION_2 = Parameters.UDP_PORT_REPLICA_B_AS;
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
		int onlineUsers = 0;
		int offlineUsers = 0;
		String currentServerStat = null;
		
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
		currentServerStat = "/" + this.acronym + "/" + onlineUsers + "/" + offlineUsers + "/online.";
		
		return currentServerStat;
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

		int portRecievingTranfer = 0;
		int tempIP = 0;
		tempIP = extractIP(NewIPAddress);
		String playerRecord = null;
		String tempFirstName, tempLastName, tempUsername, tempPassword;
		
		switch (tempIP) {
		case Parameters.NA_IP_ADDRESS :
			portRecievingTranfer = Parameters.UDP_PORT_REPLICA_B_NA;
			break;
		case Parameters.EU_IP_ADDRESS :
			portRecievingTranfer = Parameters.UDP_PORT_REPLICA_B_EU;
			break;
		case Parameters.AS_IP_ADDRESS :
			portRecievingTranfer = Parameters.UDP_PORT_REPLICA_B_AS;
			break;
		}
		
		
char initial = Character.toUpperCase(Username.toCharArray()[0]);
	
		
		//check if there are records for the username's first letter 
		if(records.get(initial)==null) {
			System.out.println("The account you try to access can not be found!");
			log("The account " + Username + " was not found on server  " + acronym);
			playerRecord = null;
		}
		//if there is already a list check if the record exists
		else {
			List<UserPlayer> lList = records.get(initial);
			for (int i = 0; i < lList.size(); i++) {
				//get the status of the transfer
					if (lList.get(i).getUserName().equals(Username) && lList.get(i).getPassword().equals(Password)) {
						System.out.println("Account " + Username + " requested transfer from  " + acronym);
						playerRecord = acronym + Parameters.UDP_PARSER +
								lList.get(i).getFirstName() + Parameters.UDP_PARSER +
									lList.get(i).getLastName() + Parameters.UDP_PARSER + 
									String.valueOf(lList.get(i).getAge())  + Parameters.UDP_PARSER + 
									lList.get(i).getUserName() + Parameters.UDP_PARSER + 
									lList.get(i).getPassword()  + Parameters.UDP_PARSER + NewIPAddress;
					}
			}
		}
		return playerRecord;

	}


	/**
	 * Method to check if the user account is already registered on the server
	 * @param username
	 * @param userIP
	 * @return false if the new record can be added
	 */
	public boolean checkIfUserRegistered (String username, String userIP) {

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
		while (isRunning) {
	     
		startUDPserver();
		}
	}
	
	public void killThread () {
		isRunning = false;
	}
	
	/**
	 * Runs the server per geolocation called from the main UDP server
	 * @param port
	 * @param IP
	 */
	private void startUDPserver () {
		String globalSystemSat = null;
		
		 try {
				
				aSocket = new DatagramSocket(serverListeningPort);
				byte [] buffer = new byte [Parameters.UDP_BUFFER_SIZE];
				byte [] bufferACK = new byte [Parameters.UDP_BUFFER_SIZE];
				byte [] bufferStat = new byte [Parameters.UDP_BUFFER_SIZE];
				
				bufferACK = "1/1".getBytes();
				System.out.println ("Replica " + IPaddress + " is up and running!");
				String methodCall = null;
				
				int statusIndex= 0;
				
				
				//always listen for new messages on the specified port
				while (true) {							
					DatagramPacket request = new DatagramPacket(buffer, buffer.length);
					aSocket.receive(request);
					dataRecieved = (new String(request.getData()).trim());
					System.out.println("Data recieved at GEO server " + dataRecieved);
					
					if (dataRecieved.contains("online")) {
						statusIndex =  dataRecieved.indexOf("online");
						dataRecieved = dataRecieved.substring(0,statusIndex);
						methodAcknowledgmentStr.concat((dataRecieved));
						
						globalSystemSat = globalSystemSat.concat(dataRecieved);

						bufferStat = (Parameters.RB_NAME + globalSystemSat + Parameters.UDP_END_PARSE).getBytes();						
						
						DatagramPacket replay3 = new DatagramPacket(bufferStat, bufferStat.length,request.getAddress(),Parameters.UDP_PORT_REPLICA_LEAD);
						aSocket.send(replay3);
						log (globalSystemSat);
						System.out.println(globalSystemSat);
					}
					
					if (!dataRecieved.contains(Parameters.REQUEST_LOCAL_STAT)) {
					messageArray = dataRecieved.split(Parameters.UDP_PARSER);
					//Extract the method forwarded from the main UDP server					
					methodCall = messageArray[1];
					} else  {
						methodCall = Parameters.REQUEST_LOCAL_STAT;
					}
					
					if (methodCall.contains(Parameters.METHOD_CODE.CREATE_ACCOUNT.name())) {
						System.out.println("Inside method on the local server CREATE_ACCOUNT");
						//call create account
						methodAcknowledgment =  createPlayerAccount(messageArray[2], messageArray[3], Integer.parseInt(messageArray[4]), messageArray[5], messageArray[6], messageArray[7]);
						
						if (methodAcknowledgment == true) {
							bufferStat = (Parameters.RB_NAME + Parameters.UDP_PARSER + "1" + Parameters.UDP_END_PARSE).getBytes();						
							
						} else  { bufferStat = (Parameters.RB_NAME + Parameters.UDP_PARSER + "0"  + Parameters.UDP_END_PARSE).getBytes();	}
						
						DatagramPacket replay3 = new DatagramPacket(bufferStat, bufferStat.length,request.getAddress(),Parameters.UDP_PORT_REPLICA_LEAD);
						aSocket.send(replay3);
					}
					else if (methodCall.contains(Parameters.METHOD_CODE.PLAYER_SIGN_IN.name())){
						System.out.println("Inside method on the local server SIGN IN");
						//call player sign in
						methodAcknowledgment =  PlayerSignIn(messageArray[2], messageArray[3], messageArray[4]);
						
						if (methodAcknowledgment == true) {
							bufferStat = (Parameters.RB_NAME + Parameters.UDP_PARSER + "1"  + Parameters.UDP_END_PARSE).getBytes();						
							
						} else  { bufferStat = (Parameters.RB_NAME + Parameters.UDP_PARSER + "0"  + Parameters.UDP_END_PARSE).getBytes();	}
						
						DatagramPacket replay3 = new DatagramPacket(bufferStat, bufferStat.length,request.getAddress(),Parameters.UDP_PORT_REPLICA_LEAD);
						aSocket.send(replay3);
					} 
					else if (methodCall.contains(Parameters.METHOD_CODE.PLAYER_SIGN_OUT.name())){
						
						//call player sign out
						methodAcknowledgment =  PlayerSignOut(messageArray[2], messageArray[3]);
						if (methodAcknowledgment == true) {
							bufferStat = (Parameters.RB_NAME + Parameters.UDP_PARSER + "1" + Parameters.UDP_END_PARSE).getBytes();						
							
						} else  { bufferStat = (Parameters.RB_NAME + Parameters.UDP_PARSER + "0" + Parameters.UDP_END_PARSE).getBytes();	}
						
						DatagramPacket replay3 = new DatagramPacket(bufferStat, bufferStat.length,request.getAddress(),Parameters.UDP_PORT_REPLICA_LEAD);
						aSocket.send(replay3);
						
					} 
					else if (methodCall.contains(Parameters.METHOD_CODE.SUSPEND_ACCOUNT.name())){
						
						//call suspend account
						methodAcknowledgment = suspendAccount(messageArray[2], messageArray[3], messageArray[4], messageArray[5]);
						if (methodAcknowledgment == true) {
							bufferStat = (Parameters.RB_NAME + Parameters.UDP_PARSER + "1" + Parameters.UDP_END_PARSE).getBytes();						
							
						} else  { bufferStat = (Parameters.RB_NAME + Parameters.UDP_PARSER + "0" + Parameters.UDP_END_PARSE).getBytes();	}
						
						DatagramPacket replay3 = new DatagramPacket(bufferStat, bufferStat.length,request.getAddress(),Parameters.UDP_PORT_REPLICA_LEAD);
						aSocket.send(replay3);
						
					} else if (methodCall.contains(Parameters.METHOD_CODE.TRANSFER_ACCOUNT.name())){
						if (!(dataRecieved.contains(Parameters.TRANSFER_DONE) || dataRecieved.contains(Parameters.TRANSFER_FAIL)))
						{
						
						
						//call transfer account
						methodAcknowledgmentStr = transferAccount(messageArray[2], messageArray[3], messageArray[4], messageArray[5]);
						if (!(methodAcknowledgmentStr.equals(null) || methodAcknowledgmentStr == null ))
						{
							
							bufferStat = (Parameters.REQUEST_LOCAL_TRANSFER + Parameters.UDP_PARSER + methodAcknowledgmentStr).getBytes();
							DatagramPacket replay6 = new DatagramPacket(bufferStat, bufferStat.length,request.getAddress(),getPortPerIP(messageArray[5]));
							aSocket.send(replay6);
							System.out.println("Init transfer " + new String(replay6.getData()));
							log("Request for account transfer is initiated");
						}
						}
						
						
					} else if (methodCall.contains(Parameters.METHOD_CODE.GET_PLAYER_STATUS.name())){
						System.out.println("GET_PLAYER_STATUS is requested!");
						//call get player status
						methodAcknowledgmentStr = getPlayerStatus(messageArray[2], messageArray[3], messageArray[4]);
					//send 2 UDP messages to the other 2 servers	
							
							
						bufferStat = (Parameters.REQUEST_LOCAL_STAT).getBytes();						
						DatagramPacket replay1 = new DatagramPacket(bufferStat, bufferStat.length,request.getAddress(),LOCAL_CONNECTION_1);
						aSocket.send(replay1);
						DatagramPacket replay2 = new DatagramPacket(bufferStat, bufferStat.length,request.getAddress(),LOCAL_CONNECTION_2);
						aSocket.send(replay2);
						
						log (methodAcknowledgmentStr);
						
						System.out.println (methodAcknowledgmentStr);
						
						statusIndex =  methodAcknowledgmentStr.indexOf("online");
						methodAcknowledgmentStr = methodAcknowledgmentStr.substring(0,statusIndex-1);
						globalSystemSat = methodAcknowledgmentStr;
						
						
					} else if (dataRecieved.contains(Parameters.REQUEST_LOCAL_STAT)) {
						if(!dataRecieved.contains("/")){
						//	buffer = null;
						bufferStat = getPlayerStatus("Admin", "Admin",String.valueOf(IPaddress)).getBytes();						
						DatagramPacket replay3 = new DatagramPacket(bufferStat, bufferStat.length,request.getAddress(),request.getPort());
						aSocket.send(replay3);
						System.out.println ("The METHOD CODE  REQUEST_LOCAL_STAT is sent to "  +request.getPort() + "  the data sent follow:" + getPlayerStatus("Admin", "Admin",String.valueOf(IPaddress)));
						log ("Local stat is requested : " + getPlayerStatus("Admin", "Admin",String.valueOf(IPaddress)));
						}
					} 
					
					if (dataRecieved.contains(Parameters.REQUEST_LOCAL_TRANSFER)){
						System.out.println("REQUEST_LOCAL_TRANSFER is requested!");
						//call get player status
						methodAcknowledgment = createPlayerAccount(messageArray[2], messageArray[3], Integer.parseInt(messageArray[4]), messageArray[5], messageArray[6], String.valueOf(IPaddress) + ".");
					//send 2 UDP messages to the other 2 servers	
							
						if (methodAcknowledgment == true) {
							bufferStat = (Parameters.TRANSFER_DONE + Parameters.UDP_PARSER + messageArray[5] + Parameters.UDP_PARSER + "1" + Parameters.UDP_END_PARSE).getBytes();						
							
						} else  { bufferStat = (Parameters.TRANSFER_FAIL + Parameters.UDP_PARSER + "0" + Parameters.UDP_END_PARSE).getBytes();	}
						
						DatagramPacket replay3 = new DatagramPacket(bufferStat, bufferStat.length,request.getAddress(),request.getPort());
						System.out.println("To send acc for the transfer " + new String( replay3.getData()));
						aSocket.send(replay3);
						
					
					}else 
					if (dataRecieved.contains(Parameters.TRANSFER_DONE)) {
						suspendAccount("Admin", "Admin", String.valueOf(IPaddress) + ".", messageArray[1]);
						bufferStat = (Parameters.RB_NAME + Parameters.UDP_PARSER + "1" + Parameters.UDP_END_PARSE).getBytes();						
						DatagramPacket replay5 = new DatagramPacket(bufferStat, bufferStat.length,request.getAddress(),Parameters.UDP_PORT_REPLICA_LEAD);
						aSocket.send(replay5);
						System.out.println("Transfer done, ACK sent " + new String(replay5.getData()));
					} else 
							
					if (dataRecieved.contains(Parameters.TRANSFER_FAIL)){
						 bufferStat = (Parameters.RB_NAME + Parameters.UDP_PARSER + "0" + Parameters.UDP_END_PARSE).getBytes();
						 DatagramPacket replay5 = new DatagramPacket(bufferStat, bufferStat.length,request.getAddress(),Parameters.UDP_PORT_REPLICA_LEAD);
						aSocket.send(replay5);	
						System.out.println("Transfer failed, NACK sent " + new String(replay5.getData()));
						
					}
			
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
	
	
	

	public int extractIP ( String inputIP ){
		int accessIP=0;
		try
		{
		int index = inputIP.indexOf(".");
		accessIP = Integer.parseInt(inputIP.substring(0, index));
		}
		catch ( Exception e ) {e.printStackTrace();}
		return accessIP;
	}
	
	public int getPortPerIP (String IPadress) {
		int portNumber = 0;
		int tempIP = 0;
		
		tempIP = extractIP(IPadress);
		
		if ( tempIP == Parameters.AS_IP_ADDRESS ){
			portNumber = Parameters.UDP_PORT_REPLICA_B_AS;
			
		}else if ( tempIP == Parameters.EU_IP_ADDRESS ){
			portNumber =Parameters.UDP_PORT_REPLICA_B_EU;
		}
		else if ( tempIP == Parameters.NA_IP_ADDRESS ){
			portNumber = Parameters.UDP_PORT_REPLICA_B_NA;
		}
		
		return portNumber;
	}
}
