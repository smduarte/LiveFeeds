package speedsense
import sensing.persistence.core.pipeline.Tuple;

class AggregateTravelTime extends Tuple{
	String segmentId;
	double sumTravelTime;
	double minTravelTime;
	double maxTravelTime;
	double avgTravelTime;
	double stdTravelTime;
	int count;
	int realCount;
}
