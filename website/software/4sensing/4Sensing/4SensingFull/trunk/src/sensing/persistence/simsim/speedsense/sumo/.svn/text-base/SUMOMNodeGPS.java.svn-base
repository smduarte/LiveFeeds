package sensing.persistence.simsim.speedsense.sumo;

import groovy.lang.GroovyObject;
import sensing.persistence.simsim.PipelineSimJ;
import sensing.persistence.simsim.map.sumo.SUMOMapModel;
import simsim.core.Globals;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;

public class SUMOMNodeGPS extends SUMOMobileNode {
	GroovyObject GPSReading;
	PeriodicTask uploadTask;

	public SUMOMNodeGPS(String vehicleId, int updateRate) {
		super(vehicleId);
		PipelineSimJ simJ = Globals.get("4sensingJ");
		GPSReading = simJ.newTuple("speedsense.SGPSReading");
	}
	
	public void init() {

		uploadTask = new PeriodicTask(Simulation.rg.nextInt(sP), sP) {
			public void run() {
				if(isActive()) {
					GPSReading.setProperty("lat", gPos.lat());
					GPSReading.setProperty("lon", gPos.lon());
					GPSReading.setProperty("speed", speed);
					GPSReading.setProperty("segmentId", edgeId);
					boolean accepted = sensorInput(GPSReading);
//					if(accepted) {
//						SUMOMapModel.Edge edge = mapModel.getEdge(edgeId);
//						edge.count++;
//						edge.sumSpeed += speed;
//					}
				} else {
					//uploadTask.cancel();
				}
			}
		};
	}
	

}
