package meeds.simsim;

import feeds.sys.*;
import feeds.sys.core.*;
import feeds.sys.tasks.*;
import feeds.sys.binding.*;
import feeds.sys.registry.*;
import feeds.sys.membership.*;

import feeds.simsim.*;

import meeds.simsim.sys.*;
import meeds.simsim.tests.latency.Latency1;
import meeds.sys.tunnel.*;
import meeds.sys.homing.*;
import meeds.sys.proxying.*;
import meeds.sys.registry.*;
import meeds.sys.directory.*;
import meeds.sys.homing.containers.Location;

public class HomebaseNode extends SS_HomebaseNode {

	public static NodeDB<HomebaseNode> db = new NodeDB<HomebaseNode>();

	public HomebaseNode() {
		super(sNode.db.randomNode());
		index = db.store(this);
	}

	public void initNode() {
		FeedsRegistry.put("DataURL", url());
		FeedsRegistry.put("BindingURLs", server.url());

		RegistryService.start();
		MeedsDirectoryStorage.init();
		MembershipService.start();
		Binding.start();

		FeedsRegistry.put("/Local/System/Meeds/Homebase/Acceptors", url());
		FeedsRegistry.put("/Local/System/Meeds/Proxying/Acceptors", url());

		HomingService.start();

		MeedsRegistryService.start();
		TunnelService.start();

		FeedsRegistry.put("DataURL", url());
		FeedsRegistry.put("BindingURLs", server.url());

		RegistryService.start();
		MembershipService.start();
		Binding.start();

		MeedsDirectoryStorage.init();
		MeedsRegistryService.start();

		FeedsRegistry.put("/Local/System/Meeds/Homebase/Acceptors", url());
		FeedsRegistry.put("/Local/System/Meeds/Proxying/Acceptors", url());

		Location.Updater locu = Container.byClass(Location.class);
		locu.set(new Position(new meeds.sys.util.XY(address.pos.x, address.pos.y)));

		ProxyDiscoveryService.start();
		ProxyBindingService.start();
		TunnelService.start();

		new Task(100) {
			public void run() {
				//Latency1.sendProbes(index) ;
			}
		};
	}
}
