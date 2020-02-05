package sensing.persistence.core.pipeline;

public class Throttle extends TProcessor {
	protected def last;


	public Throttle(int period) {
		super(period, true)
	}
	
	public Tuple process(Tuple input) {
		last = input;
		return null;
	}

	public boolean output() {
		if(last) {
			forward(last)
		}
	}
	

}
