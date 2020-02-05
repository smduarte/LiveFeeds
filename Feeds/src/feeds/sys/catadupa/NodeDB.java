package feeds.sys.catadupa;

import java.io.*;
import java.util.*;

import feeds.api.*;
import feeds.sys.core.*;
import static feeds.sys.catadupa.Catadupa.*;

@SuppressWarnings("serial")
public class NodeDB extends Container<NodeDB> implements Serializable {

	public TreeMap<Long, Node> k2n = new TreeMap<Long, Node>() ;
	
	public NodeDB() {
		super("CatadupaNodeDB") ;
	}
	
	public NodeDB( String name ) {
		super(name) ;
	}
	
	private NodeDB( NodeDB other ) {
		for( Map.Entry<Long, Node> i : other.k2n.entrySet() ) {
			k2n.put( i.getKey(),  i.getValue().clone() ) ;
		}
	}

	public NodeDB clone() {
		return new NodeDB( this ) ;
	}
	
	public List<Node> nodes() {
		return new RandomList<Node>(k2n.values()) ;
	}
	
	boolean contains( Long key ) {
		return k2n.containsKey( key ) ;
	}
	
	void store( Node n ) {
		Node x = k2n.get(n.key) ;
		if( x == null || n.data.newer( x.data ) )
			k2n.put( n.key, n) ;
	}
	
	void storeAll( Collection<Node> c ) {
		for( Node i : c ) 
			store( i ) ;
	}
		
	void merge( NodeDB db ) {
		for( Node i : db.k2n.values() ) 
			store( i ) ;
	}
	
	public Iterable<Node> nodes( long L, long H) {
		try {
			return k2n.subMap( L, H).values() ;
			
		} catch( Exception x ) {
			Feeds.out.printf( "%d %d\n", L, H ) ;
			throw new RuntimeException(x.getMessage()) ;
		}
	}
	
	Node sliceLeader( boolean excludeSelf, long key ) {
		long SLICE_WIDTH = (1L << NODE_KEY_LENGTH) / NUMBER_OF_SLICES ;		
		for( int i = 0 ; i < NUMBER_OF_SLICES ; i++ ) {			
			long leaderKey = (( key / SLICE_WIDTH + i ) % NUMBER_OF_SLICES) * SLICE_WIDTH  ;			
			Node res = firstNode( leaderKey, excludeSelf ? key : -1L ) ;
			if( res != null ) return res ;
		}
		return null ;
	}
	
	Node randomNode( long excludeKey) {
		RandomList<Node> tmp = new RandomList<Node>( k2n.values() ) ;
		Node res ;
		do {
			res = tmp.removeRandomElement()  ;
		} while( res != null && res.key == excludeKey ) ;
		return res ;
	}
	
	Node firstNode( long start, long exclude ) {
		Map<Long,Node> gt = k2n.tailMap(start) ;
		for( Map.Entry<Long, Node> i : gt.entrySet() ) {
			Node candidate = i.getValue() ;
			if( candidate.key == exclude ) continue ;
			else return candidate ;
		}
		return null ;
	}
	
	int size() {
		return k2n.size() ;
	}
	
	public String toString() {
		return k2n.keySet() + " : " + k2n.size() ;
	} 
 }
