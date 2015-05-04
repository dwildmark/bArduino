package server;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * 
 * @author Jonathan Böcker 20015-04-20
 *
 */
public class Controller {
	public static Logger logger = Logger.getLogger(Server.class.getName());
	private static Date date = new Date();
	private Server server;
	private ServerGUI serverGUI;

	public void startServerGUI() {
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
			new DiscoveryListener(this).start();
			serverGUI = new ServerGUI(logger, this);
			server = new Server(Controller.logger, this);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes sockets and starts them again
	 */
	public void restartServer() {
		server.close();
		server = new Server(Controller.logger, this);
		server.start();
	}

	/*
	 * Loads server configuration from file on disk
	 */
	public PropertiesWrapper loadServerConfig() throws Exception {
		PropertiesWrapper props;
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(PropertiesWrapper.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			props = (PropertiesWrapper) jaxbUnmarshaller
					.unmarshal(new File(ServerApp.configFileName));
			
		} catch (Exception e) {
			props = new PropertiesWrapper();
			props.setArduinoPort(8008);
			props.setClientPort(4444);
			props.setDiscoveryPort(28785);
			props.setDatabaseName("barduino");
			props.setServerAdress("localhost");
			props.setUsername("barduino");
			
			JAXBContext jaxbContext = JAXBContext.newInstance(PropertiesWrapper.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
		    //Marshal the properties list in file
			new File(ServerApp.resourceDirectory).mkdir();
		    jaxbMarshaller.marshal(props, new File(ServerApp.configFileName));
		}
		return props;
	}

	public void saveServerConfig(PropertiesWrapper prop) throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(PropertiesWrapper.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
	    //Marshal the properties list in file
	    jaxbMarshaller.marshal(prop, new File(ServerApp.configFileName));
	    
	    logger.info("Settings saved!");
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
	
	public void setGrogInTheMaking(Grog grog) {
		serverGUI.setGrogInTheMaking(grog);
	}

	/*
	 * Returns a Timestamp with the current time
	 */
	private static Timestamp time() {
		Timestamp currentTimestamp = new Timestamp(date.getTime());
		return currentTimestamp;
	}
}
