package livefeeds.twister6;

import static livefeeds.twister6.config.Config.Config;
import static simsim.core.Simulation.rg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import simsim.utils.RandomList;

public class Range implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final long KEY_RANGE = (1L << Config.NODE_KEY_LENGTH);
	private static final long MAX_KEY = KEY_RANGE - 1L;

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
		return new Range(L, H);
	}

	public boolean isEmpty() {
		return L == -1L;
	}

	public Range advancePast(long k) {
		return k != H ? new Range((k + 1L) & MAX_KEY, H) : new Range(-1L, -1L);
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
				res.add(new Range(l, h - 1L));
				l = h;
			}
			res.add(new Range(l, H));
			return res;
		}
	}

	public RandomList<CatadupaNode> nodeList(DB db ) {
		RandomList<CatadupaNode> res = new RandomList<CatadupaNode>() ;
		for( CatadupaNode i : db.nodes(L, H) ) 
			res.add(i) ;

		return res ;
	}
	
	public RandomList<CatadupaNode> nodeList(DB db, Event e ) {
		RandomList<CatadupaNode> res = new RandomList<CatadupaNode>() ;
		for( CatadupaNode i : db.nodes(L, H) ) 
			if( i.accepts(e) )
				res.add(i) ;

		return res ;
	}
	
	public List<Range> sliceNodes(int slices, DB db) {
		List<Range> res = new ArrayList<Range>();

		if (isEmpty())
			return res;

		List<CatadupaNode> remaining = nodeList(db);
		int[] children = children(slices, remaining.size());

		long k = L, x;
		for (int i = 0; children[i] > 0 && i < slices - 1; i++) {
			int C = children[i];
			x = remaining.get(C - 1).key;
			res.add(new Range(k, x));
			k = x + 1L;
			remaining = remaining.subList(C, remaining.size());
		}
		res.add(new Range(k, H));
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

	int[] children2(int G, final int N) {
		if (G == 1)
			return new int[] { N };
		else {
			int[] res = new int[G];
			int t = 0;
			for (int i = 1; i < G; i++) {
				res[i] = N / G;
				t += N / G;
			}
			res[0] = N - t;
			return res;
		}
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

		// String x = "" ;
		// for( int i : res )
		// x += i + " ";
		// System.out.printf("g=%d, n=%d, c->[%s]\n", G, N, x ) ;

		return res;
	}
}