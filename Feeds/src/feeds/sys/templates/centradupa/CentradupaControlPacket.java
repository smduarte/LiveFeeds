package feeds.sys.templates.centradupa;

import java.io.Serializable;

import feeds.sys.core.ID;
import feeds.sys.packets.cPacket;

abstract class CentradupaControlPacket implements Serializable {
	
	public void cRoute( ControlPacketRouter<?,?,?,?> cpr ) throws Exception {        
        cpr.cRoute( this ) ;
    }
	
	
	public cPacket cPacket( ID channel ) {
		if( packet == null ) {
			packet = new ControlPacket(channel, this) ;
		}
		return packet ;
	}
	
	transient cPacket packet ;
	private static final long serialVersionUID = 1L;
}
