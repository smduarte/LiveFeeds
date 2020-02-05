package sensing.simsim.app.tuples;

import sensing.core.pipeline.Tuple

public class SpeedHistogram extends Tuple {
	String segmentId;
	public Vector<Integer> histogram = [] ;
	
	
	public void update( CountedMappedSpeed a ) {
		if( histogram[a.speedClass] == null )
			histogram[a.speedClass] = a.count ;
		else
			histogram[a.speedClass] += a.count ;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder() ;
		sb.append("[ ") ;
		for( Object i : histogram ) {
			sb.append(i == null ? "0" : "" + i) ;
			sb.append(' ');
		}
		sb.append(']') ;
		return sb.toString() ;
	}
}
