//http://eclipsecorba.sourceforge.net/update

package replicaLeader;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GameServerImpl extends GameServerInterfacePOA 
{
	
	protected Hashtable<String, List<UserData>> UserData_HashTable; 
	public UDPPeer udpPeer;
	
	private static Map<String, Integer> m_UDPLocation_PortNumber_Map; 
	
	
	public String m_Location;
	
	public static String m_finalData;
	private String l_Server_Data_NA, l_Server_Data_EU, l_Server_Data_AS;
	
	GameServerImpl(String ServerLocation, int UDPPortNumber)
	{
		m_UDPLocation_PortNumber_Map = new HashMap<String, Integer>();
		
		m_Location = ServerLocation;
		udpPeer = new UDPPeer(UDPPortNumber, this);
		udpPeer.start();
		GameServerImpl.m_UDPLocation_PortNumber_Map.put("NA", Parameters.UDP_PORT_REPLICA_LEAD_NA);
		GameServerImpl.m_UDPLocation_PortNumber_Map.put("AS", Parameters.UDP_PORT_REPLICA_LEAD_AS);
		GameServerImpl.m_UDPLocation_PortNumber_Map.put("EU", Parameters.UDP_PORT_REPLICA_LEAD_EU);
		
		UserData_HashTable = new Hashtable<String, List<UserData>>();
	}
	
	
//====================================================================================================	
	@Override
	public String createPlayerAccount(String p_FirstName, String p_LastName,
			String p_Age, String p_Username, String p_Password, String p_IPAddress) 
	{
		// TODO Auto-generated method stub
		String hashTable_token = "";
		
		if(p_Username != null)
			hashTable_token = p_Username.substring(0,1);
		else 
			return "Error: Username Null";
		
		// Search in existing hash table for current input
		System.out.println(p_Username + " - Trying to Sign up\n");
		List<UserData> ud_l = this.UserData_HashTable.get(hashTable_token);
		if (ud_l != null) 
		{
			for(UserData record : ud_l)
			{
				if(record.UserName.contains(p_Username))
				{
					System.out.println(p_Username + " - already exists in records. Cannot Sign Up. Error Sent to user.\n");
					return "Error: UserName already exists. Cannot Sign Up Again.";			
				}
			}
			
			// Entry not found, sign up; enter data in the list
			UserData userData_record = new UserData(p_FirstName, p_LastName, p_Age, p_Username, p_Password, p_IPAddress);
			ud_l.add(userData_record);
			System.out.println(p_Username + " - Successfully signed up. Message sent to user\n");
			return "SignUpsuccessful";
		}
		// Table row does not exist; create row, create list in row, put user data
		// creating new list
		List<UserData> new_udl = new ArrayList<UserData>();
		// Creating new UserData record
		UserData userData_record = new UserData(p_FirstName, p_LastName, p_Age, p_Username, p_Password, p_IPAddress);
		// Put new user data record in new list
		new_udl.add(userData_record);
		// Put list in the hash table with a token of first char of user name
		this.UserData_HashTable.put(hashTable_token,new_udl);
		System.out.println(p_Username + " - Successfully signed up. Message sent to user\n");
		
		System.out.println("Hash Table: " + UserData_HashTable.toString());
		
		return "SignUpsuccessful";
	}

	
//=================================================================================================================================
	@Override
	public String playerSignIn(String p_Username, String p_Password, String p_IPAddres) 
	{
		// TODO Auto-generated method stub
		String hashTable_token = "";
		
		if(p_Username != null)
			hashTable_token = p_Username.substring(0,1);
		else 
			return "Error: Username Null";
		
		System.out.println(p_Username+" - Trying to Sign in\n");
		
		List<UserData> ud_l = this.UserData_HashTable.get(hashTable_token);
		if (ud_l != null) 
		{
			for(UserData record : ud_l)
			{
				if(record.UserName.contains(p_Username))
				{
					if(record.Password.equals(p_Password))
					{
						System.out.println(p_Username+" - Entered Correct Password \n");
						
						String player_status = record.getLoginStatus();
						if(player_status.contains("Online"))
						{
							System.out.println(p_Username+" - User already Online. Sever error sent to User\n");
							return "Error: Login Failed - You are already loggin in. First Log off to log in.";
						}
						else
						{
							boolean cur_status = record.setLoginStatus("Online");
							if(cur_status == true)
							{
								System.out.println(p_Username+" - Succefully signed in. Message sent to User\n");
								return "Login successful";
							}
							
							else
							{
								System.out.println(p_Username+" - Internal Error Occured. Verify Sign in Method. Sever error sent to User\n");
								System.out.println("Error: User login status inconsistancy. Should not happen\n");
								return "Error: Login failed - Server Error";
							}
						}
					}
					System.out.println(p_Username+" - Entered wrong Password. Message Sent\n");
					return "Login failed. Wrong Password.";
				}
			}
		}
		System.out.println(p_Username+" - Unknown user. User does not exist in records. Message sent to user\n");
		return "Login Failed. Profile Does not Exist. First Sign Up";
	}

	
//=========================================================================================================================================
	@Override
	public String playerSignOut(String p_Username, String IPAddress) 
	{
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				String hashTable_token = "";
				
				if(p_Username != null)
					hashTable_token = p_Username.substring(0,1);
				else 
					return "Error: Username Null";
				
				System.out.println(p_Username+" - Trying to Sign out\n");
				
				List<UserData> ud_l = this.UserData_HashTable.get(hashTable_token);
				if (ud_l != null) 
				{
					for(UserData record : ud_l)
					{
						if(record.UserName.contains(p_Username))
						{
							String player_status = record.getLoginStatus();
							if(player_status.contains("Online"))
							{
								boolean cur_status = record.setLoginStatus("Offline");
								if(cur_status == true)
								{
									System.out.println(p_Username+" - Successfully signed out. Message sent to user\n");
									return "Successfully Signed Out.";
								}
								else
								{
									System.out.println(p_Username+" - Internal Error Occured. Verify Sign out Method. Sever error sent to User\n");
									System.out.println("Error: User log off status inconsistancy. Should not happen\n");
									return "Error: Logoff failed - Server Error";
								}
							}
							else
							{
								System.out.println(p_Username+" - Unknown user. User does not exist in records. Message sent to user\n");
								return "Error: You have not logged in. First log in to log off.";
							}
						}
					}
				}
				System.out.println(p_Username+" - Unknown user. User does not exist in records. Message sent to user\n");
				return "Logoff Failed. Profile Does not Exist. First Sign Up";
	}
	
	
//=========================================================================================================================================
	
	private String GetLocationfromAddress(String p_IPAddress)
	{
		if("132".equals(p_IPAddress.substring(0,3)))
		{
			return "NA";
		}
	
		else if("93".equals(p_IPAddress.substring(0,2)))
		{
			return "EU";
		}
	
		else if("182".equals(p_IPAddress.substring(0,3)))
		{
			return "AS";
		}
		else
		{
			return "Error: Wrong IP Address";
		}
	}
	
	
	public String GetServerDetails()
	{
		int TotalPlayers = 0, PlayersOnline = 0;
		
		Set<String> keys = UserData_HashTable.keySet();
        for(String key: keys)
        {
        	List<UserData> ud_l = UserData_HashTable.get(key);
        	if (ud_l != null) 
    		{
    			for(UserData record : ud_l)
    			{
    				if(record.UserName != null && !record.UserName.isEmpty())
    				{
    					TotalPlayers = TotalPlayers + 1;
    					if(record.getLoginStatus().trim().equals("Online"))
    					{
    						PlayersOnline = PlayersOnline + 1;
    					}
    				}
    			}
    		}
        }
		return Integer.toString(PlayersOnline) + "/"+Integer.toString(TotalPlayers - PlayersOnline);
	}
	
//=========================================================================================================================================

	@Override
	public String transferAccount(String p_Username, String p_Password,
			String p_oldIPAddress, String p_newIPAddress) 
	{
		
		System.out.println("Hash Table: " + this.UserData_HashTable.toString());
		
		// TODO Auto-generated method stub
		String hashTable_token = "";
				
		if(p_Username != null)
			hashTable_token = p_Username.substring(0,1);
		else 
			return "Error: Username Null";
		
		System.out.println("Server trying to access "+p_Username+" Profile\n");
		
		List<UserData> ud_l = this.UserData_HashTable.get(hashTable_token);
		
		if (ud_l != null) 
		{
			for(UserData record : ud_l)
			{
				if(record.UserName.contains(p_Username))
				{
					if(record.Password.equals(p_Password))
					{
						System.out.println(p_Username+" - Entered Correct Password \n");
						// get old ip addr location
						String oldLocation = GetLocationfromAddress(p_oldIPAddress);
						String newLocation = GetLocationfromAddress(p_newIPAddress); 
					
						String UserInfo = record.Firstname + "/" + record.Lastname + "/" + record.Age + "/" + record.UserName + "/" + record.Password + "/" + record.IPAddress; 
						String p_Data = "T"+Integer.toString(m_UDPLocation_PortNumber_Map.get(oldLocation))+UserInfo;
						
						udpPeer.UDPSendRequestforData(p_Data, m_UDPLocation_PortNumber_Map.get(newLocation));
						System.out.println(oldLocation+ " Sending Request to " + newLocation);
						String UDP_PlayerAccountStatus_Message = "";
						boolean l_receiveData_Server = false;
						while(l_receiveData_Server == false)
						{
							System.out.println(p_oldIPAddress+" : "+p_Username+" - Inside while Loop");
						
							if(UDPPeer.m_PacketData != null)
							{
								System.out.println(p_oldIPAddress+" : "+p_Username+" - Data in UDP found");
								System.out.println(p_oldIPAddress+" : "+p_Username+ " - Data Received is: " + UDPPeer.m_PacketData + "\n");
								UDP_PlayerAccountStatus_Message = UDPPeer.m_PacketData;
								System.out.println("Transfer Status: " + UDP_PlayerAccountStatus_Message);
								UDPPeer.m_PacketData = null;
								l_receiveData_Server = true;
								String successCase = "SignUpsuccessful";
								if(UDP_PlayerAccountStatus_Message.trim().equals(successCase))
								{
									//	delete user account here
									ud_l.remove(record);
									// send successful messages to player 
									return "Account Transfer Complete";
									
								}
								else
								{
									return "Error: Transfer Not done. Retry";
								}
								
							}
						}
						
						
					}
					System.out.println(p_Username+" - Entered wrong Password. Message Sent\n");
					return "Authentication failed. Wrong Password.";
				}
			}
		}
		System.out.println(p_Username+" - Unknown user. User does not exist in records. Message sent to user\n");
		return "Action Failed. Profile Does not Exist. First Sign Up";
	}	
//=========================================================================================================================================

	@Override
	public String getPlayerStatus(String p_AdminUserName, String p_AdminPassword,
			String p_AdminIPAddress) 
	{
		String l_Location = "";
		String p_Data = "";
		
		
		
		if(p_AdminUserName.equals("Admin") && p_AdminPassword.equals("Admin"))
		{
			l_Location = GetLocationfromAddress(p_AdminIPAddress);
			
			if(l_Location == "NA")
			{
				p_Data = "P"+Integer.toString(m_UDPLocation_PortNumber_Map.get("NA"));
			}
		
			else if(l_Location == "EU")
			{
				p_Data = "P"+Integer.toString(m_UDPLocation_PortNumber_Map.get("EU"));
			}
		
			else if(l_Location == "AS")
			{
				p_Data = "P"+Integer.toString(m_UDPLocation_PortNumber_Map.get("AS"));
			}
			else
			{
				return l_Location;
			}
			
		}
		else
		{
			return "wrong user id or password !";
		}
		
		// TODO Auto-generated method stub
		for(Map.Entry<String, Integer> entry : GameServerImpl.m_UDPLocation_PortNumber_Map.entrySet())
		{
			if(p_Data.length() > 3)
			{
				if(!l_Location.equals(entry.getKey()))
				{
					udpPeer.UDPSendRequestforData(p_Data, entry.getValue());
					System.out.println(l_Location+ " Sending Request to " + entry.getKey());
					boolean l_receiveData_Server = false;
					while(l_receiveData_Server == false)
					{
						System.out.println(entry.getKey()+" : "+p_AdminUserName+" - Inside while Loop");
					
						if(!UDPPeer.m_PacketData.equals(""))
						{
							//System.out.println(entry.getKey()+" : "+p_AdminUserName+" - Data in UDP found");
							//System.out.println(entry.getKey()+" : "+p_AdminUserName+ " - Data Received is: " + UDPPeer.m_PacketData + "\n");
							if(entry.getKey().equals("NA"))
							{
								l_Server_Data_NA = UDPPeer.m_PacketData;
								//System.out.println("Server Data NA: " + l_Server_Data_NA);
							}
							
							else if(entry.getKey().equals("EU"))
							{
								l_Server_Data_EU = UDPPeer.m_PacketData;
								//System.out.println("Server Data EU: " + l_Server_Data_EU);
							}
							
							else if(entry.getKey().equals("AS"))
							{
								l_Server_Data_AS = UDPPeer.m_PacketData;
								//System.out.println("Server Data AS: " + l_Server_Data_AS);							
							}
							
							l_receiveData_Server = true;
							UDPPeer.m_PacketData = "";
						}
					}		
				}
				else
				{
					if(entry.getKey().equals("NA"))
					{
						l_Server_Data_NA = l_Location + Parameters.UDP_PARSER + this.GetServerDetails();// + Parameters.UDP_PARSER;
						//System.out.println("Server Data NA: " + l_Server_Data_NA);
					}
					
					else if(entry.getKey().equals("EU"))
					{
						l_Server_Data_EU = l_Location + Parameters.UDP_PARSER + this.GetServerDetails();// + Parameters.UDP_PARSER;
						//System.out.println("Server Data EU: " + l_Server_Data_EU);
					}
					
					else if(entry.getKey().equals("AS"))
					{
						l_Server_Data_AS = l_Location + Parameters.UDP_PARSER + this.GetServerDetails();// + Parameters.UDP_PARSER;
						//System.out.println("Server Data AS: " + l_Server_Data_AS);
					}
				}
			}
			
			else
			{
				return "Error: Communication among other Servers Failed; Cannot get Player Status Data";
			}
		}
		
		System.out.println("l_Server_Data_NA: " + l_Server_Data_NA);
		System.out.println("l_Server_Data_EU: " + l_Server_Data_EU);
		System.out.println("l_Server_Data_AS: " + l_Server_Data_AS);
		
		m_finalData = "1" + "/" + l_Server_Data_NA +"/" +l_Server_Data_EU +"/"+ l_Server_Data_AS;
		
		System.out.println("Final Data: " + m_finalData);
		return m_finalData;
	}


//=========================================================================================================================================

	@Override
	public String suspendAccount(String p_AdminUserName, String p_AdminPassword,
			String p_AdminIPAddress, String p_UsernametoSuspend) 
	{
		// TODO Auto-generated method stub
		
		if(p_AdminUserName.equals("Admin") && p_AdminPassword.equals("Admin"))
		{
			String hashTable_token = "";
				
			if(p_UsernametoSuspend != null)
				hashTable_token = p_UsernametoSuspend.substring(0,1);
			else 
				return "Error: Username Null";
		
			System.out.println("Server trying to access "+p_UsernametoSuspend+" Profile\n");
		
			List<UserData> ud_l = this.UserData_HashTable.get(hashTable_token);
			if (ud_l != null) 
			{
				for(UserData record : ud_l)
				{
					if(record.UserName.contains(p_UsernametoSuspend))
					{
						ud_l.remove(record);
						return "Account Suspension Confirmed.";
					}
				}
			}
			System.out.println(p_UsernametoSuspend+" - Unknown user. User does not exist in records. Message sent to user\n");
			return "Action Failed. Profile Does not Exist. First Sign Up";
		}
		else
		{
			return "Authentication Failed. Wrong Admin ID or Password";
		}
	}
}
	


