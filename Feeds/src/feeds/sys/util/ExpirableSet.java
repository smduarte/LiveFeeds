package feeds.sys.util;

import java.util.*;

import feeds.api.*;
import feeds.sys.tasks.*;

public class ExpirableSet<T> implements Set<T> {

	final private double expiration ;

	public ExpirableSet( double expiration, double sweepPeriod ) {
		this( expiration, sweepPeriod, null ) ;
	}
	
	public ExpirableSet( double expiration, double sweepPeriod, ExpirableCollectionListener<T> listener ) {
		this( expiration, sweepPeriod, new HashSet<T>(), listener) ;
	}
	
	public ExpirableSet( double sweepPeriod, Collection<T> c, ExpirableCollectionListener<T> listener) {
		this( Double.MAX_VALUE, sweepPeriod, new ArrayList<T>(), listener) ;
	}
	
	public ExpirableSet( double expiration , double sweepPeriod, Collection<T> c, ExpirableCollectionListener<T> listener) {
		init( sweepPeriod) ;
		this.addAll(c) ;
		if( listener != null ) 
			this.listener = listener ;
		this.expiration = expiration ;
	}
	
	public boolean add(T o) {
		boolean res = deadlines.containsKey(o) ;
		deadlines.put( o, Feeds.time() + expiration ) ;
		return res ;
	}

	public boolean addAll(Collection<? extends T> v) {
		boolean res = false ;
		for( T i : v ) {
			res |= add(i) ;
		}
		return res ;
	}

	public boolean add(T o, double expiration) {
		boolean res = deadlines.containsKey(o) ;
		deadlines.put( o, Feeds.time() + expiration ) ;
		return res ;
	}

	public boolean addAll(Collection<? extends T> v, double expiration) {
		boolean res = false ;
		for( T i : v ) {
			res |= add(i, expiration) ;
		}
		return res ;
	}
	
	public void clear() {
		deadlines.clear() ;
	}

	public boolean contains(Object o) {
		return deadlines.keySet().contains(o) ;
	}

	public boolean containsAll(Collection<?> c) {
		return deadlines.keySet().containsAll(c);
	}

	public boolean isEmpty() {
		return deadlines.isEmpty() ;
	}

	public Iterator<T> iterator() {
		return deadlines.keySet().iterator() ;
	}

	public boolean remove(Object o) {
		boolean res = deadlines.containsKey(o) ;
		deadlines.remove(o) ;
		return res ;
	}

	public boolean removeAll(Collection<?> v) {
		return deadlines.keySet().removeAll(v) ;
	}

	public boolean retainAll(Collection<?> c) {
		return deadlines.keySet().retainAll(c) ;
	}

	public int size() {
		return deadlines.size() ;
	}

	public Object[] toArray() {
		return deadlines.keySet().toArray() ;
	}

	public <Q> Q[] toArray(Q[] a) {
		return deadlines.keySet().toArray( a) ;
	}
	
	public String toString() {
		return deadlines.keySet().toString() ;
	}
	
	private void init( double sweepPeriod ) {		
		new PeriodicTask(0, sweepPeriod) {
			public void run() {
				double now = Feeds.time() ;
				for( Iterator<Map.Entry<T, Double>> i = deadlines.entrySet().iterator() ; i.hasNext() ;) {
					Map.Entry<T, Double> e = i.next() ;
					if( e.getValue() < now ) {
						i.remove() ;
						try {
							listener.valueExpired(deadlines.keySet(), e.getKey()) ;
						} catch( Exception x ){
							x.printStackTrace() ;
						}
					}
				}
			}
		};
	}

	protected Map<T, Double> deadlines = new HashMap<T, Double>() ;
	
	protected ExpirableCollectionListener<T> listener = new ExpirableCollectionListener<T>() {
		public void valueExpired(Collection<T> s, T value) {}
	};
}
