package feeds.sys.util;

import java.util.* ;

public class SortedHashSet<T> implements Iterable<T>{
    
    public SortedHashSet() {
    }
    
    public SortedHashSet( Collection<T> c ) {
        for( T i : c )
        	put( i ) ;
    }
    
    public int size() {
        return values.size() ;
    }
    
    public void put( T t ) {
        keys.put( t, t ) ;
        values.add( t ) ;
    }
    
    public T get( Object o ) {
        return keys.get( o ) ;
    }
    
    public T remove( Object o ) {
        T res = keys.remove( o ) ;
        if( res != null ) values.remove( res ) ;
        return res ;
    }
    
    public Collection<T> values() {
        return values ;
    }
      
    public String toString() {
        return values.toString() ;
    }
    
    Set<T> values = new TreeSet<T>() ;
    Map<T,T> keys = new HashMap<T,T>() ;
    
	public Iterator<T> iterator() {
		return values().iterator() ;
	}
}
