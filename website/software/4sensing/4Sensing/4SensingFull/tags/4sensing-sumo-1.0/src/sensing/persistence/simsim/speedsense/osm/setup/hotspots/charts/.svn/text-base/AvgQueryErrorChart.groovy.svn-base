package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

import simsim.gui.charts.XYLineChart;

@Singleton(lazy=true) class AvgQueryErrorChart extends Metric {
	
	final  XYLineChart chart;
	double totalError = 0;
	int nSamples = 0;
	QueryError qE;
	
	public AvgQueryErrorChart() {
		// Create a chart to monitor the infection progress rate
		chart = new XYLineChart("Avg Query Error", 1.0, "Error", "Time") ;
		chart.setSeriesLinesAndShapes("Avg Error", true, false) ;
		chart.setYRange(false,0,100);
		OSMSpeedSenseSim.Gui.setFrameRectangle("Avg Query Error", 484, 0, 480, 480);	
		qE = QueryError.instance;
	}
	
	public void update() {
		def(error, errorRate) = qE.getDetectionCountError();
		totalError += error;
		nSamples++;
		double avgError = totalError/nSamples;
		def series = chart.getSeries("Avg Error");
		series.add(OSMSpeedSenseSim.currentTime(), avgError);
	}
	

}

