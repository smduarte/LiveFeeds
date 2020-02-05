package livefeeds.twister7;

import static simsim.core.Simulation.rg;

public class Event3 extends Event {
	private static final long serialVersionUID = 1L;

	final double[] values ;
	
	public Event3( long srcKey ) {
		super( srcKey ) ;
		
		values = new double[ Filter3.DIMS ] ;
		for( int i = 0 ; i < values.length ; i++ )
			values[i] = rg.nextInt(100);
	}
	
	public String toString() {
		return "" ;
	}
}
