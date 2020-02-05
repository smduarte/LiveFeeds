package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts

import sensing.persistence.core.pipeline.Tuple;
import speedsense.MappedSpeed;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

class ResultSetStats {
	final static int ERROR_THRESH = 80;
	
	static class Stats {
		int congested;
		int detections;
		int falsePositive;
		int falseNegative;
		double errorRate;
		double successRate;
	}
	
	public static void debug(d) {
		//println d;	
	}
	
	public static void debugf(Object... args) {
		//printf(args);
	}

	public static void printDebugInfo(Tuple t) {
//		debug "-----"
//		if(t.debugInfo) {
//			t.debugInfo.sort{a, b -> a.time <=> b.time}.each{ MappedSpeed m ->
//				debugf ("%.2f] speed: %.2f\t%s\n", m.time, m.speed, m)
//			}
//		}
//		debug "-----"
	}
	
	public static Stats getStats() {
		if(OSMSpeedSenseSim.setup.activeSegments.size() == 0) return null;
		
		Stats resultStats = new Stats();
		
		Map resultSet = OSMSpeedSenseSim.setup.getResultSet()
		// Detections & false positives
		resultSet.each{ String segment, List entry ->
			def (d, ts) = entry
			Detections.Congestion c = Detections.instance.histGetCongestion(d)
			if(c) {
				resultStats.detections++
			} else  {
				resultStats.falsePositive++
				debug "ResultsPerSlot: False positive for $d.segmentId - Level: ${d.level} -  Window Span: ${d.winEnd-d.winStart}s [$d.winStart, $d.winEnd]"
				debug "Max speed for segment: ${OSMSpeedSenseSim.speedSenseModel.maxSpeed(d.segmentId)}"
				debug d
				Detections.instance.history[d.segmentId].each{debug it}
				printDebugInfo(d)
			}
		}
		// Congested & false negatives
		OSMSpeedSenseSim.setup.activeSegments.each{ segmentId ->
			if(Detections.instance.histIsCongested(segmentId)) {
				resultStats.congested++
			}
			if(!resultSet[segmentId] && Detections.instance.histCongestedTime(segmentId)  > ERROR_THRESH) {
				resultStats.falseNegative++
				debug "ResultsPerSlot: False negative for $segmentId"
				debug Detections.instance.history[segmentId].last()
				if(OSMSpeedSenseSim.setup.queryResult[segmentId]) {
					def (d, ts) = OSMSpeedSenseSim.setup.queryResult[segmentId]
					debug "Last detection received at $ts"
					debug d
					printDebugInfo(d)
				}
				if(SpeedSenseSim.setup.aggregateStore[segmentId]) {
					def (aggregate, ts) = 	SpeedSenseSim.setup.aggregateStore[segmentId]
					debug "Last aggregate received at $ts: $aggregate"
					debug aggregate
					printDebugInfo(aggregate)
				}
			}
		}
		
		int error = resultStats.falseNegative + resultStats.falsePositive;
		int events = error + resultStats.detections;
		if(events > 0) {
			resultStats.errorRate =  error*1.0/events*100;
		} else {
			resultStats.errorRate = 0;
		}
		resultStats.successRate = 100 - resultStats.errorRate;
		return resultStats
	}
	
}
