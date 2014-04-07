package dpss;

/**
 * Interface definition: interfaceIDL.
 * 
 * @author OpenORB Compiler
 */
public interface interfaceIDLOperations
{
    /**
     * Operation createPlayerAccount
     */
    public boolean createPlayerAccount(String pFirstName, String pLastName, int pAge, String pUsername, String pPassword, String pIPAddress);

    /**
     * Operation playerSignIn
     */
    public boolean playerSignIn(String pUsername, String pPassword, String pIPAddress);

    /**
     * Operation playerSignOut
     */
    public boolean playerSignOut(String pUsername, String pIPAddress);

    /**
     * Operation transferAccount
     */
    public boolean transferAccount(String pUsername, String pPassword, String pOldIPAddress, String pNewIPAddress);

    /**
     * Operation getPlayerStatus
     */
    public boolean getPlayerStatus(String pAdminUsername, String pAdminPassword, String pIPAddress);

    /**
     * Operation suspendAccount
     */
    public boolean suspendAccount(String pAdminUsername, String pAdminPassword, String pIPAddress, String pUsernameToSuspend);

}
