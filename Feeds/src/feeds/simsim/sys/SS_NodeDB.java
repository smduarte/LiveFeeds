package feeds.simsim.sys;

import java.util.*;

import simsim.utils.*;
import feeds.sys.core.*;

public class SS_NodeDB<T extends SS_Node> {

	public RandomList<T> nodes = new RandomList<T>();
	public Map<ID, T> k2n = new TreeMap<ID, T>();

	public void store(T n) {
		k2n.put(n.id, n);
		nodes.add(n);
	}
	
	public void dispose( T n) {
		if (n != null) {
			k2n.remove(n.id);
			seeds.remove(n);
			nodes.remove(n);
			n.dispose();
		}
	}

	public T get( ID key ) {
		return k2n.get( key ) ;
	}
	
	public long size() {
		return k2n.size();
	}

	public Collection<T> nodes() {
		return nodes;
	}

	public T randomNode() {
		return nodes.randomElement();
	}

	RandomList<T> seeds = new RandomList<T>();
	public Collection<T> randomNodes( T caller, int total) {

		Set<T> res = new HashSet<T>();

		if (seeds.isEmpty())
			res.add(nodes.randomElement());
		else
			while (res.size() < Math.min(total, seeds.size())) {
				res.add(seeds.randomElement());
			}

		seeds.add(caller);
		return res;
	}
}