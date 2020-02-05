package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import speedsense.Hotspot;

@Singleton(lazy=true)  class DetectionLatency extends Metric {
	final static int DETECTION_THRESH = 20;
	final static int ERROR_THRESH = 80;
	def congested = [:];
	
	int totalDetected = 0;
	def detected = [];
	int totalFalsePositive = 0;
	def falsePositive = [:];
	double totalLatency = 0;
	int totalError = 0;
	def error = [];
	int done = 0;
	protected detectionListeners = [];
	protected errorListeners = [];
	
	enum Error {FALSE_POSITIVE, FALSE_NEGATIVE};
	
	public void addDetectionListener(Closure clos) {
		detectionListeners << clos;
	}
	
	public void addErrorListener(Closure clos) {
		errorListeners << clos;
	}
	
	protected void notifyDetection(String segmentId, double lat, boolean isNew) {
		detectionListeners.each{it.call(segmentId, lat, isNew)}
	}
	
	protected void notifyError(String segmentId, Error e, boolean isNew) {
		errorListeners.each{it.call(segmentId, e, isNew)}
	}

	
	public void init() {
		OSMSpeedSenseSim.setup.addQueryListener { Hotspot d -> 
			//println "hotspot segment: ${d.segmentId} count: ${d.count} confidence: ${d.confidence}"
			double currTime = OSMSpeedSenseSim.currentTime();
			def c = congested[d.segmentId];
			if(c) {
				if(!c.done) {
					double lat = currTime - c.timestamp;
					//println "latency: ${lat}"
					c.done = true;
					c.success = true;
					c.checkedTs = currTime;
					done++;
					totalDetected++;
					//if(detected.indexOf(d.segmentId) == -1) detected << d.segmentId;
					//SpeedSenseSim.selectSegment(detected);
					totalLatency += lat;
					notifyDetection(d.segmentId, lat, true);
				} else {
					notifyDetection(d.segmentId,0, false); // already detected

					//println "recurring detection for ${d.segmentId}: ${currTime - c.checkedTs}s"
					c.checkedTs = currTime;
				}
			} else { // congestion not registered
				if(OSMSpeedSenseSim.setup.activeSegments.contains(d.segmentId)){
					boolean isCongested = OSMSpeedSenseSim.speedSenseModel.isCongested(d.segmentId);
					if(isCongested) {	
							println "latency: 0"
							congested[d.segmentId] = [timestamp: OSMSpeedSenseSim.currentTime(), done: true, sucess: true, clear: false, clearTs: -1, checkedTs: currTime];
							done++;
							totalDetected++;
							notifyDetection(d.segmentId, 0, true);
					} else {
						if(falsePositive[d.segmentId]) {
							//already accounted
							notifyError(d.segmentId, Error.FALSE_POSITIVE, false); //TODO
						} else {
							//println "false positive"
							totalFalsePositive++;
							done++;
							//if(falsePositive.indexOf(d.segmentId) == -1) falsePositive << d.segmentId;
							//SpeedSenseSim.selectSegment(falsePositive);
							//println "false-positive [count:${d.count}, speed: ${d.avgSpeed}, maxSpeed: ${SpeedSenseSim.speedSenseModel.maxSpeed(d.segmentId)}, avgDensity: ${SpeedSenseSim.speedSenseModel.getAverageDensity(d.segmentId)}]"
							if(d.debugInfo) d.debugInfo.each{data -> println data}
							notifyError(d.segmentId, Error.FALSE_POSITIVE, true); //TODO
						}
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
		println "detections: ${totalDetected} false-positive: ${totalFalsePositive} total-error: ${totalError} avg-error: ${totalLatency/totalDetected}"
	}
	
	public void update() {
		if(OSMSpeedSenseSim.setup.activeSegments.size()==0) return;
		double currTime = OSMSpeedSenseSim.currentTime();
		OSMSpeedSenseSim.setup.activeSegments.each{ segmentId ->
			boolean isCongested = OSMSpeedSenseSim.speedSenseModel.isCongested(segmentId);
			if(isCongested) {
				if(!congested[segmentId]) {
					congested[segmentId] = [timestamp: currTime, done: false, success: false, clear: false, clearTs: -1];
				} else { 
					if(congested[segmentId].clear && (currTime - congested[segmentId].clearTs) > DETECTION_THRESH) { // new congestion occurence
						congested[segmentId] = [timestamp: currTime, done: false, success: false, clear: false, clearTs: -1]
					}
				}
			} else if(congested[segmentId] && congested[segmentId].clear == false) {
//				if(currTime - congested[segmentId].timestamp < DETECTION_THRESH)  {
//					//println "transient: ${currTime - congested[segmentId].timestamp }"
//					congested.remove(segmentId);
//				} else {
					congested[segmentId].clear = true;
					congested[segmentId].clearTs = currTime;
//					if(congested[segmentId].done && congested[segmentId].success == false) {
//						println "cleared error"	
//					}
//				}
			}
		}
/*		for(c in congested) {
			if(!c.value.done) {
				if((currTime - c.value.timestamp > ERROR_THRESH) && 
					(!c.value.clear || (c.value.clearTs - c.value.timestamp > DETECTION_THRESH) )) {
					c.value.done = true;
					c.value.success = false;
					done++;
					totalError++;
					c.value.checkedTs = currTime;
					notifyError(c.key, Error.FALSE_NEGATIVE, true);
					error << c.key;
					SpeedSenseSim.selectSegment(error);
//					if(c.value.clear) {
//						println "clear segment congested for ${c.value.clearTs - c.value.timestamp}s"
//					} else {
//						println "segment congested for ${currTime - c.value.timestamp}s"
//					}
				} 
			} else if(!c.value.clear && (currTime - c.value.checkedTs > DETECTION_THRESH)) {
//				if((c.value.success == false)) { // notify error until congestion clears
//					println "recurring error for ${c.key}"
//				} else {
//					println "detection missing for ${c.key}" // expected detection to be refreshed after at most 20secs (due to desync when new tree is build)
//				}
				notifyError(c.key, Error.FALSE_NEGATIVE, false);
				c.value.checkedTs = currTime;
			}
		}
*/
		falsePositive = falsePositive.findAll{it.value.time < ERROR_THRESH}
		
		def select = []; 
		congested.each{segmentId, value -> if(!value.done && (!value.clear || (value.clearTs - value.timestamp > DETECTION_THRESH) )) select << segmentId}
		//SpeedSenseSim.selectSegment(select);
		/*if(((int)SpeedSenseSim.currentTime()) % SpeedSenseSim.setup.OUTPUT_INTERVAL == 0 ) {
			println "detections: ${totalDetected} false-positive: ${totalFalsePositive} false-negative: ${totalError} avg-latency: ${totalLatency/totalDetected} active-segments: ${SpeedSenseSim.setup.activeSegments.size()}"
		}*/
	}
	
}
