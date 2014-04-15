package replicaLeader;

public class LocalRMRequestProcessing 
{
	protected void ProcessRMRequests(String p_input)
	{
		String l_ParamArray[] = p_input.split(Parameters.UDP_PARSER);
			
		System.out.println("LocalRMRequestProcessing.getMethodName: l_ParamArray[0].substring(0, 15) - " + l_ParamArray[0].substring(0, 15));
		
		if(l_ParamArray[0].substring(0, 15).equals("RESTART_REPLICA"))
		{
			// Creating Instances of Local Servers - North America, Europe and Asia
			GameServer_NorthAmerica l_GameServer_NorthAmerica = new GameServer_NorthAmerica();
			GameServer_Asia l_GameServer_Asia = new GameServer_Asia();
			GameServer_Europe l_GameServer_Europe = new GameServer_Europe();
			
			l_GameServer_NorthAmerica.start();
			l_GameServer_Asia.start();
			l_GameServer_Europe.start();
			
		}
	}

}
