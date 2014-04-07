package Main;

import replicaA.ReplicaA;
import frontEnd.FrontEnd;

/**
 * This class is used to run the project and all its test cases
 * @author Mehrdad Dehdashti
 */
public class Main 
{
	public static void main(String[] args) 
	{
		new FrontEnd(args);
		new ReplicaA();
		
		// TODO : all test cases go here
	}
}