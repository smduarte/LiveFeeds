package feeds.sys.backbone;

import java.util.*;

import feeds.api.*;
import feeds.sys.*;
import feeds.sys.core.*;
import feeds.sys.util.*;
import feeds.sys.tasks.*;
import feeds.sys.backbone.containers.*;

public class HelloService {

	private void init() {
		try {
			final BackboneNodes.Updater bnu = Container.byClass(BackboneNodes.class);

			// Handle newly found nodes
			Container.monitor( BackboneNodes.class, new ContainerListener<BackboneNodes>() {
				public void handleContainerUpdate(BackboneNodes bn) {
					HashSet<ID> newNodes = new HashSet<ID>(bn.newNodes());
					newNodes.remove(thisNode);
					scheduler.scheduleAll(newNodes, Scheduler.HIGH_PRIORITY);
				}
			});

			ownURL = new DelimitedString((String) FeedsRegistry.get("/Local/System/Backbone/DataTransfer"), ";").items()[0];
			Transport incoming = FeedsNode.openTransport(ownURL, "incoming").open();
			ownURL = incoming.url();

			bnu.put( ownURL, false) ;
			bnu.putAll((String) FeedsRegistry.get("/Local/System/Backbone/NodesDB"), false);
			
			final Channel<ID, HelloRequest, Long, HelloReport> hc = Feeds.lookup("/System/HelloChannel");
			hc.setOutputRate(16.0, false) ;
			
			// Handle HelloRequests
			hc.subscribe(new Subscriber<ID, HelloRequest>() {
				public void notify(final Receipt r, final ID target, final Payload<HelloRequest> p) {
					if (!r.isLocal()) {
						HelloRequest req = p.data();
						hc.feedback(r, 1L, new HelloReport(thisNode, req));
						scheduler.reSchedule( req.src());
						bnu.put( req.url(), true);
					}
				}
			});

			// Handle Ping/Pong HelloReports.
			hc.subscribeFeedback(new FeedbackSubscriber<Long, HelloReport>() {
				public void notifyFeedback(final Receipt r, final Long v, final Payload<HelloReport> p) {

					HelloReport report = p.data();
					links.updateCost(report.src(), report.rtt());
					links.add(new Link<ID>(thisNode, report.src(), report.rtt()));
					updateRequestCounter(report.src(), -1);

					scheduler.reSchedule( report.src(), report.rtt() );

					//System.out.printf(thisNode + ">>>>Got: %dst phase %s\n", v, report);

					if (v == 1L) {
						hc.feedback(r, 2L, new HelloReport(thisNode, report));
					}
				}
			});

			new PeriodicTask(0.1) {
				public void run() {
					ID target = scheduler.next();
					if (target != null && target != thisNode) {
						hc.publish(target, new HelloRequest(target, ownURL));
						updateRequestCounter(target, 1);
						this.reSchedule( hc.getOutputRateDelay() ) ;
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------
	Map<ID, Integer> reqCounters = new HashMap<ID, Integer>();

	private void updateRequestCounter( ID targetNode, int delta) {
		try {
			int value = reqCounters.get(targetNode);
			if (delta > 0) {
				if (value > 3) {
					value = 0;
					System.out.println(FeedsNode.id() + " >>>" + targetNode + " is unreacheable");
				} else
					value++;
			} else
				value = 0;
			reqCounters.put(targetNode, value);
		} catch (NullPointerException npe) {
			reqCounters.put(targetNode, -10);
		}
	}

	static void start() {
		new HelloService().init();
	}

	private String ownURL;
	final private ID thisNode = FeedsNode.id();
	final private Scheduler<ID> scheduler = new Scheduler<ID>();
	final private NetworkLinks links = Singleton.get(NetworkLinks.class);
}