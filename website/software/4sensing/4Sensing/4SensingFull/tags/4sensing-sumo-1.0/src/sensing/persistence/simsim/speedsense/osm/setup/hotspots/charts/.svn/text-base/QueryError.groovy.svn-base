package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

@Singleton(lazy=true) class QueryError extends Metric {
	
	def congestedCount = [:];
	double error = 0;
	double congested = 0;
	double active = 0;
	
	public void update() {
		error = 0;
		congested = 0;
		def errorSegments = [];
		
		active = OSMSpeedSenseSim.activeSegments.size();
		if(active == 0 ) return;
		OSMSpeedSenseSim.activeSegments.each{ segmentId ->
			boolean isCongested = OSMSpeedSenseSim.speedSenseModel.isCongested(segmentId);
			if(isCongested && incrCongestedCount(segmentId) >= 10) { //consider only if congested for 10 consequent iterations 
				congested++;
				if(!OSMSpeedSenseSim.setup.getDetection(segmentId)) {
					error++;
					errorSegments << segmentId;
				}
			}
			if(!isCongested) {
				resetCongestedCount(segmentId);
			}
		}	

		if(OSMSpeedSenseSim.display.queryError) OSMSpeedSenseSim.selectSegment(errorSegments);
	}
	
	public getDetectionCountError() {
		return [error, error/congested, error/active];		
	}
	
	private int resetCongestedCount(String segmentId) {
		congestedCount[segmentId] = 0;
		return 0;
	}
	
	private int incrCongestedCount(String segmentId) {
		if(!congestedCount[segmentId]) {
			resetCongestedCount(segmentId);
		} 
		congestedCount[segmentId]++;
		return congestedCount[segmentId];
	}
}
