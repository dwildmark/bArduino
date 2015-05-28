package server;

import java.util.Timer;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

//import protocol.ServerProtocolParser;

/**
 * 
 * @author Jonathan Böcker, Olle Casperson 20015-04-27
 *
 */
public class ArduinoScreenHandler extends Thread {
//	private ServerProtocolParser parser;
	private ServerSocket arduinoServerSocket;
	private PrintWriter mOut;
	private BufferedReader in;
	private Socket arduino;
	private Timer timer;
	private Logger logger;
	private Controller controller;
	private boolean running = true;

	public ArduinoScreenHandler(Logger logger, Controller controller) {
//		this.parser = ServerProtocolParser.getInstance();
		this.logger = logger;
		this.controller = controller;
	}
	
	public void sendMessage(String message) {
		try{
			mOut.println(message);
		} catch (Exception e) {}
	}

	@Override
	public void run() {

		try {
			arduinoServerSocket = new ServerSocket(8006);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		while (running) {
			try {
				while (!arduinoServerSocket.isClosed()) {

					arduino = arduinoServerSocket.accept();
					mOut = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(arduino.getOutputStream())),
							true);
					in = new BufferedReader(new InputStreamReader(
							arduino.getInputStream()));
					logger.info("Server: ArduinoScreen connected at "
							+ arduino.getInetAddress());
					controller.setScreenConnected(true);
//					String oldMessage = "";
					while (mOut != null && in != null) {
//						String newMessage = parser.getScreenMessage();
//						if(!(oldMessage.equals(newMessage))) {
//							mOut.println(newMessage);
//							oldMessage = newMessage;
//						}
					}
				}

			} catch (Exception e) {
				arduinoDisconnected();
				e.printStackTrace();
			}
		}
	}

	public void close() {
		running = false;
		timer.cancel();
		timer.purge();
		mOut = null;
		try {
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			arduinoServerSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			arduino.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void arduinoDisconnected() {
		logger.warning("Lost connection to ArduinoScreen");
		mOut = null;
		in = null;
	}
}
