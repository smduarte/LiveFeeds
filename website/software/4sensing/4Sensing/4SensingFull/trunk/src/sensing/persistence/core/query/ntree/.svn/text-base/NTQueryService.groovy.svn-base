package sensing.persistence.core.query.ntree;

import sensing.persistence.core.ServiceManager;
import sensing.persistence.core.network.Peer;
import sensing.persistence.core.query.Query;
import sensing.persistence.core.query.Router;
import sensing.persistence.core.query.QueryContext;
import sensing.persistence.core.query.QueryData;
import sensing.persistence.core.query.QueryResult;
import sensing.persistence.core.query.QueryService;
import sensing.persistence.core.query.rndtree.RTQuery;
import sensing.persistence.core.pipeline.*;
import sensing.persistence.core.vtable.*;
import static sensing.persistence.core.logging.LoggingProvider.*;
import sensing.persistence.core.pipeline.Tuple;

import java.awt.geom.Rectangle2D;

public class NTQueryService extends QueryService {
	
	Map installedQueries = [:];
	
//	class DSRouter extends Processor {
//
//		public process(Tuple tuple) {
//			pg.input(tuple); 
//			dsOutputCount++
//		}
//	}
//	
//	class GARouter extends Router {
//		public GARouter(QueryContext qc) {
//			super(qc);
//		} 
//		
//		public process(Tuple tuple) {
//			if(isTupleBounded(qc, tuple)) {
//				if(qc.nextCtx) { // send to next stage (same node)
//					sendPipelineOutput(qc.nextCtx, tuple);
//				} else { // tuple is a result
//					notifyQueryResult(qc.query, tuple);
//				}
//			} else { // not bounded - send to same context on nearest node
//				sendPipelineOutput(qc.context, tuple);
//			}
//		}
//		
//		protected void sendPipelineOutput(String stage, Tuple output) {
//			def centroid = getTupleCentroid(output);
//			services.logging.log(DEBUG, this, "sendPipelineOutput","centroid: [${centroid.lat}-${centroid.lon}]");
//			if(centroid) {
//				Peer nearest = services.network.nearest(centroid.lon,centroid.lat);
//				if(nearest) {
//					sendData(output, stage, nearest);
//				} else {
//					println "no nearest node for ${output} lat:${centroid.lat} lon:${centroid.lon}"
//				}
//
//			}
//		}
//
//	}
	
	class NTRouter extends Router {
	
			public NTRouter(QueryContext qc) {
				super(qc);
			}

			public Tuple process(Tuple tuple) {
				if(qc.context == VTableDefinition.DATASRC || isTupleBounded(qc, tuple)) {
					if(qc.nextCtx) { // send to next stage 
						sendPipelineOutput(qc.nextCtx, tuple);
					} else { // query result - !nextCtx && (datasrc || bounded)
						notifyQueryResult(qc.mainQueryId, tuple); 
					}
				} else {  // !datasrc && !bounded
					sendPipelineOutput(qc.context, tuple);
				}
				if(qc.context  == VTableDefinition.DATASRC)  dsOutputCount++	
				return null;
			}
			
			
			protected void sendPipelineOutput(String stage, Tuple output) {
				def centroid = getTupleCentroid(output);
				services.logging.log(DEBUG, this, "sendPipelineOutput","centroid: [${centroid.lat}-${centroid.lon}]");
				if(centroid) {
					Peer nearest = qc.peerDB.nearest(centroid.lon,centroid.lat);
					if(nearest) {
						sendData(output, stage, nearest); 
					} else {
						println "no nearest node for ${output} lat:${centroid.lat} lon:${centroid.lon}"
					}

				}
			}
	}

	public NTQueryService(ServiceManager services) {
		super(services);
		
		services.network.addErrorHandler {Peer dst, message ->  }
	}
	
	public void runQuery(Query q, boolean direct = false) {
		//services.logging.log(DEBUG, this, "runQuery","running query");
		//Ver - NTQuery(q, services.network.pDBVersionN, services.network.local.id);
		NTQuery nq = new NTQuery(q, services.network.local.id);
		handleQuery(null, nq);
	}

	public boolean isTupleBounded(QueryContext qc, Tuple tuple) {
		def centroid = getTupleCentroid(tuple);
		if(centroid) {
			Peer nearest = qc.peerDB.nearest(centroid.lon,centroid.lat);
			return (nearest == services.network.local)
		} else {
			println "isTupleBounded - no spatial metadata: ${tuple}";
		}
	}
	
