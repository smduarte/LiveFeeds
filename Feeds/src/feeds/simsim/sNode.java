package feeds.simsim;

import feeds.sys.*;
import feeds.simsim.sys.*;
import feeds.sys.binding.*;
import feeds.sys.registry.*;
import feeds.sys.membership.*;

public class sNode extends SS_sNode {

	public static NodeDB<sNode> db = new NodeDB<sNode>() ;
	
	public sNode() {
		super( pNode.db.randomNode());
		index = db.store(this) ;
	}

	public void initNode() {

		FeedsRegistry.put("DataURL", url());
		FeedsRegistry.put("BindingURLs", server.url());
		FeedsRegistry.put("/Local/System/Binding/Acceptors", this.url(), FeedsRegistry.SOFTSTATE);
		FeedsRegistry.put("/Local/System/Binding/DataTransfer", this.url(), FeedsRegistry.SOFTSTATE);
		
		BindingService.start();
		RegistryService.start();
		MembershipService.start();
		Binding.start();
	}
}
