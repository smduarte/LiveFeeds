package livefeeds.twister7.config;

import livefeeds.twister7.ChurnModel;
import livefeeds.twister7.Main;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import simsim.core.Globals;
import simsim.utils.Persistent;

/**
 * This class manages global simulation properties.
 * 
 * Global properties must be set/initialized/loaded at startup (main method).
 * 
 * Global properties are identified by a key, usually a string, whose format
 * points to the module affected.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * 
 */
abstract public class Config extends Persistent {
	// Default config values...
	// Do not alter them directly, use a subclass...

	public boolean CATADUPA_DYNAMIC_FANOUT = true;

	public double ARRIVAL_RATE_FACTOR = 1.5;
	
	public int ENDPOINT_SIZE = 6; // (ip + port)
	public int FILTER_KEY_SIZE = 16; // ( 128-bit hash)
	public int FILTER_DATA_SIZE = 512 - ENDPOINT_SIZE;

	public double REJOIN_PROBABILITY = 0.0;

	public double DB_FILTER_REDUNDANCY = 0.0;
	public int DB_FILTER_DOWNLOAD_PIECES = 10;

	public int DB_RECENT_NODE_SIZE = 0;

	public double DB_FILTER_DOWNLOAD_DELAY = 5 * 60;
	public double DB_FILTER_DOWNLOAD_SPAN = 10 * 60;

	public double CATADUPA_LOAD_BALANCE_FACTOR = 1e10;

	public double JOIN_ATTEMPT_PERIOD = 45.0;
	public double MEMBERSHIP_REPAIR_PERIOD = 15.0;
	public double SEQUENCER_BROADCAST_PERIOD = 15.0;

	public int NODE_KEY_LENGTH = 30;
	public int PUBSUB_MAX_FANOUT = 3;
	public int BROADCAST_MAX_FANOUT = 4;

	public int JOINS_AGGREGATION_DEPTH = 3;
	public int EXITS_AGGREGATION_DEPTH = 6;

	public double JOINS_TARGET_AGGREGATION_RATE = 0.5;
	public double EXITS_TARGET_AGGREGATION_RATE = 1;

	public int VIEW_CUTOFF_WINDOW = 60 * 60;
	public int VIEW_CUTOFF = (int) (VIEW_CUTOFF_WINDOW / SEQUENCER_BROADCAST_PERIOD);

	public double WEIBULL_SHAPE = 1.8;
	public double AVERAGE_ARRIVAL_RATE = 5;

	public double MAX_SESSION_DURATION = 8 * 3600;
	public double MEAN_SESSION_DURATION = 4 * 3600;
	
	public long Sim_RandomSeed = 1L;
	public long Net_RandomSeed = 1L;

//	public String Net_Type = "Orbis";
	public String Net_Type = "Euclidean";

	public double CATADUPA_TRAFFIC_BINSIZE = 300.0;

	public double BATCH_TIME_LIMIT = 6 ; // run for 6 max sessions
	
	public double CAT_H_CAPACITY = 24 * 1024;
	public double CAT_L_CAPACITY = 24 * 1024;

	public double CAT_H_PEAK_CAPACITY = 50 * 1024;
	public double CAT_L_PEAK_CAPACITY = 50 * 1024;

	
	public boolean Traffic_DisplayLiveChannels = false;
	public double Net_Euclidean_CostFactor = 0.0005;
	public double Net_Euclidean_MinCost = 0.005;

	public int Net_Orbis_LocalLoopClasses = 10;
	public double Net_Orbis_CorePerHopLatency = 0.025;
	public double Net_Orbis_LocalLoopPerClassLatencyFactor = 0.0025;
	public String Net_Orbis_Filename = "src/simsim/net/orbis/topos/500";

	public double Net_Euclidean_MinimumNodeDistance = -1.0;

	public double UdpHeaderLength = 28;
	public double TcpHeaderLength = 40;
	public double TcpAckOverhead = 0.025;
	public double TcpHandshakeOverhead = 3 * TcpHeaderLength; // Open and close,
																// 6/7
																// packets...

	public int Filter3_DIMS = 1;
	public boolean Filter3_LOGIC = true;

	public String Home = ".." ; //System.getProperty("user.home");
	public String saveFilename = Home + "/runs/" + name() + "-stats-x1.5.xml";

	public ChurnModel churn;

	protected void init() {

		Globals.set("Sim_RandomSeed", Sim_RandomSeed);
		Globals.set("Net_RandomSeed", Net_RandomSeed);

		Globals.set("Net_Type", Net_Type);
		Globals.set("Traffic_DisplayLiveChannels", Traffic_DisplayLiveChannels);
		Globals.set("Net_Euclidean_CostFactor", Net_Euclidean_CostFactor);
		Globals.set("Net_Euclidean_MinCost", Net_Euclidean_MinCost);

		Globals.set("Net_Orbis_FPS", 0.1);
		Globals.set("Net_Orbis_LocalLoopClasses", Net_Orbis_LocalLoopClasses);
		Globals.set("Net_Orbis_CorePerHopLatency", Net_Orbis_CorePerHopLatency);
		Globals.set("Net_Orbis_LocalLoopPerClassLatencyFactor", Net_Orbis_LocalLoopPerClassLatencyFactor);
		Globals.set("Net_Orbis_Filename", Net_Orbis_Filename);

		Globals.set("Net_Euclidean_MinimumNodeDistance", Net_Euclidean_MinimumNodeDistance);

		Globals.set("Net_UdpHeaderLength", UdpHeaderLength);
		Globals.set("Net_TcpHeaderLength", TcpHeaderLength);
		Globals.set("Net_TcpHeaderOverhead", TcpAckOverhead);

		Globals.set("Net_MTU", 1500);
		Globals.set("Traffic_TrackLargePackets", true);

		churn = new ChurnModel();

		Globals.set("NoGUI", true);
	}

	public void run() {
		Main = new Main();
		Main.init();
	}

	public String name() {
		return getClass().getName();
	}

	@XStreamOmitField
	public Main Main = null;
	public static Config Config = null;
}