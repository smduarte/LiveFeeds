package meeds.simsim.sys;

import feeds.sys.core.*;
import feeds.sys.tasks.*;
import feeds.simsim.sys.*;

import static simsim.core.Simulation.*;

abstract public class SS_FixedNode extends SS_Node {
	
	protected SS_Node server;
	public static SS_NodeDB<SS_FixedNode> db = new SS_NodeDB<SS_FixedNode>() ;
	
	protected SS_FixedNode(SS_sNode server, NodeContext context) {
		super( context ) ;
		
		db.store( this ) ;
		this.server = server;

		double mpl = SS_pNode.meanLatency();
		while (address.latency(server.address) > 0.15 * mpl || address.latency(server.address) < 0.002 * mpl) {
			address = Network.createAddress(this);
			endpoint = address.endpoint;
			mpl *= 1.01 ;
		}
	}

	public void init() {
		super.init() ;
		
		new Task(2000.0 + 30 * rg.nextDouble() ) {
			public void run(){
				context.makeCurrent() ;
				initNode() ;				
			}
		};
	}
}
