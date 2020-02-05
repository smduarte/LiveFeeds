package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts

import sensing.persistence.simsim.Metric;
import sensing.persistence.simsim.PipelineSimulation;

class ResultSetSampler  extends ResultSetAverage {
	
	public ResultSetSampler() {
		this.fName = "ResultSetSampler.csv"
	}
	
	public void output() {
		super.output();
		cStats = new ResultSetStats.Stats();
		nSamples = 0;
	}
}