package clients;

class ClientTestCase 
{
	public static void main(String[] args)
	{
		PlayerClient player = new PlayerClient("Player", args);
		AdministratorClient admin = new AdministratorClient("Admin", args);
		
		player.createPlayerAccount("Mehrdad", "Dehdashti", 24, "user123", "123456", "132.10.10.10");
		player.playerSignIn("user123", "123456", "132.10.10.10");
		//admin.getPlayerStatus("Admin", "Admin", "132.20.20.20");
		player.playerSignOut("user123", "132.10.10.10");
		//admin.getPlayerStatus("Admin", "Admin", "132.20.20.20");
		player.transferAccount("user123", "123456", "132.10.10.10", "93.10.10.10");
		//admin.getPlayerStatus("Admin", "Admin", "93.20.20.20");
		admin.suspendAccount("Admin", "Admin", "93.20.20.20", "user123");
		//admin.getPlayerStatus("Admin", "Admin", "93.20.20.20");
	}
}