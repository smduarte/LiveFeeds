package sensing.persistence.simsim.speedsense.osm.ntree.continuous;
import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.map.*;
import sensing.persistence.core.ServicesConfig;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.simsim.speedsense.map.setup.*;
import sensing.persistence.simsim.speedsense.map.setup.hotspots.*;
import sensing.persistence.simsim.speedsense.map.setup.nodecount.*;
import simsim.core.Globals;

public class NTSpeedSenseSim extends OSMSpeedSenseSim {
	
	public NTSpeedSenseSim() {
		super();
		QUERY_IMPL_POLICY = ServicesConfig.QueryImplPolicy.NEAREST_TREE;
	}
	
	//args: setup, runId, env, exit (true/false)
	public static void main(String[] args) throws Exception {
		config(args);
		NTSpeedSenseSim sim = new NTSpeedSenseSim();
		sim.init();
		sim.start();
	}
}
