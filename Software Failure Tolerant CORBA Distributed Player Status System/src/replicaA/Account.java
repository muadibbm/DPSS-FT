package replicaA;

/**
 * This is the Account class containing info about players and administrators
 * This class has specific implementation used by replicaA only
 * @author Mehrdad Dehdashti
 */
class Account
{
	private String aFirstName;
	private String aLastName;
	private int aAge;
	private String aUserName;
	private String aPassword;
	private String aIPAddress;
	private boolean bOnline;

	protected Account()
	{
		aFirstName = "";
		aLastName = "";
		aAge = 0;
		aUserName = "";
		aPassword = "";
		aIPAddress = "";
		bOnline = false;
	}

	protected Account(String pFirstName, String pLastName, int pAge, 
				String pUserName, String pPassword, String pIPAddress)
	{
		aFirstName = pFirstName;
		aLastName = pLastName;
		aAge = pAge;
		aUserName = pUserName;
		aPassword = pPassword;
		aIPAddress = pIPAddress;
		bOnline = false;
	}

	protected String getFirstName() {
		return aFirstName;
	}

	protected void setFirstName(String pFirstName) {
		aFirstName = pFirstName;
	}

	protected String getLastName() {
		return aLastName;
	}

	protected void setLastName(String pLastName) {
		aLastName = pLastName;
	}

	protected int getAge() {
		return aAge;
	}

	protected void setAge(int pAge) {
		aAge = pAge;
	}

	protected String getUserName() {
		return aUserName;
	}

	protected void setUserName(String pUserName) {
		aUserName = pUserName;
	}

	protected String getPassword() {
		return aPassword;
	}

	protected void setPassword(String pPassword) {
		aPassword = pPassword;
	}

	protected String getIPAddress() {
		return aIPAddress;
	}

	protected void setIPAddress(String pIPAddress) {
		aIPAddress = pIPAddress;
	}

	protected boolean isOnline() {
		return bOnline;
	}

	protected void setOnline(boolean pOnline) {
		bOnline = pOnline;
	}
}