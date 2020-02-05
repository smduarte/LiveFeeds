package feeds.sys.catadupa;

import java.io.*;
import java.util.*;

import feeds.api.Feeds;

public class oView implements Serializable {

	Map<Long, SlidingIntSet> data = new HashMap<Long, SlidingIntSet>();

	public oView() {}
	
	private oView( oView other ) {
		for( Map.Entry<Long, SlidingIntSet> i : other.data.entrySet() ) {
			data.put( i.getKey(), i.getValue().clone() ) ;
		}
	}
	
	public oView clone() {
		return new oView( this ) ;
	}
	
	public void add(Stamp s) {
		set(s.key).set(s.serial);
	}

	private SlidingIntSet set(Long key) {
		SlidingIntSet res = data.get(key);
		if (res == null) {
			res = new SlidingIntSet();
			data.put(key, res);
		}
		return res;
	}
	
	public Map<Long, Set<Integer>> getMissingBatches(gView gv ) {
		return getMissingBatches( gv, Integer.MAX_VALUE ) ;
	}
	
	public Map<Long, Set<Integer>> getMissingBatches(gView gv, int max ) {
		Map<Long, Set<Integer>> res = new HashMap<Long, Set<Integer>>();
		for (Map.Entry<Long, Integer> i : gv.data.entrySet()) {
			Long key = i.getKey() ;
			int val = i.getValue() ;
			SlidingIntSet o = set(key);
			Set<Integer> r = new TreeSet<Integer>() ;
			for( int j = o.base ; j <= val && max > 0 ; j++ )
				if( ! o.get(j) ) {
					r.add(j) ;
					max-- ;
				}
			
			if( ! r.isEmpty() )
				res.put( key, r ) ;
		}
		return res;
	}

	public boolean contains( oView other ) {
		for( Map.Entry<Long, SlidingIntSet> i : other.data.entrySet() ) {
			SlidingIntSet tSet = data.get( i.getKey() ) ;
			if( tSet == null || ! tSet.contains( i.getValue() ))
					return false ;
		}
		return true ;
	}
	
	public int missingBatches( gView gv ) {
		int res = 0 ;
		
		for (Map.Entry<Long, Integer> i : gv.data.entrySet()) {
			Long key = i.getKey() ;
			int val = i.getValue() ;
			SlidingIntSet o = set(key);
			for( int j = o.base ; j <= val ; j++ )
				if( ! o.get(j) ) res++ ;
		}		
		return res ;
	}
	
	
	public void dump() {
		for (Map.Entry<Long, SlidingIntSet> i : data.entrySet()) {
			Feeds.out.println(i.getKey() + " -> " + i.getValue());
		}
	}

	public String toString() {
		return data.toString() ;
	}
	
	public int size() {
		return data.size() ;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
