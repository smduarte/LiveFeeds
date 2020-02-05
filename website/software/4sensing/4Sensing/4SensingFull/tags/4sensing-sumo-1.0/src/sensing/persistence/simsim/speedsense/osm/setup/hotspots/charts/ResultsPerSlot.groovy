package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.simsim.speedsense.SpeedSenseSim;
import sensing.persistence.core.pipeline.Tuple;
import speedsense.*;


@Singleton(lazy=true)  class ResultsPerSlot extends Metric {
	final static int ERROR_THRESH = 80;
	int lastSlot = -1;
	int slot;
	def detections;
	def falsePositive;
	def falseNegative;
	def congested;

	
	public ResultsPerSlot() {
		int length = (int)((OSMSpeedSenseSim.setup.IDLE_TIME+OSMSpeedSenseSim.setup.RUN_TIME)/OSMSpeedSenseSim.setup.SIM_UPDATE_INTERVAL) + 1;
		congested = new int[length];
		detections = new int[length];
		falsePositive = new int[length];
		falseNegative = new int[length];
	}
	

	public int total(array) {
		return 	array.inject(0){sum, val -> sum+val};
	}
	
	public void stop() {
		println "ResultsPerSlot: detections: ${total(detections)} false-positive: ${total(falsePositive)} false-negative: ${total(falseNegative)} total-error: ${total(falsePositive)+total(falseNegative)}"
	}
	
	protected void printDebugInfo(Tuple t) {
		println "-----"
		if(t.debugInfo) {
			t.debugInfo.sort{a, b -> a.time <=> b.time}.each{ MappedSpeed m ->
				printf ("%.2f] speed: %.2f\t%s\n", m.time, m.speed, m)
			}
		}
		println "-----"
	}
	
	protected void checkResults() {
		Map resultSet = OSMSpeedSenseSim.setup.getResultSet()
		// Detections & false positives
		resultSet.each{ String segment, List entry ->
			def (d, ts) = entry
			Detections.Congestion c = Detections.instance.histGetCongestion(d)	
			if(c) {
				detections[slot]++
			} else  {
				falsePositive[slot]++
				println "ResultsPerSlot: False positive for $d.segmentId - Level: ${d.level} -  Window Span: ${d.winEnd-d.winStart}s [$d.winStart, $d.winEnd]"
				println "Max speed for segment: ${OSMSpeedSenseSim.speedSenseModel.maxSpeed(d.segmentId)}"
				println d
				Detections.instance.history[d.segmentId].each{println it}
				printDebugInfo(d)
			}
		}
		// Congested & false negatives
		OSMSpeedSenseSim.setup.activeSegments.each{ segmentId ->
			if(Detections.instance.histIsCongested(segmentId)) {
				congested[slot]++
			}
			if(!resultSet[segmentId] && Detections.instance.histCongestedTime(segmentId)  > ERROR_THRESH) {
				falseNegative[slot]++
				println "ResultsPerSlot: False negative for $segmentId"
				println Detections.instance.history[segmentId].last()
				if(OSMSpeedSenseSim.setup.queryResult[segmentId]) {
					def (d, ts) = OSMSpeedSenseSim.setup.queryResult[segmentId]
					println "Last detection received at $ts"
					println d
					printDebugInfo(d)
				}
				if(SpeedSenseSim.setup.aggregateStore[segmentId]) {
					def (aggregate, ts) = 	SpeedSenseSim.setup.aggregateStore[segmentId]
					println "Last aggregate received at $ts: $aggregate"
					println aggregate
					printDebugInfo(aggregate)
				}		
			}
		}
	}
	
	public void update() {
		double currTime = SpeedSenseSim.currentTime();
		if(SpeedSenseSim.setup.activeSegments.size() == 0) return;
		slot = (int)(currTime/SpeedSenseSim.setup.SIM_UPDATE_INTERVAL)
		if(slot != lastSlot) {
			checkResults()
			lastSlot = slot;
			println "ResultsPerSlot: congested: ${congested}"
			println "ResultsPerSlot: detections: ${detections}"
			println "ResultsPerSlot: false-pos:  ${falsePositive}"
			println "ResultsPerSlot: false-neg:  ${falseNegative}"
			println "ResultsPerSlot: Total congested: ${total(congested)} Total detections: ${total(detections)} Total FP: ${total(falsePositive)} Total FN: ${total(falseNegative)} Total Error: ${total(falsePositive)+total(falseNegative)}"
		}
	}
}
