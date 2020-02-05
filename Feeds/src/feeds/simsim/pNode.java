package feeds.simsim;

import feeds.sys.*;
import feeds.sys.binding.*;
import feeds.sys.backbone.*;
import feeds.sys.registry.*;
import feeds.sys.membership.*;

import feeds.simsim.sys.*;

public class pNode extends SS_pNode {

	public static NodeDB<pNode> db = new NodeDB<pNode>() ;

	public pNode() {
		index = db.store(this) ;
	}
	
	public void initNode() {
		String NodesDB = "";

		for ( SS_pNode i : db.randomNodes(this, 3))
			NodesDB += i.url() + ";";

		FeedsRegistry.put("/Local/System/Binding/Acceptors", url(), FeedsRegistry.SOFTSTATE);
		FeedsRegistry.put("/Local/System/Binding/DataTransfer", url(), FeedsRegistry.SOFTSTATE);

		FeedsRegistry.put("/Local/System/Backbone/NodesDB", NodesDB, FeedsRegistry.SOFTSTATE);
		FeedsRegistry.put("/Local/System/Backbone/DataTransfer", url(), FeedsRegistry.SOFTSTATE);

		BindingService.start();
		RegistryService.start();
		BackboneServices.start();
		MembershipService.start();
	}
}
