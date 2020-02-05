package speedsense;
import sensing.persistence.core.ServicesConfig;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import static sensing.persistence.simsim.speedsense.SpeedSenseSim.mapModel;
import sensing.persistence.core.sensors.*;
import sensing.persistence.core.pipeline.*;
import static sensing.persistence.core.logging.LoggingProvider.*;

sensorInput(SGPSReading)
dataSource {
//	log("DS TrafficSpeed entry", DEBUG);
	process { SGPSReading r ->
//		if(r.segmentId == "22278076_0_F") {
//			printf("%.2f [%d] received %s\n", services.scheduler.currentTime(), services.network.local.nodeId, r) 
//		}
		def m = new MappedSpeed(r);
		m.boundingBox = mapModel.getSegmentExtent(r.segmentId);
		return m;
		//r.derive(MappedSpeed, [boundingBox: speedSenseModel.getSegmentExtent(r.segmentId) ])
	}

	timeWindow(mode: periodic, size:15, slide:10)
	groupBy(['segmentId']) {
		aggregate(AggregateSpeed) { MappedSpeed m -> 
			sum(m, 'speed', 'sumSpeed')
			count(m, 'count')
		}
	}
//	process { AggregateSpeed a ->
//		if(a.segmentId == "22278076_0_F") {
//			printf("%.2f [%d] DS produced %s\n", services.scheduler.currentTime(), services.network.local.nodeId, a)
//		}
//		return a
//	}
	//log("DS TrafficSpeed exit", DEBUG);
}

globalAggregation {
	//log("GA TrafficSpeed entry", DEBUG)
	timeWindow(mode: periodic, size:10, slide:10)
	//set(['peerId'], ttl: 10, mode: eos)
	//process new WindowCheck();
	//log("GA TrafficSpeed window", DEBUG);
	groupBy(['segmentId']) {
		//set(['peerId'], mode: change, ttl:10)
		aggregate(AggregateSpeed) {AggregateSpeed a ->
			avg(a, 'sumSpeed', 'count', 'avgSpeed')
		}
	}
	//log("GA TrafficSpeed exit", DEBUG)
//	process {AggregateSpeed a -> if(services.query.isTupleBounded(querycontext, a)) SpeedSenseSim.setup.storeAggregate(a); a}
}


