package feeds.sys.catadupa.packets;

import feeds.api.Feeds;
import feeds.sys.FeedsNode;
import feeds.sys.catadupa.JoinBatch;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;
import feeds.sys.packets.cPacket;
import feeds.sys.pipeline.BasicTemplate0;

abstract public class ControlPacketRouter extends BasicTemplate0 {
    
    protected ControlPacketRouter( ID channel ) {
        super( channel ) ;
    }
    
    void cRoute( CatadupaControlPacket x ) { 
        Feeds.out.println( "Unknown Catadupa control packet." + x.getClass() ) ;
    } ;
    
    public void cRoute( cPacket p ) throws Exception {
    	ControlPacket.decode(p).cRoute( this ) ;
    }    

    public void cRoute( Join_Request x ) throws Exception {}

    public void cRoute( JoinBatch x ) throws Exception {}

    public void cRoute( CatadupaTreeCast x ) throws Exception {}

    public void cRoute( CatadupaLeafCast x ) throws Exception {}

    public void cRoute( Repair_Request x ) throws Exception {}

    public void cRoute( Repair_Reply x ) throws Exception {}

    protected void sendTo(String url, CatadupaControlPacket p) {
		try {
			Transport t = FeedsNode.openTransport(url, "outgoing").open();
			t.send(p.cPacket(channel));
			t.dispose();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
