package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import simsim.gui.charts.XYLineChart;

@Singleton(lazy=true)  class AvgLatencyChart extends Metric {
	
	final  XYLineChart chart;
	DetectionLatency dL;
	
	public AvgLatencyChart() {
		// Create a chart to monitor the infection progress rate
		chart = new XYLineChart("Avg Latency", 1.0, "Latency", "Time") ;
		chart.setSeriesLinesAndShapes("Avg Latency", true, false) ;
		OSMSpeedSenseSim.Gui.setFrameRectangle("Avg Latency", 484, 0, 480, 480);	
		dL = DetectionLatency.instance;
	}
	
	public void update() {
		double avgL = dL.getAvgLatency();
		def series = chart.getSeries("Avg Latency");
		series.add(OSMSpeedSenseSim.currentTime(), avgL);
	}
}
