package feeds.sys.binding;

import feeds.api.*;
import feeds.sys.*;
import feeds.sys.core.*;
import feeds.sys.tasks.*;
import feeds.sys.binding.containers.*;
import feeds.sys.transports.containers.*;

public class Binding {
	volatile boolean bound = false;
	private double delay = 0;
	private final double MinDelay = 0.5;
	private final double MaxDelay = 30.0;
	private double keepAlivePeriod = MaxDelay;

	private void doIt() {
		
		final BoundServers bs = Container.byClass(BoundServers.class) ;
		
		if (!FeedsNode.isPnode()) {

			final BoundServers.Updater bsu = Container.byClass(BoundServers.class);
			final DefaultIncomingTransport dit = Container.byClass(DefaultIncomingTransport.class);

			new Task(1.0) {
				public void run() {
					
					FeedsNode.bc().publish(new BindingRequest(dit.url()));
					delay = Math.min(keepAlivePeriod, Math.max(MinDelay, 1.25 * delay));
					if (!FeedsNode.done())
						this.reSchedule(delay);
				}
			};

			FeedsNode.bc().subscribeFeedback(new FeedbackSubscriber<Void, BindingReply>() {
				public void notifyFeedback(Receipt r, Void e, Payload<BindingReply> p) {
					if( ! r.isLocal() ) { 						
						BindingReply reply = p.data();  
						keepAlivePeriod = reply.keepAlivePeriod();
						bsu.put(reply);
						bound = true;
					}
				}
			});

			final String working = ".oOo.";
			// final String working = "/-\|" ;

			int i = 0;
			// final String working = "<^>v" ;
			while (!bound) {
				Feeds.sleep(0.250);
				Feeds.out.print("\rConnecting to server " + working.charAt((i++) % working.length()));
			}			
			//Feeds.out.println("Connecting to server. Done.");
			Feeds.out.println("\nBound to: " + bs.sortedTransports() );
			delay = MaxDelay ;
		}
	}
	
	public static void start() {
		new Binding().doIt() ;
	}
}
