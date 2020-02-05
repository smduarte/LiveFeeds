package feeds.sys.catadupa;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class JoinBatchDB implements Serializable {
	
	public JoinBatchDB() {
	}
	
	private JoinBatchDB( JoinBatchDB other ) {
		data.putAll( other.data ) ;
	}

	public JoinBatchDB clone() {
		return new JoinBatchDB( this ) ;
	}
	
	public void merge( JoinBatchDB other ) {
		data.putAll( other.data ) ;
	}

	public void store( JoinBatch b ) {
		data.put( b.stamp, b) ;	
		gc() ;
 	}
	
	public Map<Stamp, JoinBatch> data() {
		return Collections.unmodifiableMap( data ) ;
	}
	
	public Set<JoinBatch> sets( Map<Long, Set<Integer>> sm ) {
		Set<JoinBatch> res = new HashSet<JoinBatch>() ;
		for( Map.Entry<Long, Set<Integer>> i : sm.entrySet() ) {
			Long key = i.getKey() ;
			for( int j : i.getValue() ) {
				JoinBatch r = data.get( new Stamp( key, j ) ) ;
				if( r != null )
					res.add( r ) ;
			}
		}
		return res ;
	}
	
	public void gc() {
		if( data.size() > 50 ) {
			Stamp newest = data.lastKey() ;
			for( Iterator<Stamp> i = data.keySet().iterator() ; i.hasNext() ; ) {
				if( newest.serial - i.next().serial > 50 )
					i.remove() ;
			}
		}
	}
	
	public SortedMap<Stamp, JoinBatch> data = new TreeMap<Stamp, JoinBatch>() ;
	
	private static final long serialVersionUID = 1L;
}
