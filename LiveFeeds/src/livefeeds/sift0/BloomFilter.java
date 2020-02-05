package livefeeds.sift0;

public class BloomFilter {
	
	protected final int K ;
	protected long d0, d1 ;

	public BloomFilter() {
		this(4) ;
	}
	
	public BloomFilter( int k ) {
		this.K = k ;
		this.d0 = this.d1 = 0L ;
	}
	
	protected BloomFilter( BloomFilter other ) {
		this.K = other.K ; this.d0 = other.d0 ; this.d1 = other.d1 ;
	}
	
	public BloomFilter clone() {
		return new BloomFilter( this );
	}
	
	public int size() {
		return 16 ;
	}
		
	public BloomFilter add( long id ) {
		for( int i = 0 ; i < K ; i++ )
			set( hash( i, id) ) ;

		return this;
	}
		
	public boolean contains( long id ) {
		for( int i = 0 ; i < K ; i++ )
			if( ! get( hash( i, id) ) ) return false ;
		return true ;
	}
	
	private int hash( int f, long id ) {
		final int[] primes = {3, 17, 37, 53, 67, 79, 97, 101} ;
		return (int)(id * primes[f]) & 63 ;
	}
	
	private void set( int b ) {
		if( b < 64 )
			d0 |= (1L << b) ;
		else {
			d1 |= (1L << (b - 64)) ;
		}
	}
	
	private boolean get(int b ) {
		if( b < 64 )
			return (d0 & (1L << b)) != 0L ;
		else
			return (d1 & (1L << (b-64))) != 0L ;
	}
	
	public String toString() {
		double v = (Long.bitCount(d0) + Long.bitCount(d1)) / 128.0;
		return String.format("%f.1%%",100*v);
	}
}
