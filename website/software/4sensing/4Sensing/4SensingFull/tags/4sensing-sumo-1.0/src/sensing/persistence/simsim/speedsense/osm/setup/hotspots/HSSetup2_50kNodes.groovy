package sensing.persistence.simsim.speedsense.osm.setup.hotspots;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.charts.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

public class HSSetup2_50kNodes extends HSSetup2 {

	public HSSetup2_50kNodes() {
		super();
		config.TOTAL_NODES = 5000;
		config.TOTAL_MNODES = 50000;
		config.DENSITY_SPEED_WEIGHT = 1/50;
		config.COUNT_THRESH = 60;
		config.ROUTE_CACHE_SIZE = 5000;
		config.MAIN_WAYS = false;
	}
}
