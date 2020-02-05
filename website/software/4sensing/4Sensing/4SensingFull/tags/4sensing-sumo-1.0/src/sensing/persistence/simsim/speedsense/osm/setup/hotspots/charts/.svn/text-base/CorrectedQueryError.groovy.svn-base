package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

@Singleton(lazy=true)  class CorrectedQueryError extends Metric {
	def congested = [:];
	double error = 0;
	double nCongested = 0;
	double nActive = 0;
	
	public void update() {
		error = 0;
		nCongested = 0;
		nActive = OSMSpeedSenseSim.setup.activeSegments.size();
		def errorSegments = [];
		
		if( nActive == 0) return;
		double currTime = OSMSpeedSenseSim.currentTime();
		OSMSpeedSenseSim.setup.activeSegments.each{ segmentId ->
			boolean isCongested = OSMSpeedSenseSim.speedSenseModel.isCongested(segmentId);
			if(isCongested) {
				if(!congested[segmentId]) {
					congested[segmentId] = [timestamp: currTime, done: false];
				}
			} else {
				congested.remove(segmentId);
			}
			if(congested[segmentId] && currTime-congested[segmentId].timestamp >= 30) {
				nCongested += 1;
				if(!OSMSpeedSenseSim.setup.getDetection(segmentId)) {
					error += 1;
					errorSegments << segmentId;
				}
			}	
		}
		if(OSMSpeedSenseSim.display.queryError) OSMSpeedSenseSim.selectSegment(errorSegments);
	}
	
	public getDetectionCountError() {
		return [error, error/nCongested, error/nActive];		
	}
}
