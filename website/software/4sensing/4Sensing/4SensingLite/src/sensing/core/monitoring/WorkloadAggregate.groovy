package sensing.core.monitoring
import sensing.core.sensors.monitoring.WorkloadReading;
import sensing.core.pipeline.Tuple;

class WorkloadAggregate extends Tuple{
	String cellId;
	int totalLoad;
	int count;
	double avgTotalLoad;
}
