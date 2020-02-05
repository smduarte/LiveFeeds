package seeds.sensors.api.gps;

import seeds.sensors.sys.common.SensorParameters;
import feeds.api.Criteria;

@SuppressWarnings("serial")
public class GpsCriteria extends Criteria<GpsData> implements SensorParameters {

	public long minTime;
	public String provider;
	public float minDistance;
	public double refreshRate;

	public GpsCriteria() {
		this( DEFAULT_REFRESH_RATE, DEFAULT_PROVIDER, DEFAULT_MIN_TIME, DEFAULT_MIN_DISTANCE ) ;
	}

	public GpsCriteria(double refreshRate, long minTime, float minDistance) {
		this( refreshRate, DEFAULT_PROVIDER, minTime, minDistance ) ;
	}
	
	public GpsCriteria(double refreshRate, String provider, long minTime, float minDistance) {
		this.minTime = minTime;
		this.provider = provider;
		this.refreshRate = refreshRate;
		this.minDistance = minDistance;
	}

	public boolean accepts(GpsData e) {
		return true;
	}

	public int hashCode() {
		return (int)( minTime >> 32 & minTime & 0xFFFFFFFFL);
	}
	
	public boolean equals( Object other ) {
		return this.equals((GpsCriteria)other) ;
	}
	
	public boolean equals( GpsCriteria other ) {
		return provider.equals(other.provider) && minDistance == other.minDistance && minTime == other.minTime && refreshRate == other.refreshRate ;
	}
	
	static final long DEFAULT_MIN_TIME = 5;
	static final String DEFAULT_PROVIDER = "gps";
	static final float DEFAULT_MIN_DISTANCE = 10;
	static final double DEFAULT_REFRESH_RATE = 5.0;
}
