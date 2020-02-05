package feeds.sys.backbone;

import java.util.*;

import feeds.api.*;
import feeds.sys.*;
import feeds.sys.core.*;
import feeds.sys.tasks.*;
import feeds.sys.graphs.*;
import feeds.sys.transports.*;
import feeds.sys.backbone.containers.*;

import simsim.graphs.*;

public class LinkStateService {
	
	private void init() {
		try {
			final BackboneNodes bn = Container.byClass(BackboneNodes.class);
			final BackboneNodes.Updater bnu = Container.byClass(BackboneNodes.class);
			final RoutingTables.Updater rtc = Container.byClass(RoutingTables.class);

			ownURL = new Url((String) FeedsRegistry.get("/Local/System/Backbone/DataTransfer")).url();

			final Channel<SpanningTreeEncoding, Void, Void, Object> lsc = Feeds.lookup("/System/LinkStateChannel");
			lsc.setOutputRate(4.0, false) ;
			
			lsc.subscribe( new Subscriber<SpanningTreeEncoding, Void>() {
				public void notify(final Receipt r, final SpanningTreeEncoding ste, final Payload<Void> p) {
					try {
						if (!r.isLocal()) {
							DiscoveryRequest request = new DiscoveryRequest(ownURL);
							for (ID i : ste.nodes())
								if (!bn.isKnown(i))
									request.add(i);
							
							if (!request.isEmpty()) {
								lsc.feedback(r, request);
							}
							nl.add(ste);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			lsc.subscribeFeedback( new FeedbackSubscriber<Void, Object>() {
				public void notifyFeedback(final Receipt r, final Void v, final Payload<Object> p) {
					try {
						Object o = p.data();

						if (o instanceof DiscoveryRequest) {
							DiscoveryReport report = new DiscoveryReport();
							DiscoveryRequest request = (DiscoveryRequest) o;

							bnu.put(request.url(), true);
							Map<ID, Transport> transports = bn.transports();

							for (ID i : request.list()) {
								Transport t = transports.get(i);
								if (t != null)
									report.add(t.url());
							}

							if (!report.isEmpty()) {
								lsc.feedback(r, report);
							}
						} else if (o instanceof DiscoveryReport) {
							DiscoveryReport report = (DiscoveryReport) o;
							bnu.putAll(report.list(), true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			new PeriodicTask(10, 10.0) {
				public void run() {
					try {	
						if (nl.size() > 0) {
							// SpanningTreeEncoding ste = new kSpanner( 2f, nl
							// ).spanningTree( thisNode ).encoding( thisNode ) ;
							SpanningTree<ID> st = new MinimumSpanningTree<ID>( nl.nodes(), nl.links());
							SpanningTreeEncoding ste = new SpanningTreeEncoding( thisNode, st);
							nl.replaceTreeCosts(ste);							
							lsc.publish(ste, null);
							
							rtc.update(ste);
							this.reSchedule( lsc.getOutputRateDelay() ) ;
						}
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
			};
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	static void start() {
		new LinkStateService().init();
	}

	private String ownURL;
	final private ID thisNode = FeedsNode.id();
	final private NetworkLinks nl = Singleton.get(NetworkLinks.class);
}
