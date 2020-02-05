package feeds.sys.catadupa;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import feeds.sys.FeedsNode;
import feeds.sys.core.ID;

@SuppressWarnings("serial")
public class Node implements Comparable<Node>, Serializable {
	
	public Long key ;
	public String url ;
	public SubscriptionData data ;
	
	Node( String url) {
		this.url = url ;
		this.key = getKey() ;
		this.data = new SubscriptionData() ;
	}
	
	Node( ID id, String url) {
		this.url = url ;
		this.key = -id.longValue() ;
		this.data = new SubscriptionData() ;
	}
	
	Node( String url, SubscriptionData data ) {
		this.url = url ;
		this.data = data ;
		this.key = getKey() ;
	}
	
	private Node( Node other ) {
		this.key = other.key ;
		this.url = other.url ;
		this.data = other.data.clone() ;
	}
	
	public Node clone() {
		return new Node( this ) ;
	}
		
	public int hashCode() {
		return key.hashCode() ;
	}
	
	public boolean equals( Node other ) {
		return key.equals( other.key ) ;
	}
	
	public boolean equals( Object other ) {
		return equals( (Node) other ) ;
	}
	
	public String toString() {
		return String.format("%s %d %s", url, key, data) ;
	}

	public int compareTo(Node other) {
		return key.compareTo( other.key ) ;
	}
	
	public boolean pAccepts( Object e, ID channel ) {
		return data.pAccepts(e, channel) ;
	}

	public boolean fAccepts( Object e, ID channel, ID dst ) {
		return data.fAccepts(e, channel, dst.major()) ;
	}

	/* Guarantees unique keys */
	/* Important for simulation with small key lengths */
	private static Long getKey() {
		long res ;
		do {
			res = (FeedsNode.rnd().nextLong() >>> 1) % (1L << Catadupa.NODE_KEY_LENGTH) ;
		} while( res > 0 && usedKeys.contains(res)) ;
		usedKeys.add( res ) ;
		return res ;
	}
	private static Set<Long> usedKeys = new HashSet<Long>() ; //for debugging purposes...
}
