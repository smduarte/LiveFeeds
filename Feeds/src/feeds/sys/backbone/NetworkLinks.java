package feeds.sys.backbone;

import java.util.* ;
import feeds.sys.core.* ;
import feeds.sys.util.*;
import feeds.sys.graphs.*;

public class NetworkLinks {
    
    public void add( Link<ID> l ) {
        nodes.add( l.v ) ;
        nodes.add( l.w ) ;
        Link<ID> m = links.remove( l ) ;
        if( m == null ) 
        	m = l ;
        
        m.samples++ ;
        m.cost = l.cost ;
        m.cost = ( l.cost + m.cost ) * 0.5 ;
        links.put( m ) ;
    }
    
    public void add( SpanningTreeEncoding ste ) {
        ID[] na = ste.nodes() ;
        double[] ca = ste.costs() ;
        for( int i = 1 ; i < na.length ; i++ ) 
            add( new Link<ID>( na[0], na[i], ca[i] ) ) ;
    }
    
    public NetworkLinks addAll( Link<ID>[] la ) {
    	for( Link<ID> i : la )
    		add( i ) ;
        return this ;
    }
    
    public int size() {
        return links.size() ;
    }
    
    public Collection<ID> nodes() {
    	return nodes ;
    }
    
    public Collection<Link<ID>> links() {
        return links.values() ;
    }
    
    public ID[] nodeArray() {
    	return nodes.toArray( new ID[ nodes.size() ] ) ;
    }
    
    @SuppressWarnings("unchecked")
	public Link<ID>[] linkArray() {
    	return links.values().toArray( new Link[ size() ] ) ;
    }
    
    public String toString() {
        return "NetworkLinks:" + nodes.size() + " / " + links().size() + "/" + links() ;
    }
    
    
    public void updateCost( ID node, double cost ) {
        Double v = (Double) costs.get( node ) ;
        if( v != null ) cost = (v + cost ) * 0.5 ;
        costs.put( node, cost) ;
    }
    

    public NetworkLinks subset( ID[] ss ) {
        return subset( Arrays.asList(ss) );
    }

    public NetworkLinks subset( Collection<ID> ssn ) {
        NetworkLinks res = new NetworkLinks() ;
    
        for( Link<ID> i : links.values() ) 
        	if( ssn.contains(i.v) && ssn.contains( i.w ) )
        		res.add(i) ;

        return res ;
    }
    
    public void replaceTreeCosts( SpanningTreeEncoding ste ) {
        ID[] na = ste.nodes() ;
        double[] ca = ste.costs() ;
        for( int i = 0 ; i < na.length ; i++ ) {
            Double ci = costs.get( na[i] ) ;
            ca[i] = ( ci != null ? ci : Double.MAX_VALUE ) ;
        }
    }
    
    private Set<ID> nodes = new HashSet<ID>() ;
    private Map<ID, Double> costs = new HashMap<ID, Double>() ;
    private SortedHashSet<Link<ID>> links = new SortedHashSet<Link<ID>>() ;
}
