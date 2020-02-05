package seeds.sensors.api.tilt;

import seeds.sensors.sys.common.SensorParameters;
import feeds.api.Criteria;

public class TiltCriteria extends Criteria<TiltData> implements SensorParameters {

	public double refreshRate;

	public TiltCriteria(){
		this(DEFAULT_REFRESH_RATE) ;
	}

	public TiltCriteria(double refreshRate){
		this.refreshRate = refreshRate;
	}
	
	public int hashCode() {
		return (int)(refreshRate * 10000) ;
	}
	
	public boolean equals( Object other ) {
		return this.equals((TiltCriteria)other) ;
	}
	
	public boolean equals( TiltCriteria other ) {
		return refreshRate == other.refreshRate ;
	}
	
	static final double DEFAULT_REFRESH_RATE = 1.0;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
