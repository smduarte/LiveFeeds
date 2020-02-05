package sensing.persistence.core.query.rndtree;

import sensing.persistence.core.ServiceManager;
import sensing.persistence.core.query.Query;
import sensing.persistence.core.query.Router;
import sensing.persistence.core.query.QueryContext;
import sensing.persistence.core.query.QueryData;
import sensing.persistence.core.query.QueryResult;
import sensing.persistence.core.query.QueryService;
import sensing.persistence.core.query.FailureNotify;
import sensing.persistence.core.query.quadtree.QTQuery;
import sensing.persistence.core.network.*;
import sensing.persistence.core.pipeline.*;
import sensing.persistence.core.vtable.*;
import sensing.persistence.core.pipeline.Tuple;

public class RTQueryService extends QueryService {
	
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



	class GARouterComplete extends Router {
		
		public GARouterComplete(QueryContext qc) {
			super(qc);
		}
		
		public Tuple process(Tuple tuple) {
			if(qc.parent) {
				sendData(tuple, 'global', qc.parent);
			} else {
				notifyQueryResult(qc.query.id, tuple);
			}		
		}
	}

	/*
	 * GARouter does not require a result to be complete to send to root
	 * It assumes a publish/subscribe network for result propagation
	 * TODO: how to determine that a tuple is a result? Currently, only
	 * the outcome of a classify operation is a result.
	 * Optionally, tuple types can be compared against the query listener
	 * input type.
   */
	
	class GARouter extends Router {
		
		public GARouter(QueryContext qc) {
			super(qc);
		}
		
		public Tuple process(Tuple tuple) {
			if(tuple.isResult || !qc.parent) {
				notifyQueryResult(qc.query.id, tuple);
			} else  {
				sendData(tuple, 'global', qc.parent);
			}
		}
	}

	public RTQueryService(ServiceManager services) {
		super(services);
		
		services.network.addErrorHandler {
			Peer dst, QueryData d ->  handleQueryTreeError(dst, d.queryId);
		}
		
		services.network.addErrorHandler {
			Peer dst, QueryResult r ->  handleQueryTreeError(dst, r.queryId);
		}
	}

	public boolean isTupleBounded(QueryContext qc, Tuple tuple){
		return (!qc.parent); // true only if is root
	}

	public getDataDestinationPeers(double x, double y) {
		return [[services.network.local], services.config.world];
	}

	protected void runQuery(Query q, boolean direct = false) {
		RTQuery rq =  new RTQuery(q, services.network.local.id);
		setupQuery(null, rq, direct);
	}



	protected void setupQuery(Peer src, RTQuery q, boolean direct = false) {
		VTableDefinition vd = getVTableDefinition(q.vtableName);
		QueryContext qcg = new QueryContext(query: q,
				context: 'global',
				vtable: vd,
				nextCtx: null,
				parent: src, 
				level: q.level);
		Pipeline pg =  services.vtable.newPipelineFrom(vd, vd.getStage(VTableDefinition.DATASRC).nextStage, getPipelineBinding(qcg));
//		pg.addComponent(new Processor({ Tuple tuple ->
//			if(qcg.parent) {
//				sendPipelineOutput(qcg, tuple);
//			} else {
//				notifyQueryResult(qcg.query, tuple);
//			}		
//		}))
		qcg.pipeline = pg;
		if(services.config.completeMode) {
			pg.addComponent(new GARouterComplete(qcg));
		} else {
			pg.addComponent(new GARouter(qcg));
		}
		
		registerQueryContext(qcg);

		QueryContext qcl = new QueryContext(query: q,
				   context: VTableDefinition.DATASRC,
				   vtable: vd,
				   nextCtx: 'global',
				   parent: src, 
				   level: q.level);
		Pipeline pl =  services.vtable.newPipeline(vd, VTableDefinition.DATASRC, getPipelineBinding(qcl));
		//pl.addComponent(new Processor({ Tuple tuple ->pg.input(tuple); dsOutputCount++}));
		qcl.pipeline = pl;
		pl.addComponent(new DSRouter(pg));
		registerQueryContext(qcl);
		setupSensorInput(qcl);
		pg.init();
		pl.init();
		
		distributeQuery(q, direct);
	}

	protected void distributeQuery(RTQuery q, boolean direct = false) {
		RTQuery subQ = new RTQuery(q);
		queryRegistry[q.id].contexts.global.children = services.network.children(q.rootId, q.aoi);
		//if(queryRegistry[q.id].contexts.global.children == null) {println services.network.local.nodeId}
		
		queryRegistry[q.id].contexts.global.children.each{ peer ->
			if(direct) {
				peer.services.query.handleQuery(services.network.local, subQ, direct);
			} else {
				services.network.send(peer, subQ);
			}
		}
	}

	
	/*
	 * Message handling
	 */
	protected void handleQuery(Peer src, Query q, boolean direct = false) {
		setupQuery(src, q, direct);
	}
	

	// not used in RTree
	protected void handleQueryResult(Peer src, QueryResult r) {}
	
/*	
	protected void notifyFailure(Peer p, int level, UUID queryId, UUID rootId) {
		services.network.sendDirect(p, new FailureNotify(queryId, [rootId, level]));
		def children = services.network.children(rootId, queryRegistry[queryId].query.aoi, p);
		children.each{notifyFailure(it, (level+1), queryId, rootId)}
	}
	
	
	protected void rebuildTree(UUID queryId, Peer root) {
		notifyFailure(root, 0, queryId, root.id);
		//services.network.broadcast(new FailureNotify(queryId, root.id));
	}
	
	protected handleFailureNotify(UUID queryId, data) {
		def (rootId, level) = data;
		//println "[${services.network.local.nodeId}] received FailureNotify - ${queryRegistry[queryId].query} - ${queryRegistry[queryId].contexts.global.parent}";
		queryRegistry[queryId].contexts.global.children = services.network.children(rootId, queryRegistry[queryId].query.aoi);
		queryRegistry[queryId].contexts.global.parent = services.network.parent(rootId, queryRegistry[queryId].query.aoi);
		queryRegistry[queryId].contexts.global.level = level;
		queryRegistry[queryId].contexts.global.pipeline.reset();
	}
*/
}
