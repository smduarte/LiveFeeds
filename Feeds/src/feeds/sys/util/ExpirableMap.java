package feeds.sys.util;

import java.util.*;

import feeds.api.*;
import feeds.sys.tasks.*;

public class ExpirableMap<K,V> implements Map<K,V> {

	public ExpirableMap( double sweepPeriod) {
		this( Double.MAX_VALUE, sweepPeriod, new HashMap<K,V>(), null) ;
	}
	
	public ExpirableMap( double sweepPeriod, ExpirableMapListener<K,V> listener) {
		this( Double.MAX_VALUE, sweepPeriod, new HashMap<K,V>(), listener) ;
	}
	
	public ExpirableMap( double expiration, double sweepPeriod ) {
		this( expiration, sweepPeriod, new HashMap<K,V>(), null) ;
	}
	
	public ExpirableMap( double sweepPeriod, double expiration, ExpirableMapListener<K,V> listener) {
		this( expiration, sweepPeriod, new HashMap<K,V>(), listener) ;
	}
	
	public ExpirableMap( double sweepPeriod, Map<K,V> m, ExpirableMapListener<K,V> listener) {
		this( Double.MAX_VALUE, sweepPeriod, m, listener ) ;
	}
	
	public ExpirableMap( double sweepPeriod, double expiration, Map<K,V> m, ExpirableMapListener<K,V> listener) {
		init( sweepPeriod) ;
		this.data = m ;
		this.expiration = expiration ;
		if( listener != null )
			this.listener = listener ;
	}
	
	public V put(K key, V value) {
		deadlines.put( key, Feeds.time() + expiration ) ;
		return data.put( key, value) ;
	}

	public void putAll(Map<? extends K, ? extends V> t) {
		data.putAll(t) ;
		for( K i : t.keySet() )
			deadlines.put( i, Feeds.time() + expiration ) ;
	}

	public V put(K key, V value, double expiration ) {
		deadlines.put( key, Feeds.time() + expiration) ;
		return data.put( key, value) ;
	}

	public void putAll(Map<? extends K, ? extends V> t, double expiration ) {
		data.putAll(t) ;
		for( K i : t.keySet() )
			deadlines.put( i, Feeds.time() + expiration ) ;
	}

	public void clear() {
		data.clear() ;
		deadlines.clear();
	}

	public boolean containsKey(Object key) {
		return data.containsKey( key) ;
	}


	public boolean containsValue(Object value) {
		return data.containsValue(value) ;
	}


	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return data.entrySet() ;
	}

	public V get(Object key) {
		return data.get( key ) ;
	}

	public boolean isEmpty() {
		return data.isEmpty() ;
	}

	public Set<K> keySet() {
		return data.keySet() ;
	}

	
	public V remove(Object key) {
		return data.remove( key ) ;
	}

	public int size() {
		return data.size() ;
	}


	public Collection<V> values() {
		return data.values() ;
	}
	
	private void init( double sweepPeriod ) {		
		new PeriodicTask(0, sweepPeriod ) {
			public void run() {
				double now = Feeds.time() ;
				
				ArrayList<K> expiredKeys = new ArrayList<K>() ;
				for( Map.Entry<K, Double> i : deadlines.entrySet() )
					if( i.getValue() < now )
						expiredKeys.add( i.getKey() ) ;
					
					for( K i : expiredKeys )
						listener.keyExpired( data, i, data.get(i)) ;

					data.keySet().removeAll( expiredKeys ) ;
					deadlines.keySet().removeAll( expiredKeys ) ;
				}
		};
	}

	
	protected Map<K, V> data ;
	final protected double expiration ;
	protected Map<K, Double> deadlines = new HashMap<K, Double>() ;
	
	protected ExpirableMapListener<K,V> listener = new ExpirableMapListener<K,V>() {
		public void keyExpired(Map<K,V> m, K key, V value) {}
	};
	
	public String toString() {
		return data.toString() ;
	}
}
