package sensing.persistence.simsim.speedsense.osm.setup.hotspots.nodefail
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts.mq.*;

class HSSetupF2_samp15_mqmetric extends HSSetupF2_15_10s_5kfixed{
	
	public HSSetupF2_samp15_mqmetric() {
		super();
		config.SAMPLING_RATE = 15;
	}
	
	protected setupCharts() {
		ResultsPerSlot res = new ResultsPerSlot(1);
		return [res, new ErrorChart(res)];

	}

}
