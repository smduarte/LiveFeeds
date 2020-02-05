package livefeeds.rtrees;

import static livefeeds.rtrees.config.Config.Config;
import static simsim.core.Simulation.rg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class Range implements Serializable {
	private static final long serialVersionUID = 1L;
	
	static final long KEY_RANGE = (1L << Config.NODE_KEY_LENGTH);
	static final long MAX_KEY = KEY_RANGE - 1L;

	long L, H;

	public Range() {
		this.L = (rg.nextLong() >>> 1) & MAX_KEY;
		this.H = (L + MAX_KEY) & MAX_KEY;
	}

	public Range(long L, long H) {
		this.L = L;
		this.H = H;
	}

	public Range clone() {
		return range(L, H);
	}

	public boolean isEmpty() {
		return L == -1L;
	}

	public Range advancePast(long k) {
		return k != H ? range((k + 1L) & MAX_KEY, H) : range(-1L, -1L);
	}

	public long size() {
		return L <= H ? (H - L + 1L) : (H - L + 1L) + KEY_RANGE;
	}

	public boolean sizeGreaterThan(int v, DB db) {
		int n = 0;
		for (Iterator<CatadupaNode> i = nodes(db).iterator(); n <= v && i.hasNext();)
			if (i.next().isOnline())
				n++;
		return n > v;
	}

	public static long size(Collection<Range> c) {
		long r = 0;
		for (Range i : c)
			r += i.size();
		return r;
	}

	public static int nsize(Collection<Range> c, DB db) {
		int r = 0;
		for (Range i : c) {
			int k = 0 ;
			for( Iterator<CatadupaNode> j = i.nodes(db).iterator() ; j.hasNext() ; k++) ;

			r += k;
		}
		return r;
	}

	public Iterable<CatadupaNode> nodes(DB db) {
		return db.nodes( L, H) ;
	}

	public String toString() {
		if (isEmpty())
			return "[]";
		else {
			String res = L <= H ? String.format("(%d, %d)", L, H) : String.format("(%d - %d( U (%d - %d)", L, KEY_RANGE, 0L, H);
			return String.format("{L=%d H=%d} --> %s -> %d", L, H, res, size());
		}
	}

	List<Range> sliceKeys(int slices) {
		ArrayList<Range> res = new ArrayList<Range>();

		if (isEmpty())
			return res;

		else {
			long W = size() / slices;

			long l = L;
			for (int i = 1; i < slices; i++) {
				long h = (l + W) % KEY_RANGE;
				res.add( range(l, h - 1L));
				l = h;
			}
			res.add( range(l, H));

			return res;
		}
	}

	public List<CatadupaNode> nodeList(DB db ) {
		ArrayList<CatadupaNode> res = new ArrayList<CatadupaNode>() ;
		for( CatadupaNode i : db.nodes(L, H) ) 
			res.add(i) ;

		return res ;
	}
	
	public List<CatadupaNode> nodeList(DB db, Event e ) {
		ArrayList<CatadupaNode> res = new ArrayList<CatadupaNode>() ;
		for( CatadupaNode i : db.nodes(L, H) ) 
			if( i.accepts(e) )
				res.add(i) ;

		return res ;
	}
	
	public List<Range> sliceNodes(int slices, DB db) {
		ArrayList<Range> res = new ArrayList<Range>();

		if (isEmpty())
			return res;

		List<CatadupaNode> remaining = nodeList(db);
		int[] children = children(slices, remaining.size());

		long k = L, x;
		for (int i = 0; children[i] > 0 && i < slices - 1; i++) {
			int C = children[i];
			x = remaining.get(C - 1).key;
			res.add( range(k, x));
			k = x + 1L;
			remaining = remaining.subList(C, remaining.size());
		}
		res.add( range(k, H));
		return res;
	}

	public List<Range> slice(int level, int slices, DB db) {
		return sliceNodes(slices, db); 
		//double h = Math.log( GlobalDB.size() ) / Math.log( Config.BROADCAST_MAX_FANOUT );
		//return level < 0.33 * h ? sliceKeys( slices) : sliceNodes(slices, db);
	}

	private static int height(int G, int n) {
		n = n + 1;
		double L = (G - 1) * n + 1;
		return (int) Math.floor(Math.log(L) / Math.log(G) - 1);
	}

	private int[] children(int G, final int N) {

		if (G == 1)
			return new int[] { N };

		int n = N;
		int H = height(G, n);
		int nc = (int) (Math.pow(G, H) - 1) / (G - 1);

		int[] res = new int[G];
		for (int i = 0; n > 0 && i < G; i++) {
			res[i] = nc;
			n -= nc;
		}

		int ch = (int) Math.pow(G, H);
		for (int i = 0; n > 0; i++, n -= ch)
			res[i] += Math.min(ch, n);

		return res;
	}
	
	protected Range range( long L, long H ) {
		return new Range( L, H ) ;
	}
	
	@SuppressWarnings("serial")
	static class SK_Range extends Range {
		SK_Range() {
			super(0L, MAX_KEY) ;
		}
		
		SK_Range(long l, long h ) {
			super(l, h) ;
		}
		
		protected Range range( long L, long H ) {
			return new SK_Range( L, H ) ;
		}
	}
	
	@SuppressWarnings("serial")
	public static class CN_Range extends Range {
		
		public CN_Range() {
			super() ;
		}
		
		protected Range range( long L, long H ) {
			return new CN_Range( L, H ) ;
		}
		
		CN_Range(long l, long h ) {
			super(l, h) ;
		}
		
		@Override
		public Iterable<CatadupaNode> nodes(DB db) {
			
			TreeSet<CatadupaNode> q = new TreeSet<CatadupaNode>() ;
			for( CatadupaNode i : db.nodes( L, H ) ) 
				q.add( i ) ;
			
			TreeSet<CatadupaNode> x = new TreeSet<CatadupaNode>( new LatencyComparator( db.owner ) ) ;
			for( CatadupaNode i : db.nodes( L, H ) ) 
				x.add( i ) ;
			
			if( q.size() != x.size() )
				System.err.println("WTF WTF!!!");
			
			return q ;
		}
		
		static class LatencyComparator implements Comparator<CatadupaNode> {

			CatadupaNode r ;
			LatencyComparator( CatadupaNode ref) {
				this.r = ref ;
			}
			
			@Override
			public int compare(CatadupaNode a, CatadupaNode b) {
				
				double dA = r.address.latency( a.address) ;
				double dB = r.address.latency( b.address) ;
				if( dA == dB ) return a.compareTo( b) ;
				else return dA < dB ? -1 : 1 ;
			}
			
		}
		
		
		
		

	}
	
	@SuppressWarnings("serial")
	public static class FN_Range extends Range {
		
		public FN_Range() {
			super() ;
		}

		protected Range range( long L, long H ) {
			return new FN_Range( L, H ) ;
		}
		
		FN_Range(long l, long h ) {
			super(l, h) ;
		}
		
		public Iterable<CatadupaNode> nodes(DB db) {
			TreeSet<CatadupaNode> x = new TreeSet<CatadupaNode>( new LatencyComparator( db.owner ) ) ;
			for( CatadupaNode i : db.nodes( L, H ) ) 
				x.add( i ) ;
			return x ;
		}
		
		static class LatencyComparator implements Comparator<CatadupaNode> {

			CatadupaNode r ;
			LatencyComparator( CatadupaNode ref) {
				this.r = ref ;
			}
			
			@Override
			public int compare(CatadupaNode a, CatadupaNode b) {
				if( a == b ) return 0 ;
				
				double dA = r.address.latency( a.address) ;
				double dB = r.address.latency( b.address) ;
				if( dA == dB ) return a.key < b.key ? -1 : 1 ;
				else return dA > dB ? -1 : 1 ;
			}
			
		}
	}
}