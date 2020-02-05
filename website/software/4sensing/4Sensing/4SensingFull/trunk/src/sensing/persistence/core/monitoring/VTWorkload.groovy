package sensing.persistence.core.monitoring
import sensing.persistence.core.sensors.monitoring.WorkloadReading;
import sensing.persistence.core.pipeline.*;
import static sensing.persistence.core.logging.LoggingProvider.*;

sensorInput(WorkloadReading)

dataSource {
	process{ WorkloadReading r ->
		def cellInfo = services.monitoring.getGridCell(r.lon, r.lat);
		r.derive(WorkloadAggregate, [boundingBox: cellInfo.boundingBox, cellId: cellInfo.cellId,  count: 1])	
	}
	log("DS VTWorkload exit", DEBUG)
}

globalAggregation{
	groupBy(['cellId']) {
		set(['peerId'], mode: triggered, ttl: 30, period: 10)
		log("GA VTWorkload Set", DEBUG)
		aggregate(WorkloadAggregate) { WorkloadAggregate a -> 
			avg(a, 'totalLoad', 'count', 'avgTotalLoad')
		}
	}
}