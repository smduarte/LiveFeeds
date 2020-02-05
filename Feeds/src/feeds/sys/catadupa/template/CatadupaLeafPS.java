package feeds.sys.catadupa.template;

import feeds.sys.catadupa.*;

public class CatadupaLeafPS extends CatadupaControlPacket {
	
	public oView view ;
	public CatadupaPayload<?,?> payload ;	

	public CatadupaLeafPS( oView view, CatadupaPayload<?, ?> payload ) {
		this.payload = payload ;
		this.view = view.clone() ;
	}
	
	final public void cRoute( ControlPacketRouter<?, ?, ?, ?> cpr ) throws Exception {        
        cpr.cRoute( this ) ;
    }  
	
	private static final long serialVersionUID = 1L;
}
