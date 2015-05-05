package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

public class DiscoverySender extends Thread {
	private DatagramSocket socket;
	private Controller controller;
	private Timer timer;
	private ToDoTask toDoTask;

	public DiscoverySender(Controller controller) {
		this.controller = controller;
		timer = new Timer();
		toDoTask = new ToDoTask();
		timer.scheduleAtFixedRate(toDoTask, 0, 1000);
	}
	
	public void close() {
		timer.cancel();
		timer.purge();
		socket = null;
	}
	
	private class ToDoTask extends TimerTask {

		@Override
		public void run() {
			try {
				socket = new DatagramSocket();
				socket.setBroadcast(true);

				byte[] data = "HELLO_BARDUINO".getBytes();

				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				while (interfaces.hasMoreElements()) {
					NetworkInterface networkInterface = (NetworkInterface)interfaces.nextElement();

					if (networkInterface.isLoopback() || !networkInterface.isUp()) {
						continue; // Don't want to broadcast to the loopback
									// interface
					}

					for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
						InetAddress broadcast = interfaceAddress.getBroadcast();
						if (broadcast == null) {
							continue;
						}

						// Send the broadcast package!
						try {
							DatagramPacket sendPacket = new DatagramPacket(data, data.length, broadcast, controller.loadServerConfig().getDiscoveryPort());
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
