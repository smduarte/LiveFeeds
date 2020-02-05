package meeds.sys.tunnel;

import java.io.DataOutput;
import java.io.IOException;

import feeds.api.Feeds;
import feeds.sys.core.Packet;
import feeds.sys.packets.cPacket;

public class InboundPayloadPacket extends TunnelControlPacket {

	transient Packet payload ;
	transient double time = Feeds.time() ;
	
	public InboundPayloadPacket( cPacket p ) {
		this.payload = p.packet() ;
	}

	
	final public void cRoute( TunnelPacketRouter<?, ?, ?, ?> r ) throws Exception {        
        r.cRoute( this ) ;
    }  
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		payload.writeTo( (DataOutput) out) ;
		out.writeDouble(time) ;
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
		payload = new Packet().readFrom(in) ;
		time = in.readDouble() ;
	}

	private static final long serialVersionUID = 1L;
}