package livefeeds.rtrees.stats.catadupa;

import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.msgs.NewArrivals;
import umontreal.iro.lecuyer.stat.Tally;


public class CatadupaTimeStats {

	public Tally time2join = new Tally("Time2Join") ;
	public Tally castLatency = new Tally("CastLatency") ;
	
	public void recordTimeToJoin( CatadupaNode n ) {
		time2join.add( n.upTime() ) ;
	}
	
	public void recordCastLatency( CatadupaNode n, NewArrivals m ) {
		if( m.stamp.c_serial > n.state.db.view.cutoff )
			castLatency.add( n.currentTime() - m.timeStamp ) ;
	}
}
