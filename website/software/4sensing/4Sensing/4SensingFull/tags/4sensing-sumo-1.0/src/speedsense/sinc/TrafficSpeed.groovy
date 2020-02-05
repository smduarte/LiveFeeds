package speedsense.sinc;

import speedsense.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import static sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim.speedSenseModel;
import sensing.persistence.core.sensors.*;
import sensing.persistence.core.pipeline.*;
import static sensing.persistence.core.logging.LoggingProvider.*;

sensorInput(GPSReading)
dataSource {
	process { GPSReading r -> 
		r.derive(MappedSpeed, [boundingBox: speedSenseModel.getSegmentExtent(r.segmentId) ])
	}
	timeWindow(mode: periodic, size:15, slide:10)
	groupBy(['segmentId']) {	
		//process new TPWindow(15,10)
		aggregate(AggregateSpeed) { MappedSpeed m -> 
			sum(m, 'speed', 'sumSpeed')
			count(m, 'count')
		}
	}
	//log("dataSource exit", DEBUG);
}
globalAggregation {
	timeWindow(mode: periodic, size:10, slide: 10)
	log("timewindow", DEBUG);
	groupBy(['segmentId']) {
		//log("bset", DEBUG);
		set(['peerId'], mode: eos, ttl:10)
		log("grp", DEBUG);
		aggregate(AggregateSpeed) {AggregateSpeed a ->
			avg(a, 'sumSpeed', 'count', 'avgSpeed')
		}
	}
	log("globalAggregation1 exit", DEBUG);
}


