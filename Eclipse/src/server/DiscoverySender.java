package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This is a broadcasting thread that simply sends broadcast messages
 * on all available interfaces of the server. This is used to let the Arduino
 * capture the message and find the servers address.
 * When the Arduino is connected, the broadcasting stops.
 * 
 * @author DennisW
 *
 */
public class DiscoverySender extends Thread {
	private DatagramSocket socket;
	private Timer timer;
	private ToDoTask toDoTask;
	private int broadcastPort = 28780; 

	/**
	 * Initiates the DiscoverySender and starts a timer scheduled to
	 * run the UDP-broadcast once a second.
	 * @param controller
	 */
	public DiscoverySender() {
		timer = new Timer();
		toDoTask = new ToDoTask();
	}
	
	/**
	 * Cancels the timer and stops the broadcasting.
	 */
	public void close() {
		timer.cancel();
		timer.purge();
		socket = null;
	}
	
	public void sendBroadcast() {
		toDoTask.run();
	}
	
	/**
	 * This class sends a UDP-broadcast on a specified port.
	 * @author DennisW
	 *
	 */
	private class ToDoTask extends TimerTask {

		@Override
		public void run() {
			try {
				socket = new DatagramSocket();
				socket.setBroadcast(true);

				//Data to send
				byte[] data = "HELLO_BARDUINO".getBytes();

				//Loops through all available interfaces on the machine
				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				while (interfaces.hasMoreElements()) {
					NetworkInterface networkInterface = (NetworkInterface)interfaces.nextElement();

					//Ignore the loopback interface
					if (networkInterface.isLoopback() || !networkInterface.isUp()) {
						continue;
					}

					
					for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
						InetAddress broadcast = interfaceAddress.getBroadcast();
						if (broadcast == null) {
							continue;
						}

						// Send the broadcast package
						try {
							DatagramPacket sendPacket = new DatagramPacket(data, data.length, broadcast, broadcastPort);
							socket.send(sendPacket);
						} catch (Exception e) {
						}

						System.out.println(getClass().getName()
								+ ">>> Request packet sent to: "
								+ broadcast.getHostAddress() + "; Interface: "
								+ networkInterface.getDisplayName());
					}
				}

			} catch (Exception e) {

			}
		}

	}
}
