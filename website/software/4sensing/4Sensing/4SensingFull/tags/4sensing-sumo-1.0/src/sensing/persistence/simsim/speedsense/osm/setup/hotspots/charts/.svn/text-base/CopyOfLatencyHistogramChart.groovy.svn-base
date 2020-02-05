package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import simsim.gui.charts.XYLineChart;
import speedsense.Hotspot;

@Singleton(lazy=true)  class CopyOfLatencyHistogramChart extends Metric {
	final static int HIST_STEP = 10 // 5 sec buckets
	final static int NUM_BUCKETS = 20// 100 sec max
	final static int DETECTION_THRESH = 20;
	final static int ERROR_THRESH = 120;
	final  XYLineChart chart;
	def congested = [:];
	int[] histogram = new int[NUM_BUCKETS];
	int totalDetected = 0;
	def detected = [];
	int totalFalsePositive = 0;
	def falsePositive = [];
	double totalLatency = 0;
	int totalError = 0;
	def error = [];
	int done = 0;
	File output;

	public CopyOfLatencyHistogramChart() {
		chart = new XYLineChart("Latency Hist Old", 1.0, "% Detections", "Latency") ;
		chart.setYRange( false, 0, 100 ) ;
		chart.setSeriesLinesAndShapes("Latency", true, true) ;
		OSMSpeedSenseSim.Gui.setFrameRectangle("Latency Hist Old", 484, 0, 480, 480);	
	}
	
	public void init() {
		output = openOutputFile("LatencyHistogramOld.csv") { writer ->
			writer << "Result\n0";
			(NUM_BUCKETS).times { 
				writer << ",${(it+1)*HIST_STEP}";
			}
			writer << ",TOTAL_DETECTED, TOTAL_FALSE_NEG, TOTA_FALSE_POS, AVG_LAT\n";	
		}
		OSMSpeedSenseSim.setup.addQueryListener { Hotspot d -> 
			//println "hotspot segment: ${d.segmentId} count: ${d.count} confidence: ${d.confidence}"
			double currTime = OSMSpeedSenseSim.currentTime();
			def c = congested[d.segmentId];
			if(c) {
				if(!c.done) {
					double lat = currTime - c.timestamp;
					//println "latency: ${lat}"
					int bucket = Math.min(NUM_BUCKETS-1,(int)(lat/HIST_STEP));
					histogram[bucket]++;
					c.done = true;
					done++;
					totalDetected++;
					//if(detected.indexOf(d.segmentId) == -1) detected << d.segmentId;
					//SpeedSenseSim.selectSegment(detected);
					totalLatency += lat;
				} else {
					//println "already detected";
				}
			} else { // congestion not registered
				if(OSMSpeedSenseSim.setup.activeSegments.contains(d.segmentId)){
					boolean isCongested = OSMSpeedSenseSim.speedSenseModel.isCongested(d.segmentId);
					if(isCongested) {	
							//println "latency: 0"
							congested[d.segmentId] = [timestamp: OSMSpeedSenseSim.currentTime(), done: true, clear: false, clearTs: -1];
							histogram[bucket]++;
							done++;
							totalDetected++;
					} else {
						//println "false positive"
						totalFalsePositive++;
						done++;
						if(falsePositive.indexOf(d.segmentId) == -1) falsePositive << d.segmentId;
						OSMSpeedSenseSim.selectSegment(falsePositive);
						println "false-positive [count:${d.count}, speed: ${d.avgSpeed}, maxSpeed: ${OSMSpeedSenseSim.speedSenseModel.maxSpeed(d.segmentId)}, avgDensity: ${OSMSpeedSenseSim.speedSenseModel.getAverageDensity(d.segmentId)}]"
						if(d.debugInfo) d.debugInfo.each{data -> println data}
					}
				} else {
					//println "segment not part of query area"
				}
			}
		}
	}
	public void cleanup() {
		congested = [:];
	}
	
	public void stop() {
		int acumulated = 0;
		if(done == 0) return;
		output.append("0");
		for(int i=0; i<NUM_BUCKETS; i++) {
			acumulated += histogram[i];
			double frac = acumulated/done;
			output.append(",${frac*100}");
		}
		output.append(",${totalDetected},${totalError},${totalFalsePositive},${totalLatency/totalDetected}\n");
		println "detections: ${totalDetected} false-positive: ${totalFalsePositive} total-error: ${totalError} avg-error: ${totalLatency/totalDetected}"
	}
	
	public void update() {
		if(OSMSpeedSenseSim.setup.activeSegments.size()==0) return;
		double currTime = OSMSpeedSenseSim.currentTime();
		OSMSpeedSenseSim.setup.activeSegments.each{ segmentId ->
			boolean isCongested = OSMSpeedSenseSim.speedSenseModel.isCongested(segmentId);
			if(isCongested) {
				if(!congested[segmentId]) {
					congested[segmentId] = [timestamp: currTime, done: false, clear: false, clearTs: -1];
				} else { 
					if(congested[segmentId].clear && (currTime - congested[segmentId].clearTs) > DETECTION_THRESH) { // new congestion occurence
						congested[segmentId] = [timestamp: currTime, done: false, clear: false, clearTs: -1]
					}
				}
			} else if(congested[segmentId] && congested[segmentId].clear == false) {
//				if(currTime - congested[segmentId].timestamp < DETECTION_THRESH)  {
//					//println "transient: ${currTime - congested[segmentId].timestamp }"
//					congested.remove(segmentId);
//				} else {
					congested[segmentId].clear = true;
					congested[segmentId].clearTs = currTime;
//				}
			}
		}
		for(c in congested) {
			if(!c.value.done && (currTime - c.value.timestamp > ERROR_THRESH) && 
					(!c.value.clear || (c.value.clearTs - c.value.timestamp > DETECTION_THRESH) )) {
				c.value.done = true;
				done++;
				totalError++;
				error << c.key;
//				SpeedSenseSim.selectSegment(error);
				if(c.value.clear) {
					//println "clear segment congested for ${c.value.clearTs - c.value.timestamp}s"
				} else {
					//println "segment congested for ${currTime - c.value.timestamp}s"
				}
			}
		}
		def select = []; 
		congested.each{segmentId, value -> if(!value.done && (!value.clear || (value.clearTs - value.timestamp > DETECTION_THRESH) )) select << segmentId}
		//SpeedSenseSim.selectSegment(select);
		updateHistogram();
		println "detections: ${totalDetected} false-positive: ${totalFalsePositive} total-error: ${totalError} avg-latency: ${totalLatency/totalDetected} active-segments: ${OSMSpeedSenseSim.setup.activeSegments.size()}"
	}
	
	protected void updateHistogram() {
		if(done == 0) return
		def series = chart.getSeries("Latency");
		series.clear();	
		int acumulated = 0;
		def hist = [];
		series.add(0,0);
		for(int i=0; i<NUM_BUCKETS; i++) {
			acumulated += histogram[i];
			double frac = acumulated/done;
			series.add((i+1)*HIST_STEP, frac*100);
			hist << [(i+1)*HIST_STEP, histogram[i], frac*100];
		}
		//if((int)SpeedSenseSim.currentTime() %10 == 0) println "${done} ${hist}";
		//println "${done} ${hist}";
	}
}
