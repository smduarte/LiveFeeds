package livefeeds.twister6;

import static simsim.core.Simulation.rg;

import java.io.Serializable;
import java.util.List;

import simsim.core.Simulation;

public class Event implements Serializable {
	private static final long serialVersionUID = 1L;

	double value = 0.9 + 0.1 * rg.nextDouble() ;
	
	private static int g_serial = 0 ;
	public final int serial = g_serial++;
	
	
	public boolean notify( TurmoilNode node ) {
		return node.notify( this ) ;
	}
	
	public boolean notify( TurmoilNode node, List<TurmoilNode> path ) {
		return node.notify( this, path ) ;
	}

	public double elapsed() {
		System.err.println( _timeStamp + "/" + Simulation.currentTime() );
		return Simulation.currentTime() - _timeStamp ;
	}
	
	public void resetStamp() {
		_timeStamp = Simulation.currentTime() ;
	}
	private double _timeStamp = -1 ;
}
