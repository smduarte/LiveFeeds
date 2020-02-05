package livefeeds.rtrees.stats;

import livefeeds.rtrees.stats.broadcast.BroadcastTraffic;
import umontreal.iro.lecuyer.stat.Tally;

public class NodeStats {
	
	public double lastRepair = 0;	
	
	public Traffic traffic = new Traffic() ;

	public BroadcastTraffic btraffic = new BroadcastTraffic() ;
	
}

