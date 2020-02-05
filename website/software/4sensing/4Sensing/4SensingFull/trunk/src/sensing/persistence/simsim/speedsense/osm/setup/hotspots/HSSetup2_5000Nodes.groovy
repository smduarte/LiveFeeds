package sensing.persistence.simsim.speedsense.osm.setup.hotspots;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.charts.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

public class HSSetup2_5000Nodes extends HSSetup2 {
	
	public HSSetup2_5000Nodes() {
		super();
		config.TOTAL_NODES = 500;
		config.TOTAL_MNODES = 5000;
	}
}
