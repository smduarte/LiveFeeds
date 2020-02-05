package speedsense;
import sensing.persistence.core.pipeline.Tuple;

public class Hotspot extends Tuple{
	String segmentId;
	double avgSpeed;
	int count;
	double confidence;
}
