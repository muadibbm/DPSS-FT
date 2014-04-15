package replicaB;
import java.net.*;
import java.io.*;
import java.io.ObjectInputStream.GetField;

public class TestLeaderUDPServer {

		public static void main(String[] args) {
			// TODO Auto-generated method stub

			DatagramSocket aSocket = null;
			try {
				
				aSocket = new DatagramSocket(Parameters.UDP_PORT_REPLICA_LEAD);
				byte [] buffer = new byte [1000];
				System.out.println("Leader UP and RUNNING.....");
				while (true) {
					
					DatagramPacket request = new DatagramPacket(buffer, buffer.length);
					aSocket.receive(request);
					System.out.println( new String(request.getData()));
					//DatagramPacket replay = new DatagramPacket(request.getData(), request.getLength(),request.getAddress(),request.getPort());
					//aSocket.send(replay);
				}
				
			}
			catch (SocketException e)
			{
				System.out.println("Socket : " + e.getMessage());
			}
			catch (IOException e)
			{
				System.out.println("IO : " + e.getMessage());
			}
			finally {
				if (aSocket != null) {
					aSocket.close();
				}
			}
		}

	}
