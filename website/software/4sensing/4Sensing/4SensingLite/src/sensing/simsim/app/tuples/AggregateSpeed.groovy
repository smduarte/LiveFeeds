package sensing.simsim.app.tuples;

import sensing.core.pipeline.Tuple

public class AggregateSpeed extends Tuple {
	String segmentId;

	double count;
	double vCount; // vehicle count
	double sumSpeed;
	double avgSpeed;
	double minSpeed;
	double maxSpeed;
	double stdSpeed;
	// weighted
	double wSumSpeed;
	double wCount
}
