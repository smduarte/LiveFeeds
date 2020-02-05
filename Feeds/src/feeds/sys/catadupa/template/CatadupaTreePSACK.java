package feeds.sys.catadupa.template;

import feeds.sys.core.*;

public class CatadupaTreePSACK extends CatadupaControlPacket {
		
	public final ID id ;
	
	public CatadupaTreePSACK( CatadupaTreePS other ) {
		this.id = other.id ;
	}
	
	public String toString() {
		return String.format("PubSubEventACK<%d>", id) ;
	}
		
	final public void cRoute( ControlPacketRouter<?, ?, ?, ?> cpr ) throws Exception {        
        cpr.cRoute( this ) ;
    }  
	
	private static final long serialVersionUID = 1L;
}
