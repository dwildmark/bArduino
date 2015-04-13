package server;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
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
	private Socket arduino;
	private Timer timer;
	private Logger logger;

	public ArduinoHandler(Logger logger) {
		this.parser = ServerProtocolParser.getInstance();
		this.logger = logger;
	}

	class ToDoTask extends TimerTask {

		@Override
		public void run() {
			try {
				if (parser.getState() == ServerProtocolParser.VACANT
						&& mOut != null && in != null) {
					mOut.println("Q");
					in.readLine();
				} else if (parser.getState() != ServerProtocolParser.BUSY) {
					parser.setState(ServerProtocolParser.MISSING_ARDUINO);
				}

			} catch (Exception e) {
				logger.warning("Lost connection to Arduino");
				this.cancel();
				mOut = null;
				in = null;
				parser.setState(ServerProtocolParser.MISSING_ARDUINO);
			}
		}
	}

	@Override
	public void run() {
		String message;
		String answer;

		try {
			Properties prop = new Properties();
			InputStream inputStream = getClass().getClassLoader()
					.getResourceAsStream("config.properties");
			if (inputStream != null) {
				prop.load(inputStream);
				inputStream.close();
			}
			arduinoServerSocket = new ServerSocket(
					Integer.parseInt((String) prop.get("arduinoport")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			try {
				while (true) {

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

					while (mOut != null && in != null) {
						if (parser.isGrogAvailable()) {
							timer.cancel();
							timer.purge();
							message = parser.dequeueGrog();

							if (message != null) {
								mOut.println(message);
								logger.info("Server to Arduino: " + message);
								do {
									answer = in.readLine();
									logger.info("Arduino said: " + answer);
								} while (!(answer.equals("ACK")));
							}
							timer = new Timer();
							timer.scheduleAtFixedRate(new ToDoTask(), 0, 1000);
						}
					}
				}

			} catch (Exception e) {
				parser.setState(ServerProtocolParser.MISSING_ARDUINO);
				mOut = null;
				in = null;
				e.printStackTrace();
			}
		}
	}
	public void close() {
		try {
			arduino.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
