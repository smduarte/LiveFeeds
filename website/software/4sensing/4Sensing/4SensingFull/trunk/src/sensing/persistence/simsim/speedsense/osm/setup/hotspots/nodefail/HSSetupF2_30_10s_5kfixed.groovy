package sensing.persistence.simsim.speedsense.osm.setup.hotspots.nodefail;

import sensing.persistence.core.query.Query;

class HSSetupF2_30_10s_5kfixed extends HSSetupF2 {
	
	public HSSetupF2_30_10s_5kfixed() {
		config.FAIL_TIME = 150;
		config.FAIL_RATE = 0.30;
		config.FAIL_STAB_PERIOD = 10;
		config.TOTAL_NODES = 5000;
		config.COUNT_THRESH = 60;
		config.DENSITY_SPEED_WEIGHT = 1/50;
		
		
	/*	config.TOTAL_NODES = 500;
		config.TOTAL_MNODES = 1000;
		config.COUNT_THRESH = 30;
		config.DENSITY_SPEED_WEIGHT = 1/10;
		config.MAIN_WAYS = true;
		config.ROUTE_CACHE_SIZE = 1000;
	*/
	}
	
}
