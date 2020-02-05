package livefeeds.twister6.stats;

import livefeeds.twister6.config.Config;

import simsim.ssj.BinnedTally;

public class TrafficStatistics extends BinnedTally {
	
	public TrafficStatistics( String name ) {
		super( Config.Config.CATADUPA_TRAFFIC_BINSIZE, name ) ;
	}
}
