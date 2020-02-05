package sensing.persistence.simsim.speedsense.setup

import sensing.persistence.core.query.Query;

class NoQuery extends SpeedSenseSetup {
	
	public NoQuery() {
		super();
		config.RUN_TIME = 0;
		config.TOTAL_NODES = 500;
		config.TOTAL_MNODES = 500;
		config.DENSITY_SPEED_WEIGHT = 1/50;
		config.COUNT_THRESH = 60;
		config.ROUTE_CACHE_SIZE = 5000;
		config.MAIN_WAYS = false;
		
	}
	
	protected void startQuery() {};
	
	public setupCharts() {return []}
}
