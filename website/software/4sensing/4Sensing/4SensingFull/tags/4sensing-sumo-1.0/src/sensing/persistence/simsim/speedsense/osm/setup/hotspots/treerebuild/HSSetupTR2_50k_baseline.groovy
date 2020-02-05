package sensing.persistence.simsim.speedsense.osm.setup.hotspots.treerebuild
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.*
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts.*;

class HSSetupTR2_50k_baseline extends HSSetup2_50kNodes_5kfixed {

	public HSSetupTR2_50k_baseline() {
		super();
		config.RUN_TIME = 7200;
		//config.OUTPUT_INTERVAL = 10*60;
		config.MIN_NODES_PER_QUAD = 6;
	}
	
	protected setupCharts() {
		List charts = super.setupCharts();
		return [*charts, new ResultSetAverage(), new ResultSetSampler()]
	}
	
}
