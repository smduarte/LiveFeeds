package meeds.sys.proxying;

import meeds.sys.MeedsNode;
import meeds.sys.proxying.containers.Proxy;
import feeds.api.FeedbackSubscriber;
import feeds.api.Payload;
import feeds.api.Receipt;
import feeds.sys.FeedsNode;
import feeds.sys.core.Container;
import feeds.sys.tasks.Task;
import feeds.sys.transports.containers.DefaultIncomingTransport;

public class ProxyBinding {
	double keepAlivePeriod = 100 ;

	public void start() {

		if (!FeedsNode.isPnode()) {

			final Proxy.Updater pxu = Container.byClass(Proxy.class);
			final DefaultIncomingTransport dit = Container.byClass(DefaultIncomingTransport.class);

			new Task(30.0) {
				public void run() {
					MeedsNode.pxc().publish( new ProxyBindingRequest(dit.url()));
					this.reSchedule( keepAlivePeriod ) ;
				}
			};

			MeedsNode.pxc().subscribeFeedback(new FeedbackSubscriber<Void, ProxyBindingReply>() {
				public void notifyFeedback(Receipt r, Void e, Payload<ProxyBindingReply> p) {
					if( ! r.isLocal() ) { 
						ProxyBindingReply reply = p.data();
						keepAlivePeriod = reply.keepAlivePeriod();							
						pxu.put( reply );
					}
				}
			});
		}

	}
}
