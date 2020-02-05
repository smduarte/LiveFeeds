package livefeeds.twister6;

import static simsim.core.Simulation.rg;

import java.io.Serializable;

public class Filter implements Serializable {
	private static final long serialVersionUID = 1L;

	final double threshold = rg.nextInt(10001) / 10000.0 ;
		
	public boolean accepts( Event e ) {
		return e.value < threshold ;
	}
}
