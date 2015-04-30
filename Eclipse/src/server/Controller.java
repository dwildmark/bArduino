package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JOptionPane;

public class Controller {
	public static Logger logger = Logger.getLogger(Server.class.getName());
	private static Date date = new Date();
	private Server server;
	private ServerGUI serverGUI;

	public void startServerGUI(){
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
			serverGUI = new ServerGUI(logger, this);
			server = new Server(Controller.logger, this);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  Closes sockets and starts them again
	 */
	public void restartServer() {
		server.close();		
		server = new Server(Controller.logger, this);
		server.start();
	}
	
	
	/*
	 * Loads server configuration from file on disk
	 */
	public Properties loadServerConfig() throws IOException {
		Properties prop = new Properties();
		File initialFile = new File(ServerApp.propFileName);
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(initialFile);
			prop.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			File dir = new File("./resources");
			dir.mkdir();
			FileOutputStream out = new FileOutputStream(ServerApp.propFileName);
			prop.setProperty("clientport", "4444");
			prop.setProperty("arduinoport", "8008");
			prop.store(out, "Default values");
			out.close();
		}
		return prop;
	}
	
	public void saveServerConfig(Properties prop){
		FileOutputStream out;
		try {
			out = new FileOutputStream(ServerApp.propFileName);
			prop.store(out, null);
			out.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}		
		
	}
	
	public List<String> getFluidKeys() throws IOException{
		Enumeration<Object> keyList = loadServerConfig().keys();
		String key;
		List<String> list = new ArrayList<String>();

		// Find all fluid keys
		while (keyList.hasMoreElements()) {
			key = (String) keyList.nextElement();

			if (key.contains("fluid") && key.contains("name")) {
				list.add(key);
			}
		}
		// Sort the keys
		Collections.sort(list);
		return list;
	}
	
	public void setArduinoConnected(boolean b) {
		serverGUI.setArduinoConnected(b);
	}
	
	public void userLoggedIn(String username) {
		serverGUI.userLoggedIn(username);
	}
	
	public void userLoggedOut(String username) {
		serverGUI.userLoggedOut(username);
	}
	
	/*
	 *  Returns a Timestamp with the current time
	 */
	private static Timestamp time() {
		Timestamp currentTimestamp = new Timestamp(date.getTime());
		return currentTimestamp;
	}
}
