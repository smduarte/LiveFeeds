package sensing.persistence.simsim.speedsense.sumo
import simsim.core.Globals;
import simsim.gui.canvas.Canvas;
import sensing.persistence.simsim.speedsense.SpeedSenseSim;
import sensing.persistence.simsim.speedsense.SpeedSenseNode;
import sensing.persistence.simsim.MobileNodeDB;
import sensing.persistence.simsim.map.MapModel;
import sensing.persistence.simsim.map.sumo.SUMOMapModel;
import sensing.persistence.simsim.speedsense.map.*;
import sensing.persistence.core.ServicesConfig;
import sensing.persistence.simsim.MobileNodeDB;
import sensing.persistence.simsim.speedsense.sumo.SUMOMNodeSegmentSpeed2;


import simsim.core.PeriodicTask;

class SUMOSpeedSenseSim extends SpeedSenseSim {
	SUMOVProbeSource tracesSrc;
	SUMOEdgeStatsSource edgeStatsSrc;
	
	public SUMOSpeedSenseSim() {
		super();
		QUERY_IMPL_POLICY = ServicesConfig.QueryImplPolicy.CENTRALIZED;
	}
	

	protected void init() {
		super.init();
		
		edgeStatsSrc = new SUMOEdgeStatsSource(setup.SUMO_EDGE_DATA,  setup.SUMO_EDGESTATS_PERIOD, mapModel);
		edgeStatsSrc.init();
		
		tracesSrc = new SUMOVProbeSource(setup.SUMO_VPROBE_DATA, 
						new SUMO4SVProbeHandler(setup.SUMO_VPROBE_SAMPLING_PERIOD, 
												setup.SUMO_EDGESTATS_PERIOD, 
												getMobileNodeFactory()));
		tracesSrc.init();
		
		new PeriodicTask(0,setup.SUMO_EDGESTATS_PERIOD) {
			public void run() {
				edgeStatsSrc.readNextPeriod();
//				if(SpeedSenseSim.selectedSegments) {
//						SpeedSenseSim.selectedSegments.each{ String sid -> 
//							println "EDGE $sid CURR_SPEED: ${mapModel.getEdge(sid).avgSpeed*3.6} PREV_SPEED: ${mapModel.getEdge(sid).pAvgSpeed*3.6}"
//						}
//				}
			}
		}
		
		new PeriodicTask(0,setup.SUMO_VPROBE_SAMPLING_PERIOD) {
			public void run() {
				tracesSrc.readNextTimestep();
			}		
		}
	}
	
	protected MapModel createMapModel() {
		MapModel m = new SUMOMapModel();
		m.load (setup.SUMO_MAP_MODEL);
		return m;
	}
	
	protected SUMOMNodeFactory getMobileNodeFactory() {
		def mNodeClass = Class.forName(setup.SUMO_MNODE_CLASS_NAME)
		return {String vehicleId -> mNodeClass.newInstance(vehicleId)} as SUMOMNodeFactory
	}
	
	public void displayOn( Canvas c ) {
		super.displayOn(c);
		if(display.mobile) {
			SUMOVehicleDB.displayOn(c);
		}
	}
	
	
//	public void invalidateDisplay() {
//		super.invalidateDisplay();
//		SUMOVehicleDB.invalidateDisplay();
//	}

	
	public static void main(String[] args) throws Exception {
		config(args);
		SUMOSpeedSenseSim sim = new SUMOSpeedSenseSim();
		sim.init();
		sim.start();
	}
}
