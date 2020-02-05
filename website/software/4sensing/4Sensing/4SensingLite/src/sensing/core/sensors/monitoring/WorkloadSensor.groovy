package sensing.core.sensors.monitoring

import sensing.core.ServiceManager;
import sensing.core.sensors.SensorData;
import java.awt.geom.Rectangle2D;
import static sensing.core.logging.LoggingProvider.*;

class WorkloadSensor {
	ServiceManager services;
	int samplingRate;
	
	public WorkloadSensor(ServiceManager services, int samplingRate) {
		this.services = services;
		this.samplingRate = samplingRate;
	}
	
	public void start() {
		services.scheduler.schedule(samplingRate) {
			services.logging.log(DEBUG, this, "workload", "new reading")
			
			
			def(lon,lat) = services.network.location;
			services.sensor.input(services.network.local, new SensorData(data: new WorkloadReading(
				peerId: services.network.local.id,
				lat: lat, 
				lon: lon,
				totalLoad: services.query.getProcessedTuples()
			)))
		}
	}
	
	public void stop() {
		unschedule()	
	}
}
