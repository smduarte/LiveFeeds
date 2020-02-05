package speedsense;
import sensing.persistence.core.sensors.GPSReading;

public class SGPSReading extends GPSReading {
	String segmentId;
	int density;
	double realSpeed;
	
	public String toString() {
		return "[time: ${time} segmentId: ${segmentId}, speed: ${speed}, realSpeed: ${realSpeed}]"	
	}
}
