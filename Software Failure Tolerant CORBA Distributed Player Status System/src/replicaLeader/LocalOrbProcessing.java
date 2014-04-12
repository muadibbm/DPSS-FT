package replicaLeader;

import java.io.BufferedReader;
import java.io.FileReader;

import org.omg.CORBA.ORB;

import replicaLeader.Parameters.METHOD_CODE;

public class LocalOrbProcessing 
{ 
	/*private String m_functionName;
	private String m_FirstName;
	private String m_LastName;
	private String m_Age;
	private String m_UserID;
	private String m_password;
	private String m_IPAddress;*/
	
	
	// extract required parameters from the input data
	private String[] extractParameters(String p_input)
	{
		String l_segments[] = p_input.split(Parameters.UDP_PARSER);
		
		if(l_segments != null)
		{
			return l_segments;			
		}
		
		System.out.println("LocalOrbProcessing.extractParameters: failed to parse input data");
		return null;
	}
	
	// extract ip address, get required Local Server <location> reference
	private GameServerInterface  getServerReference(String p_IPAddress)
	{
		GameServerInterface aGameServerRef = null;
		String args[] = null;
		
		try
		{
			if("132".equals(p_IPAddress.substring(0,3)))
			{
				ORB orb = ORB.init(args, null);
				
				BufferedReader br = new BufferedReader(new FileReader("ior_NorthAmerica.txt"));
				String ior = br.readLine();
				br.close();
		
				org.omg.CORBA.Object o = orb.string_to_object(ior);
				aGameServerRef = GameServerInterfaceHelper.narrow(o);			
			}
		
			else if("92".equals(p_IPAddress.substring(0,2)))
			{
				ORB orb = ORB.init(args, null);
				
				BufferedReader br = new BufferedReader(new FileReader("ior_Europe.txt"));
				String ior = br.readLine();
				br.close();
		
				org.omg.CORBA.Object o = orb.string_to_object(ior);
				aGameServerRef = GameServerInterfaceHelper.narrow(o);	
			}
		
			else if("182".equals(p_IPAddress.substring(0,3)))
			{
				ORB orb = ORB.init(args, null);
				
				BufferedReader br = new BufferedReader(new FileReader("ior_Asia.txt"));
				String ior = br.readLine();
				br.close();
		
				org.omg.CORBA.Object o = orb.string_to_object(ior);
				aGameServerRef = GameServerInterfaceHelper.narrow(o);	
			}
		
			else
			{
				System.out.println("LocalOrbProcessing.getServerReference : Error - IP Location Index not Valid/n");
				aGameServerRef =  null;
			}
		}
		catch(Exception e)
		{
			aGameServerRef = null;
			e.printStackTrace();					
		}
		return aGameServerRef;
	}
	
