package sensing.persistence.simsim.speedsense.osm.setup.hotspots.locationchange

import sensing.persistence.simsim.speedsense.osm.setup.hotspots.*

class HSSetupLC2_50k_10m extends HSSetupLC2_50k_baseline {

	public HSSetupLC2_50k_10m() {
		super();
		config.NODE_LOCATION_CHANGE_PERIOD = 10*60;
	}
}
