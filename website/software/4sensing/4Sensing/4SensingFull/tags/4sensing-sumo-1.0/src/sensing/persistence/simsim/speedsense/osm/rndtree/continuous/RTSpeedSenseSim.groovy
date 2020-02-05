package sensing.persistence.simsim.speedsense.osm.rndtree.continuous;

import sensing.persistence.simsim.speedsense.map.*;
import sensing.persistence.core.ServicesConfig;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.simsim.speedsense.map.setup.*;
import simsim.core.Globals;
import sensing.persistence.simsim.speedsense.map.setup.hotspots.*;
import sensing.persistence.simsim.speedsense.map.setup.nodecount.*;

public class RTSpeedSenseSim extends OSMSpeedSenseSim {	
	
	public RTSpeedSenseSim() {
		super();
		QUERY_IMPL_POLICY = ServicesConfig.QueryImplPolicy.RND_TREE;
	}
	
	public static void main(String[] args) throws Exception {
		config(args);
		RTSpeedSenseSim sim = new RTSpeedSenseSim();
		sim.init();
		sim.start();
	}

}
