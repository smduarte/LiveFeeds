package speedsense;
import sensing.persistence.core.ServicesConfig;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import static sensing.persistence.simsim.speedsense.SpeedSenseSim.mapModel;
import sensing.persistence.core.sensors.*;
import sensing.persistence.core.pipeline.*;
import static sensing.persistence.core.logging.LoggingProvider.*;

sensorInput(SGPSReading)
dataSource {
	process { SGPSReading r ->
		def m = new MappedSpeed(r);
		m.boundingBox = mapModel.getSegmentExtent(r.segmentId);
		return m;
	}

	timeWindow(mode: periodic, size:300, slide:300)
	groupBy(['segmentId']) {
		aggregate(AggregateSpeed) { MappedSpeed m -> 
//			sum(m, 'speed', 'sumSpeed')
//			count(m, 'count')
			avg(m, 'speed', 'avgSpeed')
			count(m, 'count')
		}
	}
}

globalAggregation {
//	timeWindow(mode: periodic, size:30, slide:30)
//	groupBy(['segmentId']) {
//		set(['peerId'], mode: triggered, ttl: 30, period: 30)
//		aggregate(AggregateSpeed) {AggregateSpeed a ->
//			avg(a, 'sumSpeed', 'count', 'avgSpeed')
//		}
//	}
}


