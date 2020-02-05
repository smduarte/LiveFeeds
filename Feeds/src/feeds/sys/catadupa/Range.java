package feeds.sys.catadupa;

import java.io.*;
import java.util.*;

import feeds.sys.*;
import feeds.sys.core.*;
import static feeds.sys.catadupa.Catadupa.*;


public class Range implements Serializable {	
	private static final long serialVersionUID = 1L;
	
	long L, O, K, H ;
	
	public Range() {
		this( 0, 1L << NODE_KEY_LENGTH ) ;
	}
	
	public Range(long K) {
		this( 0, K, 1L << NODE_KEY_LENGTH ) ;
	}
	
	public Range( long L, long H ) {
		this( L, L + (FeedsNode.rnd().nextLong() >>> 1) % (H - L), H ) ;
	}
	
	public Range( NodeDB db ) {
		this( 0L, db.randomNode(-1L).key, 1L << NODE_KEY_LENGTH) ;
	}
	
	public Range clone() {
		return new Range( L, O, K, H) ;
	}
	
	private Range( long L, long O, long H  ) {
		this(L, O, O, H) ;	
	}

	private Range( long L, long O, long K, long H ) {
		this.L = L ;
		this.O = O ;
		this.K = K ;	
		this.H = H ;
	}
	
	public boolean isEmpty() {
		return K == H ;
	}
	
	public Range advancePast( long k ) {	
		k++ ;
		if( O == L )
			return new Range( L, O, Math.min( H, k), H ) ;
		else 
			if( k > O ) 
				return k < H ? new Range(L, O, k, H ) : new Range(L, L, L, O) ;
			else
				return new Range( k, k, k, O) ;
	}
	
	public long size() {
		return O <= K ? (O - L) + (H - K) : O - K ;
	}
	
	
	
		
	public static long size( Collection<Range> c) {
		long r = 0 ;
		for( Range i : c)
			r += i.size() ;
		return r ;
	}
	
	public static int nsize(Collection<Range> c, NodeDB db ) {
		int r = 0 ;
		for( Range i : c)
			r += i.nodes(db).size() ;
		return r ;
	}
	
	
	public List<Node> nodes( NodeDB db) {
		ArrayList<Node> r = new ArrayList<Node>() ;

//		System.err.printf("thisRange:%s\n", this) ;
//		System.err.printf("L=%d O=%d K=%d H=%d\n", L, O, K, H) ;

		if( K < O ) {
			for( Node i : db.nodes(K, O) )
				r.add( i ) ;			
		}
		else {
			for( Node i : db.nodes(K, H) )
				r.add( i ) ;			

			for( Node i : db.nodes(L, O) )
				r.add( i ) ;			
		}
//		System.err.printf("out:\n", r) ;
		return r ;
	}
	
	public List<Node> pNodes( NodeDB db, Object ev, ID channel ) {
		ArrayList<Node> r = new ArrayList<Node>() ;

		if( K < O ) {
			for( Node i : db.nodes(K, O) )
				if( i.pAccepts(ev, channel) )
						r.add( i ) ;			
		}
		else {
			for( Node i : db.nodes(K, H) )
				if( i.pAccepts(ev, channel) )
					r.add( i ) ;			

			for( Node i : db.nodes(L, O) )
				if( i.pAccepts(ev, channel) )
					r.add( i ) ;			
		}
		return r ;
	}
	
	public List<Node> fNodes( NodeDB db, Object ev, ID channel, ID dst ) {
		ArrayList<Node> r = new ArrayList<Node>() ;

		if( K < O ) {
			for( Node i : db.nodes(K, O) )
				if( i.fAccepts(ev, channel, dst) )
						r.add( i ) ;			
		}
		else {
			for( Node i : db.nodes(K, H) )
				if( i.fAccepts(ev, channel, dst) )
					r.add( i ) ;			

			for( Node i : db.nodes(L, O) )
				if( i.fAccepts(ev, channel, dst) )
					r.add( i ) ;			
		}
		return r ;
	}
	
	public String toString() {
		if( isEmpty() ) 
			return "[]" ;
		else {
			String res = K < O ? String.format("[%d, %d[", K, O) : L == O ? String.format("[%d, %d[", K, H-1L) : String.format("[%d - %d[ U [%d - %d[", L, O, K, H); 
			return String.format("%s -> %d", res, size() ) ;
		}
	}
	
	
	/**
	 * Slices the range into a number of slices, so that a leaf-aligned complete tree is produced.
	 * @param slices outward tree degree
	 * @param db database of the calling node
	 * @return the sub-ranges requested
	 */
	public List<Range> slice( int slices, NodeDB db ) {
		
		ArrayList<Range> res = new ArrayList<Range>();

		if( isEmpty() )
			return res ;
		
		List<Node> remaining = nodes( db) ;			
		int[] children = children( slices, remaining.size() ) ;
		
		long k = K, x ;
		for( int i = 0 ; i < slices - 1 && children[i] > 0 ; i++ ) {
			int C = children[i] ;
			x = remaining.get(C-1).key + 1L;
			
			res.add( k >= O && x > O ? new Range(k, k, k, x) : new Range( L, x, k, H)  ) ;
			k = x;			
			remaining = remaining.subList(C, remaining.size()) ;
		}
		if( ! remaining.isEmpty() ) {
			x = remaining.get( remaining.size() - 1).key ;
			res.add( new Range(L, O, k, H) ) ;
		}
//		assert size() == size( res ) ;
		return res;
	}
	
	private static int height( int G, int n ) {
		n = n + 1 ;
		double L = (G-1) * n + 1 ;
		return (int) Math.floor( Math.log(L)/ Math.log(G) - 1) ;
	}
	
	private int[] children( int G, final int N ) {

		if( G == 1 ) 
			return new int[] { N } ;
		
		int n = N ;
		int H = height( G, n ) ;		
		int nc = (int)( Math.pow(G, H) - 1 ) / (G - 1) ;

		int[] res = new int[G] ;
		for( int i = 0 ; n > 0 && i < G ; i++ ) {
			res[i] = nc ;
			n -= nc ;
		}
		
		int ch = (int)Math.pow(G, H) ;
		for( int i = 0 ; n > 0 ; i++, n -= ch )
			res[i] += Math.min( ch, n) ;
		
		return res ;
	}
}

