package sensing.persistence.core.query;

import groovy.lang.Binding;

import sensing.persistence.core.Service;
import sensing.persistence.core.ServicesConfig;
import sensing.persistence.core.ServiceManager;
import sensing.persistence.core.network.*;
import sensing.persistence.core.pipeline.*;
import sensing.persistence.core.vtable.*;
import sensing.persistence.core.pipeline.Tuple;
import simsim.gui.geom.Rectangle;
import static sensing.persistence.core.logging.LoggingProvider.*;

public abstract class QueryService extends Service {
	

	static double NetworkStabPeriod = 0;
	static double TreeTransitionPeriod = 0;
	static double TreeRebuildPeriod = 0;
	
	private static Map queryImpl = [:];
	Map queryRegistry =[:];
	protected static Map queryListeners = [:]; 
	protected int dsOutputCount;
	protected  totalProcessedTuples = [:];
	
	static {
		queryImpl[ServicesConfig.QueryImplPolicy.QUAD_TREE] = "sensing.persistence.core.query.quadtree.QTQueryService";
		queryImpl[ServicesConfig.QueryImplPolicy.RND_TREE]  = "sensing.persistence.core.query.rndtree.RTQueryService";
		queryImpl[ServicesConfig.QueryImplPolicy.NEAREST_TREE]  = "sensing.persistence.core.query.ntree.NTQueryService";
		queryImpl[ServicesConfig.QueryImplPolicy.CENTRALIZED]  = "sensing.persistence.core.query.centralized.CQueryService";
	}
	
	protected QueryService(ServiceManager services) {
		super(services);
		
		// Query  message handlers
		services.network.addMessageHandler {
			Peer src, Query q ->  handleQuery(src, q);
		}
		services.network.addMessageHandler {
			Peer src, QueryData d ->  if(queryRegistry[d.queryId]) handleQueryData(src, d);
		}
		
		services.network.addMessageHandler {
			Peer src, QueryResult r ->  if(queryRegistry[r.queryId]) handleQueryResult(src, r);
		}
		
		services.network.addMessageHandler {
			Peer src, QueryControl qCtrl ->  if(queryRegistry[qCtrl.queryId]) handleQueryControl(src, qCtrl);
		}
		
		services.network.addMessageHandler {
			Peer src, FailureNotify f ->  handleFailureNotify(f.queryId, f.data);
		}
	}
	
	public static QueryService newQueryService(ServiceManager services, ServicesConfig.QueryImplPolicy policy) {
		return Class.forName(queryImpl[policy]).newInstance(services);
	}
	
	/* runQuery - called by Application Layer to start a new query
	 * q - query specification
	 * listener - the closure that will consume query results
	 */
	public runQuery(Query q, Closure cListener, boolean direct = false) {
		if(!queryRegistry[q.id]) {
			queryRegistry[q.id] = [query: null, contexts: [:]];
			queryListeners[q.id] = cListener;
			runQuery(q, direct);
		}
		// Schedule tree rebuild
		if(TreeRebuildPeriod) setupTreeRebuild(q.id, TreeRebuildPeriod);
	}
	
	public void closeQuery(Query q) {
		// TODO: check if query was started by this node
		QueryControl qCtrl = new QueryControl(queryId: q.id, cmd: QueryControl.Cmd.CLOSE);
		handleQueryControl(null, qCtrl);
		queryListeners.remove(queryId);
	}
	
	public List getQueryInstances() {
		ArrayList instances = new ArrayList();
		instances.addAll(queryRegistry.values())
		return  instances;
	}
	
	public abstract boolean isTupleBounded(QueryContext qc, Tuple tuple);
	
	public boolean isTupleBounded(QueryContext qc, EOS eos) {false}
	
	
	//	public  boolean isTupleBounded(QueryContext qc, signal) {
	//		return false
	//	}
	
	/* 
	 * returns list of candidate peers for data delivery i.e. Peers responsible for
	 * space partition containing x,y
	 */
	public abstract getDataDestinationPeers(double x, double y);
	
