package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

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
		DatagramSocket sendSocket = null;
		DatagramPacket packet = null;
		DatagramPacket answerPacket = null;
		byte[] buf;

		try {
			recieveSocket = new DatagramSocket(new InetSocketAddress(controller
					.loadServerConfig().getDiscoveryPort()));

		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				packet = new DatagramPacket(new byte[10], 10);
				recieveSocket.receive(packet);
				System.out.println("Recieved packet from" + packet.getAddress());
				buf = packet.getData();
				answerPacket = new DatagramPacket(buf, buf.length);
				sendSocket = new DatagramSocket(packet.getSocketAddress());
				sendSocket.send(answerPacket);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
