package Main;

import replicaA.ReplicaA;
import replicaManager.ReplicaManager;
import frontEnd.FrontEnd;

/**
 * This class is used to run the project and all its test cases
 * @author Mehrdad Dehdashti
 */
public class Main 
{
	public static void main(String[] args) 
	{
		new ReplicaManager();
		new ReplicaA(); // TODO : ask TA, should the replicas be initiated or by replica manager through UDP
		new Thread(new FrontEnd(args)).start();
		
		// TODO : all test cases go here
	}
}