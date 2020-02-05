package speedsense
import sensing.persistence.core.pipeline.Processor;
import sensing.persistence.core.pipeline.Tuple;
import sensing.persistence.core.pipeline.EOS;

class PMaxProcessor extends Processor {
	final double p;
	List readings = [];
	
	public PMaxProcessor(double p) {
		this.p = p;	
	}
	
	public Tuple process(MappedSpeed m ) {
		readings << m;
		return null;
	}
	
	public void process(EOS eos) {
		if(readings) {
			def speeds = readings.collect{it.speed}.sort();
			speeds = speeds[-speeds.size()*p..-1]
			double avgSpeed = speeds.sum()/speeds.size();
			AggregateSpeed result = new AggregateSpeed(segmentId: readings[0].segmentId, boundingBox: readings[0].boundingBox ,count: speeds.size(), avgSpeed: avgSpeed);
			forward(result);
			readings = [];
		}
		forward(eos);
	}
}