	protected abstract void runQuery(Query q, boolean direct = false);
	
	/*
	 * Message handling
	 */
	protected abstract void handleQuery(Peer src, Query q);
	
	protected void handleQueryData(Peer src, QueryData qd) {
		if(queryRegistry[qd.queryId] && queryRegistry[qd.queryId].contexts?."$qd.context") {
			qd.data.each { tuple ->
				queryRegistry[qd.queryId].contexts[qd.context].pipeline.input(tuple);	
				incrTotalProcessedTuples(qd.context);
			}
		}
	}
	
	protected abstract void handleQueryResult(Peer src, QueryResult qr);
	
	protected void handleQueryControl(Peer src, QueryControl qCtrl) {
		
		queryRegistry[qCtrl.queryId]?.contexts.each{ entry ->
			entry.value.children?.each{ peer ->
				if(peer != services.network.local)
					services.network.send(peer, qCtrl);
			}		
		}
		switch(qCtrl.cmd) {
			case QueryControl.Cmd.CLOSE: disposeQuery(qCtrl.queryId);
		}
	}
	
	protected void disposeQuery(String queryId) {
		queryRegistry[queryId]?.contexts.each{  entry ->
			entry.value.pipeline.each { pipeline -> pipeline.dispose()}
		}
		services.sensor.removeListeners(queryId);
		queryRegistry.remove(queryId);		
	}
	
	protected void notifyQueryResult(String qId, result) {
		queryListeners[qId]?.call(result);
	}
	
	
	protected Binding getPipelineBinding(QueryContext qc) {
		Binding b = new Binding();
		b.setVariable("services", services);
		b.setVariable("querycontext", qc);
		b.setVariable("peercontext", services.network.local);
		return b;
	}
	
	protected getQueryRegister(String queryId) {
		return queryRegistry[queryId];
	}
	
	boolean isGlobalAggregator = false;
	
	protected void registerQueryContext(QueryContext qc) {
		if(!queryRegistry[qc.query.id]) {
			queryRegistry[qc.query.id] = [query : qc.query, contexts: [:]];
		}
		if(!queryRegistry[qc.query.id].query) {
			queryRegistry[qc.query.id].query = qc.query;
		}
		queryRegistry[qc.query.id].contexts[qc.context] = qc;
		if(qc.context == VTableDefinition.DATASRC) {
			qc.pipeline.prioritary = true;
		} else {
			isGlobalAggregator = true;
		}
	}
	
	protected void setupSensorInput(QueryContext qc) {
		qc.vtable.sensorInput.each{sensorClass ->
			services.sensor.addListener(sensorClass, qc.query.id) { reading ->
				boolean queryeval = qc.query.eval(reading);
				services.logging.log(DEBUG, this, "setupSensorInput", "got $reading - eval: $queryeval - aoi: ${qc.query.aoi}");
				
				if(queryeval) {
					qc.pipeline.input(reading);
					incrTotalProcessedTuples(qc.context);
				}
				return queryeval;
			}				
		}		
	}
	
	
	protected VTableDefinition getVTableDefinition(String vtableName) {
		return services.vtable.getVTableDefinition(vtableName);		
	}
	
	/*
	 * Kill all queries queries - used for simulation
	 * 
	 */
	
	public void killQueries() {
		queryRegistry.keySet().each{queryId -> disposeQuery(queryId)}
	}
	
	/*
	 * Error handling
	 *
	 */
	
	protected static treeFailed = [:]; //TODO - query tree version?
	
	protected  void handleQueryTreeError(Peer dst, String queryId) {
		handleNodeFailure(dst);
		if(!treeFailed[queryId]) { 
			println "### [${services.scheduler.currentTime()}] Failure detected"
			treeFailed[queryId] = true;
			setupTreeRebuild(queryId, QueryService.NetworkStabPeriod);
		}
	}
	
