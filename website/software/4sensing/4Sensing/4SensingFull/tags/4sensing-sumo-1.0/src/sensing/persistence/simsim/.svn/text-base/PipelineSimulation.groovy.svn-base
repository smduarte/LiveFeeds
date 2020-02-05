package sensing.persistence.simsim;
import sensing.persistence.core.ServicesConfig;
import sensing.persistence.core.network.*;
import sensing.persistence.core.query.Query;
import sensing.persistence.core.vtable.VTableManager;

import simsim.core.*;
import static simsim.core.Simulation.*;
import simsim.gui.InputHandler;
import simsim.gui.canvas.*;
import simsim.gui.geom.Rectangle;
import simsim.gui.geom.XY;

import groovy.util.GroovyScriptEngine;

import java.awt.geom.*;
import java.awt.Point;

public abstract class PipelineSimulation extends Simulation implements Displayable, PipelineSimJ {
	static ServicesConfig.QueryImplPolicy QUERY_IMPL_POLICY;
	
	static SimSetup setup;
	static String runId;
	static String simId;
	static String env;
	static boolean exit;
	static Class nodeClass;
	static String  queryImplPolicy;
	
	static Properties config;
	
	static final int CLICK_RADIUS = 0;
//	static final int SPANNER_BRANCHF = 10;
	
//	static private nodes = [:];
//	static nodeList = [];
	
	static double lastTotalMsgSent = 0;
	static double sideLen = Globals.get("Net_Euclidean_SquareSideLength", 1000.0 ) ;
	static Rectangle world;
	static GroovyScriptEngine engine;

	static Query q;
	static Node root;

	static Closure onMouseClick = { it.toggleDebugging()};
	static Closure onRunQuery = null;
	static Closure nodeDetail = {it.nodeId};

	// display flags
	static display = [messages: true, nodes:true];
	
	static boolean nogui;
	
	static long startRTime;
	static long startSTime;
	
	static MobileNodeDB mNodeDB;
	
	PipelineSimulation() {
		super(1, EnumSet.of(Simulation.DisplayFlags.SIMULATION, Simulation.DisplayFlags.TIME, Simulation.DisplayFlags.TRAFFIC));
	}


	public static String getConfigProperty(String name, String defaultValue = null) {
		return 	config.getProperty("4sensing.${env}.${name}", defaultValue);
	}
	
	protected static void config(String[] args) {
		setup = Class.forName(args[0]).newInstance();
		runId = args[1];
		env = args[2];
		exit = Boolean.parseBoolean(args[3]);
		if(args.length > 4) {
			simId = args[4];
		}
		
		Properties ver = new Properties();
		ver.load(getClass().getResourceAsStream("/4sensing-ver.properties"));	
		int version = Integer.parseInt(ver.getProperty("4sensing.version"));	
		println "version: $version";
		setup.config.VERSION = version;
		
		println "env: ${env}";
		config = new Properties();
		config.load(getClass().getResourceAsStream("/4sensing.properties"));
		
		String codebase = getConfigProperty("codebase");
		println "sim.codebase: ${codebase}"
		engine = new GroovyScriptEngine(codebase);
		nogui =  Boolean.parseBoolean(getConfigProperty("sim.nogui", "false"));
		Globals.set("NoGUI",nogui);
		println "sim.nogui: ${nogui}"
		
		Globals.set("Sim_RandomSeed", setup.SIM_SEED ?: 0L);
		Globals.set("Net_RandomSeed", setup.NET_SEED ?: 0L);

		Globals.set("Net_Jitter", 2.5d);

		Globals.set("Net_FontSize", 60.0f ) ;
		Globals.set("Net_Euclidean_NodeRadius", 32.0d);
		Globals.set("Net_Euclidean_CostFactor", 0.0001d);		
		//Globals.set("Net_Euclidean_DisplayNodeLabels", true);
		Globals.set("Net_Euclidean_MinimumNodeDistance", 120.0d);

		Globals.set("Traffic_DisplayDeadPackets", true ) ;		
		//Globals.set("Traffic_DeadPacketHistory", 0.5f);
		Globals.set("Traffic_CheckMessageLength", false);
	}
	
	
	
	protected void stop() {
		super.stop();
		println "Exiting";
		if(exit) System.exit(0);
	}
	
	
//	protected static void registerNode(Node n) {
//		nodes[n.address] = n;
//		nodeList << n;
//	}
	
	static int homebaseIdx = 0;
	static int numRegisteredMobileNodes = 0;
	public void registerMobileNode(MobileNode n) {
		//println "${homebaseIdx}-${PipelineSimulation.nodeList.size()}";
		if(n.homeBase) {
			n.homeBase.unregisterMobileNode(n);
			//println "Reseting homebase for ${n}"
		}
		Node hb;
		while((hb = PeerDB.peersList[homebaseIdx]).isOffline()) {
			homebaseIdx = (homebaseIdx+1) % PeerDB.peerList.size();
		}
		n.setHomeBase(hb);
		hb.registerMobileNode(n);
		homebaseIdx = (homebaseIdx+1) % PeerDB.peersList.size();
		numRegisteredMobileNodes++;
	}
	
