package livefeeds.sift0.stats.catadupa;

import livefeeds.sift0.CatadupaNode;
import livefeeds.sift0.msgs.CatadupaCastPayload;
import umontreal.iro.lecuyer.stat.Tally;


public class CatadupaTimeStats {

	public Tally time2join = new Tally("Time2Join") ;
	public Tally castLatency = new Tally("CastLatency") ;
	
	public void recordTimeToJoin( CatadupaNode n ) {
		time2join.add( n.upTime() ) ;
	}
	
	public void recordCastLatency( CatadupaNode n, CatadupaCastPayload m ) {
		if( m.stamp.c_serial > n.state.db.view.cutoff ) {
			n.state.stats.broadcastLatency.add( n.currentTime() - m.timeStamp ) ;
			castLatency.add( n.currentTime() - m.timeStamp ) ;
		}
	}
}
