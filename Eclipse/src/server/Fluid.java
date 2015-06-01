package server;

import javax.xml.bind.annotation.*;

/**
 * A class that holds information about a fluid
 * 
 * @author Jonathan Böcker 2015-04-30
 *
 */
@XmlRootElement(name = "fluid")
public class Fluid implements Comparable<Fluid> {
	String name;
	int cost;
	int id;

	public String getName() {
		return name;
	}

	@XmlElement
	public void setName(String name) {
		this.name = name;
	}

	public int getCost() {
		return cost;
	}

	@XmlElement
	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getId() {
		return id;
	}

	@XmlAttribute
	public void setId(int id) {
		this.id = id;
	}

	public int compareTo(Fluid fluid) {
		if (this.id < fluid.getId())
			return -1;
		else if (this.id > fluid.getId())
			return +1;
		else
			return 0;
	}
}
