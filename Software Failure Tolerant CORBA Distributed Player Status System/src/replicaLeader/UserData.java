package replicaLeader;

public class UserData 
{
	public String Firstname;
	public String Lastname;
	public String Age;
	public String UserName;
	public String Password;
	public String IPAddress;
	public String Login_Status;
	
	UserData(String p_firstname, String p_lastname, String p_age, String p_username, String p_password, String p_ipaddr)
	{
		Firstname 	= p_firstname;
		Lastname 	= p_lastname;
		Age 		= p_age;
		UserName 	= p_username;
		Password 	= p_password;
		IPAddress 	= p_ipaddr;
		Login_Status = "Offline";
	}
	
	public boolean setLoginStatus(String p_status)
	{
		if(Login_Status != p_status)
		{
			Login_Status = p_status;
			return true;
		}
		return false;
	}
	
	public String getLoginStatus()
	{
		return Login_Status;
	}
}
