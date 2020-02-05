package livefeeds.twister7;

import static simsim.core.Simulation.rg;
import static simsim.logging.Log.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import livefeeds.twister7.msgs.CatadupaCastPayload;

import simsim.core.PeriodicTask;


public class ArrivalsDB {

	static Set<Long> sequencers = new HashSet<Long>();
	static Map<Integer, CatadupaCastPayload> arrivals = new LinkedHashMap<Integer, CatadupaCastPayload>();

	static void store(CatadupaCastPayload v) {
		if( v.stamp != null ) {
			arrivals.put(v.stamp.g_serial, v);
			sequencers.add(v.stamp.key);
		}
	}

	static CatadupaCastPayload get(Stamp s) {
		return arrivals.get(s.g_serial);
	}

	static CatadupaCastPayload get( Integer  s) {
		return arrivals.get(s);
	}

	static void redundancy() {
		Set<Integer> is = new HashSet<Integer>() ;
		List<Integer> ia = new ArrayList<Integer>() ;
		
		for( CatadupaCastPayload i : arrivals.values() ) {
			is.addAll( i.joins ) ;
			ia.addAll( i.joins ) ;
		}		
		Log.finest( String.format("Arrivals Redundancy: %.1f%%", 100 * ia.size() / (double) is.size())) ;
	}
	
	static void gc() {
		int mc = minViewCutoff() ;
		Log.finest("Minimum View Cutoff: " + mc);
		for( Iterator<CatadupaCastPayload> i = arrivals.values().iterator() ; i.hasNext() ; ) {
			CatadupaCastPayload v = i.next() ;
			if (v.stamp.c_serial < mc ) {
				i.remove() ;
				Log.fine("Stamp: " + v.stamp + " expired: " + v.keys );

				for( CatadupaNode j : GlobalDB.liveNodes() ) 
					if( j.state.db.loadedEndpoints )
						j.state.db.store(v) ;

			} //else
				//break;
		}
	}
	
	static void gc(long key) {
		if (sequencers.remove(key)) {
			for (CatadupaCastPayload i : arrivals.values()) {
				if (i.stamp.key == key && countViews( i.stamp, 3 ) < 3 ) {
					Log.warning(key + " GC possible missed stamps...") ;
					for (CatadupaNode j : GlobalDB.liveNodes()) 
					if( j.state.db.loadedEndpoints ) 
						j.state.db.store( i ) ;					
				}
			}			
		}		
	}
	
	private static int minViewCutoff() {
		int res = Integer.MAX_VALUE ;
		for (CatadupaNode j : GlobalDB.liveNodes()) {
			if (j.isOnline() && j.state.joined && j.state.db.view.cutoff < res ) 
				res = j.state.db.view.cutoff ;
		}

		return res ;
	}

	private static int countViews( Stamp s, int max ) {
		int res = 0 ;
		for (CatadupaNode j : GlobalDB.liveNodes())
			if (j.isOnline() && j.state.joined && j.state.db.view.contains(s) && ++res > max ) 
				break ;
		
		return res ;
	}
		
	public static void init() {
		
		new PeriodicTask(300.0 + rg.nextDouble() ) {
			public void run() {
				ArrivalsDB.gc();	
				ArrivalsDB.redundancy() ;
				
//				System.out.println(CatadupaNode.avg_repairPeriod.report());
//				System.out.println(CatadupaNode.avg_repairItems.report());
				
				
//				if( CatadupaNode.g_deadNodeDetection.numberObs() > 2 )
//					System.out.printf( "----------------%.2f %.2f\n", 1.0 / CatadupaNode.g_deadNodeDetection.average(), CatadupaNode.g_deadNodeDetection.numberObs() / currentTime() );
							
			}
		};
	}
}
