package simsim.graphs;

import java.util.*;

import simsim.utils.RandomList;

/**
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 *
 * Computes a shortest path tree rooted at a given node from a spanner graph.
 * 
 * @param <T> node type.
 */
public class ShortestPathsTree<T extends Comparable<T>> {

	protected T root ;	
	protected Graph<T> graph ;
	protected Map<T, T> parents = new HashMap<T, T>() ;
	protected Set<Link<T>> edges = new HashSet<Link<T>>() ;
	protected Map<T, Double> costs = new HashMap<T, Double>();
	protected Map<T, Integer> hops = new HashMap<T, Integer>();
	protected Map<T, Set<T>> children = new HashMap<T, Set<T>>() ;
	protected RandomList<T> leafSet;
	
	private Map<T, Set<Link<T>>> paths = null ;

    private double[][] directCost ;
    
    protected ShortestPathsTree( T root ) {
    	this.root = root ;
    }
   
    public ShortestPathsTree( T root, Graph<T> graph) {
    	this.root = root ;
    	this.graph = graph ;
    	
		dijkstra( graph ) ;		
	}

	public double cost( T node ) {
		return costs.get( node ) ;
	}
	
	public int hops( T node ) {
		return hops.get( node ) ;
	}
	
	public double distanceTo( T node ) {
		return costs.get( node ) ;
	}
	
	public Set<T> children( T node ) {
		return children.get( node ) ;
	}
	
	public T parent( T node ) {
		return parents.get( node ) ;
	}
	
	public Set<Link<T>> edges() {
		return edges ;
	}
		
	public Set<Link<T>> path( T node ) {
		if( paths == null ) computePaths() ;
		return paths.get( node ) ;
	}
	
	public RandomList<T> leafSet() {
		if( leafSet == null) {
			leafSet = new RandomList<T>();
			for( Map.Entry<T, Set<T>> i : children.entrySet() )
				if( i.getValue().isEmpty() )
					leafSet.add(i.getKey() ) ;
		}
		return leafSet;
	}
	
	private void dijkstra( Graph<T> graph) {
		LinkedList<T> nodes = new LinkedList<T>( graph.nodes() ) ;
		
		for( T i : nodes ) {
			costs.put( i, i.equals(root) ? 0 : Double.MAX_VALUE ) ;
			hops.put( i, i.equals(root) ? 0 : Integer.MAX_VALUE ) ;
			children.put( i, new HashSet<T>() ) ;
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
					hops.put( child, hops.get(min) + 1 ) ;
				}
			}
		}
		
		for( Map.Entry<T, T> i : parents.entrySet() ) {
			T dad = i.getValue() ; T child = i.getKey() ;
			if( dad != null ) {
				children.get( dad ).add( child ) ;
				edges.add( new Link<T>( dad, child, 0) ) ;
			}
		}
	}
	
	public List<T> pathTo( T dst ) {
		
		ArrayList<T> path = new ArrayList<T>() ;
		path.add( dst ) ;

		T dad ;
		do {
			dad = parents.get( dst ) ;
			if( dst == null || dad == null)
				return null;
			path.add( dad ) ;

			dst = dad ;
		} while( dad != root ) ;
		
		Collections.reverse(path);
		return path;
	}
	
	private void computePaths() {
	
		paths = new HashMap<T, Set<Link<T>>>() ;

		int N = graph.numberOfNodes() ;
    	directCost = new double[N][N] ;    	
    	for( Link<T> l : graph.edges() ) {
    		int v = graph.indexOf(l.v) ;
    		int w = graph.indexOf(l.w) ;
    		directCost[v][w] = directCost[w][v] = l.cost ;
    	}
    	
		// recursively compute the path from each node to the root.
		for( T i : graph.nodes() ) {
			Set<Link<T>> path = new HashSet<Link<T>>() ;
			computePath( graph, i, path ) ;
			paths.put(i, path) ;
		}
	}
	
	private void computePath( Graph<T> graph, T child, Set<Link<T>> path ) {
		T dad = parents.get( child ) ;
		if( dad != null ) {
			int di = graph.indexOf(dad) ;
			int ci = graph.indexOf(child) ;			
			path.add( new Link<T>( dad, child, directCost[di][ci]) ) ;
			computePath( graph, dad, path ) ;
		}
	}
}