package livefeeds.twister7.config;

import livefeeds.twister7.msgs.CatadupaCast;
import livefeeds.twister7.msgs.CatadupaCastPayload;
import livefeeds.twister7.msgs.DbRepairReply;
import livefeeds.twister7.msgs.DbRepairRequest;
import livefeeds.twister7.msgs.DbUploadEndpoints;
import livefeeds.twister7.msgs.DbUploadEndpointsRequest;
import livefeeds.twister7.msgs.DbUploadFilters;
import livefeeds.twister7.msgs.DbUploadFiltersRequest;
import livefeeds.twister7.msgs.FailureNotice;
import livefeeds.twister7.msgs.JoinRequest;

import simsim.core.Globals;

public class Turmoil_3_t extends Config {

	public Turmoil_3_t() {
		Config = this ;
		init() ;
	}
	
	protected void init() {
		
		ENDPOINT_SIZE = 6 ;  //(ip + port)
		FILTER_KEY_SIZE = 16 ; // no filters...
		FILTER_DATA_SIZE = 256;
		
		CATADUPA_DYNAMIC_FANOUT = true ;
		CATADUPA_LOAD_BALANCE_FACTOR = 1.0 ; // do active load balancing...

		
		DB_FILTER_REDUNDANCY = 0.5 ;
		DB_FILTER_DOWNLOAD_PIECES = 5 ;

		DB_FILTER_DOWNLOAD_DELAY = 1 * 60 ;
		DB_FILTER_DOWNLOAD_SPAN = 10 * 60 ;

		
		JOIN_ATTEMPT_PERIOD = 60.0;
		MEMBERSHIP_REPAIR_PERIOD = 30.0;
		SEQUENCER_BROADCAST_PERIOD = 30.0;

		NODE_KEY_LENGTH = 32;
		PUBSUB_MAX_FANOUT = 5;
		BROADCAST_MAX_FANOUT = 6;

		VIEW_CUTOFF_WINDOW = 30 * 60;
		VIEW_CUTOFF = (int) ( VIEW_CUTOFF_WINDOW / SEQUENCER_BROADCAST_PERIOD);

		JOINS_AGGREGATION_DEPTH = 0 ;
		EXITS_AGGREGATION_DEPTH = 0 ;
		
		JOINS_TARGET_AGGREGATION_RATE = 8 ;
		EXITS_TARGET_AGGREGATION_RATE = 10 ;

		WEIBULL_SHAPE = 1.8;
		AVERAGE_ARRIVAL_RATE = 0.125;
		
		MAX_SESSION_DURATION = 8 * 3600;
		MEAN_SESSION_DURATION = 4 * 3600;

		Filter3_DIMS = 3 ;
		Filter3_LOGIC = true ;

		
		UdpHeaderLength = 28;
		TcpHeaderLength = 40;
		TcpAckOverhead = 0.025;
		
		Sim_RandomSeed = 1L;
		Net_RandomSeed = 1L;

		super.init();

		CatadupaCastPayload.accountTraffic = true ;
		JoinRequest.accountTraffic = true ;
		CatadupaCast.accountTraffic = true ;
		FailureNotice.accountTraffic = true ;
		
		DbUploadFilters.accountTraffic = true ;
		DbUploadEndpoints.accountTraffic = true ;
		DbUploadFiltersRequest.accountTraffic = true;
		DbUploadEndpointsRequest.accountTraffic = true ;
		
		DbRepairReply.accountTraffic = true ;
		DbRepairRequest.accountTraffic = true ;
		
		Globals.set("NoGUI", false);
	}
	
	public static void main( String[] args) throws Exception {
		new Turmoil_3_t().run() ;
	}
}
