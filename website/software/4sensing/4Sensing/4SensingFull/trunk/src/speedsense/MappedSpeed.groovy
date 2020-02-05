package speedsense;
import sensing.persistence.core.pipeline.Tuple;

class MappedSpeed extends Tuple {
	String segmentId;
	double speed;
	double realSpeed;
	double sumSpeed;
	double count;
	// weighted
	double wSumSpeed; 
	double wCount
	
	public MappedSpeed(SGPSReading r) {
		super(r);
		this.segmentId = r.segmentId;
		this.speed = r.speed;
		this.realSpeed = r.realSpeed;
	}
	
	public MappedSpeed() {
		super();
	}
	

}
