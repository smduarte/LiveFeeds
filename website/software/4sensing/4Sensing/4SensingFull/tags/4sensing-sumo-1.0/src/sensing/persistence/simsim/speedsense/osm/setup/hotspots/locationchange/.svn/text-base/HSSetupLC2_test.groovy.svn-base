package sensing.persistence.simsim.speedsense.osm.setup.hotspots.locationchange

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.*
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts.*;

class HSSetupLC2_test extends HSSetup2 {

	
	public HSSetupLC2_test() {
		super();
		config.SPEED_STD_DEV = 0;
		
		config.TOTAL_NODES = 500;
		config.TOTAL_MNODES = 500;
		config.DENSITY_SPEED_WEIGHT = 1/5;
		config.COUNT_THRESH = 15;
		config.ROUTE_CACHE_SIZE = 5000;
		config.MAIN_WAYS = true;
		
		config.NODE_LOCATION_CHANGE_PERIOD = 5*60;
		
		config.RUN_TIME = 120*60;
		
		config.SIM_RANDOM_SEED = 7L;
		config.NET_RANDOM_SEED = 7L;
		//config.SIM_TIME_WARP = 2.0;
		
		config.QUERY_RESULT_VALIDITY = 10;
	}
	
	protected setupCharts() {
		List charts = super.setupCharts();
		return [*charts, ResultsPerSlot.instance, new ErrorChart(true)]
	}
	
}