	protected void handleNodeFailure(Peer p) {}
		
	protected void setupTreeRebuild(String queryId, double period, boolean direct = false) {
		println "### [${services.scheduler.currentTime()}] scheduling rebuild in $period secs"
		services.scheduler.scheduleOnce(period) {
			println "### [${services.scheduler.currentTime()}] Rebuilding aggregation tree"
			Peer oldRoot = services.network.getPeer(queryRegistry[queryId].query.rootId);
			Peer root  = null;
			while(!root || root == oldRoot) {
				root = services.network.randomPeer();
			}
			//println "NEW ROOT: ${root.nodeId}"
			rebuildTree(queryId, root, direct);
		}
	}
	
	
	protected void rebuildTree(String queryId, Peer root, boolean direct) {
		Query q = queryRegistry[queryId].query;
		services.network.broadcast(new FailureNotify(queryId));
		root.services.query.runQuery(new Query(q.vtableName, q.aoi), queryListeners[q.id], direct);
	}
	
	protected handleFailureNotify(String queryId, data) {
		if(!queryRegistry[queryId]) return;
		//tempQueryRegistry[queryId] = queryRegistry[queryId];
		//queryRegistry.remove(queryId);
		services.scheduler.scheduleOnce(TreeTransitionPeriod) {
			//			tempQueryRegistry[queryId].contexts.each{  entry ->
			//				entry.value.pipeline.each { pipeline -> pipeline.dispose()}
			//			}
			//			services.sensor.removeListeners(tempQueryRegistry[queryId][VTableDefinition.DATASRC]);
			//			tempQueryRegistry.remove(queryId);
			//println "### [${services.scheduler.currentTime()}] Disposing failed query"
			disposeQuery(queryId);
		}
		
	}
	
	/*
	* PeerDB versioning
	*
	*/
	public void notifyNewPeerDB(int versionN) {}
	public void notifyDeletedPeerDB(int version) {}
	
	
	
	/*
	 * Monitoring
	 * 
	 */
	
	protected void incrTotalProcessedTuples(String context) {
		if(totalProcessedTuples[context] == null) {
			totalProcessedTuples[context] = 1;
		} else {
			totalProcessedTuples[context]++;
		}
	}
	
	public int getTotalProcessedTuples(filter) {
		Set contexts = totalProcessedTuples.keySet();
		if(filter) {
			contexts = contexts.intersect(filter);
		}
		return contexts.inject(0){total, context -> total += totalProcessedTuples[context]}
	}
	
	public int getStateCount(filter){
		return getState(filter).size();
	}
	
	public List getDSWindowContent() {
		List content = [];
		queryRegistry.values().each{ qi ->
			if(qi.contexts[VTableDefinition.DATASRC]) {
				content.addAll(qi.contexts[VTableDefinition.DATASRC].pipeline.getWindowContent())
			}
		}
		return content
	}	
	
	public getState(filter) {
		def result = [];
		queryRegistry.values().each{ qi ->
			def contexts = qi.contexts.keySet();
			if(filter) {
				contexts = contexts.intersect(filter);
			}
			contexts.each{contextKey -> result += qi.contexts[contextKey].pipeline.getState()}
		}
		return result.unique();
	}
	
	public int getProcessedTuples(filter) {
		int result = 0;
		queryRegistry.values().each{ qi ->
			def contexts = qi.contexts.keySet();
			if(filter) {
				contexts = contexts.intersect(filter);
			}
			contexts.each{contextKey -> result += qi.contexts[contextKey].pipeline.getProcessedTuples()}
		}
		return result;
	}
	
	//	public int getProcessCount() {
	//		int result = 0;
	//		queryRegistry.values().each{ qi ->
	//			qi.contexts.values().each{context -> result += context.pipeline.getProcessCount()}
	//		}		
	//		return result;
	//	}
	
	public int getDSOutputCount() {
		return dsOutputCount;
	}
	
	
	
}
