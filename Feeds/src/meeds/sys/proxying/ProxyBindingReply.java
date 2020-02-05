package meeds.sys.proxying;

import java.io.* ;

import feeds.api.Feeds;
import feeds.sys.* ;
import feeds.sys.core.* ;

public class ProxyBindingReply implements Serializable, Comparable<ProxyBindingReply> {
    
	public ProxyBindingReply( float keepAlivePeriod, String urls ) {
        this.urls = urls ;
        this.keepAlivePeriod = keepAlivePeriod ;
    }
    
	public ProxyBindingReply setTimestamp() {
		timeStamp = Feeds.time() ;
		return this ;
	}
	
    public ID src() {
        return src ;
    }
     
    public String urls() {
    	return urls ;
    }
    
    public double keepAlivePeriod() {
        return keepAlivePeriod ;
    }
    
    public String toString() {
        return "" + src ;
    }
    
    public boolean equals( ProxyBindingReply other ) {
    	return other == this || src.equals( other.src ) ;
    }
    
    public boolean equals( Object other ) {
        return other == this || equals( (ProxyBindingReply) other ) ;
    }

    public int hashCode() {
        return src.hashCode() ;
    }
        
    String urls ;
    double timeStamp ;
    float keepAlivePeriod ;
    ID src = FeedsNode.id() ;
    
	private static final long serialVersionUID = 1L;

	public int compareTo(ProxyBindingReply other) {
		double diff = timeStamp - other.timeStamp ;
		return diff == 0 ? src.compareTo(other.src) : (diff > 0 ? -1 : 1) ;
	}
}