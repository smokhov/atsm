import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RacingCar")
public class RacingCar {
	@XmlElement(name = "years")
	private String years;
	@XmlElement(name = "make")
	private String make;
	@XmlElement(name = "model")
	private String model;
	@XmlElement(name = "engine")
	private String engine;
}
