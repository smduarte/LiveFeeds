package speedsense;
import static sensing.persistence.core.logging.LoggingProvider.*;
import sensing.persistence.core.pipeline.EOS;
import sensing.persistence.simsim.speedsense.SpeedSenseSim;

sensorInput(MappedSpeed)

dataSource {
	process {MappedSpeed m ->
		if(m.segmentId.equals(SpeedSenseSim.selectedSegments[0])) println "P1] GOT ${m.speed*3.6} at ${m.time} from ${m.mNodeId}: $m"
		forward m
	}
	timeWindow(mode: periodic, size:300, slide:300)
	//process {EOS eos -> println "WINDOW FLUSHED"; forward eos}
	groupBy(['segmentId']) {
		set(['mNodeId'], mode:eos, ttl:1)
		process {MappedSpeed m -> 
			if(m.segmentId.equals(SpeedSenseSim.selectedSegments[0])) println "P2] GOT ${m.speed*3.6} at ${m.time} from ${m.mNodeId}: $m"
			forward m
		}
		aggregate(AggregateSpeed) { MappedSpeed m -> 
			//avg(m, 'speed', 'avgSpeed')
			//sum(m, 'count', 'count')
			avg(m, 'sumSpeed', 'count', 'avgSpeed')
			count(m, 'vCount')
		}
	}
}

globalAggregation {
		process {AggregateSpeed m -> 
			if(m.segmentId.equals(SpeedSenseSim.selectedSegments[0])) println "P3] GOT ${m.avgSpeed*3.6} at ${m.time} from ${m.mNodeId}: $m"
			forward m
		}
}

