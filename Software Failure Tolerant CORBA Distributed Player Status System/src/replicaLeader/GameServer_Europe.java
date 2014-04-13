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

public class GameServer_Europe {

	/**
	 * @param args
	 * @throws InvalidName 
	 * @throws WrongPolicy 
	 * @throws ServantAlreadyActive 
	 * @throws ObjectNotActive 
	 * @throws FileNotFoundException 
	 * @throws AdapterInactive 
	 */
	protected void StartGameServer_Europe() throws InvalidName, ServantAlreadyActive, WrongPolicy, ObjectNotActive, FileNotFoundException, AdapterInactive 
	{
		System.out.println("GameServer_Europe.StartGameServer_Europe: Server going Online");
		
		String[] args = null;
		
		// TODO Auto-generated method stub
		// initializing ORB for server instance
		ORB orb = ORB.init(args,null);
		POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
		
		int UDPPortNumber = 3474;
		
		GameServerImpl agameServer = new GameServerImpl("Europe",UDPPortNumber);
		byte[] id = rootPOA.activate_object(agameServer);
		org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);
		
		String ior = orb.object_to_string(ref);
		System.out.println(ior);
				
		PrintWriter file = new PrintWriter("ior_Europe.txt");
		file.println(ior);
		file.close();
				
		//activate the orb
		rootPOA.the_POAManager().activate();
		orb.run();		

	}

}
