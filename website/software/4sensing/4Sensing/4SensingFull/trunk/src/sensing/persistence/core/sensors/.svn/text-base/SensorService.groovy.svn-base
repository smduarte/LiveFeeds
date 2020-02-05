package sensing.persistence.core.sensors;

import sensing.persistence.core.*;
import sensing.persistence.core.network.Peer;
import sensing.persistence.core.network.UnavailablePeer;
import sensing.persistence.core.pipeline.Tuple;
import sensing.persistence.core.sensors.monitoring.WorkloadSensor;
import sensing.persistence.core.sensors.monitoring.WorkloadReading;
import static sensing.persistence.core.logging.LoggingProvider.*;

import java.awt.Shape;

//TODO: remove listeners

public class SensorService extends Service {
	
	class Binding {
		Peer dest;
		Shape bounds;
		boolean isActive;
	}
	
	Map listeners = [:];
	Map binding = [:];
	protected int acquiredCount = 0;
	protected int remoteCount = 0;
	protected int bindingCount = 0;
	
	protected WorkloadSensor ws;

	public SensorService(ServiceManager services) {
		super(services);
		services.network.addMessageHandler {
			Peer src, SensorData q ->  if(input(q.data)) {
				acquiredCount++
				if(src != services.network.local) {
					remoteCount++;
				}
			};
		}
	}

	public void addListener(Class sensorClass, Object owner, Closure callback) {
		if(!listeners[sensorClass.name]) {
			listeners[sensorClass.name] = [];
		}
		listeners[sensorClass.name] << [owner: owner, callback: callback];
		services.logging.log(DEBUG, this, "addListener", "added $sensorClass listener for $owner");
		if(sensorClass == WorkloadReading && !ws) { 
			ws = new WorkloadSensor(services, 20)
			ws.start()
		}
	}

	public void removeListeners(owner) {
		listeners.keySet().each { sensor ->
			List oldListeners = listeners[sensor];
			listeners[sensor] = listeners[sensor].findAll{ listener ->
				listener.owner != owner;
			}
			int removed =  oldListeners.size() - listeners[sensor].size();
			if(removed > 0 ) {
				services.logging.log(DEBUG, this, "removeListeners", "removed $removed listeners for $owner");
			} else {
				services.logging.log(DEBUG, this, "removeListeners", "no listeners for $owner");
			}
		}
		if(ws && !listeners[WorkloadReading.name]) {
			ws.stop()
			ws = null()	
		}
	}

	public boolean send(Tuple reading) {
		Binding b;
		if((b = binding[reading.mNodeId]) == null || !b.bounds || !b.bounds.contains(reading.lon, reading.lat)) { // rebind
//			switch(b) {
//				case {!it}:
//					services.logging.log(DEBUG, this, "send", "rebinding - no bind available");
//					break;
//				case {!it.bounds}:
//					services.logging.log(DEBUG, this, "send", "rebinding - no bounds");
//					break;
//				case {!it.bounds.contains(reading.lon, reading.lat)}:
//					services.logging.log(DEBUG, this, "send", "rebinding - out of bounds");
//					break;
//			}
			def (availableDest, bounds) = services.query.getDataDestinationPeers(reading.lon, reading.lat);
			if(!availableDest) {
				println "no nearest node for ${reading} lat:${reading.lat} lon:${reading.lon} bb:${reading.boundingBox}"
				return null;
			}
			if(b && availableDest.indexOf(b.dest) != -1) { // keep current destination
				//services.logging.log(DEBUG, this, "send", "rebinding - keep current destination");
				b.bounds = bounds;
			} else {
			//services.logging.log(DEBUG, this, "send", "rebinding - new destination");
				b = new Binding();
				b.dest = availableDest[services.random.nextInt(availableDest.size())];
				b.bounds =  bounds;
			}
		} 
		//services.logging.log(DEBUG, this, "send", "sending tuple to ${b.dest.id}");
		try{
			boolean accepted = b.dest.services.sensor.input(services.network.local,  reading);
			if(accepted) {
				//services.logging.log(DEBUG, this, "send", "sending tuple - accepted");
				if(b != binding[reading.mNodeId]) {
					binding[reading.mNodeId] = b;
					bindingCount++;
				}
				binding[reading.mNodeId].isActive = true;
			} else {
				if(b != binding[reading.mNodeId]) {
					binding[reading.mNodeId] = b;
				}
				binding[reading.mNodeId].isActive = false;
			}
			return accepted;
		} catch(UnavailablePeer e) {
			println "[${services.network.local.nodeId}] Sensor Service send - unavailable peer $b.dest"
			services.network.markOffline(b.dest)
			binding[reading.mNodeId] = null;
			send(reading);
		}
	}

	public UUID getBindingDestinationId(UUID mNodeId) {
		if(binding[mNodeId]?.isActive) {
			return 	binding[mNodeId].dest.id;
		} else {
			return null;
		}
	}
	
	
	protected boolean input(Peer src, reading) {
		//TODO shortcut, should be done through messaging
		if(services.network.local.isOffline()) { 
			throw new UnavailablePeer();
		}

		services.network.syncPDB(src.services.network.pDBVersionN);
		boolean accepted =  input(reading); 
		if(accepted) {
			acquiredCount++
			if(src != services.network.local) {
				remoteCount++;
			}
		}
		//services.logging.log(DEBUG, this, "input", "${reading.data.class.name} reading accepted: $accepted")
		return accepted;
	}
	
	protected boolean input(Tuple reading) {
		int acceptedCount = 0;
		services.logging.log(DEBUG, this, "inputImpl", "got $reading");
		services.logging.log(DEBUG, this, "inputImpl", "listeners for ${reading.class.name}: ${listeners[reading.class.name]}");
		if(listeners[reading.class.name]) {

			listeners[reading.class.name].each { listener ->
				acceptedCount += listener.callback(reading) ? 1 : 0;
			}
		}
		return acceptedCount > 0;
	}
	

	
	public int getAcquiredCount() {
		return acquiredCount;
	}
	
	public int getRemoteCount() {
		return remoteCount;
	}
	
	public int getBindingCount() {
		return bindingCount;
	}
	
	
}
