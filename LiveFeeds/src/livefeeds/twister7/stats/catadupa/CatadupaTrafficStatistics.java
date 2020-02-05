package livefeeds.twister7.stats.catadupa;

import livefeeds.twister7.config.Config;

import simsim.ssj.BinnedTally;

public class CatadupaTrafficStatistics extends BinnedTally {
	
	public CatadupaTrafficStatistics( String name ) {
		super( Config.Config.CATADUPA_TRAFFIC_BINSIZE, name ) ;
	}
}
