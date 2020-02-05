package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts.mq
;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import speedsense.Hotspot;

class ResultsPerSlot extends Metric {
	final static int ERROR_THRESH = 80;
	int timeSlotLength;
	int slot = -1;
	def congested = [:];
	def detections;
	def falsePositive;
	def falseNegative;
	def numCongested;

	
	public ResultsPerSlot(int timeSlotLength) {
		this.timeSlotLength = timeSlotLength;
		int length = (int)((OSMSpeedSenseSim.setup.IDLE_TIME+OSMSpeedSenseSim.setup.RUN_TIME)/timeSlotLength+0.5) + 1;
		numCongested = new int[length];
		detections = new int[length];
		falsePositive = new int[length];
		falseNegative = new int[length];
	}
	
	public void init() {

	}
	
	
	public void cleanup() {
		congested = [:];
	}
	
	protected int sum(array) {
		return 	array.inject(0){sum, val -> sum+val};
	}
	
	public void stop() {
		println "detections: ${sum(detections)} false-positive: ${sum(falsePositive)} false-negative: ${sum(falseNegative)} total-error: ${sum(falsePositive)+sum(falseNegative)}"
	}
	
	def congestedHistory = [:];
	
	protected void storeHistory(String segmentId) {
		def r = congested[segmentId]
		congestedHistory[segmentId] << [timestamp: r.timestamp, clear: r.clear, clearTs: r.clearTs];
	}
	
	protected void updateCongested(double currTime) {
		OSMSpeedSenseSim.setup.activeSegments.each{ segmentId ->
			if(!congestedHistory[segmentId]) congestedHistory[segmentId] = [];
			boolean isCongested = OSMSpeedSenseSim.speedSenseModel.isCongested(segmentId);
			if(isCongested) {
				if(!congested[segmentId]) {
					// New congestion
					congested[segmentId] = [timestamp: currTime, clear: false, clearTs: -1];
					storeHistory(segmentId);
				} else if(congested[segmentId].clear) {
					congested[segmentId].clear = false;
					congested[segmentId].clearTs = -1;
					congested[segmentId].timestamp = currTime;
					storeHistory(segmentId);
				}
			} else if(congested[segmentId]) {
				if(congested[segmentId].clear == false) {
					// Clear congestion
					congested[segmentId].clear = true;
					congested[segmentId].clearTs = currTime;
					storeHistory(segmentId);
				} 
				else if(currTime - congested[segmentId].clearTs > ERROR_THRESH) { // can be removed
					congested.remove(segmentId);
					congestedHistory[segmentId] << [currTime, "Removed"];
				}
			}
		}
	}
		
	protected void addDetection() {
		detections[slot]++;
	}
	
	protected void addFalsePositive() {
		falsePositive[slot]++;
	}
	
	protected void addFalseNegative() {
		falseNegative[slot]++;
	}
	
	protected void checkResults(double currTime) {
		OSMSpeedSenseSim.setup.activeSegments.each{ segmentId ->
			def result = OSMSpeedSenseSim.setup.getResult(segmentId);
			def detection = null;
			double detectionTs;
			if(result) {
				detection = result[0];
				//println "DETECTION: ${detection}"
				//detection.debugInfo.each{println it}
				detectionTs = result[1];
			}
			
			def congested = congested[segmentId];

			if(congested) {
				if (!congested.clear) {
					numCongested[slot]++;
				}
				if(detection != null) {
					addDetection()
				} else if (!congested.clear && (currTime - congested.timestamp > ERROR_THRESH)) {
						addFalseNegative();
						println "false-negative: ${segmentId} - ${congested}";
						congestedHistory[segmentId].each{println it};
				}
			} else { 
				if(detection != null) { //not congested and detection
					addFalsePositive();
					println "false-positive: ${detectionTs} - ${detection}";
					detection.debugInfo.each{println it}
					congestedHistory[segmentId].each{println it};
				} else {
					// addDetection();
				}
			}
		}
	}
	
	public void update() {
		double currTime = OSMSpeedSenseSim.currentTime();
		if(OSMSpeedSenseSim.setup.activeSegments.size() == 0) return;
		updateCongested(currTime);
		if((int)(currTime+0.5) % timeSlotLength == 0) {
			slot++;
			checkResults(currTime);
		}
		println "congested: ${numCongested}";
		println "detections: ${detections}";
		println "false-pos:  ${falsePositive}";
		println "false-neg:  ${falseNegative}";
	}
	
}
