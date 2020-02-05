package feeds.sys.registry;

import static feeds.sys.FeedsRegistry.HARDSTATE;
import static feeds.sys.FeedsRegistry.SOFTSTATE;

import java.util.Iterator;
import java.util.Set;

import feeds.api.FeedbackSubscriber;
import feeds.api.Feeds;
import feeds.api.Payload;
import feeds.api.Receipt;
import feeds.api.Subscriber;
import feeds.sys.FeedsNode;
import feeds.sys.FeedsRegistry;
import feeds.sys.core.ID;
import feeds.sys.tasks.Task;

/**
 * Note: This version uses key hashing...for query routing purposes...
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */
public class RegistryService {

	public void init() {
		try {
			final String regOp = FeedsNode.isServer() ? " Storing:{" : " Caching:{";

			if (FeedsNode.isServer()) {

				FeedsNode.rqc().setOutputRate(16.0, false);
				FeedsNode.rrc().setOutputRate(16.0, false);

				// Handle incoming RegistryQueries
				FeedsNode.rqc().subscribe(new Subscriber<String, Void>() {
					public void notify(final Receipt r, final String key, final Payload<Void> mev) {


						RegistryItem ri = FeedsRegistry.getItem(key);
						Feeds.out.println("Got Query for >" + key + " from " + r.source() + (ri==null?" <miss>":" <hit>"));

						if (ri != null)
							// We can satisfy the query, just reply to the
							// source of the query
							FeedsNode.rqc().feedback( r, null, ri);
						else
							// We cannot. Ask somebody else.
							FeedsNode.rqc().publish( key, null );
						}
				});

				// Handle incoming Replicated RegistryItems
				FeedsNode.rrc().subscribe(new Subscriber<String, RegistryItem>() {
					public void notify(final Receipt r, final String key, final Payload<RegistryItem> p) {
						RegistryItem ri = p.data() ;
						//feeds.api.Feeds.out.println("Got RegistryItem:" + ri + " from " + r.source());
						if (ri != null && !ri.isExpired()) {
							RegistryItem oi = FeedsRegistry.getItem(ri.key());
							if (oi == null || (oi != null && ri.newer(oi))) {
								ri.takeOwnership();
								FeedsRegistry.putItem(ri, HARDSTATE);
								Feeds.out.println(regOp + ri.key() + "}");
							}
						}
					}
				});
			}
			// Handle incoming RegistryItems produced by queries.
			FeedsNode.rqc().subscribeFeedback(new FeedbackSubscriber<Void, RegistryItem>() {
				public void notifyFeedback(final Receipt r, final Void v, final Payload<RegistryItem> p) {
					if (!r.isLocal()) {
						RegistryItem ri = p.data();
						Feeds.out.println(regOp + ri.key() + "}");

						if (!ri.isExpired() && !thisNode.equals(ri.creator())) {
							RegistryItem oi = FeedsRegistry.getItem(ri.key());
							if (oi == null || (oi != null && ri.newer(oi)))
								FeedsRegistry.putItem(ri, FeedsNode.isServer() ? HARDSTATE : SOFTSTATE);
						}
					}
				}
			});

			//
			// Main loop task. Disseminate contents of local registry.
			//
			if (FeedsNode.isPnode() && false)
				new Task(0) {
					public void run() {
						RegistryItem item = selectItem();
						if (item != null) {
							FeedsNode.rrc().publish(item.key, item);
						}
						this.reSchedule(FeedsNode.rrc().getOutputRateDelay());
					}
				};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	RegistryItem selectItem() {
		RegistryItem item = null;
		try {
			if (iterator.hasNext())
				item = iterator.next();
			else
				iterator = null;
		} catch (Exception x) {
			iterator = FeedsRegistry.values("/Global", HARDSTATE).iterator();
			if (iterator.hasNext())
				return selectItem();
		}
		if (item != null && item.isExpired()) {
			iterator.remove();
			return selectItem();
		}
		return item;
	}

    static public ID hashesTo( Set<ID> ids, String s ) {
    	ID res = null ;
    	long v = s.hashCode() << 31 ^ s.hashCode() ;
    	for( ID i : ids )
    		if( res == null || (i.longValue() ^ v) < (res.longValue() ^ v ))
    				res = i ;
    	
    	return res ;
    }

	// -------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------
	public static void start() {
		new RegistryService().init();
	}

	// -------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------
	private final ID thisNode = FeedsNode.id();
	private Iterator<RegistryItem> iterator = null;
}
