package sensing.persistence.simsim.speedsense.osm.setup.hotspots;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.charts.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

public class HSSetup2_1000Nodes extends HSSetup2 {
	
	public HSSetup2_1000Nodes() {
		super();
		config.TOTAL_NODES = 500;
		config.TOTAL_MNODES = 1000;
		config.COUNT_THRESH = 30;
		config.DENSITY_SPEED_WEIGHT = 1/10;
	}
}
