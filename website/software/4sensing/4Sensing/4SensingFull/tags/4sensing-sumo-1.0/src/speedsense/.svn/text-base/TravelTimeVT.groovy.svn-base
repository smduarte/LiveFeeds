package speedsense

import static sensing.persistence.core.logging.LoggingProvider.*;
import sensing.persistence.core.pipeline.EOS;
import sensing.persistence.simsim.speedsense.SpeedSenseSim;

sensorInput(TravelTime)

dataSource {
	process {TravelTime t ->
		if(t.segmentId.equals(SpeedSenseSim.selectedSegments[0])) println "P1 ${t.time}] GOT ${t.isBlocked ? 'BLOCKED' : ''} ${t.travelTime} at ${t.pos} lane:${t.laneId} speed: ${t.avgSpeed} from ${t.mNodeId}: $t"
		if(!t.isBlocked) { 
			forward t
		} 
	}
	timeWindow(mode: periodic, size:SpeedSenseSim.setup.VT_WINDOW_SIZE, slide:SpeedSenseSim.setup.VT_WINDOW_SIZE)
	groupBy(['segmentId']) {
		set(['mNodeId'], mode:eos, ttl:1)
		process {TravelTime t ->
			if(t.segmentId.equals(SpeedSenseSim.selectedSegments[0])) println "P2 ${t.time}] GOT ${t.travelTime} at ${t.pos} lane:${t.laneId} speed: ${t.avgSpeed} from ${t.mNodeId}: $t"
			forward t
		}
		aggregate(AggregateTravelTime) { TravelTime t -> 
			min(t, 'travelTime', 'minTravelTime')
			max(t, 'travelTime', 'maxTravelTime')
			avg(t, 'travelTime', 'avgTravelTime')
			std(t, 'travelTime', 'stdTravelTime')
			count(t, 'count')
			sum(t, 'isRealTT', 'realCount')
		}
	}
}

globalAggregation {
		process {AggregateTravelTime t -> 
			if(t.segmentId.equals(SpeedSenseSim.selectedSegments[0])) println "P3 ${t.time}] GOT min:${t.minTravelTime}  max: ${t.maxTravelTime} avg:${t.avgTravelTime} std:${t.stdTravelTime}: $t"
			forward t
		}
}
