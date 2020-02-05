package seeds.sensors.sys.tilt;

import java.util.Collection;

import seeds.sensors.api.tilt.TiltCriteria;
import seeds.sensors.api.tilt.TiltData;
import seeds.sensors.sys.common.SensorParameters;
import seeds.sensors.sys.common.VSensor;
import feeds.sys.tasks.Task;

abstract public class TiltSensor extends VSensor {

	protected TiltData currentValue = new TiltData() ;
	protected TiltCriteria currentConfig = new TiltCriteria() ;
	

	public TiltSensor() {
		super("tilt");
		init();
	}

	protected void init() {
		new Task(currentConfig.refreshRate) {
			public void run() {
				notifyUpdateNow() ;
				this.reSchedule(currentConfig.refreshRate);
			}
		};
	}

	
	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) this.currentValue;
	}
	
	@SuppressWarnings("unchecked")
	public <Q> Q getConfig() {
		return (Q) currentConfig ;
	}

	public void setParams( Collection<SensorParameters> params) {
			
		TiltCriteria newConfig = new TiltCriteria() ;
		
		for (SensorParameters i : params ) {
			TiltCriteria j = (TiltCriteria) i ;
			newConfig.refreshRate = Math.min( newConfig.refreshRate, j.refreshRate ) ;
		}

		if( ! currentConfig.equals( newConfig ) ) {
			currentConfig = newConfig ;
			reconfigureSensor() ;
		}
	}
}
