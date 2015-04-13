package server;


import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Logger;

import protocol.ServerProtocolParser;

public class Server implements Runnable {
	public static Logger logger;
	private boolean running = false;
	private ServerSocket serverSocket;
	private ServerProtocolParser parser;
	

	

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
			InputStream inputStream = getClass().getClassLoader()
					.getResourceAsStream("config.properties");
			if (inputStream != null) {
				prop.load(inputStream);
				inputStream.close();
			}
			// create a server socket. A server socket waits for requests to
			// come in over the network.
			serverSocket = new ServerSocket(Integer.parseInt((String) prop
					.get("clientport")));
			logger.info("Server: Running " + InetAddress.getLocalHost() + "/"
					+ serverSocket.getLocalPort());
			new ArduinoHandler(logger).start();
			while (running) {
				// create client socket... the method accept() listens for a
				// connection to be made to this socket and accepts it.
				client = serverSocket.accept();
				logger.info("Server: Client connected"
						+ client.getInetAddress() + "/" + client.getPort());
				new ClientHandler(client, logger).start();
			}
			serverSocket.close();
		} catch (Exception e) {
			logger.severe("Server: Error");

		} finally {
			Handler[] handlers = logger.getHandlers();
			for (Handler handler : handlers) {
				handler.close();
			}
		}
	}
}
