package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * 
 * @author Jonathan Böcker 20015-04-27
 *
 */
public class DiscoveryListener extends Thread {
	private Controller controller;

	public DiscoveryListener(Controller controller) {
		this.controller = controller;
	}

	@SuppressWarnings("resource")
	public void run() {
		DatagramSocket recieveSocket = null;
		DatagramPacket packet = null;
		
		
		

		try {
			recieveSocket = new DatagramSocket(new InetSocketAddress(controller
					.loadServerConfig().getDiscoveryPort()));
			recieveSocket.setBroadcast(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				byte[] recvBuf = new byte[15000];
				packet = new DatagramPacket(recvBuf, recvBuf.length);
		        recieveSocket.receive(packet);
				System.out.println("Recieved packet from" + packet.getAddress());
				DatagramPacket sendPacket = new DatagramPacket(packet.getData(), packet.getData().length, packet.getAddress(), packet.getPort());
				recieveSocket.send(sendPacket);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
