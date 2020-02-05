package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import speedsense.Hotspot;
import sensing.persistence.simsim.*;
import simsim.gui.charts.XYLineChart;
import org.jfree.data.xy.XYSeries;

class DetectionsPerLevelChart extends Metric {
	final  XYLineChart chart;
	List detections = [];
	int length;
	int slot = 0;
	
	public DetectionsPerLevelChart() {
		chart = new XYLineChart( "Detections Per Level", 1.0, "Detections", "Time") ;
		PipelineSimulation.Gui.setFrameRectangle("Detections Per Level", 484, 0, 480, 480);
		length = OSMSpeedSenseSim.setup.IDLE_TIME+OSMSpeedSenseSim.setup.RUN_TIME + 1;
	}

	public void init() {
		OSMSpeedSenseSim.setup.addQueryListener { Hotspot d ->
			if (d.level >= detections.size()) {
				(detections.size()..d.level).each{ level ->
					detections[level] = new int[length];
					//chart.createSeries("Level-${d.level}");
					chart.setSeriesLinesAndShapes("Level-${level}", true, false);
					println ">>>> added level ${level}";
				}
			}
			slot = (int)(OSMSpeedSenseSim.currentTime()/10);
			detections[d.level][slot]++;
		}
	}
	
	public void update() {
		detections.eachWithIndex{det, idx ->
			if(det != null) {
				//println ">>>> level ${idx}: ${det}";
				XYSeries series = chart.getSeries("Level-${idx}");
				series.clear();
				for(int i=0; i <= slot; i++) {
					//println "${i}>"
					series.add(i, det[i]);
				}
			}
		}
	}

}
