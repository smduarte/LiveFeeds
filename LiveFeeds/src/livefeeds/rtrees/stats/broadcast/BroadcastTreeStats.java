package livefeeds.rtrees.stats.broadcast;

import simsim.ssj.BinnedTally;
import simsim.utils.Persistent;

public class BroadcastTreeStats  {

	final double SAMPLE_SIZE = 5 * 60 ;
	
	public BinnedTally fanout = new BinnedTally(SAMPLE_SIZE, "Broadcast Fanout") ;
	public BinnedTally latency = new BinnedTally(SAMPLE_SIZE, "Broadcast Latency") ;
		
	public void recordFanout( double sample, int f ) {
		fanout.tally( sample, f) ;
	}

	public void recordLatency( double sample, double l ) {
		latency.tally( sample, l) ;
	}

}
