package meeds.sys.tunnel;

import java.io.DataOutput;
import java.io.IOException;

import feeds.sys.core.ID;
import feeds.sys.core.Packet;
import feeds.sys.packets.cPacket;

public class OutboundPayloadPacket extends TunnelControlPacket {

	transient ID src ;
	transient Packet payload ;
	
	public OutboundPayloadPacket( ID proxy, cPacket p ) {
		this.src = proxy ;
		this.payload = p.packet() ;		
	}

	final public void cRoute( TunnelPacketRouter<?, ?, ?, ?> r ) throws Exception {        
        r.cRoute( this ) ;
    }  
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		payload.writeTo( (DataOutput) out) ;
		src.writeTo( out ) ;
		out.writeInt( serial ) ;
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
		payload = new Packet().readFrom(in) ;
		src = new ID( in ) ;
		serial = in.readInt() ;
	}

	public String toString() {
		return String.format("OB Packet: %d from %s", serial, src ) ;
	}
	
	int serial = g_serial++ ;
	private static int g_serial = 0 ;
	private static final long serialVersionUID = 1L;
}