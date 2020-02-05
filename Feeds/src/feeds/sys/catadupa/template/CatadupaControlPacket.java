package feeds.sys.catadupa.template;

import java.io.*;
import feeds.sys.core.*;
import feeds.sys.packets.*;

abstract class CatadupaControlPacket implements Serializable {
	
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
