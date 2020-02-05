package seeds.sensors.sys.tilt;

import seeds.sensors.api.tilt.TiltCriteria;
import seeds.sensors.api.tilt.TiltData;
import seeds.sensors.sys.common.Sensor;
import seeds.sensors.sys.common.SensorChannel;
import feeds.sys.core.ContainerListener;
import feeds.sys.packets.pPacket;

abstract public class TiltChannel extends SensorChannel<TiltCriteria, TiltCriteria> {

	public void init() {
		super.init();

		getSensor().monitor(new ContainerListener<Sensor>() {
			public void handleContainerUpdate(Sensor tilt) {

				TiltData data = tilt.getValue();

				pPacket<TiltData, Object> p = new pPacket<TiltData, Object>(channel(), thisNode, data, null);
				loq.send(p);
			}
		});
	}

}
