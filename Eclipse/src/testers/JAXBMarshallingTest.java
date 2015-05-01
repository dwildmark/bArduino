package testers;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import server.Fluid;
import server.PropertiesWrapper;
import server.ServerApp;

/**
 * 
 * @author Jonathan Böcker 2015-04-30
 *
 */
public class JAXBMarshallingTest {
	public static void main(String[] args) throws Exception {
		PropertiesWrapper properties = new PropertiesWrapper();
		properties.setArduinoPort(8008);
		properties.setClientPort(4444);
		properties.setDatabaseName("barduino");
		properties.setServerAdress("localhost");
		properties.setUsername("barduino");
		
		Fluid fluid1 = new Fluid();
		fluid1.setName("Vodka");
		fluid1.setCost(40);
		fluid1.setId(0);
		
		Fluid fluid2 = new Fluid();
		fluid2.setName("Rum");
		fluid2.setCost(50);
		fluid2.setId(1);
		
		properties.addFluid(fluid1);
		properties.addFluid(fluid2);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(PropertiesWrapper.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		//Marshal the employees list in console
	    jaxbMarshaller.marshal(properties, System.out);
	    
	    //Marshal the employees list in file
	    jaxbMarshaller.marshal(properties, new File(ServerApp.configFileName));
	}
}