	public int getNumRegisteredMobileNodes() {
		return 	numRegisteredMobileNodes;
	}
	
	protected static void putOffline(nodes) {
		nodes*.putOffline();
		NetworkService.update();
	}
	

//	protected static Node getNode(NetAddress a) {
//		return nodes[a];
//	}

	public static void eachNode(Closure c) {
		PeerDB.peersList.each(c);
	}

	public static Node randomNode() {
		return PeerDB.peersList[rg.nextInt(PeerDB.peersList.size())];
	}

	protected void init() {
		setup.init(this);
		Globals.set("4sensingJ", this);
		// Set up and parameterize the GUI.		
		// Set the position and size of the default simulator window/frame: "MainFrame"
		Gui.setFrameRectangle("MainFrame", 0, 0, 600, 600);	
		Gui.addInputHandler("MainFrame", [onMouseMove: { XY pu, XY ps ->}, onMouseDragged: { int button, XY pu, XY ps -> },
		    onMouseClick: { int button, XY pu, XY ps -> 
				if(button == 1) {
					closestNode(ps, onMouseClick);
					
				} else {
					// reload script
				   	runScript();
				}
			}] as InputHandler);
		
//		new PeriodicTask(1) {
//			public void run() {
//				PipelineSimulation.setup.update();
//			}	
//		};
		if(setup.SIM_UPDATE_INTERVAL) {
			new PeriodicTask(setup.SIM_UPDATE_INTERVAL) {
				public void run() { 
					double realTime = (System.currentTimeMillis()-PipelineSimulation.startRTime)/1000F;
					printf("time: %.2f realTime: %.2f ratio: %.2f\n", currentTime(), realTime,  currentTime()/realTime);
					PipelineSimulation.this.update();
					PipelineSimulation.setup.update();
				}
			};	
		}
	
		if(setup.OUTPUT_INTERVAL) {
			if(setup.OUTPUT_INTERVAL) {
				new PeriodicTask(setup.OUTPUT_INTERVAL) {
					public void run() {
						double realTime = (System.currentTimeMillis()-PipelineSimulation.startRTime)/1000F;
						double simTime = currentTime() - PipelineSimulation.startSTime;
						printf("output elased: %.2f realTime: %.2f ratio: %.2f\n", simTime, realTime, simTime/realTime );
						PipelineSimulation.setup.output();
						PipelineSimulation.startRTime = System.currentTimeMillis();
						PipelineSimulation.startSTime = currentTime();
					}
				};
			}
		}
		setSimulationMaxTimeWarp(setup.SIM_TIME_WARP ?: 1e9);
	}
	
	
	protected void update() {};


	protected void start() {
		mNodeDB = Globals.get("4sensing_mNodeDB");
		if(!nogui) runScript();
		PeerDB.peersList*.start();
		startRTime = System.currentTimeMillis()
		super.start();
	}

//	public void setupQuery(Query q) {
//		this.q  = q;
//		root = randomNode();
//		new Task(30) {
//			public void run() {
//				PipelineSimulation.root.runQuery(q) {data -> handleQueryData(data)};
//			}
//		};		
//	}
	
	public void runQuery(Query q, Closure handler) {
		this.q  = q;
		root = randomNode();
		println "RUNQUERY ${root.nodeId}"
		root.runQuery(q, handler);
		if(onRunQuery) onRunQuery(q);
	}
	

	public void stopActiveQuery() {
		if(q) root.stopQuery(q);
	}
	
	public void killQueries() {
		eachNode {it.services.query.killQueries()}
		q  = null;
	}

	public void reloadPipelines() {
		VTableManager.instance.invalidateCache();
	}

	// ------------------------------------------------------------------------------------------------------------------
	public void displayOn( Canvas c ) {
		if(display.nodes) {
			eachNode{node -> node.displayOn(c)};
			closestNode(c.sMouse()) {node, pos -> node.displayDetailOn(c)};
		}
		if(display.mobile) {
			mNodeDB.displayOn(c);
		}
	}

	protected  void closestNode(XY pos, Closure clos) {
		Node closest = getClosestNode(pos);
		if(closest) {
			clos(closest, new Point(pos.X(), pos.Y()));
		}
	}

	protected abstract Node getClosestNode(XY pos);

	protected void runScript() {
    	String script = this.class.name.substring(0, this.class.name.lastIndexOf('.')).replaceAll(/\./, "/")+"/Script.groovy";
    	println script;
    	Binding b = new Binding();
		b.setVariable("sim", this);
		try{
			engine.run(script, b)
		} catch(Exception e) {
			println "Script error"
			e.printStackTrace()
		}		
	}

	protected handleQueryData(data) {
	}
	
	// PipelineSimJ
	
	public GroovyObject newTuple(String className) {
		return (GroovyObject)Class.forName(className).newInstance();
	}
	
	public <T> T getConfig(String name) {
		return (T) setup[name];
	}

}
