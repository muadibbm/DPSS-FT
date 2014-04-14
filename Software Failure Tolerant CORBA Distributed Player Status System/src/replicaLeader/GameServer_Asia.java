package replicaLeader;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class GameServer_Asia extends Thread
{
	
	public void run()
	{
		try 
		{
			StartGameServer_Asia();
		} 
		catch (InvalidName | ServantAlreadyActive | WrongPolicy
				| ObjectNotActive | FileNotFoundException | AdapterInactive e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @throws InvalidName 
	 * @throws WrongPolicy 
	 * @throws ServantAlreadyActive 
	 * @throws ObjectNotActive 
	 * @throws FileNotFoundException 
	 * @throws AdapterInactive 
	 */
	private void StartGameServer_Asia() throws InvalidName, ServantAlreadyActive, WrongPolicy, ObjectNotActive, FileNotFoundException, AdapterInactive 
	{
		System.out.println("GameServer_Asia.StartGameServer_Asia: Server going Online");
		
		String[] args = null;
		
		// TODO Auto-generated method stub
		// initializing ORB for server instance
		ORB orb = ORB.init(args,null);
		POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
		
		int UDPPortNumber = 3469;
		
		GameServerImpl agameServer = new GameServerImpl("Asia",UDPPortNumber);
		byte[] id = rootPOA.activate_object(agameServer);
		org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);
		
		String ior = orb.object_to_string(ref);
		System.out.println(ior);
				
		PrintWriter file = new PrintWriter("ior_Asia.txt");
		file.println(ior);
		file.close();
				
		//activate the orb
		rootPOA.the_POAManager().activate();
		orb.run();		

	}

}
