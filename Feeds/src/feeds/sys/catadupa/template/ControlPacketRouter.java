package feeds.sys.catadupa.template;

import feeds.api.Feeds;
import feeds.sys.FeedsNode;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;
import feeds.sys.packets.cPacket;
import feeds.sys.pipeline.BasicTemplate;

abstract public class ControlPacketRouter<E, P, F, Q> extends BasicTemplate<E, P, F, Q> {
    
    protected ControlPacketRouter( ID channel ) {
        super( channel ) ;
    }
    
    void cRoute( CatadupaControlPacket x ) { 
        Feeds.out.println( "Unknown Catadupa control packet." + x.getClass() ) ;
    }
    
    public void cRoute( cPacket p ) throws Exception {
        ControlPacket.decode( p ).cRoute( this ) ;
    }    

    void cRoute( CatadupaLeafPS x ) throws Exception {}

    void cRoute( CatadupaTreePS x ) throws Exception {}

    void cRoute( CatadupaTreePSACK x ) throws Exception {}

    protected void sendTo(String url, CatadupaControlPacket p) {
		try {
			Transport t = FeedsNode.openTransport(url, "outgoing").open();
			t.send(p.cPacket(channel));
			t.dispose();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
    
    protected void sendTo(String url, cPacket p) {
		try {
			Transport t = FeedsNode.openTransport(url, "outgoing").open();
			t.send(p );
			t.dispose();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
