package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts

import java.io.File;

import sensing.persistence.simsim.Metric;
import sensing.persistence.simsim.PipelineSimulation;

class ResultSetAverage  extends Metric  {
	
	protected File output;
	protected ResultSetStats.Stats cStats = new ResultSetStats.Stats();
	protected int nSamples = 0;
	protected String fName;
	protected List header = ["CONGESTED", "DETECTIONS", "FALSE_POS", "FALSE_NEG", "SUCCESS_RATE", "ERROR_RATE"]
	
	public ResultSetAverage() {
		this.fName = "ResultSetAverage.csv"
	}
	

	public void init() {
		output = openOutputFile(fName, header);
	}
	
	
	public void update() {
		ResultSetStats.Stats stats = ResultSetStats.getStats();
		if(stats) {
			cStats.congested += stats.congested;
			cStats.detections += stats.detections;
			cStats.falsePositive += stats.falsePositive;
			cStats.falseNegative += stats.falseNegative;
			cStats.successRate += stats.successRate;
			cStats.errorRate += stats.errorRate;
			nSamples++;
		}
	}
	
	public void output() {
		if(nSamples > 0) {
			writeToFile(fName,[cStats.congested*1.0/nSamples, 
								cStats.detections*1.0/nSamples, 
								cStats.falsePositive*1.0/nSamples, 
								cStats.falseNegative*1.0/nSamples, 
								cStats.successRate/nSamples,
								cStats.errorRate/nSamples],
						header);
		}
	}
}