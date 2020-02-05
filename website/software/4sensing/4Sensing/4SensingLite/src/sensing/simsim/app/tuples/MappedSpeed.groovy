package sensing.simsim.app.tuples;
import sensing.core.pipeline.Tuple

class MappedSpeed extends Tuple {
	String segmentId;
	double speed;
	double realSpeed;
	double sumSpeed;
	double count;
	// weighted
	double wSumSpeed; 
	double wCount
	int speedClass;
	
	public MappedSpeed(SGPSReading r) {
		super(r);
		this.segmentId = r.segmentId;
		this.speed = r.speed;
		this.realSpeed = r.realSpeed;
		speedClass = (int)((r.speed + 5) / 10);
	}
	
	public MappedSpeed() {
		super();
	}
	

}
