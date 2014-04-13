package clients;

class ClientTestCase 
{
	public static void main(String[] args)
	{
		PlayerClient player = new PlayerClient("Player", args);
		AdministratorClient admin = new AdministratorClient("Admin", args);
		
		player.createPlayerAccount("Mehrdad", "Dehdashti", 24, "user123", "123456", "132.10.10.10");
		
		// TODO : Test Cases go HERE
	}
}