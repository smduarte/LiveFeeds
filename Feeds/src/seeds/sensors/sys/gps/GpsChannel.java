package seeds.sensors.sys.gps;

import seeds.sensors.api.gps.GpsCriteria;
import seeds.sensors.api.gps.GpsData;
import seeds.sensors.api.gps.GpsLocation;
import seeds.sensors.sys.common.Sensor;
import seeds.sensors.sys.common.SensorChannel;
import feeds.sys.core.ContainerListener;
import feeds.sys.packets.pPacket;

abstract public class GpsChannel extends SensorChannel<GpsCriteria, GpsCriteria> {

	protected GpsLocation previous = null;

	public void init() {
		super.init();
				
		getSensor().monitor( new ContainerListener<Sensor>() {
			public void handleContainerUpdate(Sensor gps) {
				GpsLocation l = gps.getValue();
				if (l != null) {
					if(previous == null)
						previous = l;
					loq.send( new pPacket<GpsData, Object>(channel(), thisNode, new GpsData(l, previous), null) );
					previous = l;
				}
			}
		}) ;		
	}
	
}
