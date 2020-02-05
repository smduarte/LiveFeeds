package seeds.sensors.api.wifi;

import seeds.sensors.api.gps.GpsData;
import seeds.sensors.sys.common.SensorParameters;
import feeds.api.Criteria;

public class WiFiCriteria extends Criteria<WiFiData> implements SensorParameters {

	
	public double refreshRate;
	public double gpsRate;
	public float minDistance;
	
	public WiFiCriteria() {
		this(5, 10, 100) ; //TODO dummy values....
	}
	
	public WiFiCriteria(double refreshRate, double gpsDate, float minDistance) {
		this.refreshRate = refreshRate;
		this.gpsRate = gpsDate;
		this.minDistance = minDistance; 
	}
	
	
	public boolean accepts(GpsData e) {
		return true;
	}
	
	public int hashCode() {
		return (int)(refreshRate);
	}
	
	public boolean equals( Object other ) {
		return this.equals((WiFiCriteria)other) ;
	}
	
	public boolean equals( WiFiCriteria other ) {
		return refreshRate == other.refreshRate && minDistance == other.minDistance && gpsRate == other.gpsRate ;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
