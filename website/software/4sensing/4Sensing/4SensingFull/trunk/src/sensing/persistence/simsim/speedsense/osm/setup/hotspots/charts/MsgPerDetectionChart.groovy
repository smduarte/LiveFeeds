package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

import simsim.gui.charts.XYLineChart;


@Singleton(lazy=true) class MsgPerDetectionChart extends Metric {
	final  XYLineChart chart;
	
	public MsgPerDetectionChart() {
		chart = new XYLineChart("Msg/Detection", 1.0, "Msg/Detection", "Time") ;
		chart.setSeriesLinesAndShapes("Msg/Detection", true, false) ;
		OSMSpeedSenseSim.Gui.setFrameRectangle("Msg/Detection", 484, 0, 480, 480);	
	}
	
	public void update() {
		if(OSMSpeedSenseSim.setup.totalDetections==0) return;
		double msgSent = OSMSpeedSenseSim.nodeList.inject(0) {sum, node->
			sum += node.getSentMsg("sensing.persistence.core.query.QueryData");
			sum += node.getSentMsg("sensing.persistence.core.query.QueryResult");
		}
		def series = chart.getSeries("Msg/Detection");
		println "msgSent: ${msgSent} totalD: ${OSMSpeedSenseSim.setup.totalDetections} ratio:${msgSent/OSMSpeedSenseSim.setup.totalDetections}";
		series.add(OSMSpeedSenseSim.currentTime(), msgSent/OSMSpeedSenseSim.setup.totalDetections);
	}
}
