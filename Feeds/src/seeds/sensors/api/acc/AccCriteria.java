package seeds.sensors.api.acc;

import seeds.sensors.sys.common.SensorParameters;
import feeds.api.Criteria;

public class AccCriteria extends Criteria<AccData> implements SensorParameters {

	public double refreshTime;
	public float min_rotation;
	public boolean isTimeRefreshable;

	public AccCriteria() {
		this(DEFAULT_TIME_REFRESHABLE, DEFAULT_MIN_ROTATION, DEFAULT_REFRESH_TIME ) ;
	}
	
	public AccCriteria(boolean isTimeRefreshable, float min_rotation, double refreshTime) {
		this.isTimeRefreshable = isTimeRefreshable;
		this.min_rotation = min_rotation;
		this.refreshTime = refreshTime;
	}

	public boolean accepts(AccData e) {
		return true;
	}

	public int hashCode() {
		return (int)(refreshTime * 10000) ;
	}
	
	public boolean equals( Object other ) {
		return this.equals((AccCriteria)other) ;
	}
	
	public boolean equals( AccCriteria other ) {
		return isTimeRefreshable == other.isTimeRefreshable && min_rotation == other.min_rotation && refreshTime == other.refreshTime ;
	}
	
	static final float DEFAULT_MIN_ROTATION = 10;
	static final double DEFAULT_REFRESH_TIME = 2.0;
	static final boolean DEFAULT_TIME_REFRESHABLE = true;

	private static final long serialVersionUID = 1L;
}
