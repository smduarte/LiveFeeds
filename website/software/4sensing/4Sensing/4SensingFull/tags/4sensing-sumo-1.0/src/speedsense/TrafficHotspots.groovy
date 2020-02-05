package speedsense;
import sensing.persistence.simsim.speedsense.osm.pipeline.*;
import static sensing.persistence.simsim.speedsense.SpeedSenseSim.setup;
import static sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim.speedSenseModel;

import sensing.persistence.core.sensors.*;
import sensing.persistence.core.pipeline.*;

import static sensing.persistence.core.logging.LoggingProvider.*;


tableInput("speedsense.TrafficSpeed") 

globalAggregation {
	classify { AggregateSpeed a ->
		if(a.count >=  setup.COUNT_THRESH && a.avgSpeed < setup.CONGESTION_FACTOR * speedSenseModel.maxSpeed(a.segmentId)) {
			a.derive(Hotspot, [confidence: Math.min(1, a.count/setup.COUNT_THRESH * 0.5) ])
		}	
	}
	//filter {Hotspot h -> SpeedSenseSim.speedSenseModel.isCongested(h.segmentId, h.avgSpeed)}
	//log("globalAggregate2 exit", DEBUG);
}
