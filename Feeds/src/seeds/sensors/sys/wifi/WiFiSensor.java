package seeds.sensors.sys.wifi;


import java.util.Collection;

import seeds.sensors.api.wifi.WiFiCriteria;
import seeds.sensors.api.wifi.WiFiData;
import seeds.sensors.sys.common.SensorParameters;
import seeds.sensors.sys.common.VSensor;

abstract public class WiFiSensor extends VSensor{

	protected WiFiData currentValue ;
	protected WiFiCriteria currentConfig ;
	
	public WiFiSensor() {
		super("WiFi");
		init();
	}

	protected void init(){
		currentValue = new WiFiData() ;
		currentConfig = new WiFiCriteria() ;
	}


	@SuppressWarnings("unchecked")
	public <T> T getValue(){
		return (T)this.currentValue;
	}

	@SuppressWarnings("unchecked")
	public <Q> Q getConfig() {
		return (Q) currentConfig ;
	}

	public void setParams( Collection<SensorParameters> params) {
		
		WiFiCriteria newConfig = new WiFiCriteria() ;
		
		for (SensorParameters i : params ) {
			WiFiCriteria j = (WiFiCriteria) i ;
			newConfig.refreshRate = Math.min( newConfig.refreshRate, j.refreshRate ) ;
		}

		if( ! currentConfig.equals( newConfig ) ) {
			currentConfig = newConfig ;
			reconfigureSensor() ;
		}
	}


	
}