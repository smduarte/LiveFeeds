package sensing.persistence.simsim.speedsense.osm.setup.nodecount

import sensing.persistence.simsim.Metric;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts.QueryError;
import simsim.gui.charts.XYLineChart;

@Singleton(lazy=true) class QueryErrorChart extends Metric {
	final  XYLineChart chart;
	QueryError qE;
	
	public QueryErrorChart() {
		// Create a chart to monitor the infection progress rate
		chart = new XYLineChart("Query Error", 1.0, "Error (%)", "Time") ;
		chart.setYRange( false, 0, 100 );
		chart.setSeriesLinesAndShapes("Error", true, false) ;
		OSMSpeedSenseSim.Gui.setFrameRectangle("Query Error", 484, 0, 480, 480);	
		qE = QueryError.instance;
	}
	
	public void update() {
		double realCount = OSMSpeedSenseSim.activeMobileNodes.size();
		double resultCount = OSMSpeedSenseSim.setup.lastCount;
		println "realCount: ${realCount} resultCount: ${resultCount}";
		
		def series = chart.getSeries("Error");
		series.add(OSMSpeedSenseSim.currentTime(), Math.abs(1-resultCount/realCount)*100);
	}
}
