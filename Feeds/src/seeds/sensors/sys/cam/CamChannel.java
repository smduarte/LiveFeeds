package seeds.sensors.sys.cam;

import seeds.sensors.api.cam.CamData;
import seeds.sensors.api.cam.CamRequest;
import seeds.sensors.sys.common.SensorChannel;
import feeds.sys.packets.fPacket;
import feeds.sys.packets.pPacket;

abstract public class CamChannel extends SensorChannel<CamRequest,CamData> {


	public void init() {
		super.init();

		camera = (CamSensor) getSensor() ;
	}

	public void pRoute(pPacket<CamRequest, Void> p) throws Exception {
		CamData photo = camera.takePicture(p.envelope());
		if( photo != null ) {
			loq.send( new fPacket<CamData, Void>( channel(), thisNode,thisNode, photo,null) ) ;
		}
	}
	
	CamSensor camera ;
}
