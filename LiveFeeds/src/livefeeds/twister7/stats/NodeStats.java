package livefeeds.twister7.stats;

import livefeeds.twister7.stats.catadupa.CatadupaTraffic;
import livefeeds.twister7.stats.turmoil.TurmoilFilterStats;
import livefeeds.twister7.stats.turmoil.TurmoilTraffic;
import umontreal.iro.lecuyer.stat.Tally;

public class NodeStats {
	
	public double lastRepair = 0;	

	public Churn churn = new Churn() ;

	public TurmoilTraffic turmoilTraffic = new TurmoilTraffic() ;

	public TurmoilFilterStats filter = new TurmoilFilterStats() ;

	public CatadupaTraffic catadupaTraffic = new CatadupaTraffic() ;
	
	
	public Tally avgPayloadRecoveryDelay_S = new Tally("avgPayloadRecoveryDelay_S");
	public Tally avgPayloadRecoveryDelay_R = new Tally("avgPayloadRecoveryDelay_R");

	public Tally broadcastLatency = new Tally("BroadcastLatency") ;
}

