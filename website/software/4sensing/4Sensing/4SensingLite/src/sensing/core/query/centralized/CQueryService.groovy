package sensing.core.query.centralized;

import sensing.core.network.Peer;
import sensing.core.query.Query;
import sensing.core.query.QueryResult;
import sensing.core.query.QueryContext;
import sensing.core.query.QueryService;
import sensing.core.query.QueryData;
import sensing.core.query.Router;
import sensing.core.vtable.VTableDefinition;
import sensing.core.network.Peer;
import sensing.core.pipeline.Pipeline;
import sensing.core.pipeline.Processor;
import sensing.core.pipeline.Tuple;
import sensing.core.ServiceManager;


class CQueryService  extends QueryService {
	
	class ProxyRouter extends Router {
		public ProxyRouter(QueryContext qc) {
			super(qc);
		}
		public Tuple process(Tuple tuple) {
			QueryData qd = new QueryData(queryId: qc.query.id, query: qc.query, context:  VTableDefinition.DATASRC, data: [tuple]);
			services.network.sendDirect(qc.parent, qd);
			sendData(tuple, VTableDefinition.DATASRC, qc.parent);
		}
	}
	
	class DSRouter extends Processor {
		Pipeline pg;
		public DSRouter(Pipeline pg) {
			this.pg = pg;
		}
		public Tuple process(Tuple tuple) {
			pg.input(tuple); 
			dsOutputCount++;
			return null;
		}
	}


	class GARouter extends Processor {
		QueryContext qc;
		public GARouter(QueryContext qc) {
			this.qc = qc;
		}
		public Tuple process(Tuple tuple) {
			notifyQueryResult(qc.query.id, tuple);	
		}
	}

	public CQueryService(ServiceManager services) {
		super(services);
	}
	
	public boolean isTupleBounded(QueryContext qc, Tuple tuple){
		return (!qc.parent); // true only if is the central node
	}
	
	public getDataDestinationPeers(double x, double y) {
		return [[services.network.local], services.config.world]; // always send to proxy node i.e. homebase
	}
	
	protected void runQuery(Query inQ, boolean direct = false) {
		Query q = new Query(inQ);
		q.srcId = services.network.local.id;
		VTableDefinition vd = getVTableDefinition(q.vtableName);
		QueryContext qcg = new QueryContext(query: q,
				context: 'global',
				vtable: vd,
				nextCtx: null,
				parent: null, 
				level: 0);
		Pipeline pg =  services.vtable.newPipelineFrom(vd, vd.getStage(VTableDefinition.DATASRC).nextStage, getPipelineBinding(qcg));
		qcg.pipeline = pg;
		pg.addComponent(new GARouter(qcg));
		registerQueryContext(qcg);

		QueryContext qcl = new QueryContext(query: q,
				   context: VTableDefinition.DATASRC,
				   vtable: vd,
				   nextCtx: 'global',
				   parent: null, 
				   level: 0);
		Pipeline pl =  services.vtable.newPipeline(vd, VTableDefinition.DATASRC, getPipelineBinding(qcl));
		qcl.pipeline = pl;
		pl.addComponent(new DSRouter(pg));
		registerQueryContext(qcl);

		pl.init();	
		pg.init();
		setupQuery(q);
	}
	
	protected void setupQuery(Query q) {
		VTableDefinition vd = getVTableDefinition(q.vtableName);
		QueryContext qcp = new QueryContext(query: q,
				context: 'proxy',
				vtable: vd,
				nextCtx: VTableDefinition.DATASRC,
				parent: services.network.getPeer(q.srcId),
				level: 1);
		Pipeline proxyP = new Pipeline();
		proxyP.context = getPipelineBinding(qcp);
		qcp.pipeline = proxyP;
		proxyP.addComponent(new ProxyRouter(qcp));
		registerQueryContext(qcp);
		setupSensorInput(qcp);
		proxyP.init();
		distributeQuery(q);
	}
	
	protected void distributeQuery(Query q) {
		Query subQ = new Query(q);
		
		def children = services.network.children(q.srcId, q.aoi);
		//if(queryRegistry[q.id].contexts.global.children == null) {println services.network.local.nodeId}
		
		children.each{ peer ->
			services.network.send(peer, subQ);
		}
		
	}
	
	/*
	 * Message handling
	 */
	protected void handleQuery(Peer src, Query q) {
		setupQuery(q);
	}
	

	// not used
	protected void handleQueryResult(Peer src, QueryResult r) {}

}
