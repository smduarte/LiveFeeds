package example.chord;

import static simsim.core.Simulation.rg;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import simsim.core.EndPoint;
import simsim.utils.RandomList;

class NodeDB {
	public static final int KEY_LENGTH = 12 ;
	public static final long KEY_RANGE = (1L << KEY_LENGTH) ;
	
	static RandomList<Node> nodes = new RandomList<Node>();
	static TreeMap<Long, Node> k2n = new TreeMap<Long, Node>();

	static long store(Node n) {
		for (;;) {
			long key = (rg.nextLong() >>> 1) % KEY_RANGE ;
			if (!k2n.containsKey(key)) {
				k2n.put(key, n);
				nodes.add(n);
				return key;
			}
		}
	}
	
	Set<Long> keys() {
		return k2n.keySet();
	}

	static void dispose(Node n) {
		if (n != null) {
			k2n.remove(n.key);
			seeds.remove(n);
			nodes.remove(n);
			n.dispose();
		}
	}

	static int size() {
		return k2n.size();
	}

	static Collection<Node> nodes() {
		return nodes;
	}

	static Node succ( long key ) {
		SortedMap<Long, Node> tm = k2n.tailMap(key) ;		
		if( tm.isEmpty() ) tm = k2n.tailMap(0L) ;
		
		if( tm.isEmpty() ) return null ;
		else return k2n.get( tm.firstKey() ) ;
	}
	
	static Node randomNode() {
		return nodes.randomElement();
	}

	static RandomList<Node> seeds = new RandomList<Node>();

	static Collection<EndPoint> randomEndPoints(Node caller, int total) {

		Set<EndPoint> res = new HashSet<EndPoint>();

		if (seeds.isEmpty())
			res.add(nodes.randomElement().endpoint);
		else
			while (res.size() < Math.min(total, seeds.size())) {
				res.add(seeds.randomElement().endpoint);
			}

		seeds.add(caller);
		return res;
	}

}