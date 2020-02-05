package feeds.sys.graphs;

import java.io.* ;
import java.util.* ;
import feeds.sys.core.* ;

import simsim.graphs.*;

@SuppressWarnings("serial")
public class SpanningTreeEncoding implements Serializable {
    
    public SpanningTreeEncoding() {
        nodes = new ID[0] ;
        edges = new byte[0] ;
        costs = new double[0] ;
    }
    
    @SuppressWarnings("unchecked")
	public SpanningTreeEncoding( ID root, SpanningTree<ID> tree ) {

    	Collection<Link<ID>> lc = tree.links() ;
    	
        Link<ID>[] la = lc.toArray( new Link[ lc.size() ] ) ;
        
        
        int q = la.length ;
        
        nodes = new ID[ q + 1 ] ;
        edges = new byte[ q + 1 ] ;
        costs = new double[ q + 1 ] ;
        
        
        int i = 0, j = 1 ;
        costs[i] = 0 ;
        nodes[i] = root ;
        do {
            int children = 0 ;
            ID node = nodes[ i ] ;
            for( int k = 0 ; k < q ; ) {
                Link<ID> l = la[k] ;
                if( node.equals( l.v )  ) {
                    children++ ;
                    nodes[ j ] = l.w ;
                    costs[j++] = l.cost ;
                    la[k] = la[--q] ;
                }
                else if( node.equals( l.w ) ) {
                    children++ ;
                    nodes[ j ] = l.v ;
                    costs[j++] = l.cost ;
                    la[k] = la[--q] ;
                } else k++ ;
            }
            edges[i++] = (byte)children ;
        } while( j > i ) ;
        
        setHopsAndDads() ;
    }
    
    public int indexOf( ID node ) {
        for( int i = 0 ; i < nodes.length ; i++ )
            if( node.equals( nodes[i] ) ) return i ;
        return -1 ;
    }
    
    @SuppressWarnings("unchecked")
	public SpanningTree<ID> spanningTree() {
        int i = 1, j = 0, l = 0;
        Link<ID>[] la = new Link[ nodes.length - 1] ;
        do {
            ID node = nodes[j] ;
            int children = edges[j++] ;
            for( int k = 0 ; k < children ; k++, i++, l++ ) {
                la[l] = new Link<ID>( node, nodes[i], costs[i] ) ;
            }
        } while( j < i ) ;
        return new SpanningTree<ID>( Arrays.asList(nodes), Arrays.asList( la ) ) ;
    }
    
    public ID root() {
        return nodes[0] ;
    }
    
    public int size() {
        return nodes.length ;
    }
    
    public ID node(int i) {
        return nodes[i] ;
    }
    
    public ID[] nodes() {
        return nodes ;
    }
    
    public Set<ID> nodeSet() {
    	return new HashSet<ID>( Arrays.asList( nodes ) ) ;
    }
    
    public double[] costs() {
        return costs ;
    }
    
    public int[] hops() {
        if( hops == null ) setHopsAndDads() ;
        return hops ;
    }
    
    public int[] dads() {
        if( dads == null) setHopsAndDads() ;
        return dads ;
    }
    
    public int hops( int node ) {
        return hops[node] ;
    }
    
    public int children( int node ) {
        return edges[node] ;
    }
    
    public List<ID> children( ID node ) {
        int inode = indexOf( node ) ;
        List<ID> children = new ArrayList<ID>() ;
        for( int i = inode + 1 ; i < nodes.length ; i++ )
            if( dads[i] == inode ) children.add( nodes[i] ) ;
        return children ;
    }
    
    public List<ID> neighbours( ID node ) {
        int inode = indexOf( node ) ;
        List<ID> neighbours = new ArrayList<ID>() ;
        for( int i = inode + 1 ; i < nodes.length ; i++ )
            if( dads[i] == inode ) neighbours.add( nodes[i] ) ;
        
        int dad = dads[inode] ;
        if( dad >= 0 ) neighbours.add( nodes[dad] ) ;
        
        return neighbours ;
    }
    
    public List<ID> descendents( ID node ) {
        setHopsAndDads() ;
        List<ID> descendents = new ArrayList<ID>() ;
        LinkedList<ID> seed = new LinkedList<ID>() ;
        seed.add( node ) ;
        do {
            List<ID> c = children( seed.removeFirst() ) ;
            seed.addAll( c ) ;
            descendents.addAll(c) ;
        } while( ! seed.isEmpty() ) ;
        return descendents ;
    }
    
    public boolean contains( ID node ) {
        return indexOf( node) >= 0 ;
    }
    
    private void setHopsAndDads() {
        int i = 0, j = 0 ;
        
        dads = new int[ nodes.length ] ;
        hops = new int[ nodes.length ] ;
        if( nodes.length > 0 ) {
            hops[ i ] =  0 ;
            dads[i++] = -1 ;
            do {
                int children = edges[j] ;
                for( int k = 0 ; k < children ; k++) {
                    dads[ i ] = j ;
                    hops[i++] = hops[j] + 1 ;
                }
            } while( ++j < i ) ;
        }
    }
    
    public String toString() {
        String r = "" ;
        for( int i = 0 ; i < nodes.length ; i++ )
            r += nodes[i] + "[e(" + edges[i] + ")h(" +  hops[i] + ")d(" + dads[i] + ")] " ;
        
        return r ;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject( nodes ) ;
        out.writeObject( edges ) ;
        out.writeObject( costs ) ;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        nodes = (ID[]) in.readObject() ;
        edges = (byte[]) in.readObject() ;
        costs = (double[]) in.readObject() ;
        setHopsAndDads() ;
    }
    
    protected ID[]  nodes ;
    protected byte[] edges ;
    protected double[]  costs ;
    
    transient protected int[] hops ;
    transient protected int[] dads ;
}
