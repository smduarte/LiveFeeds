package sensing.persistence.simsim.speedsense.osm.setup.hotspots;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts.*;

class HSSetup2_test extends HotspotsSetup {
	
	public HSSetup2_test() {
		super();
		config.RUN_TIME = 3600;
		config.MIN_NODES_PER_QUAD = 6;
		config.SPEED_STD_DEV = 0;
		config.SIM_SEED = 5L;
		config.NET_SEED = 5L;

	}
	
	public Query getQuery() {
		String pipelineName = "speedsense.TrafficHotspots";
		double centerLat = 38.748100;
		double centerLon = -9.1639818;
		double width = 0.5 * OSMSpeedSenseSim.world.width;
		double height = 0.5 * OSMSpeedSenseSim.world.height;
		
		return createQuery(pipelineName, centerLat, centerLon, width, height);
	}
	
	protected setupCharts() {
		List charts = super.setupCharts();
		//new ResultSetAverage(), new ResultSetSampler(), 
		return [Detections.instance, ResultsPerSlot.instance, new ErrorChart(false)]	
	}
	
}
