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

public class ClientHandler extends Thread {
	private Socket client;
	private PrintWriter mOut;
	private BufferedReader in;
	private ServerProtocolParser parser;
	private Logger logger;

	public ClientHandler(Socket client, Logger logger) {
		this.logger = logger;
		this.client = client;
		this.parser = ServerProtocolParser.getInstance();
	}

	public void run() {
		try {
			mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					client.getOutputStream())), true);
			in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			mOut.println(parser.getIngredients());
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
				if (!message.equals("AVAREQ")) {
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
						answer = "LOGIN OK";
					} else {
						answer = "LOGIN BAD";
					}
				} else {
						answer = parser.processClientMessage(message);
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

	public void close() {
		try {
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
