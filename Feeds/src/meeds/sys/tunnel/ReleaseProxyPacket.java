package meeds.sys.tunnel;

import feeds.sys.*;
import feeds.sys.core.*;

public class ReleaseProxyPacket extends TunnelControlPacket {

	final ID src = FeedsNode.id();
	final int serial = g_serial++ ;
	
	
	final public void cRoute( TunnelPacketRouter<?, ?, ?, ?> r ) throws Exception {        
        r.cRoute( this ) ;
    }  

	public String toString() {
		return String.format("RPP src: %s (%d)", src, serial) ;
	}
	
	static int g_serial = 0 ;
	private static final long serialVersionUID = 1L;
}