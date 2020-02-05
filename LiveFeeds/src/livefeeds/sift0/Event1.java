package livefeeds.sift0;

import static simsim.core.Simulation.rg;

public class Event1 extends Event {
	private static final long serialVersionUID = 1L;

	public double value = rg.nextDouble() ;
	
	public Event1( long srcKey ) {
		super( srcKey ) ;
	}
	
	public String toString() {
		return "" + value ;
	}
}
