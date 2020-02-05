package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

import simsim.gui.charts.XYLineChart;

@Singleton(lazy=true) class QueryErrorChart extends Metric {
	final  XYLineChart chart;
	CorrectedQueryError qE;
	
	public QueryErrorChart() {
		// Create a chart to monitor the infection progress rate
		chart = new XYLineChart("Query Error", 1.0, "Error (%)", "Time") ;
		chart.setYRange( false, 0, 100 );
		chart.setSeriesLinesAndShapes("Error", true, false) ;
		OSMSpeedSenseSim.Gui.setFrameRectangle("Query Error", 484, 0, 480, 480);	
		qE = CorrectedQueryError.instance;
	}
	
	public void update() {
		def(error, errorRateCongested, errorRateTotal) = qE.getDetectionCountError();
		//println "error: ${error} errorRateCongested:${errorRateCongested} errorRateTotal: ${errorRateTotal}";
		def series = chart.getSeries("Error");
		series.add(OSMSpeedSenseSim.currentTime(), errorRateCongested*100);
	}
}
