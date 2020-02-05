package meeds.simsim.tests.latency;

import umontreal.iro.lecuyer.stat.Tally;
import feeds.api.*;
import meeds.api.*;
import meeds.simsim.tests.Probe;
import feeds.sys.tasks.*;

public class Latency1 {

	public static void sendProbes(int nodeIndex) {

		final Channel<Integer, Probe, Integer, Void> ch = Meeds.lookup("/testChannel");
		new PeriodicTask(3500, 1) {
			int counter = 0;
			public void run() {
				Feeds.err.println("Publishing..." + counter );
				ch.publish(0, new Probe(counter++));
			}
		};
	}

	static Tally averageLatency = new Tally("averageLatency");
	static Tally averageInstantLatency = new Tally("averageInstantLatency");

	public static void recvProbes(int nodeIndex) {
		if (nodeIndex > 10)
			return;

		final Tally lI = new Tally("LatestInterval");

		final Channel<Integer, Probe, Void, Void> ch = Meeds.lookup("/testChannel");
		ch.subscribe(0, new Subscriber<Integer, Probe>() {
			public void notify(Receipt r, Integer e, Payload<Probe> p) {
				Probe probe = p.data();
				double delay = Feeds.time() - probe.time;
				if (delay < 30) {
					averageLatency.add(delay);
					lI.add(delay);
				}
			}
		});
		
		new PeriodicTask(30) {
			public void run() {
				if (lI.numberObs() > 2) {
					averageInstantLatency.add( lI.average() ) ;
					lI.init() ;
					if(averageLatency.numberObs() > 2)
						Feeds.err.println(averageLatency.reportAndCIStudent(0.95));
					if(averageInstantLatency.numberObs() > 2)
						Feeds.err.println(averageInstantLatency.reportAndCIStudent(0.95));
				}
			}
		};
	}	
}
