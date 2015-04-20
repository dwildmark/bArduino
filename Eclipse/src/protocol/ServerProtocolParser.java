package protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

import server.ServerApp;

/**
 * Tool for processing bArduino protocol messages.
 * 
 * States has to be set accordingly for the parser to return appropriate
 * messages.
 * 
 * @author Jonathan Bocker
 * @version 0.1
 * 
 *          2015-04-02
 *
 */
public class ServerProtocolParser {
	public static final int VACANT = 0;
	public static final int BUSY = 1;
	public static final int MISSING_ARDUINO = 2;

	private int numberOfAvailableFluids = 4;
	private Queue<String> arduinoMessages = new LinkedList<String>();
	private Properties prop;
	private boolean grogAvailable = false;
	private int state;

	private static ServerProtocolParser parser = new ServerProtocolParser();

	private ServerProtocolParser() {
		updateProps();
	}

	/**
	 * @return An instance of {@link ServerProtocolParser}
	 */
	public static ServerProtocolParser getInstance() {
		return parser;
	}

	public void updateProps() {
		try {
			prop = new Properties();
			File initialFile = new File(ServerApp.propFileName);
			InputStream inputStream = new FileInputStream(initialFile);

			prop.load(inputStream);
			inputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Sets state of server service. Available states are defined as constants
	 * in the {@link ServerProtocolParser} class
	 * 
	 * @param nextState
	 *            State of server at the moment
	 * @throws IllegalArgumentException
	 *             if no such state is defined
	 */
	public synchronized void setState(int nextState) {
		if (nextState >= VACANT && nextState <= MISSING_ARDUINO) {
			state = nextState;
		} else
			throw new IllegalArgumentException("No Such State: " + nextState);
	}

	/**
	 * Gets state of server service. Available states are defined as constants
	 * in the {@link ServerProtocolParser} class
	 * 
	 * @return State of server at the moment
	 */
	public synchronized int getState() {
		return state;
	}

	public synchronized String getIngredients() {
		updateProps();
		String ingredients = "INGREDIENTS:";
		ingredients += prop.getProperty("fluid1");
		ingredients += "," + prop.getProperty("fluid2");
		ingredients += "," + prop.getProperty("fluid3");
		ingredients += "," + prop.getProperty("fluid4");

		return ingredients;
	}

	public synchronized String setIngredients(String ingredients) {
		String response;
		String[] str = ingredients.split(",");
		FileOutputStream out;

		try {
			out = new FileOutputStream(ServerApp.propFileName);
			prop.setProperty("fluid1", str[0]);
			prop.setProperty("fluid2", str[1]);
			prop.setProperty("fluid3", str[2]);
			prop.setProperty("fluid4", str[3]);
			prop.store(out, null);
			out.close();
			response = "INGREDIENTSOK";

		} catch (Exception e) {
			e.printStackTrace();
			response = "ERROR WRONGFORMAT";
		}
		return response;

	}

	/**
	 * Takes a message and depending of the state of the server and content of
	 * message returns an appropriate response.
	 * 
	 * @param message
	 *            Message recieved from client
	 * @return An appropriate response depending of the state of server and
	 *         content of message
	 */
	public synchronized String processClientMessage(String message) {
		String response = null;

		if (message.equals("INGREDIENTS")) {
			response = getIngredients();

		} else if (message.split(":")[0].equals("SETINGREDIENTS")) {
			response = setIngredients(message.split(":")[1]);
		} else if (state == VACANT) {

			if (message.equals("AVAREQ"))
				response = "AVAILABLE";
			else
				response = processGrogRequest(message);

		} else if (state == BUSY) {
			response = "ERROR BUSY";

		} else if (state == MISSING_ARDUINO) {
			response = "ERROR NOCONNECTION";
		}

		return response;
	}

	/**
	 * Processes a GROG formatted request and returns either "ERROR WRONGFORMAT"
	 * if format is wrong or "GROGOK" if format is accepted. The class constant
	 * NUMBER_OF_AVAILABLE_FLUIDS must be taken in to account when formatting
	 * the message
	 * 
	 * @param message
	 *            A GROG formatted request
	 * @return Returns "ERROR WRONGFORMAT" if the format is wrong and "GROGOK"
	 *         if the formatting is accepted.
	 */
	public synchronized String processGrogRequest(String message) {
		String response = null;
		String[] request = message.split(" ");
		arduinoMessages.clear();
		char fluid = 'A';

		if (!(request[0].equals("GROG"))
				|| (request.length - 1) > numberOfAvailableFluids) {
			response = "ERROR WRONGFORMAT";
		} else if (grogAvailable) {
			response = "ERROR BUSY";
		} else {

			try {
				int volume = 0;
				for (int i = 1; i < request.length; i++) {
					volume = Integer.parseInt(request[i]);
					
					if (volume > 0 && volume < 100) {
						if (volume < 10)
							arduinoMessages.add(fluid + "0" + request[i]);
						else
							arduinoMessages.add(fluid + request[i]);
					}
					fluid++;
				}
				response = "GROGOK";
				if (arduinoMessages.size() > 0) {
					grogAvailable = true;
					state = BUSY;
				}
				System.out.println("Server: GROG available, now BUSY");

			} catch (NumberFormatException e) {
				arduinoMessages.clear();
				response = "ERROR WRONGFORMAT";
			}
		}

		return response;
	}

	/**
	 * This method is used for making sure there is a grog to be sent to the
	 * Arduino, so that the method getGrog() can be called safely and return a
	 * grog
	 * 
	 * @return True if a grog is available, False if not.
	 */
	public synchronized boolean isGrogAvailable() {
		return grogAvailable;
	}

	/**
	 * 
	 * @return An {@link ArrayList} with Integer values representing the amount
	 *         of fluid of each fluid
	 */
	public synchronized String dequeueGrog() {
		String str = null;
		if (grogAvailable) {
			str = arduinoMessages.remove();
			if (arduinoMessages.isEmpty()) {
				grogAvailable = false;
			}
		}
		return str;
	}

	public synchronized void clearGrog() {
		arduinoMessages.clear();
		grogAvailable = false;
		state = VACANT;
	}
}
