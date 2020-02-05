package livefeeds.rtrees.config;

import simsim.core.Globals;

public class Broadcasting_RR_4_ST extends Config {

	public Broadcasting_RR_4_ST() {
		Config = this ;
		init() ;
	}
	
	protected void init() {
	
		BROADCAST_RANDOM_ROOT = true ;
		BROADCAST_RANDOM_TREES = false ;
		BROADCAST_USE_DYNAMIC_FANOUT = false ;
		
		BROADCAST_PAYLOAD_MIN_SIZE = 1000 ;
		BROADCAST_PAYLOAD_MAX_SIZE = 1000 ;
		BROADCAST_MAX_FANOUT = 4 ;

		AVERAGE_ARRIVAL_RATE = 4;

		super.init();
		
		Globals.set("NoGUI", false);
	}
	
	public static void main( String[] args) throws Exception {
		new Broadcasting_RR_4_ST().run() ;
	}
}
