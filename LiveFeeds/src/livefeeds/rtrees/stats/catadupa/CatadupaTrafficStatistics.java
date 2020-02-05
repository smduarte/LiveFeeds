package livefeeds.rtrees.stats.catadupa;


import livefeeds.rtrees.config.Config;

import simsim.ssj.BinnedTally;



public class CatadupaTrafficStatistics extends BinnedTally {

	public double SAMPLE_DURATION = 15 * 60 ;	
	
	protected CatadupaTrafficStatistics(String name ) {
		super( Config.Config.name() + "-" + name ) ;
	}

	public void tally( double sessionDuration, double rate ) {
		int tally = (int) (sessionDuration / SAMPLE_DURATION) ;
		bin( tally).add( rate ) ;
	}	
	
//	public String name() {
//		int i = name.lastIndexOf('-') ;
//		return i < 0 ? name : name.substring( i + 1) ;
//	}
//	
//	
//	public String name( Map<String, String> translator) {
//		int i = name.lastIndexOf('-') ;
//		String shortName = i < 0 ? name : name.substring( i + 1) ;
//		return translator.containsKey( shortName ) ? translator.get( shortName ) : shortName ;
//	}
	
}