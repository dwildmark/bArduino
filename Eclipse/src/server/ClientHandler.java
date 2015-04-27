package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

import protocol.ServerProtocolParser;
import protocol.UserTools;

/**
 * This class handles the connection to a client. It uses a the serversocket to send data between the client and
 * the server. The serverProtocolParser is acts as the interpreter between the the messages sent from and to the 
 * client. We also need a bufferedReader to write and read from and to the propertyfile,
 * that contains information the client uses.
 * @author Andreas, Jonathan, Olle.
 *
 */

public class ClientHandler extends Thread {
	private Socket client;
	private PrintWriter mOut;
	private BufferedReader in;
	private ServerProtocolParser parser;
	private Logger logger;
	private boolean loggedIn = false;
	private String username = null;
	/**
	 * Constructor for the class.
	 * @param client, the specific client that is connected.
	 * @param logger, logs all data.
	 */

	public ClientHandler(Socket client, Logger logger) {
		this.logger = logger;
		this.client = client;
		this.parser = ServerProtocolParser.getInstance();
	}
	/**
	 * mOut writes to the file and in reads from the file. We catch exceptions if
	 * something goes wrong during reading or writing. 
	 * We use a try and then a while to constantly check for messages that are sent and received.
	 * if something that is tried and fails, we catch the exception and prints
	 * the stactrace in the console.
	 */

	public void run() {
		try {
			mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					client.getOutputStream())), true);
			in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			while (true) {
				String message = null;
				String answer = null;
				message = in.readLine();
				if (message == null)
					break;
				
				if (message.substring(0, 5).equals("LOGIN")) {
					logger.info("Client: " + client.getInetAddress()
							+ " said: LOGIN");
				}else if (!message.equals("AVAREQ")){
					logger.info("Client: " + client.getInetAddress()
							+ " said: " + message);
				}

				if (message.equals("STOP")) {
					mOut.println("STOP");
					break;
				}

				if (message.substring(0, 5).equals("LOGIN")){
					if (UserTools.confirmUser(
							message.substring(6).split(":")[0], message
									.substring(6).split(":")[1].toCharArray())){
						
						username = message.substring(6).split(":")[0];
						answer = "LOGIN OK";
						loggedIn = true;
					} else {
						answer = "LOGIN BAD";
					}
				} else {
					if(loggedIn){
						answer = parser.processClientMessage(message);
					} else {
						answer = "ERROR NOLOGIN";
					}
				}
				if (!message.equals("AVAREQ")) {
					logger.info("Server answers: " + answer);
				}

				mOut.println(answer);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Client: Disconnected " + client.getInetAddress());

		try {
			mOut.close();
			in.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
/**
 * closes the connection. and catches any exception.
 */
	public void close() {
		try {
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
