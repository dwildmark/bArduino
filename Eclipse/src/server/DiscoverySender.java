package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * This is a broadcasting thread that simply sends a broadcast message on all
 * available interfaces of the server. This is used to let the Arduino capture
 * the message and find the server's address. Broadcast message is sent when 
 * the server admin presses a button in the server interface.
 * 
 * @author DennisW
 *
 */
public class DiscoverySender extends Thread {
	private DatagramSocket socket;
	private int broadcastPort = 28780;

	public void sendBroadcast() {
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);

			// Data to send
			byte[] data = "HELLO_BARDUINO".getBytes();

			// Loops through all available interfaces on the machine
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = (NetworkInterface) interfaces
						.nextElement();

				// Ignore the loopback interface
				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue;
				}

				// For each interface with broadcast available, send a broadcast
				// packet
				for (InterfaceAddress interfaceAddress : networkInterface
						.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null) {
						continue;
					}

					// Send the broadcast package
					try {
						DatagramPacket sendPacket = new DatagramPacket(data,
								data.length, broadcast, broadcastPort);
						socket.send(sendPacket);
					} catch (Exception e) {
					}
				}
			}

		} catch (Exception e) {

		}
	}
}
