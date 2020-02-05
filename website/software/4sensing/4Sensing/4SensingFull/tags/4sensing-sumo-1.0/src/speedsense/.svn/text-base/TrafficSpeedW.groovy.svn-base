package speedsense;
import static sensing.persistence.core.logging.LoggingProvider.*;
import sensing.persistence.core.pipeline.EOS;
import sensing.persistence.simsim.speedsense.SpeedSenseSim;

sensorInput(SpeedWindows)

def windowSize = SpeedSenseSim.setup.VT_WINDOW_SIZE

dataSource {
	process {SpeedWindows m ->
		if(m.segmentId.equals(SpeedSenseSim.selectedSegments[0])) println "P1] GOT ${m.sWindows.size()} windows from ${m.mNodeId}: $m"
		forward m
	}
}

globalAggregation {
	timeWindow(mode: periodic, size: windowSize, slide: windowSize)
	set(['mNodeId','segmentId'], mode:eos, ttl:1)
	process { SpeedWindows s ->
		s.sWindows.each{ MappedSpeed m ->
			def windowStart = services.scheduler.currentTime() - windowSize
			if(m.time >= windowStart) {
				m.mNodeId = s.mNodeId
				forward m
			}

		}
	}
	process {MappedSpeed m ->
		if(m.segmentId.equals(SpeedSenseSim.selectedSegments[0])) println "P2] GOT ${m.speed*3.6} at ${m.time} from ${m.mNodeId}: $m"
		forward m
	}
	groupBy(['mNodeId', 'segmentId']) {
		aggregate(AggregateSpeed) { MappedSpeed m ->
			//avg(m, 'sumSpeed', 'count', 'avgSpeed')
//			sum(m, 'sumSpeed', 'sumSpeed')
//			sum(m, 'count', 'count')
			avg(m, 'sumSpeed', 'count', 'avgSpeed')
		}
	}
	groupBy(['segmentId']) {	
		aggregate(AggregateSpeed) { AggregateSpeed a ->
			min(a, 'avgSpeed', 'minSpeed')
			max(a, 'avgSpeed', 'maxSpeed')
			std(a, 'avgSpeed', 'stdSpeed')
			count(a, 'vCount')
			
			avg(a, 'sumSpeed', 'count', 'avgSpeed')

		}
	}
	process {AggregateSpeed m -> 
		if(m.segmentId.equals(SpeedSenseSim.selectedSegments[0])) println "P3] GOT ${m.avgSpeed*3.6} at ${m.time} from ${m.mNodeId}: $m"
		forward m
	}
}

