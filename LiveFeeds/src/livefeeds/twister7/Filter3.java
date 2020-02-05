package livefeeds.twister7;

import static livefeeds.twister7.config.Config.Config;
import static simsim.core.Simulation.rg;


public class Filter3 extends Filter {
	
	static final int DIMS = Config.Filter3_DIMS ;
	
	private static final long serialVersionUID = 1L;
	
	final double[] masks ;
	
	Filter3() {
		masks = new double[ DIMS] ; 
		for( int i = 0 ; i < masks.length ; i++ )
			masks[i] = 1 + rg.nextInt(100);
	}
	
	public boolean accepts( Event e ) {
		final boolean match = Config.Filter3_LOGIC ;
		
		Event3 x = (Event3) e ;
		for( int i = 0 ; i < DIMS ; i++ )
			if( x.values[i] < masks[i] ) return match ;
		
		return ! match ;
	}	
	
	public String toString() {
		return "" ;
	}
}
