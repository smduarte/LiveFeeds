package speedsense.sinc;
import speedsense.*;

import sensing.persistence.simsim.speedsense.osm.pipeline.*;
import static sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim.speedSenseModel;

import sensing.persistence.core.sensors.*;
import sensing.persistence.core.pipeline.*;

import static sensing.persistence.core.logging.LoggingProvider.*;


tableInput("speedsense.sinc.TrafficSpeed") 

globalAggregation {
	//log("globalAggregate2 entry", DEBUG);
	classify { AggregateSpeed a ->
		if(a.count > 10 && a.avgSpeed <= 0.5 * speedSenseModel.maxSpeed(a.segmentId)) {
			a.derive(Hotspot, [confidence: Math.min(1, a.count/10* 0.5) ])
		}	
	}
	//filter {Hotspot h -> SpeedSenseSim.speedSenseModel.isCongested(h.segmentId, h.avgSpeed)}
	//log("globalAggregate2 exit", DEBUG);
}
