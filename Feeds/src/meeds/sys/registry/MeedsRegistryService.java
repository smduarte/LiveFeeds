package meeds.sys.registry;

import feeds.api.*;
import feeds.sys.*;
import feeds.sys.core.*;
import feeds.sys.registry.*;
import static feeds.sys.FeedsRegistry.*;

import meeds.sys.*;

public class MeedsRegistryService {

	public void init() {
		try {

			MeedsNode.rqc().subscribe(new Subscriber<String, Void>() {
				public void notify(final Receipt r, final String key, final Payload<Void> mev) {

					Feeds.out.println("Meeds Got Query for >" + key + " from " + r.source());

						RegistryItem ri = FeedsRegistry.getItem(key);

						if (ri != null)
							// We can satisfy the query, just reply to the
							// source of the query
							MeedsNode.rqc().feedback( r, null, ri);
						else
							// We cannot. Ask somebody else.
							FeedsNode.rqc().publish( key, null );
						}
				});

			// Handle incoming RegistryItems produced by queries.
			MeedsNode.rqc().subscribeFeedback(new FeedbackSubscriber<Void, RegistryItem>() {
				public void notifyFeedback(final Receipt r, final Void v, final Payload<RegistryItem> p) {
					if (!r.isLocal()) {
						RegistryItem ri = p.data();
						Feeds.out.println("Meeds Caching..." + ri.key() + "}");

						if (!ri.isExpired() && !thisNode.equals(ri.creator())) {
							RegistryItem oi = FeedsRegistry.getItem(ri.key());
							if (oi == null || (oi != null && ri.newer(oi)))
								FeedsRegistry.putItem(ri, FeedsNode.isServer() ? HARDSTATE : SOFTSTATE);
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------
	public static void start() {
		new MeedsRegistryService().init();
	}

	// -------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------
	private final ID thisNode = FeedsNode.id();
}
