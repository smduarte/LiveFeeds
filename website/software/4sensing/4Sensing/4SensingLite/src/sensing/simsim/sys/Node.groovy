package sensing.simsim.sys;


import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import simsim.core.*;
import simsim.gui.canvas.*;
import simsim.gui.geom.*;
import simsim.net.euclidean.EuclideanNetwork.EuclideanAddress;
import static simsim.core.Simulation.*;

import sensing.persistence.simsim.msg.*;
import sensing.simsim.sys.msg.PipelineSimMessage;
import sensing.core.*;
import sensing.core.network.*;
import sensing.core.scheduler.*;
import sensing.core.logging.*;
import sensing.core.query.*;
import sensing.core.sensors.GPSReading;

import groovy.lang.Closure;
import static sensing.simsim.sys.PipelineSimulation.display;
import static sensing.simsim.sys.Sim4Sensing.mapView;
import sensing.core.vtable.VTableDefinition;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Node extends AbstractNode implements HomeBase, Displayable, Peer, NetworkProvider, SchedulerProvider, LoggingProvider, MessageHandler {
	static final int DIST_TREE_BRACHF = 3;
	static nodeCount = 1; //used to generate nodeId
	int nodeId;
	boolean debugNode = false;
	int msgSent = 0;

	protected ServiceManager services;
	final UUID id;

	final ServicesConfig config;
	final Rectangle world;
	static double minNodeDistance = Globals.get("Net_Euclidean_MinimumNodeDistance", 15.0) ;
	static long seed = Globals.get("Sim_RandomSeed", 0L ) ;

	Ellipse filter;
	Rectangle filterBounds;
	Random locGenRnd; // Random used for location generation

	List mNodes = [];
	
	public Node() {
		nodeId = nodeCount++;
		this.id = UUID.nameUUIDFromBytes(nodeId.toString().getBytes())
		this.locGenRnd = new Random(id.hashCode());
		this.world = PipelineSimulation.world;
				
		this.config = new ServicesConfig(
				peer: this,
				world: world,
				networkImpl: this,
				distTreeType: ServicesConfig.DistTreeType.RND,
				distTreeBranchF: DIST_TREE_BRACHF,
				schedulerImpl: this,
				loggingImpl: this,
				queryImplPolicy: PipelineSimulation.QUERY_IMPL_POLICY,
				rand: PipelineSimulation.rg,
				vTableCodebase: PipelineSimulation.getConfigProperty("codebase")
		);
	
		// Monitoring
		if(PipelineSimulation.setup.MONITORING) {
			config.monitoring = [:];
			if(PipelineSimulation.setup.MONITORING_WORKLOAD){
				config.monitoring.workLoad = true;
				config.monitoring.gridLen = PipelineSimulation.setup.MONITORING_GRID_LEN;
			}
		}
	}

	public void init() {
		services = new ServiceManager(config);		
		services.init();	
	}
	
	public void start() {
		services.start();
	}
	
	public void registerMobileNode(MobileNode mnode) {
		mNodes << mnode;
	}
	
	public void unregisterMobileNode(MobileNode mnode) {
		mNodes.remove(mnode);
	}
	

	public void runQuery(Query q, Closure listener) {
		services.queryInterface.runQuery(q, listener);
	}
	
	public void closeQuery(Query q) {
		services.queryInterface.closeQuery(q);
	}
	
	// Homebase
	public boolean sensorInput(r) {
		return services.sensor.send(r);
	}
	
	public List<Double> getBindingDestination(UUID mNodeId) {
		UUID destination = services.sensor.getBindingDestinationId(mNodeId);
		if(destination) {
			return services.network.getLocation(destination);
		} else {
			return null;
		}
	}
	
	// screen position used for rendering
	public abstract XY getPos();
	
//	public void setPeerXY() {
//		this.x  = address.pos.x;
//		this.y  = address.pos.y;
//		posXY = address.pos;
//	}
	
	public List newPeerLocation() {
		[address.pos.x, address.pos.y]
	}

	public void toggleDebugging() {
		debugNode = !debugNode;
	}
	

	/* Peer methods
	 * 
	 */
	// For simulation only - direct sensor input
//	public boolean sensorInput(sensorReading) {
//		return services.sensor.input(sensorReading);
//	}
	
	/* NeworkProvider/Peer methods
	 * 
	 */
	
	public boolean accepts(Rectangle2D bounds) {
		return true;
	}

	public getAddress() {
		return this.@address;
	}
	
	
	def sentMsg = [:];
	public void send(Peer p, NetworkMessage m) {
		if(isOffline()) return;
 		if(p != this) {
			udpSend(p.endpoint, new PipelineSimMessage(this.id, m, p.id));
			msgSent++;
			if(sentMsg[m.payload.class.name]) {
				sentMsg[m.payload.class.name]++;
			} else {
				sentMsg[m.payload.class.name] = 1;
			}
		} else {
			onReceive(this.endpoint, new PipelineSimMessage(this.id, m, this.id));
		}
	}
	
	public void onSendFailure( EndPoint dst, Message m ) {
		println "!!!!!! onSendFailure ${m.class.name}"
		services.network.sendFailed(m.dstId, m.msg);
	}
	
	def receivedMsg = [:];
	public void onReceive(EndPoint src, PipelineSimMessage m) {
		if(src != this.endpoint) {
			if(receivedMsg[m.msg.payload.class.name]) {
				receivedMsg[m.msg.payload.class.name]++;
			} else {
				receivedMsg[m.msg.payload.class.name] = 1;
			}
		}
		services.network.receive(m.srcId, m.msg);
	}


	public List distTreeChildren(Peer root, Peer p, ServicesConfig.DistTreeType t) {
		List result;
		switch(t) {
		case ServicesConfig.DistTreeType.SPT:
			result = Simulation.Spanner.children(root.address, p.address).collect{NetAddress a -> PipelineSimulation.getNode(a)};
			break;
		case ServicesConfig.DistTreeType.RND: 
			result = PipelineSimulation.spanner.children(root, p);
			break;
		}
		return result;
	}

	public Peer distTreeParent(Peer root, Peer p, ServicesConfig.DistTreeType t) {
		switch(t) {
		case NetworkProvider.TreeType.SPT:
			return PipelineSimulation.getNode(Simulation.Spanner.parent(root.address, p.address));
		case NetworkProvider.TreeType.RND: 
			result = PipelineSimulation.spanner.parent(root, p);
			break;
		}
	}
	

	/* SchedulerProvider methods
	 * 
	 */

	protected Map tasks = [:];
	
	class PeriodRecord {
		PeriodicTask task;
		CopyOnWriteArrayList<Closure> callbacks = new CopyOnWriteArrayList<Closure>();
		ConcurrentHashMap owner = new ConcurrentHashMap();
	}
	
	

	
	public void _schedule(double period, Object owner, Closure clos) {
		if(tasks[period] && tasks[period].callbacks[owner]) {
			//tasks[period].add(0,clos);
			tasks[period].callbacks[owner].add(clos);
		} else {
			if(!tasks[period]) {
				PeriodRecord pRec = new PeriodRecord();
				pRec.task = new PeriodicTask(this, period) {
					public void run() {
						pRec.callbacks.each{ Object cOwner, cList ->
							cList*.call()
						}
					}
				}
				tasks[period] = pRec;
			}
			List clist = new CopyOnWriteArrayList<Closure>();
			//clist.add(0,clos);
			clist.add(clos);
			
			tasks[period].callbacks[owner] = clist;
		}
	}
	
	public void schedule(double period, Object taskContext, Object owner, Closure clos, boolean prioritary = false) {
		if(!tasks[taskContext]) tasks[taskContext] = [:];
		if(!tasks[taskContext][period]) tasks[taskContext][period] = new PeriodRecord();
		if(!tasks[taskContext][period].owner[owner]) tasks[taskContext][period].owner[owner] = new CopyOnWriteArrayList<Closure>();
		
		if(prioritary) {
			tasks[taskContext][period].callbacks.add(0,clos);
		} else {
			tasks[taskContext][period].callbacks.add(clos);
		}
		tasks[taskContext][period].owner[owner].add(clos);
		
		if(!tasks[taskContext][period].task) {
			PeriodRecord pRec = tasks[taskContext][period];
			pRec.task = new PeriodicTask(this, period) {
				public void run() { pRec.callbacks*.call() }
			}
		}
	}
	
	public void scheduleOnce(double due, Closure clos) {
		new Task(due) {
			public void run() {
				clos.call();
			}
		};
	}

	
	public void unschedule(Object taskContext, Object owner) {
		List toCancel = []
		
		tasks[taskContext].each { double period, PeriodRecord pRec ->
			pRec.owner[owner].each{ pRec.callbacks.remove(it) }
			pRec.owner.remove(owner)
			if(pRec.callbacks.size() == 0) {
				toCancel << period
			}
		}
		toCancel.each{ period ->
			tasks[taskContext][period].task.cancel();
			tasks[taskContext].remove(period);
		}
		if(tasks[taskContext].size() == 0) {
			tasks.remove(taskContext);	
		}
	}
	
	public void schedule(double period, Closure clos, boolean prioritary = false) {
		schedule(period, "mainctx", this, clos, prioritary)
	}
	
	public void unschedule() {
		uschedule("mainctx", this)	
	}
	
	public void _unschedule(Object owner) {
		tasks.each { entry ->
			Iterator i = entry.value.iterator();
			while(i.hasNext()) {
				Closure clos = i.next();
				if(clos.getOwner() == owner) {
					entry.value.remove(clos);
				}
			}
		}
	}

	/* LoggingProvider methods
	 * 
	 */
	public void log(int level, Object src, String where, String msg) {
		if(debugNode || level != DEBUG)
			println("[Node ${nodeId} Time ${currentTime()}: ${src.class.name} : ${where} ] ${msg}");
	}

	/* ...
	 * 
	 */
	static  Pen nodePen = new Pen(RGB.GRAY, 1);
	static  Pen offlinePen = new Pen(RGB.BLACK, 2);
	static  int nodeSize = 5;
	
	static Pen saPen = new Pen(RGB.BLUE, 2, 5);
	static Pen treePen = new Pen(RGB.RED);
	static Pen leafTreePen = new Pen(RGB.RED, 1, 5);
	static Pen quadPen = new Pen(RGB.ORANGE, 4, 10);
	
	
	public void displayOn(Canvas c) {
		if(isOffline()) {
			c.sDraw( offlinePen, new Circle(pos.x, pos.y, nodeSize+3));
		} else if(debugNode) {
			displayDetailOn(c);
			c.sFill( RGB.RED, new Circle(pos.x, pos.y, nodeSize+3));
		} else {
			c.sFill( nodePen, new Circle(pos.x, pos.y, nodeSize));
		}
		
	}

	public void displayDetailOn(Canvas c) {
		//displayLabel(c, pos, "${nodeId}");
		displayLabel(c, pos, services.query.getTotalProcessedTuples());
		//displayLabel(c, pos, "${getProcessedTuples(["global"])}");

		if(display.mobile) {
			mNodes.each { MobileNode mNode ->
				displayLabel(c, mNode.pos, "${nodeId}");
				mNode.displayDetailOn(c);
			}
		}
		if(services.network.peerFilterBounds) {
			Rectangle screenBounds =  mapView.latLonBoundsToScreen(services.network.peerFilterBounds);
			c.sDraw(filterPen, new Ellipse2D.Double(screenBounds.x, screenBounds.y, screenBounds.width, screenBounds.height));
		}
	}
	
	static Pen labelPen = new Pen(RGB.BLACK);
	protected static void displayLabel(Canvas c, XY pos, data) {
		c.sFont(24);
		c.sDraw(labelPen, data.toString(), pos.x-10, pos.y-10);			
	}
	

	
	
	/* 
	 * Monitoring
	 */
	public int getProcessedTuples(filter) {
		return services.query.getProcessedTuples(filter);
	}
	
	public int getTotalProcessedTuples(filter) {
		return services.query.getTotalProcessedTuples(filter);
	}
	
	public int getProcessCount() {
		return services.query.getProcessCount();
	}
	
	public int getSentMsg(String className) {
		return (sentMsg[className] != null) ? sentMsg[className] : 0;
	}
	

	public int getReceivedMsg(String className) {
		return (receivedMsg[className] != null) ? receivedMsg[className] : 0;
	}
	
	public int getAcquiredCount() {
		return services.sensor.getAcquiredCount();
	}
	
	public int getRemoteAcquiredCount() {
		return services.sensor.getRemoteCount();
	}
	
	public int getBindingCount() {
		return services.sensor.getBindingCount();
	}
	
	public int getStateCount(String filter) {
		return services.query.getStateCount(filter);
	}
	public int getDSOutputCount() {
		return services.query.getDSOutputCount();
	}
	
	int totalState = 0;
	int nSamples = 0;
	public double getAvgState() {
		totalState += getStateCount();
		nSamples++;
		return totalState * 1.0d / nSamples;
	}
	

}
