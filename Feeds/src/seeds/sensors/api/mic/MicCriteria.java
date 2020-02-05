package seeds.sensors.api.mic;

import seeds.sensors.sys.common.SensorParameters;
import feeds.api.Criteria;

public class MicCriteria extends Criteria<MicData> implements SensorParameters {

	public double duration;
	public double refreshRate;

	public MicCriteria() {
		this( DEFAULT_REFRESH_RATE, DEFAULT_DURATION ) ;
	}

	public MicCriteria(double refreshRate, double duration ) {
		this.duration = duration;
		this.refreshRate = refreshRate;
	}
	
	public boolean accepts(MicData e) {
		return true;
	}
	
	public int hashCode() {
		return (int)( refreshRate + duration );
	}
	
	public boolean equals( Object other ) {
		return this.equals((MicCriteria)other) ;
	}
	
	public boolean equals( MicCriteria other ) {
		return refreshRate == other.refreshRate && duration == other.duration ;
	}
	
	static final float DEFAULT_DURATION = 5;
	static final double DEFAULT_REFRESH_RATE = 60.0;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
