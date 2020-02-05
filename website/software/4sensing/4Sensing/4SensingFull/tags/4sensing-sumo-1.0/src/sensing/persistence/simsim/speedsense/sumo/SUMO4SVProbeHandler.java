package sensing.persistence.simsim.speedsense.sumo;

import java.math.BigDecimal;

import sensing.persistence.simsim.MobileNodeDB;
import sensing.persistence.simsim.PipelineSimJ;
import sensing.persistence.simsim.map.sumo.SUMOMapModel;
import simsim.core.Globals;
import simsim.core.Simulation;
import sensing.persistence.simsim.MobileNode;

public class SUMO4SVProbeHandler implements SUMOVProbeHandler {
	
	final SUMOMapModel mapModel;
	final int sR;
	final int period;
	final int edgeStatPeriod;
	final double mNodeRatio;
	final SUMOMNodeFactory mNodeFactory;
	final MobileNodeDB<SUMOMobileNode> mNodeDB;
	final PipelineSimJ simJ;

	double ts = -1;
	int numVehicles = 0;
	int numStopped = 0;
	double totalSpeed = 0;
	int updateCounter = 0;


	public SUMO4SVProbeHandler(int period, int edgeStatPeriod, SUMOMNodeFactory mNodeFactory) {
		this.period = period;
		this.edgeStatPeriod = edgeStatPeriod;
		this.mNodeFactory = mNodeFactory;
		
		simJ = Globals.get("4sensingJ");
		mNodeDB = Globals.get("4sensing_MobileNodeDB");
		mapModel = Globals.get("4sensing_MapModel");
		
		sR = simJ.<Integer>getConfig("SAMPLING_RATE");
		mNodeRatio = simJ.<BigDecimal>getConfig("SUMO_MNODE_RATE").doubleValue();
		
	}

	
	@Override
	public void timestepStart(double ts) {
		this.ts = ts;
		numVehicles = 0;
		numStopped = 0;
		totalSpeed = 0;	
		
		if(updateCounter == (edgeStatPeriod/period)) {
			mapModel.updateVProbeStats();
			updateCounter = 0;
			System.out.printf("%.1f] updated edge stats\n", Simulation.currentTime());
		}
		updateCounter++;
		System.out.printf("%.1f] Timestep %d start\n", Simulation.currentTime(), (int)ts);
	}
	
	@Override
	public void timestepEnd() {
		System.out.printf("%.1f] Timestep %d end - total vehicles: %d; mobile nodes: %d; avg speed: %.2f km/h  %.2f m/s stopped: %.1f\n", Simulation.currentTime(), (int)ts, numVehicles,  simJ.getNumRegisteredMobileNodes(), totalSpeed/numVehicles/1000*3600, totalSpeed/numVehicles, numStopped*1.0/numVehicles*100);		
	}


	@Override
	public void vehicleUpdate(String vehicleId, double lat, double lon, double x, double y, double speed, String laneId, double pos) {
		if(speed == 0) numStopped++;
		numVehicles++;
		totalSpeed += speed; 
		boolean selected = false;
		if(vehicleId.equals("791731"))  selected = true;
		if(selected) System.out.printf("%.2f] vehicleUpdate for %s at %s\n", Simulation.currentTime(), vehicleId, laneId);
		SUMOMobileNode v  = mNodeDB.get(vehicleId);
		if(v == null) {
			if(selected) System.out.printf("%.2f] vehicleUpdate for %s - new Vehicle\n", Simulation.currentTime(), vehicleId);
			v =  mNodeFactory.newMobileNode(vehicleId);
			if(Simulation.rg.nextDouble() >= (1-mNodeRatio)) {
				if(selected) System.out.printf("%.2f] vehicleUpdate for %s - new Vehicle is 4Sensing node\n", Simulation.currentTime(), vehicleId);
				simJ.registerMobileNode(v);
				v.init();
			} 
			updateVehicle(v, lat, lon, x, y, speed, laneId, pos);
			mNodeDB.put(vehicleId, v);
		} else {
			if(selected) System.out.printf("%.2f] vehicleUpdate for %s - known vehicle - 4sensing:%s\n", Simulation.currentTime(), vehicleId, v.is4SensingNode() ? "true" : "false");
			updateVehicle(v, lat, lon, x, y, speed, laneId, pos);
		}
	}
	
	private void updateVehicle(SUMOMobileNode v, double lat, double lon, double x, double y, double speed, String laneId, double pos) {
		v.update(lat, lon, x, y, speed, laneId, pos);		
		SUMOMapModel.Edge edge = mapModel.getEdge(v.getEdgeId());
		edge.vProbeSpeeds.add(speed);
	}

}
