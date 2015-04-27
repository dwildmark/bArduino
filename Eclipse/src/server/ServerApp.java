package server;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 
 * @author Jonathan Böcker 20015-04-27
 *
 */
public class ServerApp {
	public static Logger logger = Logger.getLogger(Server.class.getName());
	private static Date date = new Date();
	public final static String propFileName = "./resources/config.properties";
	public final static String usersFileName = "./resources/users.properties";

	public static void main(String[] args) {
		loginWindow(new ServerApp());
	}
	
	public static Timestamp time() {
		Timestamp currentTimestamp = new Timestamp(date.getTime());
		return currentTimestamp;
	}
	
	public void startServer(){
		FileHandler fh = null;
		SimpleFormatter formatter = new SimpleFormatter();
		try {
			File dir = new File("./logs");
			dir.mkdir();
			fh = new FileHandler("./logs/"
					+ time().toString().replace(':', '-') + ".log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		fh.setFormatter(formatter);
		logger.addHandler(fh);
		
		try {
			new DiscoveryListener().start();
			new ServerGUI(logger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loginWindow(ServerApp serverApp){
		new LoginFrame(serverApp);
	}
}
