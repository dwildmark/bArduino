package server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Handler;
import java.util.logging.Logger;

import protocol.ServerProtocolParser;
/**
 * @author Andreas, Jonathan, Olle.
 * The server-class task is to provide a client to connect to the server and a arduino to do the same.
 * While doing this it also logs all the data that is sent between those two. 
 * A thread will be created for each connected client.
 *	
 */

public class Server extends Thread {
	public static Logger logger;
	private boolean running = false;
	private ServerSocket serverSocket;
	private ServerProtocolParser parser;
	private LinkedList<ClientHandler> clientQueue = new LinkedList<ClientHandler>();
	private LinkedList<ArduinoHandler> arduinoQueue = new LinkedList<ArduinoHandler>();
	private Controller controller;

	public Server(Logger logger, Controller controller) {
		Server.logger = logger;
		this.controller = controller;
	}
	/**
	 * The method run is were the socket will be created. We will need the protocol for the connection
	 * between the arduino and a client, therefore .getInstance(). Then we create a new prop-file and a inputstream
	 * to read the property-file. After this the serversocket for the client is created and a serversocket for the 
	 * arduino as well.
	 * The while-loop makes the server run at all time and checks if a client  wants to connect.
	 * If anything goes wrong with the accept process the exception will print it in the log for the server.
	 */

	public void run() {
		parser = ServerProtocolParser.getInstance();
		parser.setState(ServerProtocolParser.MISSING_ARDUINO);
		parser.setController(controller);
		Socket client;
		running = true;

		try {
			PropertiesWrapper prop = controller.loadServerConfig();
			
			serverSocket = new ServerSocket(prop.getClientPort());
			logger.info("Server: Running " + InetAddress.getLocalHost() + "/"
					+ serverSocket.getLocalPort());
			ArduinoHandler tempArduino = new ArduinoHandler(logger, controller);
			arduinoQueue.add(tempArduino);
			tempArduino.start();
			
			
			while (running) {
				client = serverSocket.accept();
				logger.info("Server: Client connected"
						+ client.getInetAddress() + "/" + client.getPort());
				ClientHandler tempClient = new ClientHandler(client, logger, controller);
				clientQueue.add(tempClient);
				tempClient.start();
			}
			serverSocket.close();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logger.warning(sw.toString());

		} finally {
			Handler[] handlers = logger.getHandlers();
			for (Handler handler : handlers) {
				handler.close();
			}
		}
	}

	/**
	 * If someone turns the server off, all clients in the clientQueue will be closed, and the 
	 * queues information will be deleted, it's the same for all the arduinos that is connected.
	 * then at last the serversocket will be closed.
	 * 
	 */
	public void close() {
		for (int i = 0; i < clientQueue.size(); i++) {
			ClientHandler tempClient = clientQueue.get(i);
			if (tempClient != null) {
				tempClient.close();
			}
		}
		clientQueue.clear();
		for (int i = 0; i < arduinoQueue.size(); i++) {
			ArduinoHandler tempArduino = arduinoQueue.get(i);
			if (tempArduino != null) {
				tempArduino.close();
			}
		}
		arduinoQueue.clear();
		running = false;
		try {
			serverSocket.close();
		} catch (Exception e) {}
	}
	
	public void cancelGrog() {
		for (int i = 0; i < arduinoQueue.size(); i++) {
			ArduinoHandler tempArduino = arduinoQueue.get(i);
			if (tempArduino != null) {
				tempArduino.close();
			}
		}
		ArduinoHandler tempArduino = new ArduinoHandler(logger, controller);
		arduinoQueue.add(tempArduino);
		tempArduino.start();
	}
}
