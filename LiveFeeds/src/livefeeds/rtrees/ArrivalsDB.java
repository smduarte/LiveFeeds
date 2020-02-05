package livefeeds.rtrees;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import livefeeds.rtrees.msgs.NewArrivals;

public class ArrivalsDB {

	static Set<Long> sequencers = new HashSet<Long>();
	static Map<Integer, NewArrivals> arrivals = new LinkedHashMap<Integer, NewArrivals>();

	static void store(NewArrivals v) {
		arrivals.put(v.stamp.g_serial, v);
		sequencers.add(v.stamp.key);
	}

	static NewArrivals get(Stamp s) {
		return arrivals.get(s.g_serial);
	}

	static NewArrivals get( Integer  s) {
		return arrivals.get(s);
	}

	static void redundancy() {
		Set<Integer> is = new TreeSet<Integer>() ;
		List<Integer> ia = new ArrayList<Integer>() ;
		
		for( NewArrivals i : arrivals.values() ) {
			is.addAll( i.joins ) ;
			ia.addAll( i.joins ) ;
		}
		
		System.out.println( "$$$$$$$$------>" +  ia.size() / (double) is.size() );
	}
	
	static void gc() {
		int mc = minViewCutoff() ;
		System.out.println("Minimum View Cutoff: " + mc);
		for( Iterator<NewArrivals> i = arrivals.values().iterator() ; i.hasNext() ; ) {
			NewArrivals v = i.next() ;
			if (v.stamp.c_serial < mc ) {
				i.remove() ;
				System.out.println("Stamp: " + v.stamp + " expired: " + v.keys );

				for( CatadupaNode j : GlobalDB.liveNodes() ) 
					if( j.state.db.loadedEndpoints )
						j.state.db.store(v) ;

			} //else
				//break;
		}
	}
	
	static void gc(long key) {
		if (sequencers.remove(key)) {
			for (NewArrivals i : arrivals.values()) {
				if (i.stamp.key == key && countViews( i.stamp, 3 ) < 3 ) {
					System.err.println(key + " GC possible missed stamps...") ;
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
		
//	static RandomList<NewArrivals> get(Collection<Stamp> stamps) {
//		RandomList<NewArrivals> res = new RandomList<NewArrivals>();
//		for (Stamp i : stamps) {
//			NewArrivals j = arrivals.get(i);
//			if (j != null)
//				res.add(j);
//		}
//		return res;
//	}
}
