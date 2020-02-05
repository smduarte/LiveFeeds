package livefeeds.twister7;
import static simsim.core.Simulation.rg;

public class Event2 extends Event {
	private static final long serialVersionUID = 1L;

	final double value ;
	final int topic ;
	
	public Event2( long srcKey ) {
		super( srcKey ) ;
		
		value = rg.nextDouble() ;
		topic = rg.nextInt( Filter2.MAX_GROUPS ) ;
	}
	
	public String toString() {
		return "" + topic + "/" + value ;
	}
}
