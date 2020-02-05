package meeds.simsim.tests.loss;

import java.awt.Color;
import java.util.*;

import simsim.gui.charts.*;

import feeds.api.*;
import feeds.sys.core.*;
import feeds.sys.tasks.*;
import feeds.sys.catadupa.*;

import meeds.api.*;
import meeds.simsim.tests.*;

public class PacketLoss {

	public static void sendProbes(int nodeIndex) {
		if (nodeIndex > 5)
			return;

		final Channel<Integer, Probe, Integer, Void> ch = Meeds.lookup("/testChannel");
		new PeriodicTask(100, 1) {
			int counter = 0;

			public void run() {
				ch.publish(0, new Probe(counter++));
			}
		};
	}


	public static double total = 0 ;
	public static double duplicated = 0;
	public static double l_lost, l_total = 0;
	public static double d_discarded = 0, d_total = 0;

	public static void recvProbes(final int nodeIndex) {
		
		new Task(0) {
			
			final Map<ID, Integer> rcvMap = new HashMap<ID, Integer>();
			final Map<ID, SlidingIntSet> smap = new HashMap<ID, SlidingIntSet>();
			final Map<ID, Set<Integer>> rmap = new HashMap<ID, Set<Integer>>();
			final Map<ID, List<Integer>> dmap = new HashMap<ID, List<Integer>>();

			public void run() {
			

				if (nodeIndex > 10)
					return;
				final Channel<Integer, Probe, Void, Void> ch = Meeds.lookup("/testChannel");

				ch.subscribe(0, new Subscriber<Integer, Probe>() {
					public void notify(Receipt r, Integer e, Payload<Probe> p) {
						l_total++;
						Probe probe = p.data();
						SlidingIntSet ss = smap.get(probe.src);
						Set<Integer> rs = rmap.get(probe.src);
						List<Integer> ds = dmap.get(probe.src);
						if (ss == null) {
							smap.put(probe.src, (ss = new SlidingIntSet(2)));
							rmap.put(probe.src, (rs = new HashSet<Integer>()));
							dmap.put(probe.src, (ds = new ArrayList<Integer>()));
						}
//						if (ss.get(probe.seqN))
//							duplicated++;
						
						if (rs.contains(probe.seqN))
							duplicated++;

//						rs.add(probe.seqN);
//						ds.add(probe.seqN);
						ss.set(probe.seqN);

//						Feeds.err.println(ss);
						Integer received = rcvMap.get(probe.src);
						received = received == null ? probe.seqN : Math.max(received, probe.seqN);
						rcvMap.put(probe.src, received);

//						Feeds.out.println("rs=" + rs.size());
//						Feeds.out.println("ds=" + ds.size());
					}
				});

				new PeriodicTask(30) {
					public void run() {
						for (ID j : smap.keySet()) {
							int received = rcvMap.get( j) ;
							SlidingIntSet k = smap.get(j);
							for (int i = k.base(); i < received - 32; i++) {
								if (!k.get(i))
									l_lost++;
								k.set(i);
							}
							System.out.println( k ) ;
						}
					}
				};
			}
		};

		if (nodeIndex == 0) {
			final XYLineChart chart = new XYLineChart("Accumulated Packet Statistics", 1, "lost messages (%)", "time (h)");
			chart.setSeriesLinesAndShapes("Misses", true, false);
			chart.setSeriesLinesAndShapes("Duplicated", true, false);
			chart.chart().setAntiAlias(true);
			chart.chart().removeLegend();
			chart.chart().setBackgroundPaint(Color.white);
			new PeriodicTask(30) {
				double T0 = 0;

				public void run() {
					double T = (Feeds.time() - T0) / 3600.0;
					if (l_total > 0) {
						chart.getSeries("Misses").add(T, 100 * l_lost / l_total);
					} else
						T0 = Feeds.time();
				}
			};
		}
	}
}
