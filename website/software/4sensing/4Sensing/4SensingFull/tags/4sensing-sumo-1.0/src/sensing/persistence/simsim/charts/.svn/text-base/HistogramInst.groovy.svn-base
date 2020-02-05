package sensing.persistence.simsim.charts
import sensing.persistence.simsim.PipelineSimulation

import sensing.persistence.simsim.Metric;
import simsim.gui.charts.XYLineChart;

class HistogramInst extends Histogram {
	Closure metric

	public HistogramInst(params, Closure metric) {
		super(params)
		this.metric = metric
	}
	
	
	public void updateHist() {
		List data = metric.call()
		clear()
		if(data) data.each{addValue(it)}
	}
}
