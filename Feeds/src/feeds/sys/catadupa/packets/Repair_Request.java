package feeds.sys.catadupa.packets;

import feeds.sys.catadupa.*;
/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 */
public class Repair_Request extends CatadupaControlPacket {    	

    public String url ;
    public oView oView  ;
    
    public Repair_Request( String url, oView ov ) {
    	this.url = url ;
    	this.oView = ov.clone() ;
    }
    
    public void cRoute( ControlPacketRouter cpr ) throws Exception {        
        cpr.cRoute( this ) ;
    }   

    private static final long serialVersionUID = 1L;
}
