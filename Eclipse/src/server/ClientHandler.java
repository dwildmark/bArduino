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
		this.parser = ServerProtocolParser.getInstance();
	}

	public void run() {
		try {
			mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			while (true) {
				String message = null;
				String answer = null;
				message = in.readLine();
				
				if (message != null) {
					if(message.equals("STOP")){
						mOut.println("STOP");
						break;
					}						
						
					answer = parser.processClientMessage(message);
					System.out.println("Client: " + client.getInetAddress() + " said: " + message);
					System.out.println("Server answers: " + answer);
					mOut.println(answer);
				}
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Client: Disconnected " + client.getInetAddress());
		
		try {
			mOut.close();
			in.close();
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
