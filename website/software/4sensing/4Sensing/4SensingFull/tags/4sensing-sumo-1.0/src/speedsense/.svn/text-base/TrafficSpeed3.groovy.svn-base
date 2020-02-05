package speedsense;
import static sensing.persistence.simsim.speedsense.SpeedSenseSim.mapModel;
import sensing.persistence.core.sensors.*;
import sensing.persistence.core.pipeline.*;
import static sensing.persistence.core.logging.LoggingProvider.*;

sensorInput(GPSReading)
dataSource {
	process { GPSReading r ->
		def m = new MappedSpeed(r);
		m.boundingBox = mapModel.getSegmentExtent(r.segmentId);
		return m;
	}

	timeWindow(mode: periodic, size:300, slide:300)
	groupBy(['segmentId']) {
		process(new PMaxProcessor(0.15))
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