	public getDataDestinationPeers(double x, double y) {
		Peer nearest = services.network.nearest(x, y);
		if(nearest) {
			return [[nearest], null]
		}	else {
			return [null, null];
		}
	}
	
	public List getDataDestinationPeers(Tuple t) {
		Map centroid = getTupleCentroid(t)
		Peer nearest = services.network.nearest(centroid.lon,centroid.lat);
		return [nearest];
	}
	
	protected void setupQuery(Peer src, Query q, String stage) {
		VTableDefinition vd = getVTableDefinition(q.vtableName);
		services.network.localPDBVersions[q.pDBVersionN]
		QueryContext qc = new QueryContext(query: q,
											mainQueryId: q.id.split(":")[0],
											context: stage,
											nextCtx: vd.getStage(stage).nextStage,
											vtable: vd,
											peerDB : services.network.localPDBVersions[q.pDBVersionN] );
		Pipeline p =  services.vtable.newPipeline(vd, stage, getPipelineBinding(qc));
		qc.pipeline = p;
		p.addComponent(new NTRouter(qc));
		registerQueryContext(qc);
		if(stage == VTableDefinition.DATASRC) {
			setupSensorInput(qc);
		}
		p.init();
	}

	protected void distributeQuery(Query q) {
		queryRegistry[q.id].contexts["MainQCtx"].children = services.network.children(q.rootId, q.aoi);
		queryRegistry[q.id].contexts["MainQCtx"].children.each{ peer ->
			//services.logging.log(DEBUG, this, "distributeQuery","sending query to ${peer.nodeId}");
			if(peer) {
				services.network.send(peer, q);
			} else {
				println "null peer at ${services.network.local.id}" 
				queryRegistry[q.id].contexts[VTableDefinition.DATASRC].children.each {
					if(it) {
						print "${it.id} "
					} else {
						print "X "
					}
				}
			}
		}
	}


	protected getTupleCentroid(Tuple tuple) {
		if(tuple.boundingBox) {
			Rectangle2D bb = tuple.boundingBox;
			return [lat: (bb.y + bb.height/2), lon:(bb.x + bb.width/2)]

		}
		if(tuple.lat && tuple.lon) {
			return [lat: tuple.lat, lon: tuple.lon];
		}
		return null;
	}

	
	/*
	 * Message handling
	 */
	protected void handleQuery(Peer src, Query q) {
		//services.logging.log(DEBUG, this, "handleQuery","received query from ${src.nodeId}");
		installedQueries[q.id] = q;
		registerQueryContext(new QueryContext(query: q, context: "MainQCtx")) // dummy context used to store query dissemination path

		setupQuery(src, new NTQuery(q, services.network.pDBVersionN), VTableDefinition.DATASRC);
		distributeQuery(q);
	}
	
	protected void handleQueryData(Peer src, QueryData qd) {
		assert qd.query.pDBVersionN <= services.network.pDBVersionN
		if(!services.network.localPDBVersions[qd.query.pDBVersionN]) return 
		if(!queryRegistry[qd.queryId] || !queryRegistry[qd.queryId].contexts?."$qd.context") {
//				println "unknown query, context: $qd.context : ${queryRegistry[qd.queryId]} ${queryRegistry[qd.queryId].contexts?."$qd.context"}";
//				if(queryRegistry[qd.queryId].contexts)  println "?? ${queryRegistry[qd.queryId].contexts[qd.context]}"
				setupQuery(src, qd.query, qd.context);
		} //else {println "context found $qd.context"}
		super.handleQueryData(src, qd);
	}

	protected void handleQueryResult(Peer src, QueryResult qr) {
		qr.data.each { tuple -> notifyQueryResult(queryRegistry[qr.queryId].query, tuple)}
	}
	
	/*
	 *  PeerDB versioning
	 */
	
	public void notifyNewPeerDB(int versionN) {
		installedQueries.each{queryId, query -> 
			setupQuery(null, new NTQuery(query, versionN), VTableDefinition.DATASRC);
		}
	
	}
	
	
	public void notifyDeletedPeerDB(int version) {
		List toDispose = [];
		queryRegistry.each{queryId, reg ->
			List splitId = queryId.split(":");
			if(splitId.size() > 1 && Integer.parseInt(splitId[1]) == version) {
				toDispose << queryId;
				//println "disposing query $reg.query.vtableName  $reg.query.id ";
			}
		}
		toDispose.each{disposeQuery(it)};
	}

}
