package feeds.simsim;

import java.util.*;

import simsim.utils.*;

public class NodeDB<T extends Node> {

	public RandomList<T> nodes = new RandomList<T>();
	public Map<Integer, T> i2n = new HashMap<Integer, T>() ;
	
	public int store(T n) {
		int i = nodes.size() ;
		nodes.add(n);
		i2n.put(i, n ) ;
		return i ;
	}
	
	public void dispose( T n) {
		if (n != null) {
			i2n.remove(n.index) ;
			seeds.remove(n);
			nodes.remove(n);
			n.dispose();
		}
	}

	public long size() {
		return nodes.size();
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