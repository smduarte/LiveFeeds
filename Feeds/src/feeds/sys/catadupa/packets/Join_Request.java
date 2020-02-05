package feeds.sys.catadupa.packets;

import feeds.sys.catadupa.*;

public class Join_Request extends CatadupaControlPacket {

	public Node node ;
    
	public Join_Request( Node n ) {
        this.node = n ;
    }
	
    final public void cRoute( ControlPacketRouter cpr ) throws Exception {        
        cpr.cRoute( this ) ;
    }   
    
	private static final long serialVersionUID = 1L;
}
