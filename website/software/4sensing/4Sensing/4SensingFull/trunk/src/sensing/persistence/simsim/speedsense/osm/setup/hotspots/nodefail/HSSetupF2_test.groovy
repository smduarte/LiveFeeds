package sensing.persistence.simsim.speedsense.osm.setup.hotspots.nodefail;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts.mq.*;

class HSSetupF2_test extends HSSetupF2 {

	public HSSetupF2_test() {
		super();
		config.TOTAL_MNODES = 500;
		config.COUNT_THRESH = 15;
		config.DENSITY_SPEED_WEIGHT = 1/5;
		config.ROUTE_CACHE_SIZE = 5000;
		config.MAIN_WAYS = true;
		
		config.RUN_TIME = 300;
		
		config.FAIL_TIME = 100;
		config.FAIL_RATE = 0.15;
		config.FAIL_STAB_PERIOD = 10;
		
		config.SPEED_STD_DEV = 2;
		config.SIM_SEED = 6L;
		config.NET_SEED = 6L;
	}
	
	protected setupCharts() {
		ResultsPerSlot res = new ResultsPerSlot(1);
		return [res, new ErrorChart(res)];

	}
}
