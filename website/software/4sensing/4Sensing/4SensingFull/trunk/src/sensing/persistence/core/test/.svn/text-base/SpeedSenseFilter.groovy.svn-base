package sensing.persistence.core.test;

public class SpeedSenseFilter {
	private SpeedSenseGridded last;
	
	SpeedSenseGridded process(SpeedSenseGridded r) {
		if(!last || r.speed > last.speed ) {
			last = r;
			return r;
		} else {
			return null;
		}
	}
}
