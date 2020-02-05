package sensing.persistence.simsim.speedsense.osm.setup.hotspots.locationchange
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.*
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts.*;

class HSSetupLC2_50k_baseline extends HSSetup2_50kNodes_5kfixed  {

	
	public HSSetupLC2_50k_baseline() {
		super();
		config.RUN_TIME = 7200;
		//config.OUTPUT_INTERVAL = 10*60;
	}
	
	protected setupCharts() {
		List charts = super.setupCharts();
		return [*charts, new ResultSetAverage(), new ResultSetSampler()]
	}
	
}
