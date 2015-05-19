package protocol;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import server.ClientHandler;
import server.Controller;
import server.Fluid;
import server.Grog;
import server.PropertiesWrapper;
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
	
	private String sqlPassword, sqlUserName, sqlDatabaseName, sqlServerName;
	private Queue<Grog> grogQueue = new LinkedList<Grog>();
	private PropertiesWrapper prop;
	private boolean grogAvailable = false;
	private int state;
	private Controller controller;

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
	
	public synchronized void setSQLCredentials(String user, String password, String serverName, String databaseName){
		this.sqlPassword = password;
		this.sqlUserName = user;
		this.sqlServerName = serverName;
		this.sqlDatabaseName = databaseName;
		
	}

	public synchronized String getSqlPassword() {
		return sqlPassword;
	}
	
	public synchronized String getSqlUserName() {
		return sqlUserName;
	}
	
	public synchronized String getSqlDatabaseName(){
		return sqlDatabaseName;
	}
	
	public synchronized String getSqlServerName(){
		return sqlServerName;
	}

	public synchronized void updateProps() {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(PropertiesWrapper.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			prop = (PropertiesWrapper) jaxbUnmarshaller
					.unmarshal(new File(ServerApp.configFileName));
			
		} catch (Exception e) {
			
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

	/**
	 * Returns a string formatted as
	 * "INGREDIENTS:'fluid1','fluid2', etc" with the fluid names in
	 * chronological order The names is read from {@code ServerApp.propFileName}
	 * local file
	 * 
	 * @return A string with all fluids names
	 */
	public synchronized String getIngredients() {
		updateProps();
		List<Fluid> list = prop.getFluidList();
		String ingredients = "INGREDIENTS:";
		
		for(Fluid fluid : list){
			ingredients += fluid.getName() + "<cost>" + fluid.getCost()+ ",";
		}

		return ingredients;
	}

	/**
	 * Takes a message and depending of the state of the server and content of
	 * message returns an appropriate response.
	 * 
	 * @param message
	 *            Message recieved from client
	 * @param clientHandler 
	 * 			  User who sent the message
	 * @return An appropriate response depending of the state of server and
	 *         content of message
	 */
	public synchronized String processClientMessage(String message, ClientHandler clientHandler) {
		String response = null;

		if (message.equals("INGREDIENTS")) {
			response = getIngredients();

		} else if (state == VACANT) {

			if (message.equals("AVAREQ"))
				response = "AVAILABLE";
			else
				response = processGrogRequest(message, clientHandler);

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
	 * @param clientHandler 
	 * 			  User who sent the message
	 * @return Returns "ERROR WRONGFORMAT" if the format is wrong and "GROGOK"
	 *         if the formatting is accepted.
	 */
	public synchronized String processGrogRequest(String message, ClientHandler clientHandler) {
		String response = null;
		String[] request = message.split(" ");
		Queue<String> arduinoMessages = new LinkedList<String>();
		char fluid = 'A';
		double credit = UserTools.getCredits(clientHandler.getUsername());
		if(UserTools.getApproved(clientHandler.getUsername()).equals("no")){
			response = "ERROR BLOCKED";
		}else if (!(request[0].equals("GROG"))
				|| (request.length - 1) > prop.getFluidList().size()) {
			response = "ERROR WRONGFORMAT";
		} else if (grogAvailable) {
			response = "ERROR BUSY";
		} else {
			int cost = calculateCost(message);
			if(cost > credit) {
				response = "ERROR INSUFFICIENT FUNDS";
			} else { 
			try {
				int volume = 0;
				for (int i = 1; i < request.length; i++) {
					volume = Integer.parseInt(request[i]);

					if (volume > 0 && volume < 100) {
						arduinoMessages.add(fluid + request[i]);
					}
					fluid++;
				}
				arduinoMessages.add("K");
				UserTools.alterCredits(clientHandler.getUsername(), 0 - cost);
				response = "GROGOK " + UserTools.getCredits(clientHandler.getUsername());
				
				if (arduinoMessages.size() > 0) {
					grogAvailable = true;
					state = BUSY;
					grogQueue.add(new Grog(arduinoMessages, clientHandler));
				}
				
			} catch (NumberFormatException e) {
				arduinoMessages.clear();
				response = "ERROR WRONGFORMAT";
			}
		}
		}
		return response;

	}
	private int calculateCost(String grog) {
		int cost = 0;
		List<Fluid> list;
		String[] amount = grog.split(" ");
		try {
			list = controller.loadServerConfig().getFluidList();
			for(int i = 0; i < list.size(); i++) {
				cost += (list.get(i).getCost() * Integer.parseInt(amount[i + 1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cost;
	}
	public void setController(Controller controller) {
		this.controller = controller;
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
	 * @return An {@link String} for sending to the Arduino, containing which
	 *         fluid and how much.
	 */
	public synchronized Grog dequeueGrog() {
		Grog str = null;
		if (grogAvailable) {
			str = grogQueue.remove();
			if (grogQueue.isEmpty()) {
				grogAvailable = false;
			}
		}
		return str;
	}

	/**
	 * Removes all queued Arduino messages
	 */
	public synchronized void clearGrog() {
		grogQueue.clear();
		grogAvailable = false;
	}
	
	public String getScreenMessage() {
		switch(state) {
		case VACANT:
			return "READY";
		case BUSY:
			return "MAKING DRINK";
		case MISSING_ARDUINO:
			return "BARDUINO DC";
		default:
			return "";
		}
	}
}
