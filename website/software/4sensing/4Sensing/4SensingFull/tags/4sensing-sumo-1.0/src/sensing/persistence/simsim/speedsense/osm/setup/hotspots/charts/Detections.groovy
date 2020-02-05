package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts

import sensing.persistence.simsim.*
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import speedsense.Hotspot
import simsim.core.PeriodicTask;

@Singleton(lazy=true)  class Detections extends Metric {
	
	class Congestion {
		double startTs
		double endTs = Double.POSITIVE_INFINITY
		double detectedTs
		double latency
		
		Congestion(double startTs) {
			this.startTs = startTs
		}
		
		public String toString() {
			String.format("startTs: %.2f, endTs: %.2f, detectedTs: %.2f, latency: %.2f", startTs, endTs, detectedTs, latency)
		}
	}
	
	final static int HISTORY_MAX = 120
	
	
	def history = [:]
	
	int totalDetected = 0
	double totalLatency = 0
	
	protected detectionListeners = []
	protected segmentStatusListeners = []
	
	
	public void addDetectionListener(Closure clos) {
		detectionListeners << clos
	}
	
	public void addSegmentStatusListener(Closure clos) {
		segmentStatusListeners << clos
	}

	protected void notifyDetection(Hotspot d, double lat, boolean isNew) {
		detectionListeners.each{it.call(d, lat, isNew)}
	}
	
	public enum SegmentStatus {CONGESTED, CLEAR}
	
	protected void notifySegmentStatus(String segmentId, SegmentStatus status, double duration ) {
		segmentStatusListeners.each{it.call(segmentId, status, duration)}
	}
	
	public void init() {
		OSMSpeedSenseSim.setup.addQueryListener { Hotspot d -> 
			//println "hotspot segment: ${d.segmentId} count: ${d.count} confidence: ${d.confidence}"
			double currTime = OSMSpeedSenseSim.currentTime()
			Congestion c = histGetCongestion(d)
			if(c) {
				if(!c.detectedTs) {
					c.latency = currTime - c.startTs
					c.detectedTs = currTime
					totalDetected++
					totalLatency += c.latency
					notifyDetection(d, c.latency, true)
	//				println "Detections: Detection for $d.segmentId - Level: ${d.level} -  Window Span: ${d.winEnd-d.winStart}s [$d.winStart, $d.winEnd] - Latency: ${lat}"
	//				println d
	//				Detections.instance.history[d.segmentId].each{println it}
	//				println "-----"
	//				d.debugInfo.sort{a, b -> a.time <=> b.time}.each{ MappedSpeed m ->
	//					println String.format("%.2f] speed: %.2f\t%s", m.time, m.speed, m)
	//				}
				} else {
					notifyDetection(d , 0, false) // already detected
				}
			}
		}
		new PeriodicTask(HISTORY_MAX) {
			public void run() {
				def currTime = OSMSpeedSenseSim.currentTime();
				OSMSpeedSenseSim.setup.activeSegments.each{ segmentId ->
					history[segmentId]  = history[segmentId].findAll{Congestion c -> c.endTs > currTime - HISTORY_MAX}
				}
			}	
		}
		
	}
	
	public void cleanup() {
		history = [:]
	}
	
	public void stop() {
		println "Detections: total-detected: ${totalDetected} avg-latency: ${totalLatency/totalDetected}"
	}
	
	public void update() {
		double currTime = OSMSpeedSenseSim.currentTime()
		OSMSpeedSenseSim.setup.activeSegments.each{ segmentId ->
			if(!history[segmentId]) history[segmentId] = []
			boolean isCongested = OSMSpeedSenseSim.speedSenseModel.isCongested(segmentId)
			if(isCongested) {
				if(!histIsCongested(segmentId)) {
					if(history[segmentId]) {
						notifySegmentStatus(segmentId, SegmentStatus.CLEAR, currTime - history[segmentId].last().endTs)
					}
					history[segmentId] << new Congestion(currTime)
				}
			} else { // !isCongested
				if(histIsCongested(segmentId)) {
					history[segmentId].last().endTs = currTime;
					notifySegmentStatus(segmentId, SegmentStatus.CONGESTED, currTime - history[segmentId].last().startTs);
				}
			}
			// clean old history
			//history[segmentId]  = history[segmentId].findAll{Congestion c -> c.endTs > currTime - HISTORY_MAX}
		}
	}
	
	
	public boolean histIsCongested(String segmentId) {
		history[segmentId] && history[segmentId].last().endTs == Double.POSITIVE_INFINITY
	}
	
	public double histCongestedTime(String segmentId) {
		if(history[segmentId]) {
			Congestion c = history[segmentId].last()
			if(c.endTs == Double.POSITIVE_INFINITY) {
				return OSMSpeedSenseSim.currentTime() - c.startTs
			} else {
				return 0
			}
		} else  {
			return 0
		}
	}
	
	public Congestion histGetCongestion(Hotspot d) {
		history[d.segmentId]?.find{Congestion c -> c.startTs <= d.winEnd && c.endTs >= d.winStart}
	}
	
}
