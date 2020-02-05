package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import simsim.gui.charts.XYLineChart;
import speedsense.Hotspot;

class DetectionRateChart extends Metric {
	final int timeSlotLength;
	final  XYLineChart chart;
	int[] detections;
	int[] events;
	int bucket = -1;
	File output;
	DetectionLatency detector;
	
	def errorSegments;
	def detectionSegments;

	public DetectionRateChart(int timeSlotLength) {
		this.timeSlotLength = timeSlotLength;
		int length = (int)((OSMSpeedSenseSim.setup.IDLE_TIME+OSMSpeedSenseSim.setup.RUN_TIME)/timeSlotLength+0.5) + 1;
		detections = new int[length];
		events = new int[length];
		chart = new XYLineChart("Success Rate", 1.0, "% Success", "Time");
		chart.setYRange( false, 0, 100 ) ;
		chart.setSeriesLinesAndShapes("Success", true, true) ;
		OSMSpeedSenseSim.Gui.setFrameRectangle("Success Hist", 484, 0, 480, 480);	
	}
	
	public void init() {
		detector = DetectionLatency.instance;
		output = openOutputFile("DetectionRate.csv") { writer ->
			writer << "Result\n1";
			(detections.size()-1).times { 
				writer << ",${it+2}";
			}
		}
		detector.addDetectionListener { double lat, boolean isNew -> 

			//detectionSegments
		}
		detector.addErrorListener { DetectionLatency.Error e, boolean isNew ->
			bucket = (int)(OSMSpeedSenseSim.currentTime() / timeSlotLength)
			events[bucket]++;
		}
		
	}
	
	protected int setBucket() {
		int newBucket = (int)(OSMSpeedSenseSim.currentTime() / timeSlotLength);
		if(newBucket != bucket) {
			errorSegments = [:];
			detectionSegments = [:];
			bucket = newBucket
		}	
		return bucket;
	}
	
	protected addDetection(String segmentId) {
		bucket = setBucket();
		detections[bucket]++;
		events[bucket]++;
	}
		
	public void stop() {
		if(bucket < 0) return
		for(int i=0; i< bucket; i++) {
			double val;
			if(events[i] > 0) {
				val =  (detections[i]*1.0)/events[i]*100;
			} else {
				val = 100;
			}
			output.append(",${val}");
		}
		output.append("\n");

	}
	
	public void update() {
		if(bucket < 0) return
		def series = chart.getSeries("Success");
		series.clear();
		for(int i=0; i<=bucket; i++) {
			if(events[i] > 0) {
				double frac =  (detections[i]*1.0)/events[i];
				series.add(i+1, frac*100);
			} else {
				series.add(i+1, 100);
			}
		}
		if(((int)OSMSpeedSenseSim.currentTime()) % 10 == 0 ) {
			println "events:     ${events}";
			println "detections: ${detections}"
		}
	}
}
