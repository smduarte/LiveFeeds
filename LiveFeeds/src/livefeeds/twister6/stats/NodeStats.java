package livefeeds.twister6.stats;

import umontreal.iro.lecuyer.stat.Tally;

public class NodeStats {
	
	public double lastRepair = 0;	
	
	public Traffic traffic = new Traffic() ;

	Tally avgPayloadRecoveryDelay_S = new Tally("avgPayloadRecoveryDelay_S");
	Tally avgPayloadRecoveryDelay_R = new Tally("avgPayloadRecoveryDelay_R");

}

