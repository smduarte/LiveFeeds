package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import simsim.gui.charts.XYLineChart;
import speedsense.Hotspot;

class ErrorRateChart extends Metric {
	final int timeSlotLength;
	final  XYLineChart chart;
	int[] detections;
	int[] events;
	int bucket = -1;
	File output;
	DetectionLatency detector;
	
	HashSet errorSegments;
	HashSet detectionSegments;

	public ErrorRateChart(int timeSlotLength) {
		this.timeSlotLength = timeSlotLength;
		int length = (int)((OSMSpeedSenseSim.setup.IDLE_TIME+OSMSpeedSenseSim.setup.RUN_TIME)/timeSlotLength+0.5) + 1;
		detections = new int[length];
		events = new int[length];
		chart = new XYLineChart("Error Rate", 1.0, "% Success", "Time");
		chart.setYRange( false, 0, 100 ) ;
		chart.setSeriesLinesAndShapes("Error", true, true) ;
		OSMSpeedSenseSim.Gui.setFrameRectangle("Error Rate", 484, 0, 480, 480);	
	}
	
	public void init() {
		detector = DetectionLatency.instance;
		output = openOutputFile("ErrorRate.csv") { writer ->
			writer << "Result\n1";
			(detections.size()-1).times { 
				writer << ",${it+2}";
			}
			writer << ",ERROR\n";
		}
		detector.addDetectionListener {String segmentId, double lat, boolean isNew -> 
			addDetection(segmentId)
		}
		detector.addErrorListener {String segmentId,  DetectionLatency.Error e, boolean isNew ->
			addError(segmentId)
		}
		
	}
	
	protected void setBucket() {
		int newBucket = (int)(OSMSpeedSenseSim.currentTime() / timeSlotLength);
		if(newBucket != bucket) {
			errorSegments = [:];
			detectionSegments = [:];
			bucket = newBucket
		}
	}
	
	protected addDetection(String segmentId) {
		setBucket();
		if(!detectionSegments.contains(segmentId)) {
			detections[bucket]++;
			events[bucket]++;
			detectionSegments.add(segmentId);
		}
	}
	
	protected addError(String segmentId) {
		setBucket();
		if(!errorSegments.contains(segmentId)) {
			events[bucket]++;
			errorSegments.add(segmentId);
		}
	}
	
	
		
	public void stop() {
		if(bucket < 0) return
		int totalEvents = 0;
		int totalDetections = 0;
		for(int i=0; i< bucket; i++) {
			double val;
			if(events[i] > 0) {
				val =  (events[i] - detections[i])*1.0/events[i]*100;
			} else {
				val = 0;
			}
			totalEvents += events[i];
			totalDetections += detections[i];
			output.append("${val},");
		}
		double errorRate = (totalEvents-totalDetections)*1.0/totalEvents*100;
		output.append("${errorRate}\n");
		println "Error rate: ${errorRate}"
	}
	
	public void update() {
		if(bucket < 0) return
		def series = chart.getSeries("Error");
		series.clear();
		for(int i=0; i<=bucket; i++) {
			if(events[i] > 0) {
				double frac = (events[i] - detections[i])*1.0/events[i];
				series.add(i+1, frac*100);
			} else {
				series.add(i+1, 0);
			}
		}
/*		if(((int)SpeedSenseSim.currentTime()) % SpeedSenseSim.setup.OUTPUT_INTERVAL == 0 ) {
			println "events:     ${events}";
			println "detections: ${detections}"
		}
*/
	}
}
