package seeds.sensors.sys.wifi;

import seeds.sensors.api.Seeds;
import seeds.sensors.api.gps.GpsCriteria;
import seeds.sensors.api.gps.GpsData;
import seeds.sensors.api.wifi.WiFiCriteria;
import seeds.sensors.api.wifi.WiFiData;
import seeds.sensors.sys.common.Sensor;
import seeds.sensors.sys.common.SensorChannel;
import feeds.api.Channel;
import feeds.api.Payload;
import feeds.api.Receipt;
import feeds.api.Subscriber;
import feeds.sys.core.ContainerListener;
import feeds.sys.packets.pPacket;
import feeds.sys.pipeline.ChannelSubscriptions;

abstract public class WiFiChannel extends SensorChannel<WiFiCriteria, WiFiCriteria> implements Subscriber<GpsData, Void> {

	public void init() {
		super.init() ;

		final Channel<GpsData, Void, ?, ?> channel = Seeds.lookup("/System/Sensors/Gps");

		psc.monitor(new ContainerListener<ChannelSubscriptions<WiFiCriteria>>() {
			public void handleContainerUpdate(ChannelSubscriptions<WiFiCriteria> c) {				
				channel.subscribe(new GpsCriteria(), WiFiChannel.this) ;
			}
		});		
		
		getSensor().monitor(new ContainerListener<Sensor>() {
			public void handleContainerUpdate(Sensor wifi) {

				WiFiData data = wifi.getValue();
				data.location = location.currentLocation ;
				
				pPacket<WiFiData, Object> p = new pPacket<WiFiData, Object>(channel(), thisNode, data, null);
				loq.send(p);
			}
		});
	}

	public void notify(Receipt r, GpsData e, Payload<Void> p) {
		location = e ;
	}
	
	protected GpsData location = new GpsData() ;
}
