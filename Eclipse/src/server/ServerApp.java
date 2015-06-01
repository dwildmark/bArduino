package server;

/**
 * An application for controlling a Barduino machine and handling clients
 * 
 * @author Jonathan Böcker 20015-04-27
 *
 */
public class ServerApp {
	public final static String configFileName = "./resources/config.xml";
	public final static String resourceDirectory = "./resources";

	public static void main(String[] args) {
		new LoginFrame(new Controller());
	}
}
