package feeds.sys.templates.centradupa.anycast;

import java.io.IOException;

import feeds.sys.core.ID;
import feeds.sys.packets.cPacket;

public class ControlPacket extends cPacket {
	
	public ControlPacket( ID channel, CentradupaControlPacket payload ) {
		super( channel, 0 ) ;
		this.payload = payload ;
	}
		
	public void encode() throws IOException {
		super.encode() ;
		try {
		encoder().writeObject( payload ) ;		
		} catch( Exception x ){
			x.printStackTrace() ;
		}
	}
	
	static CentradupaControlPacket decode( cPacket p) throws IOException {
		 if( p.isLocal ) {
			 return ((ControlPacket)p).payload ;
		 } else {
			 CentradupaControlPacket res = p.decoder().readObject() ;
			 res.packet = p ;
			 return res ;
		 }
	}

	CentradupaControlPacket payload = null ;
	private static final long serialVersionUID = 1L;
}
