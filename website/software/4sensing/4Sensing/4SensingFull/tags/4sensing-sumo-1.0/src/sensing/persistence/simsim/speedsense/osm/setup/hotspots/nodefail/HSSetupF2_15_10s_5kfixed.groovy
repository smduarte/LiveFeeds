package sensing.persistence.simsim.speedsense.osm.setup.hotspots.nodefail;

import sensing.persistence.core.query.Query;

class HSSetupF2_15_10s_5kfixed extends HSSetupF2 {
	
	public HSSetupF2_15_10s_5kfixed() {
		config.FAIL_TIME = 150;
        config.FAIL_RATE = 0.15;
        config.FAIL_STAB_PERIOD = 10;
		config.FAIL_TRANSITION_PERIOD = 60;
        config.TOTAL_NODES = 5000;
        config.COUNT_THRESH = 60;
        config.DENSITY_SPEED_WEIGHT = 1/50;
		config.ROUTE_CACHE_SIZE = 5000;
        config.SAMPLING_RATE = 5;
		config.RUN_TIME = 300;
	}
}
