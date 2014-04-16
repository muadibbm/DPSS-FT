package replicaLeader;

public class LocalReplicsRequestProcessing 
{
	static String m_LeaderResultProcessed;
	static String m_Replica_A_Processed;
	static String m_Replica_B_Processed;
	static boolean m_HasBeenProcessed;
	
	private static int m_checkedByPrevReplica;
	
	protected static void CompareResults()
	{
		System.out.println("Result Processed By Leader - " + m_LeaderResultProcessed);
		System.out.println("Result Processed By Replica A - " + m_Replica_A_Processed);
		System.out.println("Result Processed By Replica B - " + m_Replica_B_Processed);
		
		if(m_LeaderResultProcessed != null && m_Replica_A_Processed != null && m_Replica_B_Processed != null)
		{
	
			if(m_HasBeenProcessed == true)
			{
				return;
			}
			
			System.out.println("All # Results are Valid (comparision Underway)");
			
			String l_leaderData_end_parser = m_LeaderResultProcessed + "/" + "$";
			String l_segments_Leader[] = l_leaderData_end_parser.split(Parameters.UDP_PARSER);
			String l_segments_A[] = m_Replica_A_Processed.split(Parameters.UDP_PARSER);
			String l_segments_B[] = m_Replica_B_Processed.split(Parameters.UDP_PARSER);
					
			System.out.println("TEsting 1");
			
			// check if all results are same
			if(l_segments_Leader[0].equals(l_segments_A[0]) || l_segments_Leader[0].equals(l_segments_B[0]))
			{
				String l_rmdatagram = "";
				if(l_segments_Leader.length < 4 && l_segments_A.length < 4 && l_segments_B.length < 4)
				{
					if(!l_segments_Leader[0].equals(l_segments_A[0]))
					{
						l_rmdatagram = "RA";
					}
				
					else if(!l_segments_Leader[0].equals(l_segments_B[0]))
					{
						l_rmdatagram = "RB";
					}
					
					// Create a data packet for FE
					String l_Data_FE = Parameters.LR_NAME;
					String result = "";
					
					// sending packet to Leader
					for(int i = 0; i < l_segments_Leader.length; i++)
					{
						result  = result + Parameters.UDP_PARSER + l_segments_Leader[i];
					}
								
					l_Data_FE = l_Data_FE + result;
					
					
					// Sending datagram to Front End the result of Leader
					System.out.println("LocalReplicsRequestProcessing.CompareResults: to Front End - l_Data: " + l_Data_FE);
					UDP_replicaLeader.sendPacket(l_Data_FE, Parameters.UDP_PORT_FE);
									
					// sending packet to Replica Manager
					// If Replica Manager Datagram is not empty, send it.
					if(!l_rmdatagram.equals(""))
					{
						l_rmdatagram =  Parameters.LR_NAME + Parameters.UDP_PARSER + l_rmdatagram;
						System.out.println("Data gram sent to replica manager l_rmdatagram - " + l_rmdatagram);
						UDP_replicaLeader.sendPacket(l_rmdatagram, Parameters.UDP_PORT_REPLICA_MANAGER);
					}
					
					
				}
				else // for get player status
				{
					// m_LeaderResultProcessed is 1/NA/0/0/EU/0/0/AS/0/0
					String Temp_leader = m_LeaderResultProcessed.substring(2,m_LeaderResultProcessed.length());
					String Temp_A = m_Replica_A_Processed.substring(2,m_Replica_A_Processed.length());
					String Temp_B = m_Replica_B_Processed.substring(2,m_Replica_B_Processed.length());
					
					
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
					
					// Fil up the Gata gram to set FE
					String l_Data_FE = Parameters.LR_NAME ;
					String result = "";
					
					// sending packet to Leader
					for(int i = 0; i < l_segments_Leader.length; i++)
					{
						result  = result + Parameters.UDP_PARSER + l_segments_Leader[i];
					}
								
					l_Data_FE = l_Data_FE + result;
					
					
					// Sending datagram to Front End the result of Leader
					System.out.println("LocalReplicsRequestProcessing.CompareResults: to Front End - l_Data: " + l_Data_FE);
					UDP_replicaLeader.sendPacket(l_Data_FE, Parameters.UDP_PORT_FE);
									
					// sending packet to Replica Manager
					// If Replica Manager Datagram isnot empty, send it.
					if(!l_rmdatagram.equals(""))
					{
						l_rmdatagram =  Parameters.LR_NAME + Parameters.UDP_PARSER + l_rmdatagram;
						System.out.println("Data gram sent to replica manager l_rmdatagram - " + l_rmdatagram);
						UDP_replicaLeader.sendPacket(l_rmdatagram, Parameters.UDP_PORT_REPLICA_MANAGER);
					}
				}				
				m_HasBeenProcessed = true;
			}
			
			
		}
		// If LR RA and RB have not yet returned a value
		else
		{			
			m_checkedByPrevReplica += 1;
			System.out.println("Result tried to be Processed By a Replica - " + m_checkedByPrevReplica);
		}
		
		// Increment the check
		// if check reaches 2, send data to FE
		if(m_checkedByPrevReplica == 2)
		{
			//m_LeaderResultProcessed = Parameters.LR_NAME + Parameters.UDP_PARSER + m_LeaderResultProcessed;
			String FEDAta = Parameters.LR_NAME + Parameters.UDP_PARSER + m_LeaderResultProcessed;
			System.out.println("LocalReplicsRequestProcessing.CompareResults: to Front End - FEDAta: " + FEDAta);
			UDP_replicaLeader.sendPacket(FEDAta, Parameters.UDP_PORT_FE);
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
