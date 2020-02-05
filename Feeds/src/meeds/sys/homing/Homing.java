package meeds.sys.homing;

import meeds.api.Meeds;
import meeds.sys.MeedsNode;
import meeds.sys.homing.containers.Homebase;
import meeds.sys.homing.containers.HomebaseTargets;
import meeds.sys.homing.containers.Location;
import meeds.sys.proxying.containers.ProxyTargets;
import feeds.api.FeedbackSubscriber;
import feeds.api.Feeds;
import feeds.api.Payload;
import feeds.api.Receipt;
import feeds.sys.FeedsNode;
import feeds.sys.core.Container;
import feeds.sys.tasks.Task;
import feeds.sys.transports.containers.DefaultIncomingTransport;

public class Homing {
	volatile boolean bound = false;
	private double delay = 0;
	private final double MinDelay = 0.5;
	private final double MaxDelay = 30.0;
	private double keepAlivePeriod = MaxDelay;

	public void doIt() {

		final ProxyTargets.Updater ptu = Container.byClass(ProxyTargets.class);
		final Homebase.Updater hbu = Container.byClass(Homebase.class);
		final DefaultIncomingTransport dit = Container.byClass(DefaultIncomingTransport.class);

		final Location loc = Container.byClass( Location.class) ;
		
		new Task(1.0) {
			public void run() {
				MeedsNode.hbc().publish(new HomingRequest( loc.pos(), dit.url()));
				delay = Math.min(keepAlivePeriod, Math.max(MinDelay, 1.25 * delay));
				if (!FeedsNode.done())
					this.reSchedule(delay);
			}
		};

		MeedsNode.hbc().subscribeFeedback(new FeedbackSubscriber<Void, HomingReply>() {
			public void notifyFeedback(Receipt r, Void e, Payload<HomingReply> p) {
				HomingReply reply = p.data();
				keepAlivePeriod = reply.keepAlivePeriod();
				ptu.put( reply.proxy ) ;
				hbu.put(reply);
				bound = true;
			}
		});

		final HomebaseTargets hbt = Container.byClass( HomebaseTargets.class) ;
		while (!bound) {
			Feeds.out.println("Connecting to homebase..." + hbt.servers() );
			Feeds.sleep(0.25);
		}

		final Homebase hb = Container.byClass(Homebase.class);
		Feeds.out.println("I am " + FeedsNode.id() + " homing to:" + hb.sortedTransports());
	}

	public static void start() {
		new Homing().doIt();
	}
}
