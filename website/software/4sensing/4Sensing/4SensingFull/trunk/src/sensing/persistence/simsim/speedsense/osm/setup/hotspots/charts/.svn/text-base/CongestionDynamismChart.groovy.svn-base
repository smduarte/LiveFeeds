package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

@Singleton(lazy=true)  class CongestionDynamismChart extends Metric {

def congested = [:];
int maxDuration = -1;
	
public void update() {
	if(OSMSpeedSenseSim.setup.activeSegments.size()==0) return;
	double currTime = OSMSpeedSenseSim.currentTime();
	OSMSpeedSenseSim.setup.activeSegments.each{ segmentId ->
		boolean isCongested = OSMSpeedSenseSim.speedSenseModel.isCongested(segmentId);
		if(isCongested) {
			if(!congested[segmentId]) {
				congested[segmentId] = [timestamp: currTime, done: false];
			}
		} else {		
			def duration = (currTime - congested.timestamp);
			if(duration > maxDuration) maxDuration = (int)duration
			congested.remove(segmentId);
		}
	}
	if(currTime%10 == 0) {
		updateGraph()
	}
}

protected void updateGraph() {
	
}

}
