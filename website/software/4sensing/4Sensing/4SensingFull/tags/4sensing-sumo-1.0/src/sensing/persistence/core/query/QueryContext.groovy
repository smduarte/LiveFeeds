package sensing.persistence.core.query;

import sensing.persistence.core.pipeline.Pipeline;
import sensing.persistence.core.vtable.VTableDefinition;
import sensing.persistence.core.network.Peer;
import sensing.persistence.core.network.PeerDB;

public class QueryContext {
	Query query;
	String mainQueryId; // versioning
	Pipeline pipeline;
	String context;
	VTableDefinition vtable;
	String nextCtx;
	Peer parent;
	List children = [];
	PeerDB peerDB;
	int level;
	//TODO:  for snap-shot query cleanup:
	int EOScount = 0;
	//TODO: debug info
	List quadIntercept = [];
	
}
