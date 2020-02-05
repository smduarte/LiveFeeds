package seeds.sensors.sys.cam;

import seeds.sensors.api.cam.CamData;
import seeds.sensors.api.cam.CamRequest;
import seeds.sensors.sys.common.VSensor;

abstract public class CamSensor extends VSensor.Impl {
	
	
	public CamSensor() {
		super("Camera");
		init() ;
	}
	
	protected void init() {}
	
	abstract protected CamData takePicture( CamRequest req ) ;
}
