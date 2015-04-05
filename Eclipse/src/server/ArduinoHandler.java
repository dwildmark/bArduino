package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import protocol.ServerProtocolParser;

public class ArduinoHandler extends Thread {
	private ServerProtocolParser parser;
	private ServerSocket arduinoServerSocket;
	private PrintWriter mOut;
	private BufferedReader in;
	public static final int SERVERPORT = 666;

	public ArduinoHandler() {
		this.parser = ServerProtocolParser.getInstance();
	}

	@Override
	public void run() {
		Socket arduino;
		while (true) {
			try {
				arduinoServerSocket = new ServerSocket(SERVERPORT);
				while (true) {
					arduino = arduinoServerSocket.accept();
					mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(arduino.getOutputStream())), true);
					in = new BufferedReader(new InputStreamReader(arduino.getInputStream()));
					
					System.out.println("Server: Arduino connected at " + arduino.getInetAddress());
					parser.setState(ServerProtocolParser.VACANT);
					
					while (true) {
						mOut.println(parser.dequeueGrog());
						if(!(in.readLine().equals("ACK"))){
							break;
						}
					}
				}

			} catch (Exception e) {

			}
		}

	}
}
