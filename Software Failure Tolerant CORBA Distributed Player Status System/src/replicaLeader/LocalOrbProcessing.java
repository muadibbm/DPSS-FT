package replicaLeader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

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
	
	
/*	// extract required parameters from the input data
	private String[] extractParameters(String p_input)
	{
		String l_segments[] = p_input.split(Parameters.UDP_PARSER);
		
		if(l_segments != null)
		{
			
			System.out.println("LocalOrbProcessing.extractParameters: failed to parse input data" + l_segments);
			return l_segments;			
		}
		
		System.out.println("LocalOrbProcessing.extractParameters: failed to parse input data");
		return null;
	}
*/	
	
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
		
			else if("93".equals(p_IPAddress.substring(0,2)))
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
	
	protected String performRMI(String p_input) throws InvalidName, ServantAlreadyActive, WrongPolicy, ObjectNotActive, FileNotFoundException, AdapterInactive
	{
		String l_ParamArray[] = p_input.split(Parameters.UDP_PARSER);
		
		if(l_ParamArray != null)
		{
			
			int l_numElements = l_ParamArray.length;
			
			
			METHOD_CODE l_functionValue = METHOD_CODE.valueOf(l_ParamArray[0]);
			
			// Send CREATE PLAYER ACCOUNT
			// createPlayerAccount(String FirstName, String LastName, String Age, String Username, String Password, String IPAddress)
			if(l_functionValue == METHOD_CODE.CREATE_ACCOUNT) 
			{
				System.out.println("LocalOrbProcessing.performRMI : Creating Player Account Request/n");
				if(l_numElements == 7)
				{
					
					GameServerInterface l_LocalGameServerReference =  getServerReference(l_ParamArray[6]);
					
					if(l_LocalGameServerReference == null)
					{
						System.out.println("LocalOrbProcessing.performRMI : Error - Cannot perform RMI, GameServerInterface = NULL/n");
						return "0";
					}
					
					String l_MethodStatus =  l_LocalGameServerReference.createPlayerAccount(l_ParamArray[1], l_ParamArray[2], l_ParamArray[3], l_ParamArray[4], l_ParamArray[5], l_ParamArray[6]);
					if(l_MethodStatus.equals("SignUpsuccessful"))
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
			else if(l_functionValue == METHOD_CODE.PLAYER_SIGN_IN) 
			{
				System.out.println("LocalOrbProcessing.performRMI : Player Sign In Request/n");
				if(l_numElements == 4)
				{

					GameServerInterface l_LocalGameServerReference =  getServerReference(l_ParamArray[3]);
					
					if(l_LocalGameServerReference == null)
					{
						System.out.println("LocalOrbProcessing.performRMI : Error - Cannot perform RMI, GameServerInterface = NULL/n");
						return "0";
					}
					
					String l_MethodStatus =  l_LocalGameServerReference.playerSignIn(l_ParamArray[1], l_ParamArray[2], l_ParamArray[3]);
					if(l_MethodStatus.equals("Login successful"))
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
			else if(l_functionValue == METHOD_CODE.PLAYER_SIGN_OUT) 
			{
				System.out.println("LocalOrbProcessing.performRMI : Player Sign Out Request/n");
				if(l_numElements == 3)
				{

					GameServerInterface l_LocalGameServerReference =  getServerReference(l_ParamArray[2]);
					
					if(l_LocalGameServerReference == null)
					{
						System.out.println("LocalOrbProcessing.performRMI : Error - Cannot perform RMI, GameServerInterface = NULL/n");
						return "0";
					}
					
					String l_MethodStatus =  l_LocalGameServerReference.playerSignOut(l_ParamArray[1], l_ParamArray[2]);
					if(l_MethodStatus.equals("Successfully Signed Out."))
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
			else if(l_functionValue == METHOD_CODE.TRANSFER_ACCOUNT) 
			{
				System.out.println("LocalOrbProcessing.performRMI : Player Account Transfer Request/n");
				if(l_numElements == 5)
				{

					GameServerInterface l_LocalGameServerReference =  getServerReference(l_ParamArray[3]);
					
					if(l_LocalGameServerReference == null)
					{
						System.out.println("LocalOrbProcessing.performRMI : Error - Cannot perform RMI, GameServerInterface = NULL/n");
						return "0";
					}
					
					String l_MethodStatus =  l_LocalGameServerReference.transferAccount(l_ParamArray[1], l_ParamArray[2], l_ParamArray[3], l_ParamArray[4]);
					if(l_MethodStatus.equals("Account Transfer Complete"))
					{
						return "1";
					}
					System.out.println("LocalOrbProcessing.performRMI : Player Account Transfer Request - " + l_ParamArray[1] + l_ParamArray[2] + l_ParamArray[3] + l_ParamArray[4]);
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
			else if(l_functionValue ==  METHOD_CODE.GET_PLAYER_STATUS) 
			{
				System.out.println("LocalOrbProcessing.performRMI : Get Player Status Request/n");
				if(l_numElements == 4)
				{

					GameServerInterface l_LocalGameServerReference =  getServerReference(l_ParamArray[3]);
					
					if(l_LocalGameServerReference == null)
					{
						System.out.println("LocalOrbProcessing.performRMI : Error - Cannot perform RMI, GameServerInterface = NULL/n");
						return "0";
					}
					
					String l_MethodStatus =  l_LocalGameServerReference.getPlayerStatus(l_ParamArray[1], l_ParamArray[2], l_ParamArray[3]);
					if(!l_MethodStatus.equals(""))
					{
						return l_MethodStatus;
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
			else if(l_functionValue == METHOD_CODE.SUSPEND_ACCOUNT) 
			{
				System.out.println("LocalOrbProcessing.performRMI : Get Suspend Account Request/n");
				if(l_numElements == 5)
				{

					GameServerInterface l_LocalGameServerReference =  getServerReference(l_ParamArray[3]);
					
					if(l_LocalGameServerReference == null)
					{
						System.out.println("LocalOrbProcessing.performRMI : Error - Cannot perform RMI, GameServerInterface = NULL/n");
						return "0";
					}
					
					String l_MethodStatus =  l_LocalGameServerReference.suspendAccount(l_ParamArray[1], l_ParamArray[2], l_ParamArray[3], l_ParamArray[4]);
					if(l_MethodStatus.equals("Account Suspension Confirmed."))
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
		}
		System.out.println("LocalOrbProcessing.performRMI : Eror: Parsing for method selection not done right");
		return "0";
		
	}
}
