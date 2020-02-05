package seeds.sensors.sys.acc;

import java.util.Collection;

import seeds.sensors.api.acc.AccCriteria;
import seeds.sensors.api.acc.AccData;
import seeds.sensors.sys.common.SensorParameters;
import seeds.sensors.sys.common.VSensor;

abstract public class AccelerometerSensor extends VSensor {

	protected AccData currentValue ;
	protected AccCriteria currentConfig;
	
	public AccelerometerSensor() {
		super("accelerometer");
		init();
	}
	
	protected void init(){
		currentValue = new AccData() ;
		currentConfig = new AccCriteria() ;
	}
	
	@SuppressWarnings("unchecked")
	public <Q> Q getValue() {
		return (Q) currentValue ;
	}

	@SuppressWarnings("unchecked")
	public <Q> Q getConfig() {
		return (Q) currentConfig ;
	}

	public void setParams( Collection<SensorParameters> params) {
			
		AccCriteria newConfig = new AccCriteria() ;
		
		for (SensorParameters i : params) {
			AccCriteria j = (AccCriteria) i ;

			newConfig.min_rotation = Math.min( newConfig.min_rotation, j.min_rotation ) ;
			if( j.isTimeRefreshable )
				newConfig.refreshTime = Math.min( newConfig.refreshTime, j.refreshTime ) ;
		}

		if( ! currentConfig.equals( newConfig ) ) {
			currentConfig = newConfig ;
			reconfigureSensor() ;
		}
	}

}
