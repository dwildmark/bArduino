package server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import protocol.ServerProtocolParser;

public class Server extends Thread {
	public static final int SERVERPORT = 4444;
	private boolean running = false;
	private ServerSocket serverSocket;
	private ServerProtocolParser parser;

	public static void main(String[] args) {
		Server server = new Server();
		server.start();

	}

	public void run() {
		super.run();
		parser = ServerProtocolParser.getInstance();
		parser.setState(ServerProtocolParser.MISSING_ARDUINO);
		Socket client;
		running = true;
		
		try {
			// create a server socket. A server socket waits for requests to
			// come in over the network.
			serverSocket = new ServerSocket(SERVERPORT);
			System.out.println("Server: Running " + InetAddress.getLocalHost() + "/ " 
					+ serverSocket.getLocalPort());
			new ArduinoHandler().start();
			while (running) {
				// create client socket... the method accept() listens for a
				// connection to be made to this socket and accepts it.
				client = serverSocket.accept();
				System.out.println("Server: Client connected " + client.getInetAddress() 
						+ "/ " + client.getPort());
				new ClientHandler(client).start();
			}
			serverSocket.close();
		} catch (Exception e) {
			System.out.println("Server: Error");
		} finally {
			
		}
	}
}
