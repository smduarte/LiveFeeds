package livefeeds.rtrees.config;

import static livefeeds.rtrees.config.Config.Config;
import livefeeds.rtrees.ChurnModel;
import livefeeds.rtrees.Main;

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

	public boolean BROADCAST_RANDOM_ROOT = true ;

	public boolean BROADCAST_RANDOM_TREES = false ;
	
	public int BROADCAST_MAX_FANOUT = 4 ;
	public int BROADCAST_PAYLOAD_MIN_SIZE = 100 ;
	public int BROADCAST_PAYLOAD_MAX_SIZE = 1000 ;

	public boolean BROADCAST_USE_DYNAMIC_FANOUT = false ;
	
	public int ENDPOINT_SIZE = 6 ;  //(ip + port)
	public int FILTER_KEY_SIZE = 16 ; //( 128-bit hash)
	public int FILTER_DATA_SIZE = 512 - ENDPOINT_SIZE;
	
	public double REJOIN_PROBABILITY = 0.0;

	public double DB_FILTER_REDUNDANCY = 0.0 ;
	public int DB_FILTER_DOWNLOAD_PIECES = 1 ;

	public int DB_RECENT_NODE_SIZE = 0 ;
	
	public double DB_FILTER_DOWNLOAD_DELAY = 0 * 60 ;
	public double DB_FILTER_DOWNLOAD_SPAN = 10 * 60 ;

	public double CATADUPA_LOAD_BALANCE_FACTOR = 0.95 ;
	
	public double JOIN_ATTEMPT_PERIOD = 45.0;
	public double MEMBERSHIP_REPAIR_PERIOD = 15.0;
	public double SEQUENCER_BROADCAST_PERIOD = 30.0;

	public int NUMBER_OF_SLICES = 1;
	public int NODE_KEY_LENGTH = 50;
	public int CATADUPA_MAX_FANOUT = 4;

	public int VIEW_CUTOFF_WINDOW = 15 * 60;
	public int VIEW_CUTOFF = (int) (NUMBER_OF_SLICES * VIEW_CUTOFF_WINDOW / SEQUENCER_BROADCAST_PERIOD);

	public double WEIBULL_SHAPE = 1.8;
	public double AVERAGE_ARRIVAL_RATE = 0.5;

	public double MAX_SESSION_DURATION = 8 * 3600;
	public double MEAN_SESSION_DURATION = 4 * 3600;

	public double BROADCAST_START = MAX_SESSION_DURATION / 2 + 30 * 60 ;

	public long Sim_RandomSeed = 1L;
	public long Net_RandomSeed = 1L;

	public String Net_Type = "Orbis";

	public boolean Traffic_DisplayLiveChannels = false;
	public double Net_Euclidean_CostFactor = 0.0005;
	public double Net_Euclidean_MinCost = 0.005;

	public int Net_Orbis_LocalLoopClasses = 10;
	public double Net_Orbis_CorePerHopLatency = 0.025;
	public double Net_Orbis_LocalLoopPerClassLatencyFactor = 0.0025;
	public String Net_Orbis_Filename = "../SimSim/src/simsim/net/orbis/topos/500";

	public double Net_Euclidean_MinimumNodeDistance = -1.0;

	public double UdpHeaderLength = 0*28;
	public double TcpHeaderLength = 0*40;
	public double TcpAckOverhead = 0*0.025;
	public double TcpHandshakeOverhead = 3 * TcpHeaderLength; // Open and close, 6/7 packets...

	
	public String Home = System.getProperty("user.home") ;
	public String saveFilename = Home + "/runs/" + name() + "-stats.xml" ;
		
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

		Globals.set("Net_MTU", 1500) ;
		Globals.set("Traffic_TrackLargePackets", true ) ;

		churn = new ChurnModel() ;
		
		Globals.set("NoGUI", false) ;		
	}

	public void run() {
		new Main().init() ;
	}

	public String name() {
		return getClass().getName() ;
	}

	public static Config Config = null;	
}
