package simsim.graphs;

import java.util.*;

/**
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 *
 * Computes a shortest path tree rooted at a given node from a spanner graph.
 * 
 * @param <T> node type.
 */
public class Dijkstra<T extends Comparable<T>> {

	protected T root ;	
	protected Graph<T> graph ;
	protected Map<T, T> parents = new HashMap<T, T>() ;
	protected Map<T, Double> costs = new HashMap<T, Double>();
	
    
    public Dijkstra( T root, Graph<T> graph) {
    	this.root = root ;
    	this.graph = graph ;
	}

	public double cost( T node ) {
		return costs.get( node ) ;
	}
	
	public double distanceTo( T node ) {
		return costs.get( node ) ;
	}
		
	public List<T> pathTo( T dst ) {
		if( parents.isEmpty() )
			reacheable() ;
		
		ArrayList<T> path = new ArrayList<T>() ;
		path.add( dst ) ;

		T dad ;
		do {
			dad = parents.get( dst ) ;
			path.add( dad ) ;
			if( dst == null || dad == null)
				return null;

			dst = dad ;
		} while( dad != root ) ;
		
		Collections.reverse(path);
		return path;
	}
	
	public List<T> reacheable() {
		LinkedList<T> nodes = new LinkedList<T>( graph.nodes() ) ;
		
		for( T i : nodes ) {
			costs.put( i, i.equals(root) ? 0 : Double.MAX_VALUE ) ;
			parents.put( i, null ) ;
		}
				
		while( ! nodes.isEmpty() ) {

			T min = null ;
			for( T i : nodes )
				min = (min == null ? i : (costs.get(i) < costs.get(min) ? i : min)) ;
			
			nodes.remove( min ) ;
			
			for( Link<T> e : graph.edges( min) ) {
				T child = e.v.equals(min) ? e.w : e.v ;
				double alt = costs.get(min) + e.cost ;
				
				if( alt < costs.get(child) ) {
					costs.put( child, alt) ;
					parents.put( child, min) ;
				}
			}
		}
		
		List<T> res = new ArrayList<T>() ;
		for( Map.Entry<T, T> i :  parents.entrySet() )
			if( i.getValue() != null )
				res.add( i.getKey() ) ;
		
		return res;
	}

}