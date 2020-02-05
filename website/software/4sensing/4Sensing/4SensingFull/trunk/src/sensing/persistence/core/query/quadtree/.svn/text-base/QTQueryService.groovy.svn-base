package sensing.persistence.core.query.quadtree;

import static sensing.persistence.core.query.quadtree.QTConstants.*;
import sensing.persistence.core.*;
import sensing.persistence.core.query.*;
import sensing.persistence.core.network.*;
import static sensing.persistence.core.logging.LoggingProvider.*;
import sensing.persistence.core.pipeline.*;
import sensing.persistence.core.vtable.*;
import sensing.persistence.core.pipeline.Tuple;

//TODO: layer violation - rectangle implementation should be independent from simulator
import simsim.gui.geom.*;
import java.awt.geom.*;
import java.util.List;
import java.util.Map;

public class QTQueryService extends QueryService {
	
	static int minBranch = -1;
	static int maxBranch = 0;
	static int totalBranch = 0;
	static int nBranch = 0;
	static int oneBranch = 0;
	static int maxLevel = 0;
	
	def static getBranchStat() {
		return [min: minBranch, max: maxBranch, n: nBranch, avg: totalBranch*1.0/nBranch, one: oneBranch, maxLevel: maxLevel];
	}

	class DSRouter extends Router {
		public DSRouter(QueryContext qc) {
			super(qc);
		}
		
		public Tuple process(Tuple tuple) {
			dsOutputCount++;
			sendData(tuple, 'global', qc.parent);
		}
	}

	class GARouterComplete extends Router {
		public GARouterComplete(QueryContext qc) {
			super(qc);
		}
		
		public Tuple process(Tuple tuple) {
			if(qc.parent) {
				if(isTupleBounded(qc, tuple)) {
					// send result upstream
					sendResult(tuple, qc.parent) 
				} else {
					// send to upper level
					sendData(tuple, 'global', qc.parent);
				}
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
			if(tuple.isResult || isTupleBounded(qc, tuple)) {
				notifyQueryResult(qc.query.id, tuple);
			} else {
				sendData(tuple, 'global', qc.parent);
			} 
		}
	}
	
	public QTQueryService(ServiceManager services) {
		super(services);
		
		services.network.addErrorHandler {
			Peer dst, QueryData d ->  handleQueryTreeError(dst, d.queryId);
		}
		
		services.network.addErrorHandler {
			Peer dst, QueryResult r ->  handleQueryTreeError(dst, r.queryId);
		}
		
	}

	public boolean isTupleBounded(QueryContext qc, Tuple tuple) {
		services.logging.log(DEBUG, this, "isTupleBounded", "testing: ${tuple.class.name}");	
		if(!qc.parent) return true; // no more data available if at root
		if(tuple.boundingBox) {
			boolean contains = qc.query.quadrant.contains(tuple.boundingBox);
			if(contains) {
				services.logging.log(DEBUG, this, "isTupleBounded", "${tuple} with extent ${tuple.boundingBox} is bounded by ${qc.query.quadrant}");
			} else {
				services.logging.log(DEBUG, this, "isTupleBounded", "${tuple} with extent ${tuple.boundingBox} is not bounded by ${qc.query.quadrant}");
			}
			return contains;
		} else {
			//TODO: this assumes that all point data produced by a pipeline
			// is included on the query quadrant - should be true
			services.logging.log(DEBUG, this, "isTupleBounded", "${tuple} has no extent");
			return true;
		}
	}

	public getDataDestinationPeers(double x, double y) {
		return services.network.minQuadRange(x, y);
	}

	protected void runQuery(Query q, boolean direct = false) {
		QTQuery qtQuery = new QTQuery(q, services.config.world, services.network.local.id);
		setupQuery(null, qtQuery, direct);
	}

	/*
	 * Message handling
	 */
	protected  void handleQuery(Peer src, Query q, boolean direct = false) {
		if(q.asLeaf) { 
			setupQueryAsLeaf(src, q, direct);
		} else {
			setupQuery(src, q, direct);
		}	
	}


	protected void handleQueryResult(Peer src, QueryResult qr){
		QueryContext qc = queryRegistry[qr.queryId].contexts.global;
		if(qc.parent) {
			services.network.send(qc.parent, qr);
		} else {
			qr.data.each{ tuple -> 
				notifyQueryResult(qc.query, tuple);
			}
		}
	}


	
	protected void setupQuery(Peer src, QTQuery q, boolean direct = false) {
//		if(direct && queryRegistry[q.id].global) { 
//			queryRegistry[q.id].query = q;
//			queryRegistry[q.id].global.query = q;
//			queryRegistry[q.id].global.src = src;
//			queryRegistry[q.id].global.level =  q.ancestors.size();
//		} else {
			VTableDefinition vd = getVTableDefinition(q.vtableName);
			QueryContext qc = new QueryContext(query: q,
											   context: 'global',
											   vtable: vd,
					  						   parent: src, 
					  						   level: q.ancestors.size());
	
			Pipeline p =  services.vtable.newPipelineFrom(vd, vd.getStage(VTableDefinition.DATASRC).nextStage, getPipelineBinding(qc));
			qc.pipeline = p;
			if(services.config.completeMode) {
				p.addComponent(new GARouterComplete(qc));
			} else {
				p.addComponent(new GARouter(qc));
			}
			registerQueryContext(qc);	
			p.init();
//		}
		distributeQuery(q, direct);
	}

