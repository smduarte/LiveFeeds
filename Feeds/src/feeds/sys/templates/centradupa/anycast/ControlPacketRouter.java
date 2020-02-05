package feeds.sys.templates.centradupa.anycast;

import feeds.api.Feeds;
import feeds.sys.core.ID;
import feeds.sys.packets.cPacket;
import feeds.sys.pipeline.BasicTemplate;

abstract public class ControlPacketRouter<E, P, F, Q> extends BasicTemplate<E, P, F, Q> {
    
    protected ControlPacketRouter( ID channel ) {
        super( channel ) ;
    }
    
    void cRoute( CentradupaControlPacket x ) { 
        Feeds.out.println( "Unknown Centradupa control packet." + x.getClass() ) ;
    } ;
    
    public void cRoute( cPacket p ) throws Exception {
        ControlPacket.decode( p ).cRoute( this ) ;
    }    
    
    void cRoute( Join_Request x ) throws Exception {}

}
