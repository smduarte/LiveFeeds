package sensing.persistence.simsim.speedsense.sumo;
import sensing.persistence.simsim.map.sumo.SUMOMapModel;
import groovy.lang.GroovyObject;
import simsim.core.Simulation;

public class SUMOMNodeTravelTime extends SUMOMobileNode  {
	
	SUMOMapModel.Edge sampledEdge;
	double edgeEntryTs;
	boolean isRouteStart;
	
	public SUMOMNodeTravelTime(String vehicleId) {
		super(vehicleId);
	}

	public void init() {
		super.init();
		sampledEdge = null;
		edgeEntryTs = -1;
		isRouteStart = false;
	}
	
	public void update(double lat, double lon, double x, double y, double speed, String laneId, double pos) {
		super.update(lat, lon, x, y, speed, laneId, pos);

		if(homeBase != null) {	
			if(sampledEdge == null || !isActive()) { // route start or teleported
				sampledEdge = mapModel.getEdge(edgeId);
				edgeEntryTs = Simulation.currentTime();
				isRouteStart = true;
				//System.out.println(vehicleId + "] ROUTE START: " + edgeId);
			} else if(!sampledEdge.id.equals(edgeId)) { // crossed edge
				sendReading();
				sampledEdge = mapModel.getEdge(edgeId);
				edgeEntryTs = Simulation.currentTime();
				isRouteStart = false;
				//System.out.println(vehicleId + "] CROSSED EDGE: " + edgeId);
			}
		}
	}
	
	protected void sendReading() {
		//System.out.println(vehicleId + "] SEND: " + selectedSegmentId);
		if(!sampledEdge.id.equals(selectedSegmentId)) return;
		
		GroovyObject tt = simJ.newTuple("speedsense.TravelTime");
		tt.setProperty("segmentId", sampledEdge.id);
		tt.setProperty("boundingBox", sampledEdge.bbox);
		double elapsed = Simulation.currentTime() - edgeEntryTs;
		tt.setProperty("travelTime", elapsed);
		System.out.println(vehicleId + "] SEND: " + elapsed);
		sensorInput(tt);
	}
}