	protected void setupQueryAsLeaf(Peer src, QTQuery q, boolean direct = false) {
//		if(direct && queryRegistry[q.id][VTableDefinition.DATASRC]) {
//			queryRegistry[q.id].query = q;
//			queryRegistry[q.id][VTableDefinition.DATASRC].query = q;
//			queryRegistry[q.id][VTableDefinition.DATASRC].src = src;
//			queryRegistry[q.id][VTableDefinition.DATASRC].level =  q.ancestors.size();
//		} else {		
			VTableDefinition vd = getVTableDefinition(q.vtableName);
			QueryContext qc = new QueryContext(query: q,
												context: VTableDefinition.DATASRC,
												vtable: vd,
												parent: src,
												level: q.ancestors.size());
			Pipeline p =  services.vtable.newPipeline(vd, VTableDefinition.DATASRC, getPipelineBinding(qc));
			qc.pipeline = p;
			p.addComponent(new DSRouter(qc));
			registerQueryContext(qc);
			setupSensorInput(qc);
			p.init();
			
			maxLevel = qc.level > maxLevel ? qc.level : maxLevel;
//		}
	}

	
	protected void distributeQuery(QTQuery q, boolean direct = false) {
		def toSend = [];
		buildSendList(q, toSend);
		// register all children before sending query
		queryRegistry[q.id].contexts.global.children.addAll(toSend.collect{it[0]});
		int branch = toSend.size();
		oneBranch += (branch == 1) ? 1 : 0; 
		minBranch = (minBranch == -1) ? branch : Math.min(minBranch, branch);
		maxBranch = Math.max(maxBranch, toSend.size());
		totalBranch += toSend.size();
		nBranch++;
		toSend.each{
			def (peer, query) = it;
			if(direct) {
				//println "sending query directly to ${peer.nodeId}"
				peer.services.query.handleQuery(services.network.local, query, direct);
			} else {
				services.network.send(peer, query);
			}
		}
	}
	
	protected void buildSendList(QTQuery q, toSend) {
		def subQuads = getQuadDiv(q.quadrant);
		subQuads.each { quad ->
			Rectangle qi = new Rectangle(0,0,0,0);
			Rectangle.intersect(quad, q.searchArea, qi);	
			if(!qi.isEmpty()) { 
				List inRangePeers = services.network.range(qi) - services.network.local; 
				if(inRangePeers.size() < MIN_NODES_PER_QUAD) { // not an autonomous area, send as leaf to all
					toSend.addAll(distributeQueryToLeafs(q, quad, qi));
//				} else if(inRangePeers.contains(services.network.local)) { // is autonomous and contains self - assume aggregation role
//					QTQuery subQ = new QTQuery(q, quad);
//					buildSendList(subQ, toSend);
				} else {
//					def candidatePeers =  (inRangePeers - q.ancestors) - services.network.local;
//					println services.network.local
					def candidatePeers =inRangePeers.findAll { peer -> peer != services.network.local && !q.ancestors.find{ancestor -> peer.id == ancestor}};
					//assert(candidatePeers == inRangePeers.findAll { peer -> peer != services.network.local && !q.ancestors.find{ancestor -> peer.id == ancestor}});
					if(candidatePeers.size() > 0) {
						toSend.addAll(distributeQuery(q, quad, qi, candidatePeers));
					} else {
						toSend.addAll(distributeQueryToLeafs(q, quad, qi));
					}
				}
			}
		}		
	}

	protected List distributeQuery(QTQuery q, Rectangle quad, Rectangle interception, peers) {
		List result = [];
		queryRegistry[q.id].contexts.global.quadIntercept << interception;
		Peer p = peers[services.random.nextInt(peers.size())];
		QTQuery subQ = new QTQuery(q, quad);
		subQ.ancestors << services.network.local.id;
		result << [p, subQ];
		return result;
	}
	

	protected List distributeQueryToLeafs(Query q, Rectangle quad, Rectangle interception) {
		List peers = services.network.range(interception);
		return peers.collect{ distributeQueryToLeaf(q, quad, it);}
	}

	protected List distributeQueryToLeaf(Query q, Rectangle quad, Peer p) {
		QTQuery subQ = new QTQuery(q, quad, true);
		//services.network.send(peer, subQ);
		return [p, subQ];
	}
	

	public static List getQuadDiv(Rectangle r) {
		double hSide = r.width/2;
		double vSide = r.height/2;
		Rectangle q1 = new Rectangle(new Point2D.Double(r.x,r.y), new Point2D.Double(hSide,vSide));
		Rectangle q2 = new Rectangle(new Point2D.Double(r.x+hSide,r.y), new Point2D.Double(hSide,vSide));
		Rectangle q3 = new Rectangle(new Point2D.Double(r.x+hSide,r.y+vSide), new Point2D.Double(hSide,vSide));
		Rectangle q4 = new Rectangle(new Point2D.Double(r.x,r.y+vSide), new Point2D.Double(hSide,vSide));
		return [q1, q2, q3, q4];
	}
	

}
