package feeds.sys.graphs;

import java.util.* ;

import feeds.sys.* ;
import feeds.sys.core.* ;

/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 */

public class ST_RoutingTables {
    
    public ST_RoutingTables() {
        children = new ArrayList<ID>() ;
        neighbours = new ArrayList<ID>() ;
        ucastRTable = new HashMap<ID, ID>() ;
        bcastRTable = new HashMap<ID, List<ID>>() ;
    }
    
    public ST_RoutingTables( SpanningTreeEncoding ste, ID node) {
        this.node = node ;
        this.size = ste.size() ;
        this.dads = ste.dads() ;
        this.nodes = ste.nodes() ;
        this.inode = ste.indexOf( node ) ;
        if( this.inode < 0 ) {
        	System.out.println("EEEERRROOORRR") ;
        	System.out.println( node + " --> " +  ste ) ;
        	throw new IllegalArgumentException( FeedsNode.id() + "[" + node + " not present in SpanningTree]") ;
        }
    }
    
    public ID parent() {
        int dad = dads[ inode ] ;
        if( dad >= 0 )return nodes[ dad ] ;
        else return null ;
    }
    
    public List<ID> neighbours() {
        if( neighbours == null ) {
            neighbours = new ArrayList<ID>( children() ) ;
            int dad = dads[ inode ] ;
            if( dad >= 0 ) 
            	neighbours.add( nodes[ dad ] ) ;
        }
        return neighbours ;
    }
    
    public List<ID> children() {
        if( children == null ) {
            children = new ArrayList<ID>() ;
            for( int i = inode + 1 ; i < size ; i++ )
                if( dads[i] == inode ) 
                	children.add( nodes[i] ) ;
        }
        return children ;
    }
    
    public ID nextHop( ID node ) {
        return getUnicastRTable().get( node ) ;
    }
    
    public List<ID> children( ID src ) {
        List<ID> r = getBroadcastRTable().get( src ) ;
        if( r == null ) r = neighbours() ;
        return r ;
    }
        
    public Map<ID, List<ID>> getBroadcastRTable() {
        if( bcastRTable == null ) {
            bcastRTable = new HashMap<ID, List<ID>>() ;
            bcastRTable.put( node, new ArrayList<ID>() ) ;
            
            List<ID> neighbours = this.neighbours() ;
            Map<ID, List<ID>> routes = new HashMap<ID, List<ID>>() ;
            for( int i = 0 ; i < size ; i++ ) {
                ID child = nextHop( nodes[i] ) ;
                List<ID> r = routes.get( child ) ;
                if( r == null ) {
                    r = new ArrayList<ID>( neighbours ) ;
                    r.remove( child ) ;
                    routes.put( child, r ) ;
                }
                bcastRTable.put( nodes[i], r ) ;
            }
        }
        return bcastRTable ;
    }
    
    public Map<ID, ID> getUnicastRTable() {
        if( ucastRTable == null ) {
            ucastRTable = new HashMap<ID, ID>() ;
            
            int dad = dads[inode] ;
            int[] ra = new int[ size ] ;
            for( int i = 0 ; i < size ; i++ ) ra[i] = -1 ;
            
            ra[inode] = inode ;
            for( int i = inode + 1 ; i < size ; i++)
                if( dads[i] == inode ) 
                	setDescendentsUcastRouters( i, i, ra ) ;
            
            if( dad != -1 ) {
                for( int i = 0 ; i < size ; i++ )
                    if( ra[i] < 0 ) ra[i] = dad ;
            }
            
            for( int i = 0 ; i < size ; i++ )
                ucastRTable.put( nodes[i], nodes[ra[i]] ) ;
            
            ucastRTable.put( FeedsNode.id(), FeedsNode.id() ) ;
        }
        return ucastRTable ;
    }
    
    private void setDescendentsUcastRouters( int n, int router, int[] ra  ) {
        ra[n] = router ;
        for( int i = n + 1 ; i < size ; i++)
            if( dads[i] == n ) setDescendentsUcastRouters( i, router, ra ) ;
    }
    
    //-------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------
    
    private ID node ;
    private int inode ;
    private int size ;
    private int[] dads ;
    private ID[] nodes ;
    private List<ID> children ;
    private List<ID> neighbours ;

    private Map<ID, ID> ucastRTable ;
    private Map<ID, List<ID>> bcastRTable ;
}
