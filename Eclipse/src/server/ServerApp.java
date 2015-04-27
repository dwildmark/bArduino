package server;

/**
 * 
 * @author Jonathan Böcker 20015-04-27
 *
 */
public class ServerApp {
	public final static String propFileName = "./resources/config.properties";
	public final static String usersFileName = "./resources/users.properties";

	public static void main(String[] args) {
		loginWindow();
	}
	
	public static void loginWindow(){
		new LoginFrame(new Controller());
	}
}
