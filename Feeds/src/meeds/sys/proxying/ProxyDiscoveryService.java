package meeds.sys.proxying;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import meeds.api.Meeds;
import meeds.sys.MeedsNode;
import meeds.sys.homing.Position;
import meeds.sys.homing.containers.Location;
import feeds.api.Feeds;
import feeds.api.Payload;
import feeds.api.Receipt;
import feeds.api.Subscriber;
import feeds.api.Subscription;
import feeds.sys.FeedsNode;
import feeds.sys.FeedsRegistry;
import feeds.sys.core.Container;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;


public class ProxyDiscoveryService {

	public static final double RADIUS = 100 ;
	
	String dataTransfer = "";

	Subscription handler;

	public void init() {
		try {
			try {

				final Location loc = Container.byClass( Location.class ) ;

				if (MeedsNode.isCnode()) {
					String acceptorUrls = FeedsRegistry.get("/Local/System/Meeds/Proxying/Acceptors");
					List<Transport> incomings = new ArrayList<Transport>();
					Scanner s = new Scanner(acceptorUrls).useDelimiter(";");
					while (s.hasNext()) {
						Transport t = FeedsNode.openTransport(s.next(), "incoming").open();
						incomings.add(t);
						dataTransfer += t.url() + ";";
					}
					
					MeedsNode.pdc().subscribe( new RadiusFilter( loc.pos(), RADIUS), new Subscriber<Position, ID>() {
						public void notify(Receipt r, Position e, Payload<ID> p) {
							MeedsNode.pdc().feedback(r, p.data(), new ProxyInfo(loc.pos(), RADIUS, dataTransfer )) ;
						}
					});
				}			
			} catch (Exception x) {
				Feeds.out.println("ProxyDiscoveryService service aborted.\nTrouble opening transports.\n[" + x.getMessage() + "]\n");
				x.printStackTrace();
				return;
			}

		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	static public void start() {
		new ProxyDiscoveryService().init();
	}
}

