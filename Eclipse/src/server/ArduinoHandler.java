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
 * 
 * @author Jonathan Böcker, Olle Casperson, Andreas Langhammer 2015-04-27
 *
 *	This class handles all communication between the server and 
 *	the arduino.
 */
public class ArduinoHandler extends Thread {
	private ServerProtocolParser parser;
	private ServerSocket arduinoServerSocket;
	private PrintWriter mOut;
	private BufferedReader in;
	private Socket arduino;
	private Timer timer;
	private Logger logger;
	private Controller controller;
	private boolean running = true;

	public ArduinoHandler(Logger logger, Controller controller) {
		this.parser = ServerProtocolParser.getInstance();
		this.logger = logger;
		this.controller = controller;
	}
	/**
	 * Checks the connection to the arduino by sending "Q" to the arduino, if no respond occurs 
	 * within 0,5 seconds, we want to set the state to arduino disconnected. 
	 * We catch this exception below as well. 
	 */

	class ToDoTask extends TimerTask {

		@Override
		public void run() {
			try {
				if (parser.getState() == ServerProtocolParser.VACANT
						&& mOut != null && in != null) {
					arduino.setSoTimeout(500);
					mOut.println("Q");
					in.readLine();
				} else if (parser.getState() != ServerProtocolParser.BUSY) {
					parser.setState(ServerProtocolParser.MISSING_ARDUINO);
					controller.setArduinoConnected(false);
				}

			} catch (Exception e) {
				this.cancel();
				arduinoDisconnected();
			}
		}
	}
	/**
	 * Method starts a serversocket and listens for connection.
	 * As long as the serversocket is up, we want current information to be send to
	 * the servers log. The while-loop does this for us. If a grog is ordered we
	 * use a second while-loop followed by a if-statement to pure up the liquid. then
	 * the last while-loop is needed to check if another liquid is ordered, if so, we 
	 * want to dequeue it and so on.
	 */

	@Override
	public void run() {
		String message;
		String answer;
		Grog grog;

		try {
			PropertiesWrapper prop = controller.loadServerConfig();
			arduinoServerSocket = new ServerSocket(prop.getArduinoPort());

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
					logger.info("Server: Arduino connected at "
							+ arduino.getInetAddress());
					timer = new Timer();
					timer.scheduleAtFixedRate(new ToDoTask(), 0, 1000);
					parser.setState(ServerProtocolParser.VACANT);
					controller.setArduinoConnected(true);

					while (mOut != null && in != null) {
						if (parser.isGrogAvailable()) {
							grog = parser.dequeueGrog();
							controller.setGrogInTheMaking(grog);
							timer.cancel();
							timer.purge();
							arduino.setSoTimeout(0);
							while (grog.hasMoreMessages()) {
								message = grog.dequeueMessage();

								if (message != null) {
									mOut.println(message);
									logger.info("Server to Arduino: " + message);
									answer = in.readLine();
									logger.info("Arduino said: " + answer);
									if (!grog.hasMoreMessages()) {
										parser.setState(ServerProtocolParser.VACANT);
									}
								}
							}
							controller.setGrogInTheMaking(null);
							timer = new Timer();
							timer.scheduleAtFixedRate(new ToDoTask(), 0, 1000);
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
	 * Method closes Serversocket in order to do so we need to
	 * cancel and purge the timer to disconnect correctly.
	 */

	public void close() {
		running = false;
		timer.cancel();
		timer.purge();
		mOut = null;
		controller.setArduinoConnected(false);
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
	 * This method is only used if the connection to the arduino is lost,
	 * if this happens we want the info to be printed in the logg.
	 * We also want to set the state to missing arduino in the parser, and the state 
	 * in the controller as well.
	 */
	
	public void arduinoDisconnected() {
		logger.warning("Lost connection to Arduino");
		mOut = null;
		in = null;
		parser.setState(ServerProtocolParser.MISSING_ARDUINO);
		controller.setArduinoConnected(false);
	}
}
