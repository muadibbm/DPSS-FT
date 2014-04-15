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
			String l_segments_Leader[] = m_LeaderResultProcessed.split(Parameters.UDP_PARSER);
			String l_segments_A[] = m_Replica_A_Processed.split(Parameters.UDP_PARSER);
			String l_segments_B[] = m_Replica_B_Processed.split(Parameters.UDP_PARSER);
			
			String Temp_leader = m_LeaderResultProcessed.substring(2,m_LeaderResultProcessed.length());
			String Temp_A = m_Replica_A_Processed.substring(2,m_Replica_A_Processed.length());
			String Temp_B = m_Replica_B_Processed.substring(2,m_Replica_B_Processed.length());
			
			System.out.println("Result Processed By Leader - " + m_LeaderResultProcessed);
			System.out.println("Result Processed By Replica A - " + m_Replica_A_Processed);
			System.out.println("Result Processed By Replica B - " + m_Replica_B_Processed);
			
			// check if all results are same
			if(l_segments_Leader[0].equals(l_segments_A[0]) || l_segments_Leader[0].equals(l_segments_B[0]))
			{
				String l_rmdatagram = "";
				if(l_segments_Leader.length < 2)
				{
					if(!l_segments_Leader[0].equals(l_segments_A[0]))
					{
						l_rmdatagram = "RA";
					}
				
					else if(!l_segments_Leader[0].equals(l_segments_B[0]))
					{
						l_rmdatagram = "RB";
					}
				}
				else // for get player status
				{
					if(!l_segments_Leader[0].equals(l_segments_A[0]))
					{
						if(checkgetPlayerStatus(Temp_leader, Temp_A).equals("Problem"))
							l_rmdatagram =  "RA";
					
						System.out.println("LocalReplicsRequestProcessing.CompareResults: Get Player Status Check l_rmdatagram - " + l_rmdatagram);
					}
				
					else if(!l_segments_Leader[0].equals(l_segments_B[0]))
					{
						if(checkgetPlayerStatus(Temp_leader, Temp_B).equals("Problem"))
							l_rmdatagram =  "RB";
						System.out.println("LocalReplicsRequestProcessing.CompareResults: Get Player Status Check l_rmdatagram - " + l_rmdatagram);

					}
				}

				m_LeaderResultProcessed = Parameters.LR_NAME ;
				String result = "";
				
				// sending packet to Leader
				for(int i = 0; i < l_segments_Leader.length; i++)
				{
					result  = Parameters.UDP_PARSER + l_segments_Leader[i] + result;
				}
							
				m_LeaderResultProcessed = m_LeaderResultProcessed + result;
				
				UDP_replicaLeader.sendPacket(m_LeaderResultProcessed, Parameters.UDP_PORT_FE);
								
				// sending packet to Replica Manager
				if(l_rmdatagram.equals(""))
				{
					l_rmdatagram =  Parameters.LR_NAME + Parameters.UDP_PARSER + l_rmdatagram;
					System.out.println("Data gram sent to replica manager l_rmdatagram - " + l_rmdatagram);
					UDP_replicaLeader.sendPacket(l_rmdatagram, Parameters.UDP_PORT_REPLICA_MANAGER);
				}
			}
		}
		else
		{
			m_checkedByPrevReplica += 1;
			System.out.println("Result tried to be Processed By a Replica - " + m_checkedByPrevReplica);
		}		
	}
	
	
	private static String checkgetPlayerStatus(String A, String B)
	{
		String A_list[] = A.split(Parameters.UDP_PARSER);
		String B_list[] = B.split(Parameters.UDP_PARSER);
		
		if(A_list.length != B_list.length)
		{
			return "Problem";
		}
		
		//    NA/0/1/AS/0/1/EU/0/1/$
		
		for(int j = 0; j < 3; j++)
		{
			for(int i = 0; i < A_list.length; i++)
			{
				if(A_list[j*3] == B_list[i] && B_list[i] != Parameters.UDP_END_PARSE)
				{
					if(A_list[(j*3) + 1] == B_list[i+1])
					{
						if(A_list[(j*3) + 2] == B_list[i+2])
						{
							return "Good";
						}
					}
				}
			}
		}
		return "Problem"; 
	}
	
}
