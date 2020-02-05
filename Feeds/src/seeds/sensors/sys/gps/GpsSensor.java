package seeds.sensors.sys.gps;

import java.util.Collection;

import seeds.sensors.api.gps.GpsCriteria;
import seeds.sensors.api.gps.GpsLocation;
import seeds.sensors.sys.common.SensorParameters;
import seeds.sensors.sys.common.VSensor;

abstract public class GpsSensor extends VSensor {

	protected GpsCriteria currentConfig ;
	protected GpsLocation currentLocation;

	public GpsSensor() {
		super("gps") ;
		init();
	}

	protected void init() {
		currentConfig = new GpsCriteria() ;
		currentLocation = new GpsLocation() ;
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) this.currentLocation;
	}

	@SuppressWarnings("unchecked")
	public <Q> Q getConfig() {
		return (Q) currentConfig ;
	}

	public void setParams( Collection<SensorParameters> params) {
		
		GpsCriteria newConfig = new GpsCriteria() ;
		
		for (SensorParameters i : params) {
			GpsCriteria j = (GpsCriteria) i ;

			newConfig.minTime = Math.min( newConfig.minTime, j.minTime ) ;
			newConfig.refreshRate = Math.min( newConfig.refreshRate, j.refreshRate ) ;
			newConfig.minDistance = Math.min( newConfig.minDistance, j.minDistance ) ;
		}

		if( ! currentConfig.equals( newConfig ) ) {
			currentConfig = newConfig ;
			reconfigureSensor() ;
		}
	}
}
