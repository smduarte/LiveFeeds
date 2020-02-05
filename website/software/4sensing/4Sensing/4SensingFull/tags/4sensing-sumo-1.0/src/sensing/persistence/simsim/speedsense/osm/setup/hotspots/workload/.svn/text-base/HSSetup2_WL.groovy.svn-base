package sensing.persistence.simsim.speedsense.osm.setup.hotspots.workload;
import sensing.persistence.simsim.PipelineSimulation;
import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.*
import sensing.persistence.core.sensors.monitoring.WorkloadReading;
import sensing.persistence.simsim.charts.WorkloadChart;
import simsim.core.*;

public class HSSetup2_WL extends HSSetup2 {
	
	public HSSetup2_WL() {
		super();
		config.SPEED_STD_DEV = 0;
		
		config.TOTAL_NODES = 500;
		config.IDLE_TIME = 5;
		
		config.MONITORING = true;
		config.MONITORING_WORKLOAD = true;
		config.MONITORING_GRID_LEN = 25;
		
		config.RUN_TIME = 120*60;
		
		config.SIM_RANDOM_SEED = 5L;
		config.NET_RANDOM_SEED = 5L;
	}
	
	
	protected setupCharts() {
		List charts = super.setupCharts();
		charts.addAll([
			new WorkloadChart({vals, node -> vals << node.getProcessedTuples(["global"])}, "Aggregation", {1}, config.TOTAL_NODES, true)
		])
		return charts;
	}
}
