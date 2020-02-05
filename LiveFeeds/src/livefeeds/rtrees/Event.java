package livefeeds.rtrees;

import static simsim.core.Simulation.rg;

import java.io.Serializable;

public class Event implements Serializable {
	private static final long serialVersionUID = 1L;

	double value = 0.9 + 0.1 * rg.nextDouble() ;
	
	private static int g_serial = 0 ;
	public final int serial = g_serial++;
}
