package server;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import protocol.ServerProtocolParser;

/**
 * This class is a thread that handles the connection to the LED-screen.
 * It collects a message from the server protocol and sends it as a 
 * TCP-message to the arduino.
 * @author Dennis Wildmark, Jonathan Böcker, Olle Casperson 2015-04-27
 *
 */
public class ArduinoScreenHandler extends Thread {
	private ServerProtocolParser parser;
	private ServerSocket arduinoServerSocket;
	private PrintWriter mOut;
	private BufferedReader in;
	private Socket arduino;
	private Timer timer;
	private Logger logger;
	private Controller controller;
	private boolean running = true;

	/**
	 * Creates an ArduinoScreenHandler object with reference to
	 * a Logger and a Controller object.
	 * @param logger the logger.
	 * @param controller the controller.
	 */
	public ArduinoScreenHandler(Logger logger, Controller controller) {
		this.parser = ServerProtocolParser.getInstance();
		this.logger = logger;
		this.controller = controller;
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
					
					//collect the incoming connection
					arduino = arduinoServerSocket.accept();
					
					//assign the input- and output streams
					mOut = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(arduino.getOutputStream())),
							true);
					in = new BufferedReader(new InputStreamReader(
							arduino.getInputStream()));
					
					//print to the log that the Arduino is connected
					logger.info("Server: ArduinoScreen connected at "
							+ arduino.getInetAddress());
					controller.setScreenConnected(true);
					String oldMessage = "";
					while (mOut != null && in != null) {
						String newMessage = parser.getScreenMessage();
						/*if message differs from the message already on the screen,
						 * send it to the Arduino
						*/
						if(!(oldMessage.equals(newMessage))) {
							mOut.println(newMessage);
							oldMessage = newMessage;
						}
					}
				}

			} catch (Exception e) {
				arduinoDisconnected();
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method closes any connections and cancels the timer-task.
	 */
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
	
	/**
	 * Prints to the log that the screen is disconnected
	 * and closes the input and output streams.
	 */
	public void arduinoDisconnected() {
		logger.warning("Lost connection to ArduinoScreen");
		mOut = null;
		in = null;
	}
}
