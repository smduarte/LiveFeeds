package meeds.simsim;

import feeds.api.*;
import feeds.sys.*;
import feeds.sys.tasks.*;
import feeds.sys.binding.*;
import feeds.sys.registry.*;
import feeds.sys.membership.*;
import feeds.sys.core.Container;

import feeds.simsim.*;

import meeds.api.Meeds;
import meeds.sys.homing.*;
import meeds.sys.tunnel.*;
import meeds.sys.proxying.*;
import meeds.sys.registry.*;
import meeds.sys.directory.*;
import meeds.sys.homing.containers.Location;

import meeds.simsim.sys.*;
import meeds.simsim.tests.latency.*;
import meeds.simsim.tests.loss.PacketLoss;

public class ProxyNode extends SS_ProxyNode {

	public static NodeDB<ProxyNode> db = new NodeDB<ProxyNode>();

	public ProxyNode() {
		super(sNode.db.randomNode());
		index = db.store(this);
	}

	public void initNode() {

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

		if (index == 0)
			Meeds.clone("catadupa", "/testChannel");
		else
			Feeds.sleep(100);

		new Task(100) {
			public void run() {
				//Latency1.recvProbes(index) ;
				//PacketLoss.recvProbes(index) ;
			}
		};
	}

//	Map<Object, SlidingIntSet> rmap = new HashMap<Object, SlidingIntSet>();
//	Map<Object, SlidingIntSet> dmap = new HashMap<Object, SlidingIntSet>();
//	int received = 0, duplicated = 0;
//	int missed = 0;
//
//	static Tally rcv = new Tally("Received");
//	static Tally dup = new Tally("Duplicated");
//	static Tally mis = new Tally("Missed");
//
//	void go() {
//
//		if (index > 0)
//			return;
//
//		final Channel<Integer, Probe, Void, Void> ch = Meeds.lookup("/testChannel");
//
//		ch.subscribe(new Subscriber<Integer, Probe>() {
//			public void notify(Receipt r, Integer e, Payload<Probe> p) {
//				Probe probe = p.data();
//
//				SlidingIntSet ds = dmap.get(probe.src);
//				SlidingIntSet rs = rmap.get(probe.src);
//				if (ds == null || rs == null) {
//					rmap.put(probe.src, (rs = new SlidingIntSet()));
//					dmap.put(probe.src, (ds = new SlidingIntSet()));
//				}
//
//				rs.set(probe.seqN);
//				if (ds.get(probe.seqN))
//					ds.set(probe.seqN);
//
//				Feeds.out.println( probe.src + ":" + ds);
//				lat.add(Feeds.time() - probe.time);
//			}
//		});
//
//		new PeriodicTask(240, 121) {
//			public void run() {
//				for (SlidingIntSet i : rmap.values()) {
//					int missed = 0;
//					int B = i.base();
//					for (int j = i.base(); j < B + 64; j++) {
//						if (!i.get(j))
//							missed++;
//
//						i.set(j);
//					}
//				}
//
//				rcv.report();
//				dup.report();
//			}
//		};
//	}
	
	
	
}
