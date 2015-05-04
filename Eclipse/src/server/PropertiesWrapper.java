package server;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * 
 * @author Jonathan Böcker 2015-04-30
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ServerConfiguration")
public class PropertiesWrapper {

	@XmlElementWrapper(name = "Fluids")
	@XmlElement(type=Fluid.class, name = "Fluid")
	private List<Fluid> fluidList = new ArrayList<>();
	
	private String serverAdress;
	private String databaseName;
	private String username;
	private int arduinoPort;
	private int clientPort;
	private int discoveryPort;
	
	public PropertiesWrapper() {
	}

	public PropertiesWrapper(List<Fluid> list) {
		this.fluidList = list;
	}

	public void addFluid(Fluid fluid) {
		this.fluidList.add(fluid);
	}
	
	public void removeFluid(Fluid fluid) {
		this.fluidList.remove(fluid);
	}

	public List<Fluid> getFluidList() {
		return this.fluidList;
	}

	public void setFluids(List<Fluid> fluidList) {
		this.fluidList = fluidList;
	}
	
	public String getServerAdress(){
		return this.serverAdress;
	}
	
	public void setServerAdress(String adress){
		this.serverAdress = adress;
	}
	
	public String getDatabaseName(){
		return this.databaseName;
	}
	
	public void setDatabaseName(String database){
		this.databaseName = database;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getArduinoPort() {
		return arduinoPort;
	}

	public void setArduinoPort(int arduinoPort) {
		this.arduinoPort = arduinoPort;
	}

	public int getClientPort() {
		return clientPort;
	}

	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}
	
	public int getDiscoveryPort() {
		return discoveryPort;
	}
	
	public void setDiscoveryPort(int discoveryPort) {
		this.discoveryPort = discoveryPort;
	}
	
}
