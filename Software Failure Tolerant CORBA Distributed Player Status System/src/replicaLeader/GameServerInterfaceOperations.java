package replicaLeader;

/**
 * Interface definition: GameServerInterface.
 * 
 * @author OpenORB Compiler
 */
public interface GameServerInterfaceOperations
{
    /**
     * Operation createPlayerAccount
     */
    public String createPlayerAccount(String FirstName, String LastName, String Age, String Username, String Password, String IPAddress);

    /**
     * Operation playerSignIn
     */
    public String playerSignIn(String UserName, String Password, String IPAddres);

    /**
     * Operation playerSignOut
     */
    public String playerSignOut(String Username, String IPAddress);

    /**
     * Operation transferAccount
     */
    public String transferAccount(String Username, String Password, String oldIPAddress, String newIPAddress);

    /**
     * Operation getPlayerStatus
     */
    public String getPlayerStatus(String AdminUserName, String AdminPassword, String AdminIPAddress);

    /**
     * Operation suspendAccount
     */
    public String suspendAccount(String AdminUserName, String AdminPassword, String AdminIPAddress, String UsernametoSuspend);

}
