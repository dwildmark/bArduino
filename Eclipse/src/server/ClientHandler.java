package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import protocol.ServerProtocolParser;

public class ClientHandler extends Thread {
	private Socket client;
	private PrintWriter mOut;
	private BufferedReader in;
	private ServerProtocolParser parser;

	public ClientHandler(Socket client) {
		this.client = client;
		this.parser = new ServerProtocolParser(4);

	}

	public void run() {
		try {
			mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			while (client.isConnected()) {
				String message = null;
				message = in.readLine();
				if (message != null) {
					// call the method messageReceived from ServerBoard class
					mOut.println(parser.processClientMessage(message));
				}
			}
		} catch (SocketException e) {
			System.out.println("Client: Dissconnected");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("H�r ska st� n�got smart");
			e.printStackTrace();
		}

	}
}
