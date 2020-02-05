package seeds.sensors.sys.mic;

import java.util.Collection;

import seeds.sensors.api.mic.MicCriteria;
import seeds.sensors.api.mic.MicData;
import seeds.sensors.sys.common.SensorParameters;
import seeds.sensors.sys.common.VSensor;

abstract public class MicSensor extends VSensor {

	protected MicData currentValue ;
	protected MicCriteria currentConfig ;
	
	public MicSensor(){
		super("Microphone");
		init();
	}
	
	public void init() {
		currentValue = new MicData() ;
		currentConfig = new MicCriteria() ;
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public <Q> Q getValue() {
		return (Q) currentValue;
	}

	@SuppressWarnings("unchecked")
	public <Q> Q getConfig() {
		return (Q) currentConfig ;
	}

	public void setParams( Collection<SensorParameters> params) {
			
		MicCriteria newConfig = new MicCriteria() ;
		
		for (SensorParameters i : params) {
			MicCriteria j = (MicCriteria) i ;

			newConfig.refreshRate = Math.min( newConfig.refreshRate, j.refreshRate ) ;
			newConfig.duration = Math.max( newConfig.duration, j.duration ) ;
		}

		if( ! currentConfig.equals( newConfig ) ) {
			currentConfig = newConfig ;
			reconfigureSensor() ;
		}
	}
}
