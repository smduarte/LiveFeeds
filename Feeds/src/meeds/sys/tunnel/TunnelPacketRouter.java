package meeds.sys.tunnel;

import feeds.api.Feeds;
import feeds.sys.core.ID;
import feeds.sys.packets.cPacket;
import feeds.sys.pipeline.BasicTemplate;

abstract public class TunnelPacketRouter<E, P, F, Q> extends BasicTemplate<E, P, F, Q> {
    
    protected TunnelPacketRouter( ID channel ) {
        super( channel ) ;
    }
    
    void cRoute( TunnelControlPacket x ) { 
        Feeds.out.println( "Unknown TunnelPacket control packet." + x.getClass() ) ;
    }
    
    public void cRoute( cPacket p) throws Exception {
        ControlPacket.decode( p ).cRoute( this ) ;
    }    

    void cRoute( InboundPayloadPacket x ) throws Exception {}
    
    void cRoute( OutboundPayloadPacket x ) throws Exception {}

    void cRoute( MembershipPacket x ) throws Exception {}

    void cRoute( ReleaseProxyPacket x ) throws Exception {}
}
