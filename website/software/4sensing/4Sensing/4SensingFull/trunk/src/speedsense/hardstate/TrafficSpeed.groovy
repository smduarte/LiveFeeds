package speedsense.hardstate;
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
	groupBy(['segmentId']) {	
		timeWindow(mode: triggered, size:15, slide:10)
		//process new TPWindow(15,10)
		aggregate(AggregateSpeed) { MappedSpeed m -> 
			sum(m, 'speed', 'sumSpeed')
			count(m, 'count')
		}
	}
	//log("dataSource exit", DEBUG);
}
globalAggregation {
	//log("globalAggregation1 entry", DEBUG);
	groupBy(['segmentId']) {
		set(['peerId'], mode: change, ttl:0)
		aggregate(AggregateSpeed) {AggregateSpeed a ->
			avg(a, 'sumSpeed', 'count', 'avgSpeed')
		}
		filter {AggregateSpeed a ->  changeAbsolute('avgSpeed', 15) || changeAbsolute('count',2 )}
		//process new SSFilter();
	}
	//log("globalAggregation1 exit", DEBUG);
}


