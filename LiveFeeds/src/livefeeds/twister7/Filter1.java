package livefeeds.twister7;

import static simsim.core.Simulation.rg;

public class Filter1 extends Filter {
	
	private static final long serialVersionUID = 1L;
	
	final double mask = getValue() ;
	
	double getValue() {
		return rg.nextDouble() ;
	}
	
	public boolean accepts( Event e ) {
		return ((Event1)e).value < mask ;
	}	
	
	public String toString() {
		return "" + mask ;
	}
}
