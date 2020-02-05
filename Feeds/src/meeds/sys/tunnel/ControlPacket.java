package meeds.sys.tunnel;

import java.io.IOException;

import feeds.sys.core.ID;
import feeds.sys.packets.cPacket;

public class ControlPacket extends cPacket {
	
	public ControlPacket( ID channel, TunnelControlPacket payload ) {
		super( channel, 0 ) ;
		this.payload = payload ;
	}
		
	public void encode() throws IOException {
		super.encode() ;
		encoder().writeObject( payload ) ;		
	}
	
	static TunnelControlPacket decode( cPacket p) throws IOException {
		 if( p.isLocal ) {
			 return ((ControlPacket)p).payload ;
		 } else {
			 TunnelControlPacket res = p.decoder().readObject() ;
			 res.packet = p ;
			 return res ;
		 }
	}

	TunnelControlPacket payload = null ;

	private static final long serialVersionUID = 1L;
}
