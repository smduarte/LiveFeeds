package feeds.sys.catadupa.template;

import feeds.sys.core.*;
import feeds.sys.catadupa.*;

public class CatadupaTreePS extends CatadupaControlPacket {
	
	String url ;
	
	public int level ;
	public oView view ;
	public Range range ;
	public final ID id ;
	public CatadupaPayload<?, ?> payload ;	

	public CatadupaTreePS( Node src, Range range, oView view, CatadupaPayload<?, ?> payload ) {
		this.level = 0 ;
		this.id = new ID() ;
		this.payload = payload ;
		this.view = view.clone() ;
		this.range = range.clone() ;
		this.url = src.url ;
	}

	public CatadupaTreePS( Node router, CatadupaTreePS other, Range range ) {
		this.range = range ;
		this.id = other.id ;
		this.url = router.url ;
		this.view = other.view;
		this.level = other.level + 1 ;
		this.payload = other.payload ;
	}
	
	final public void cRoute( ControlPacketRouter<?, ?, ?, ?> cpr ) throws Exception {        
        cpr.cRoute( this ) ;
    }  
	
	public String toString(){
		return payload.envelope + "/" + payload.data ;
	}
	
	private static final long serialVersionUID = 1L;
}
