package livefeeds.twister7.stats.catadupa;

import umontreal.iro.lecuyer.stat.Tally;

public class CatadupaFanoutStats {

	public Tally fanout = new Tally("Catadupa Fanout") ;
		
	public void recordFanout( int f ) {
		fanout.add( f ) ;
	}
	
}
