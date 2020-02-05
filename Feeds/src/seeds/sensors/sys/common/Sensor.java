package seeds.sensors.sys.common;

import java.util.Collection;

public interface Sensor {

	public <Q> Q getValue();

	public void setParams( Collection<SensorParameters> params) ;

}
