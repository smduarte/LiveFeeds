package seeds.sensors.sys.mic;

import seeds.sensors.api.Seeds;
import seeds.sensors.api.gps.GpsCriteria;
import seeds.sensors.api.gps.GpsData;
import seeds.sensors.api.mic.MicCriteria;
import seeds.sensors.api.mic.MicData;
import seeds.sensors.sys.common.Sensor;
import seeds.sensors.sys.common.SensorChannel;
import feeds.api.Channel;
import feeds.api.Payload;
import feeds.api.Receipt;
import feeds.api.Subscriber;
import feeds.sys.core.ContainerListener;
import feeds.sys.packets.pPacket;
import feeds.sys.pipeline.ChannelSubscriptions;

abstract public class MicChannel extends SensorChannel<MicCriteria, MicCriteria> implements Subscriber<GpsData, Void> {

	public void init() {
		super.init() ;

		final Channel<GpsData, Void, ?, ?> channel = Seeds.lookup("/System/Sensors/Gps");
		psc.monitor(new ContainerListener<ChannelSubscriptions<MicCriteria>>() {
			public void handleContainerUpdate(ChannelSubscriptions<MicCriteria> c) {				
				channel.subscribe(new GpsCriteria(), MicChannel.this) ;
			}
		});		
		
		getSensor().monitor(new ContainerListener<Sensor>() {
			public void handleContainerUpdate(Sensor mic) {

				MicData data = mic.getValue();
				data.location = location.currentLocation ;
				
				pPacket<MicData, Object> p = new pPacket<MicData, Object>(channel(), thisNode, data, null);
				loq.send(p);				
			}
		});
	}

	public void notify(Receipt r, GpsData e, Payload<Void> p) {
		location = e ;
	}
	
	protected GpsData location = new GpsData() ;
}
