package server;

import java.util.Queue;

/**
 * A class that holds information about an ordered grog.
 * 
 * @author Jonathan Böcker 2015-05-01
 *
 */
public class Grog {
	private Queue<String> arduinoMessages;
	private ClientHandler user;

	public Grog(Queue<String> arduinoMessages, ClientHandler clientHandler) {
		this.arduinoMessages = arduinoMessages;
		this.user = clientHandler;
	}

	public String dequeueMessage() {
		return arduinoMessages.remove();
	}

	public boolean hasMoreMessages() {
		return !arduinoMessages.isEmpty();
	}
	
	public ClientHandler getClientHandler(){
		return user;
	}

}
