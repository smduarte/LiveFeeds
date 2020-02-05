package livefeeds.sift0.config;

import livefeeds.sift0.msgs.CatadupaCast;
import livefeeds.sift0.msgs.CatadupaCastPayload;
import livefeeds.sift0.msgs.DbRepairReply;
import livefeeds.sift0.msgs.DbRepairRequest;
import livefeeds.sift0.msgs.DbUploadAccept;
import livefeeds.sift0.msgs.DbUploadEndpoints;
import livefeeds.sift0.msgs.DbUploadEndpointsRequest;
import livefeeds.sift0.msgs.DbUploadFilters;
import livefeeds.sift0.msgs.DbUploadFiltersRequest;
import livefeeds.sift0.msgs.DbUploadReject;
import livefeeds.sift0.msgs.FailureNotice;
import livefeeds.sift0.msgs.JoinRequest;

import simsim.core.Globals;

public class Catadupa_4_99_38 extends Config {

	public Catadupa_4_99_38() {
		Config = this;
		init();
	}

	protected void init() {

		ENDPOINT_SIZE = 6; // (ip + port)
		FILTER_KEY_SIZE = 16; // no filters...
		FILTER_DATA_SIZE = 250;

		CATADUPA_DYNAMIC_FANOUT = true;
		CATADUPA_LOAD_BALANCE_FACTOR = 1.0; // do active load balancing...

		DB_FILTER_REDUNDANCY = 0.99;
		DB_FILTER_DOWNLOAD_PIECES = 1;

		DB_FILTER_DOWNLOAD_DELAY = 1 * 60;
		DB_FILTER_DOWNLOAD_SPAN = 10 * 60;

		JOIN_ATTEMPT_PERIOD = 60.0;
		MEMBERSHIP_REPAIR_PERIOD = 30.0;
		SEQUENCER_BROADCAST_PERIOD = 30.0;

		NODE_KEY_LENGTH = 62;
		PUBSUB_MAX_FANOUT = 5;
		BROADCAST_MAX_FANOUT = 6;

		VIEW_CUTOFF_WINDOW = 30 * 60;
		VIEW_CUTOFF = (int) (VIEW_CUTOFF_WINDOW / SEQUENCER_BROADCAST_PERIOD);

		JOINS_TARGET_AGGREGATION_RATE = 8;
		EXITS_TARGET_AGGREGATION_RATE = 10;
		
		SLICE_AGGREGATION_DEPTH = 4;
		JOINS_AGGREGATION_DEPTH = 7;
		EXITS_AGGREGATION_DEPTH = 8;

		WEIBULL_SHAPE = 1.8;
		AVERAGE_ARRIVAL_RATE = 3.8;

		MAX_SESSION_DURATION = 8 * 3600;
		MEAN_SESSION_DURATION = 4 * 3600;

		UdpHeaderLength = 28;
		TcpHeaderLength = 40;
		TcpAckOverhead = 0.025;

		Sim_RandomSeed = 1L;
		Net_RandomSeed = 1L;

		super.init();

		CatadupaCastPayload.accountTraffic = true;
		JoinRequest.accountTraffic = true;
		CatadupaCast.accountTraffic = true;
		FailureNotice.accountTraffic = true;

		DbUploadAccept.accountTraffic = true;
		DbUploadReject.accountTraffic = true;
		DbUploadFilters.accountTraffic = true;
		DbUploadEndpoints.accountTraffic = true;
		DbUploadFiltersRequest.accountTraffic = true;
		DbUploadEndpointsRequest.accountTraffic = true;

		DbRepairReply.accountTraffic = true;
		DbRepairRequest.accountTraffic = true;

		Globals.set("NoGUI", false);
	}

	public static void main(String[] args) throws Exception {
		new Catadupa_4_99_38().run();
	}
}
