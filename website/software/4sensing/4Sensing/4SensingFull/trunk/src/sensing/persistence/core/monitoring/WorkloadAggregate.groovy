package sensing.persistence.core.monitoring;

import sensing.persistence.core.sensors.monitoring.WorkloadReading;
import sensing.persistence.core.pipeline.Tuple;

class WorkloadAggregate extends Tuple{
	String cellId;
	int totalLoad;
	int count;
	double avgTotalLoad;
}
