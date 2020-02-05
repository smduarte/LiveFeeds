package livefeeds.twister7;

import static simsim.core.Simulation.rg;

public class Filter2 extends Filter {
	
	static final int MAX_GROUPS = 1 ;
	
	private static final long serialVersionUID = 1L;
	
	final double mask ;
	final int topic ;
	
	
	Filter2() {
		mask = rg.nextDouble() ;
		topic = rg.nextInt( MAX_GROUPS ) ;
	}
	
	public boolean accepts( Event e ) {
		Event2 x = (Event2) e ;
		return x.topic <= topic && x.value <= mask ;
	}	
	
	public String toString() {
		return "" + topic + "/" + mask ;
	}
}
