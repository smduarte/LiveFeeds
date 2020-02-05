package livefeeds.sift0;

import java.util.HashSet;
import java.util.Iterator;

public class Path extends HashSet<Long> {
	private static final long serialVersionUID = 1L;

	public Path() {
		super() ;
	}

	protected Path( Path other ) {
		super( other ) ;
	}
	
	public Path add( long key ) {
		super.add( key ) ;
		return this ;
	}
	
	public boolean contains( long key ) {
	  return super.contains(key) ;	
	}
	
	public Path clone() {
		return new Path(this) ;
	}
	
//	public Path clone( Range r ) {
//		return new Path(this).retain(r) ;
//	}
	
	public Path retain( Range r ) {
		for( Iterator<Long> i = super.iterator() ; i.hasNext() ; ) {
			if( ! r.inRange( i.next() ) )
				i.remove() ;
		}
		return this ;
	}
}