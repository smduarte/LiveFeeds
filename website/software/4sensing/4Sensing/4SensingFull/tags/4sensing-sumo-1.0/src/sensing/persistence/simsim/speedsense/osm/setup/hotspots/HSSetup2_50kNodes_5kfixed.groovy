package sensing.persistence.simsim.speedsense.osm.setup.hotspots;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.charts.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

public class HSSetup2_50kNodes_5kfixed extends HSSetup2_50kNodes {

	public HSSetup2_50kNodes_5kfixed() {
		super();
		config.TOTAL_NODES = 5000;
	}
}
