package feeds.sys.catadupa.packets;

import java.io.IOException;

import feeds.sys.core.ID;
import feeds.sys.packets.cPacket;

public class ControlPacket extends cPacket {
	

	public ControlPacket( ID channel, CatadupaControlPacket payload ) {
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
	
	static CatadupaControlPacket decode( cPacket p) throws IOException {
		 if( p.isLocal ) {
			 return ((ControlPacket)p).payload ;
		 } else {
			 CatadupaControlPacket res = p.decoder().readObject() ;
			 res.packet = p ;
			 return res ;
		 }
	}

	CatadupaControlPacket payload = null ;
	private static final long serialVersionUID = 1L;
}