	protected String performRMI(String p_input)
	{
		String[] l_ParamArray = extractParameters(p_input);
		
		if(l_ParamArray != null)
		{
			int l_numElements = l_ParamArray.length;
			GameServerInterface l_LocalGameServerReference =  getServerReference(l_ParamArray[l_numElements - 1]);
			
			if(l_LocalGameServerReference == null)
			{
				System.out.println("LocalOrbProcessing.performRMI : Error - Cannot perform RMI, GameServerInterface = NULL/n");
				return "0";
			}
			
			METHOD_CODE l_functionValue = METHOD_CODE.valueOf(l_ParamArray[0]);
			
			// Send CREATE PLAYER ACCOUNT
			// createPlayerAccount(String FirstName, String LastName, String Age, String Username, String Password, String IPAddress)
			if(l_functionValue.equals(0)) 
			{
				System.out.println("LocalOrbProcessing.performRMI : Creating Player Account Request/n");
				if(l_numElements == 6)
				{
					String l_MethodStatus =  l_LocalGameServerReference.createPlayerAccount(l_ParamArray[0], l_ParamArray[1], l_ParamArray[2], l_ParamArray[3], l_ParamArray[4], l_ParamArray[5]);
					if(l_MethodStatus == "SignUpsuccessful")
					{
						return "1";
					}
					return "0";
				}
				else
				{
					System.out.println("LocalOrbProcessing.performRMI : Eror: Have not parsed enough params for create player account/n");
					return "0";
				}
			}
			
			// Send PLAYER SIGN IN
			// playerSignIn(String UserName, String Password, String IPAddres);
			else if(l_functionValue.equals(1)) 
			{
				System.out.println("LocalOrbProcessing.performRMI : Player Sign In Request/n");
				if(l_numElements == 3)
				{
					String l_MethodStatus =  l_LocalGameServerReference.playerSignIn(l_ParamArray[0], l_ParamArray[1], l_ParamArray[2]);
					if(l_MethodStatus == "Login successful")
					{
						return "1";
					}
					return "0";
				}
				else
				{
					System.out.println("LocalOrbProcessing.performRMI : Eror: Have not parsed enough params for player sign in/n");
					return "0";
				}
			}
			
			// Send PLAYER SIGN OUT
			// playerSignOut(String p_Username, String IPAddress) 
			else if(l_functionValue.equals(2)) 
			{
				System.out.println("LocalOrbProcessing.performRMI : Player Sign Out Request/n");
				if(l_numElements == 2)
				{
					String l_MethodStatus =  l_LocalGameServerReference.playerSignOut(l_ParamArray[0], l_ParamArray[1]);
					if(l_MethodStatus == "Successfully Signed Out.")
					{
						return "1";
					}
					return "0";
				}
				else
				{
					System.out.println("LocalOrbProcessing.performRMI : Eror: Have not parsed enough params for player sign out/n");
					return "0";
				}
			}
			
			// Send PLAYER TRANSFER ACCOUNT
			//String transferAccount(String p_Username, String p_Password, String p_oldIPAddress, String p_newIPAddress)
			else if(l_functionValue.equals(3)) 
			{
				System.out.println("LocalOrbProcessing.performRMI : Player Account Transfer Request/n");
				if(l_numElements == 4)
				{
					String l_MethodStatus =  l_LocalGameServerReference.playerSignOut(l_ParamArray[0], l_ParamArray[1]);
					if(l_MethodStatus == "Account Transfer Complete")
					{
						return "1";
					}
					return "0";
				}
				else
				{
					System.out.println("LocalOrbProcessing.performRMI : Eror: Have not parsed enough params for player account transfer/n");
					return "0";
				}
			}
			
			// Send GET PLAYER STATUS
			// getPlayerStatus(String AdminUserName, String AdminPassword, String AdminIPAddress);
			else if(l_functionValue.equals(4)) 
			{
				System.out.println("LocalOrbProcessing.performRMI : Get Player Status Request/n");
				if(l_numElements == 3)
				{
					String l_MethodStatus =  l_LocalGameServerReference.getPlayerStatus(l_ParamArray[0], l_ParamArray[1], l_ParamArray[2]);
					if(l_MethodStatus != "")
					{
						return "1";
					}
					return "0";
				}
				else
				{
					System.out.println("LocalOrbProcessing.performRMI : Eror: Have not parsed enough params for get player status/n");
					return "0";
				}
			}
			
			// Send SUSPEND ACCOUNT
			// suspendAccount(String p_AdminUserName, String p_AdminPassword, String p_AdminIPAddress, String p_UsernametoSuspend) 
			else if(l_functionValue.equals(5)) 
			{
				System.out.println("LocalOrbProcessing.performRMI : Get Suspend Account Request/n");
				if(l_numElements == 4)
				{
					String l_MethodStatus =  l_LocalGameServerReference.getPlayerStatus(l_ParamArray[0], l_ParamArray[1], l_ParamArray[2]);
					if(l_MethodStatus != "Account Suspension Confirmed.")
					{
						return "1";
					}
					return "0";
				}
				else
				{
					System.out.println("LocalOrbProcessing.performRMI : Eror: Have not parsed enough params for suspend account/n");
					return "0";
				}
			}
			
			// START GAME SERVER
			// suspendAccount(String p_AdminUserName, String p_AdminPassword, String p_AdminIPAddress, String p_UsernametoSuspend) 
			else if(l_functionValue.equals(6)) 
			{
				System.out.println("LocalOrbProcessing.performRMI : Start Game Server Request/n");
				
				// instantiate the GAME SERVERS
			}		
		}
		System.out.println("LocalOrbProcessing.performRMI : Eror: Parsing for method selection not done right/n");
		return "0";	
	}
}