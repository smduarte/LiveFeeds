package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import simsim.gui.charts.XYLineChart;
import speedsense.Hotspot;

class LatencyHistogramChart extends Metric {
	final static int HIST_STEP = 10 // 10 sec buckets
	final static int NUM_BUCKETS = 20// 200 sec max
	final  XYLineChart chart;
	int[] histogram = new int[NUM_BUCKETS];
	File output;
	DetectionLatency detector;
	boolean display;

	public LatencyHistogramChart(boolean display=true) {
		this.display = display;
		if(display) {
			chart = new XYLineChart("Latency Hist", 1.0, "Num Detections", "Latency") ;
			//chart.setYRange( false, 0, 100 ) ;
			chart.setSeriesLinesAndShapes("Latency", true, true) ;
			OSMSpeedSenseSim.Gui.setFrameRectangle("Latency Hist", 484, 0, 480, 480);	
		}
	}
	
	public void init() {
		detector = DetectionLatency.instance;
		output = openOutputFile("LatencyHistogram.csv") { writer ->
			writer << "Result\n0";
			(NUM_BUCKETS).times { 
				writer << ",${(it+1)*HIST_STEP}";
			}
			writer << ",TOTAL_DETECTED\n";	
		}
		detector.addDetectionListener {String segmentId,  double lat, boolean isNew -> 
			if(isNew) {
				int bucket = Math.min(NUM_BUCKETS-1,(int)(lat/HIST_STEP));
				histogram[bucket]++;
			}
		}
		
	}
	
	
	public void stop() {
        int totalDetections = histogram.inject(0){sum, val -> sum + val};
        if(totalDetections == 0) return;
		//int acumulated = 0;
		output.append("0");
		for(int i=0; i<NUM_BUCKETS; i++) {
			//acumulated += histogram[i];
			//double frac = acumulated/totalDetections;
			output.append(",${histogram[i]}");
		}
		output.append(",${totalDetections}\n");
		println "detections: ${totalDetections} "
	}
	
	public void update() {
		if(!display) return;
		int totalDetections = histogram.inject(0){sum, val -> sum + val};
		if(totalDetections == 0) return;
		def series = chart.getSeries("Latency");
		series.clear();	
		//int acumulated = 0;
		//def hist = [];
		series.add(0,0);
		for(int i=0; i<NUM_BUCKETS; i++) {
			//acumulated += histogram[i];
			//double frac = acumulated/totalDetections;
			series.add((i+1)*HIST_STEP, histogram[i]);
			//hist << [(i+1)*HIST_STEP, histogram[i], frac*100];
		}
		//if((int)SpeedSenseSim.currentTime() %10 == 0) println "${done} ${hist}";
		//println "${done} ${hist}";
	}
}
