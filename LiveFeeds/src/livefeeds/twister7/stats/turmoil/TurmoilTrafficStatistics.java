package livefeeds.twister7.stats.turmoil;

import simsim.ssj.BinnedTally;
import livefeeds.twister7.config.Config;

public class TurmoilTrafficStatistics extends BinnedTally {
	
	public TurmoilTrafficStatistics( String name ) {
		super( Config.Config.CATADUPA_TRAFFIC_BINSIZE, name ) ;
	}
}
