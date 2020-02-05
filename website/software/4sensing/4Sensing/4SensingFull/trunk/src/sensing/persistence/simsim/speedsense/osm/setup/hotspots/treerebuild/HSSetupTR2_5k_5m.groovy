package sensing.persistence.simsim.speedsense.osm.setup.hotspots.treerebuild
import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.*

class HSSetupTR2_5k_5m extends HSSetup2 {

	
	public HSSetupTR2_5k_5m() {
		super();
		config.TOTAL_NODES = 500;
		config.TOTAL_MNODES = 5000;
		config.DENSITY_SPEED_WEIGHT = 1/20;
		config.COUNT_THRESH = 40;
		config.ROUTE_CACHE_SIZE = 5000;
		config.MAIN_WAYS = true;
		
		config.TREE_TRANSITION_PERIOD = 80;
		config.TREE_REBUILD_PERIOD = 5*60; 
		
		config.RUN_TIME = 120*60;
	}
	

	
}