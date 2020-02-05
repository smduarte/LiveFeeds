package seeds.sensors.sys.acc;

import seeds.sensors.api.acc.AccCriteria;
import seeds.sensors.api.acc.AccData;
import seeds.sensors.sys.common.Sensor;
import seeds.sensors.sys.common.SensorChannel;
import feeds.sys.core.ContainerListener;
import feeds.sys.packets.pPacket;

abstract public class AccelerometerChannel extends SensorChannel<AccCriteria, AccCriteria> {
	
	public void init() {
        super.init() ;
    
        getSensor().monitor( new ContainerListener<Sensor>() {            	
            public void handleContainerUpdate( Sensor accelerometer ) { 
            	
            	AccData data = accelerometer.getValue();
            	
            	pPacket<AccData,Object> p = new pPacket<AccData,Object>( channel(), thisNode, data, null);
            	loq.send(p);
            }
        }) ;
    }	
}
