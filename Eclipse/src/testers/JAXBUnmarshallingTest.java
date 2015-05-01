package testers;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import server.Fluid;
import server.PropertiesWrapper;
import server.ServerApp;

public class JAXBUnmarshallingTest {

	public static void main(String[] args) throws Exception {
		JAXBContext jaxbContext = JAXBContext
				.newInstance(PropertiesWrapper.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		PropertiesWrapper props = (PropertiesWrapper) jaxbUnmarshaller
				.unmarshal(new File(ServerApp.configFileName));

		for (Fluid fluid : props.getFluidList()) {
			System.out.println(fluid.getId());
			System.out.println(fluid.getName());
			System.out.println(fluid.getCost());
		}
		System.out.println(props.getArduinoPort());
		System.out.println(props.getClientPort());
		System.out.println(props.getServerAdress());
		System.out.println(props.getDatabaseName());
		System.out.println(props.getUsername());
	}
}
