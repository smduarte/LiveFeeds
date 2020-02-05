package livefeeds.twister7;

import static simsim.core.Simulation.rg;

public class Event4 extends Event3 {
	private static final long serialVersionUID = 1L;

	public Event4( long srcKey ) {
		super( srcKey ) ;
		
		boolean wide = rg.nextBoolean() ;
		for( int i = 0 ; i < values.length ; i++ )
			values[i] = wide ? rg.nextInt(10) : 90 + rg.nextInt(10);
	}
	
	public String toString() {
		return "" ;
	}
}
