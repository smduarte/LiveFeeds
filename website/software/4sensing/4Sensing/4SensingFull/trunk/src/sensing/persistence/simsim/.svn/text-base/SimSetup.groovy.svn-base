package sensing.persistence.simsim;

import java.util.Map;

import sensing.persistence.core.query.Query;

import simsim.core.Task;
import simsim.core.PeriodicTask;
import sensing.persistence.core.query.QueryService;
import sensing.persistence.core.network.NetworkService;
import  sensing.persistence.core.query.quadtree.QTConstants;
import sensing.persistence.simsim.speedsense.SpeedSenseSim;

abstract class SimSetup {
	protected  config = [:];	
	Query q;
	
	PipelineSimulation sim;
	List queryListeners = [];
	List charts = [];
	int queryCount;
	Map queryResult = [:];
	
	public SimSetup() {
		config.TOTAL_NODES = 500;
		config.RUN_TIME = 0;
		config.SIM_UPDATE_INTERVAL = 5;
		config.MIN_NODES_PER_QUAD = 2;
		config.MIN_NODE_DISTANCE = 250;
		config.SIM_SEED = 0L;
		config.NET_SEED = 0L;
		config.NUM_QUERIES = 1;
		config.IDLE_TIME = 0;
	}
	
	protected static Query createQuery(String pipelineName, double centerLat, double centerLon, double width, double height) {
		double minLat = centerLat-height/2;
		double minLon = centerLon-width/2;
		double maxLat = centerLat+height/2;
		double maxLon = centerLon+width/2;
		Query q = new Query(pipelineName).area(minLat: minLat, minLon: minLon, maxLat: maxLat, maxLon: maxLon);	
		println "minlat: ${minLat},  minLon: ${minLon}, maxLat: ${maxLat}, maxLon: ${maxLon}"
		return q;
	}
	
	public void init(PipelineSimulation sim) {
		this.sim = sim;
		QTConstants.MIN_NODES_PER_QUAD = config.MIN_NODES_PER_QUAD;
		QueryService.NetworkStabPeriod = config.FAIL_STAB_PERIOD ? config.FAIL_STAB_PERIOD + (sim.rg.nextDouble() * config.FAIL_STAB_PERIOD) : 0;
		QueryService.TreeTransitionPeriod = config.TREE_TRANSITION_PERIOD ?: 0;
		QueryService.TreeRebuildPeriod = config.TREE_REBUILD_PERIOD ?: 0;	
		NetworkService.LocationChangePeriod = config.NODE_LOCATION_CHANGE_PERIOD ?: 0;
		
		charts = setupCharts();
		charts*.init();
		println "SETUP ${this.class.name}";
		config.each{ key, val -> println "${key}: ${val}"};
		queryCount = 0;
		setupQueryTask();
	}
	
	public void addQueryListener(Closure listener) {
		queryListeners << listener;
	}
	
	
	protected void setupQueryTask() {
		new Task(IDLE_TIME) {
			public void run() {
				startQuery();
				setupKillQueryTask();
			};
		}
	}
	
	protected void setupKillQueryTask() {
		if(RUN_TIME > 0) {
			new Task(RUN_TIME) {
				public void run() {killQueries()}
			}
		}
	}

	protected abstract void startQuery();
	
	
	protected void runQuery(Query q, Closure qListener) {
		this.q = q;
		sim.runQuery(q) { result -> 
			qListener.call(result); 
			queryListeners*.call(result)
		}
	}
		

	protected void killQueries() {
		if(++queryCount < NUM_QUERIES) {
			sim.killQueries();
			q = null;
			charts*.cleanup();
			setupQueryTask();
		} else {
			stop();
		}
	}
	

	public List getResult(String key) {
		List result = queryResult[key];
		if(result) {
			def(tuple, ts) = result;
			if(sim.currentTime() - ts > QUERY_RESULT_VALIDITY) {
				queryResult.remove(key);
				result = null;
			}
		}
		return result;
	}
	
	
	public Map getResultSet() {
		return queryResult.findAll{
			def (t, ts) = it.value
			sim.currentTime()-ts <= QUERY_RESULT_VALIDITY
		}
	}
	
	protected void putResult(String key, t) {
		queryResult[key] = [t, sim.currentTime()];
	}

	public void update() {
		charts*.update();
	}
	
	public void output() {
		charts*.output();
	}
	
	public void stop() {
		println "Stopping sim"
		charts*.stop();
		sim.stop();
	}
	
	
	def getProperty(String name) {
		def prop = config[name];
		prop != null ? prop : this.metaClass.getMetaProperty(name)?.getProperty(this);
	}

	def getConfigKeys() {
		return config.keySet();
	}
	
	
	public getOutputBasePath() {
		return "results/${this.class.name}/run_${sim.runId}/${sim.class.name}/"
	}
	

	protected setupCharts() {
		return null;	
	}
}
