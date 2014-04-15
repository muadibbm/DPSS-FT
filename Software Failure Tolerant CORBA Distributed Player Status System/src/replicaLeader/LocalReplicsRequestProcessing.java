package replicaLeader;

public class LocalReplicsRequestProcessing 
{
	static String m_LeaderResultProcessed = "";
	static String m_Replica_A_Processed = "";
	static String m_Replica_B_Processed = "";
	
	private static int m_checkedByPrevReplica = 0;
	
	protected static void CompareResults()
	{
		if(m_LeaderResultProcessed != "" && m_Replica_A_Processed != "" && m_Replica_B_Processed != "")
		{
			System.out.println("Result Processed By Leader - " + m_LeaderResultProcessed);
			System.out.println("Result Processed By Replica A - " + m_Replica_A_Processed);
			System.out.println("Result Processed By Replica B - " + m_Replica_B_Processed);
			
			if(m_LeaderResultProcessed.equals(m_Replica_A_Processed) || m_LeaderResultProcessed.equals(m_Replica_B_Processed))
			{
				String l_rmdatagram = "";
				
				if(!m_LeaderResultProcessed.equals(m_Replica_A_Processed))
				{
					l_rmdatagram = "RA";
				}
				
				else if(!m_LeaderResultProcessed.equals(m_Replica_B_Processed))
				{
					l_rmdatagram = "RB";
				}

				// sending packet to Leader
				m_LeaderResultProcessed = Parameters.LR_NAME + Parameters.UDP_PARSER + m_LeaderResultProcessed;
				UDP_replicaLeader.sendPacket(m_LeaderResultProcessed, Parameters.UDP_PORT_FE);
				
				
				// sending packet to Replica Manager
				if(!l_rmdatagram.equals(""))
				{
					l_rmdatagram =  Parameters.LR_NAME + Parameters.UDP_PARSER + l_rmdatagram;
					System.out.println("Data gram sent to replica manager l_rmdatagram - " + l_rmdatagram);
					UDP_replicaLeader.sendPacket(l_rmdatagram, Parameters.UDP_PORT_REPLICA_MANAGER);
				}
			}
		}
		else
			m_checkedByPrevReplica += 1;
		
		System.out.println("Result tried to be Processed By a Replica - " + m_checkedByPrevReplica);
		
	}
}
