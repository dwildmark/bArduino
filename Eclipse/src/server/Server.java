package server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import protocol.ServerProtocolParser;

public class Server extends Thread {
	public static Logger logger = Logger.getLogger(Server.class.getName());
	private boolean running = false;
	private ServerSocket serverSocket;
	private ServerProtocolParser parser;
	private Date date = new Date();

	public static void main(String[] args) {
		Server server = new Server();
		FileHandler fh = null;
		SimpleFormatter formatter = new SimpleFormatter();
		try {
			File dir = new File("./logs");
			dir.mkdir();
			fh = new FileHandler("./logs/"
					+ server.time().toString().replace(':', '-') + ".log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		fh.setFormatter(formatter);
		logger.addHandler(fh);
		server.start();

	}

	public Timestamp time() {
		Timestamp currentTimestamp = new Timestamp(date.getTime());
		return currentTimestamp;
	}

	public void run() {
		super.run();
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
