package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Logger;

import protocol.ServerProtocolParser;

public class Server extends Thread {
	public static Logger logger;
	private boolean running = false;
	private ServerSocket serverSocket;
	private ServerProtocolParser parser;
	private LinkedList<ClientHandler> clientQueue = new LinkedList<ClientHandler>();
	private LinkedList<ArduinoHandler> arduinoQueue = new LinkedList<ArduinoHandler>();

	public Server(Logger logger) {
		Server.logger = logger;
	}

	public void run() {
		parser = ServerProtocolParser.getInstance();
		parser.setState(ServerProtocolParser.MISSING_ARDUINO);
		Socket client;
		running = true;

		try {
			Properties prop = new Properties();
			File initialFile = new File(ServerApp.propFileName);
			InputStream inputStream = new FileInputStream(initialFile);
			prop.load(inputStream);
			inputStream.close();
			
			// create a server socket. A server socket waits for requests to
			// come in over the network.
			serverSocket = new ServerSocket(Integer.parseInt((String) prop
					.get("clientport")));
			logger.info("Server: Running " + InetAddress.getLocalHost() + "/"
					+ serverSocket.getLocalPort());
			ArduinoHandler tempArduino = new ArduinoHandler(logger);
			arduinoQueue.add(tempArduino);
			tempArduino.start();
			while (running) {
				// create client socket... the method accept() listens for a
				// connection to be made to this socket and accepts it.
				client = serverSocket.accept();
				logger.info("Server: Client connected"
						+ client.getInetAddress() + "/" + client.getPort());
				ClientHandler tempClient = new ClientHandler(client, logger);
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

	public void close() {
		for (int i = 0; i < clientQueue.size(); i++) {
			ClientHandler tempClient = clientQueue.get(i);
			if (tempClient != null) {
				clientQueue.get(i).close();
			}
		}
		clientQueue.clear();
		for (int i = 0; i < arduinoQueue.size(); i++) {
			ArduinoHandler tempArduino = arduinoQueue.get(i);
			if (tempArduino != null) {
				arduinoQueue.get(i).close();
			}
		}
		arduinoQueue.clear();
		running = false;
		try {
			serverSocket.close();
		} catch (Exception e) {}
	}
}
