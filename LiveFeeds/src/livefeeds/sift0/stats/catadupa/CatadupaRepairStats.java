package livefeeds.sift0.stats.catadupa;

import livefeeds.sift0.CatadupaNode;
import umontreal.iro.lecuyer.stat.Tally;


public class CatadupaRepairStats {

	public Tally repairPeriod = new Tally("RepairPeriod") ;
	public Tally repairReplies = new Tally("RepairReplies") ;
		
	public void recordRepairPeriod( CatadupaNode n ) {
		double now = n.currentTime() ;
		repairPeriod.add( now - n.state.stats.lastRepair ) ;
		n.state.stats.lastRepair = now ;
	}
	
	public void recordRepairReplies( int total ) {
		repairReplies.add( total ) ;
	}
}
