package feeds.sys.catadupa.template;

import java.io.*;

import feeds.sys.core.*;
import feeds.sys.packets.*;

public class ControlPacket extends cPacket {
	

	public ControlPacket( ID channel, CatadupaControlPacket payload ) {
		super( channel, 0 ) ;
		this.payload = payload ;
	}
		
	public void encode() throws IOException {
		super.encode() ;
		encoder().writeObject( payload ) ;		
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
