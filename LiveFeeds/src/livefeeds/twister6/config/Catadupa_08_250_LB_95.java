package livefeeds.twister6.config;

import livefeeds.twister6.msgs.CatadupaCast;
import livefeeds.twister6.msgs.DbRepairReply;
import livefeeds.twister6.msgs.DbRepairRequest;
import livefeeds.twister6.msgs.DbUploadAccept;
import livefeeds.twister6.msgs.DbUploadEndpoints;
import livefeeds.twister6.msgs.DbUploadEndpointsRequest;
import livefeeds.twister6.msgs.DbUploadFilters;
import livefeeds.twister6.msgs.DbUploadFiltersRequest;
import livefeeds.twister6.msgs.DbUploadReject;
import livefeeds.twister6.msgs.DepartureNotice;
import livefeeds.twister6.msgs.JoinRequest;
import livefeeds.twister6.msgs.NewArrivals;

import simsim.core.Globals;

public class Catadupa_08_250_LB_95 extends Config {

	public Catadupa_08_250_LB_95() {
		Config = this ;
		init() ;
	}
	
	protected void init() {
		
		ENDPOINT_SIZE = 6 ;  //(ip + port)
		FILTER_KEY_SIZE = 16 ; // no filters...
		FILTER_DATA_SIZE = 250;


		DB_FILTER_REDUNDANCY = 0.95 ;
		DB_FILTER_DOWNLOAD_PIECES = 10 ;

		DB_FILTER_DOWNLOAD_DELAY = 0 * 60 ;
		DB_FILTER_DOWNLOAD_SPAN = 10 * 60 ;


//		CATADUPA_LOAD_BALANCE_FACTOR = 1e10 ; // no active load balancing...
		CATADUPA_LOAD_BALANCE_FACTOR = 0.95 ; 
		
		JOIN_ATTEMPT_PERIOD = 45.0;
		MEMBERSHIP_REPAIR_PERIOD = 15.0;
		SEQUENCER_BROADCAST_PERIOD = 30.0;

		NUMBER_OF_SLICES = 3;
		NODE_KEY_LENGTH = 32;
		PUBSUB_MAX_FANOUT = 2;
		BROADCAST_MAX_FANOUT = 4;

		VIEW_CUTOFF_WINDOW = 15 * 60;
		VIEW_CUTOFF = (int) (NUMBER_OF_SLICES * VIEW_CUTOFF_WINDOW / SEQUENCER_BROADCAST_PERIOD);

		WEIBULL_SHAPE = 1.8;
		AVERAGE_ARRIVAL_RATE = 8;
		
		MAX_SESSION_DURATION = 8 * 3600;
		MEAN_SESSION_DURATION = 4 * 3600;

		Sim_RandomSeed = 1L;
		Net_RandomSeed = 1L;
		
		super.init();

		NewArrivals.accountTraffic = true ;
		JoinRequest.accountTraffic = true ;
		CatadupaCast.accountTraffic = true ;
		DepartureNotice.accountTraffic = true ;
		
		DbUploadAccept.accountTraffic = true ;
		DbUploadReject.accountTraffic = true ;
		DbUploadFilters.accountTraffic = true ;
		DbUploadEndpoints.accountTraffic = true ;
		DbUploadFiltersRequest.accountTraffic = true;
		DbUploadEndpointsRequest.accountTraffic = true ;
		
		DbRepairReply.accountTraffic = true ;
		DbRepairRequest.accountTraffic = true ;
		
		Globals.set("NoGUI", true);
	}
	
	public static void main( String[] args) throws Exception {
		new Catadupa_08_250_LB_95().run() ;
	}
}
