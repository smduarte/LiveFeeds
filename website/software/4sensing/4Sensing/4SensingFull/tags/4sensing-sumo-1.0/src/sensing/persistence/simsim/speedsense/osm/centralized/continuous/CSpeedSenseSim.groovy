package sensing.persistence.simsim.speedsense.osm.centralized.continuous;
import sensing.persistence.simsim.speedsense.map.*;
import sensing.persistence.simsim.speedsense.SpeedSenseNode;
import sensing.persistence.simsim.speedsense.osm.OSMMobileNode;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.simsim.speedsense.setup.*;
import sensing.persistence.core.ServicesConfig;
import simsim.core.Globals;
import sensing.persistence.simsim.MobileNode;
import sensing.persistence.simsim.SimSetup;
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.*;
import sensing.persistence.simsim.speedsense.osm.setup.nodecount.*;

public class CSpeedSenseSim extends OSMSpeedSenseSim {
	
	public CSpeedSenseSim(String runId, String exit) {
		super(runId, exit)
	}	
	
	protected static void config() {	
		OSMSpeedSenseSim.config();
		Globals.set("Sim_RandomSeed", 0L);
		Globals.set("Net_RandomSeed", 0L);
	}
	
	protected void init(SpeedSenseSetup setup) {
		super.init(setup);
		
		setup.TOTAL_NODES.times { 
			if(it%100 == 0)println "initializing fixed node [${it}]"
			SpeedSenseNode node = new SpeedSenseNode();
			node.config.queryImplPolicy = ServicesConfig.QueryImplPolicy.CENTRALIZED;
			node.init();
			registerNode(node);
		};
		
		setup.TOTAL_MNODES.times {
			if(it%100 == 0)println "initializing mobile node [${it}]"
			MobileNode mnode = new OSMMobileNode();
			registerMobileNode(mnode);
			mnode.init();
		}

		super.setSimulationMaxTimeWarp(1e9);
	}
	
	public static void main(String[] args) throws Exception {
		config(args);
		CSpeedSenseSim sim = new CSpeedSenseSim();
		sim.init();
		sim.start();
	}
}
