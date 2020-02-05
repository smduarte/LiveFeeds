package feeds.sys.catadupa.packets;

import feeds.sys.catadupa.*;

public class CatadupaLeafCast extends CatadupaControlPacket {

	public JoinBatch batch ;
	
	public CatadupaLeafCast( JoinBatch b ) {
		this.batch = b.clone() ;
    }
	
    final public void cRoute( ControlPacketRouter cpr ) throws Exception {        
        cpr.cRoute( this ) ;
    }   
    
    public String toString() {
    	return String.format("%s", batch) ;
    }
	private static final long serialVersionUID = 1L;
}
