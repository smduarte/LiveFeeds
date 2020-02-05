package feeds.sys.catadupa;

import java.io.* ;
import java.util.* ;

@SuppressWarnings("serial")
public class Interval implements Serializable {

	final protected SortedSet<SimpleInterval> intervals ;
	
	public Interval( long l, long h ) {
		intervals = new TreeSet<SimpleInterval>() ;
		intervals.add( new SimpleInterval( l, h ) ) ;
	}
	
	protected Interval( Interval other ) {
		this.intervals = new TreeSet<SimpleInterval>(other.intervals) ;
	}
	
	protected Interval( SortedSet<SimpleInterval> intervals ) {
		this.intervals = new TreeSet<SimpleInterval>( intervals ) ;
	}
	
	protected Interval() {
		this.intervals = new TreeSet<SimpleInterval>() ;
	}
	
	public long L() {
		return intervals.first().L ;
	}
	
	public long H() {
		return intervals.last().H ;
	}
	
	public long length() {
		long res = 0 ;
		for( SimpleInterval i : intervals ) 
			res += i.length() ;
		return res ;
	}
	
	public boolean isEmpty() {
		return intervals.isEmpty() ;
	}
	
	public boolean contains( Interval other ) {
		return this.equals( this.union( other ) ) || other.equals( this.union( other ));
	}
	
	public boolean contains( long val ) {
		for( SimpleInterval i : intervals ) 
			if( i.contains( val ) ) return true ;

		return false ;
	}
	
	public Interval union( Interval other ) {
		
		ArrayList<SimpleInterval> x = new ArrayList<SimpleInterval>( this.intervals ) ;
		x.addAll( other.intervals ) ;
		
LOOP:	for(;;) {
			for( int i = 0 ; i < x.size() ; i++) {
				SimpleInterval I = x.get(i) ;
				for( int j = i + 1 ; j < x.size() ; j++ ) {
					SimpleInterval J = x.get(j);
					if( I.intersects(J) || I.contiguous(J) ) {
						x.remove(I) ; x.remove(J) ;
						x.add( I.union(J)) ;
						continue LOOP ;
					}
				}
			}
			break ;
		}
		return new Interval( new TreeSet<SimpleInterval>(x) ) ;
	}

	public Interval difference( Interval other ) {
		
		TreeSet<SimpleInterval> x = new TreeSet<SimpleInterval>( this.intervals ) ;
LOOP:	for(;;) {
			for( SimpleInterval i : x )
				for( SimpleInterval j : other.intervals )
					if( i.intersects(j) ) {
						x.remove(i) ;
						x.addAll( i.difference(j)) ;
						continue LOOP ;
					}
			break ;			
		}
		return new Interval( x ) ;
	}

	
	public Interval intersection( Interval other ) {
		TreeSet<SimpleInterval> res = new TreeSet<SimpleInterval>() ;
		for( SimpleInterval i : this.intervals )
			for( SimpleInterval j: other.intervals ) 
				if( i.intersects(j) ) 
					res.add( i.intersection(j)) ;
			
		return new Interval(res) ;
	}
	
	public boolean equals( Interval other) {
		if( this.intervals.size() == other.intervals.size() ) 
			return this.intervals.containsAll( other.intervals ) ;
		else return false ;
	}
	
	
	public String toString() {
		String res = intervals.isEmpty()? "()" : "" ;
		for( SimpleInterval i : intervals )
			res += i ;
		return res ;
	}
	
	protected long moveInside( long v ) {
		for( SimpleInterval i : intervals ) {
			if( v < i.L ) return i.L ;
				if( v < i.H ) return v ;
		}
		return L() ;
	}

}


@SuppressWarnings("serial")
class SimpleInterval implements Comparable<SimpleInterval>, Serializable {
	final long L, H;

	public SimpleInterval(long l, long h) {
		L = l; 
		H = h;
	}

	protected SimpleInterval( SimpleInterval other ) {
		L = other.L ;
		H = other.H ;
	}
	
	public int compareTo(SimpleInterval other) {
		return (L == other.L) ? ( H == other.H ? 0 : (H < other.H ? -1 : 1)) : (L < other.L ? -1 : 1) ;
	}

	public int hashCode() {
		long hl = L ^ H ;
		return (int)((hl >> 32) ^ (hl & 0xFFFFFFFF)) ;
	}

	public boolean equals( Object other ) {
		return equals( (SimpleInterval) other ) ;
	}
	
	public boolean equals( SimpleInterval other ) {
		return L == other.L && H == other.H ;
	}
	
	public boolean contains( long val ) {
		return val >= L && val <= H ;
	}
	
	public boolean intersects( SimpleInterval other ) {
		return !( H < other.L || L > other.H ) ;
	}
	
	public boolean contains( SimpleInterval other ) {
		return L <= other.L && H >= other.H ;
	}
	
	public boolean contiguous( SimpleInterval other ) {
		return (H+1) == other.L || (other.H+1) == L ;
	}
	
	public SimpleInterval intersection( SimpleInterval other ) {
		return new SimpleInterval( Math.max(L, other.L), Math.min( H, other.H ) ) ;
	}
	
	public SimpleInterval union( SimpleInterval other ) {
		return new SimpleInterval( Math.min(L, other.L), Math.max( H, other.H ) ) ;
	}

	public List<SimpleInterval> difference( SimpleInterval other ) {
		ArrayList<SimpleInterval> res = new ArrayList<SimpleInterval>() ;
		if( other.contains(this) ) return res ;
			
		if( this.contains(other) ) {
			SimpleInterval l = new SimpleInterval( this.L, other.L - 1 ) ;
			SimpleInterval r = new SimpleInterval( other.H + 1, this.H ) ;

			if( l.isValid() ) 
				res.add(  l ) ;
			if( r.isValid() ) 
				res.add( r ) ;
		}
		else if( L < other.L ) {
			SimpleInterval l = new SimpleInterval( this.L, other.L - 1) ;
			if( l.isValid() )
				res.add( l ) ;			
		} else {
			SimpleInterval l = new SimpleInterval( other.H + 1, this.H ) ;
			if( l.isValid() )
				res.add( l ) ;		
		}
		return res ;
	}
	
	protected long length() {
		return (H - L ) + 1 ;
	}
		
	private boolean isValid() {
		return  L <= H ;
	}
	
	public String toString() {
		return String.format("[%s, %s]", L, H ) ;
	}
}