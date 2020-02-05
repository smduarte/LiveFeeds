package feeds.sys.packets;


import java.io.IOException;
import java.io.Serializable;

import feeds.api.Feeds;
import feeds.api.FeedsException;
import feeds.api.Payload;
import feeds.sys.core.Encoder;
import feeds.sys.core.Packet;

public class PayloadPacket<E, P> implements Payload<P>, Serializable {
    
	transient private P data ;
    transient private E envelope ;
    
    private Packet packet ;
    transient private boolean envelopeDecoded, dataDecoded ;
    
    public PayloadPacket( Packet p ) {
        this.packet = p ;
        envelopeDecoded = dataDecoded = false ;
    }
    
    public PayloadPacket(E e, P d) {
        this.data = d ;
        this.envelope = e ;
        this.envelopeDecoded = this.dataDecoded = true ;
    }
    
    @SuppressWarnings("unchecked")
	synchronized public E envelope() throws FeedsException {
        try {
            if( ! envelopeDecoded ) {
                envelope = (E)packet.decoder().readObject() ;
                envelopeDecoded = true ;
            }
            return envelope ;
        } catch( Exception x ) {
        	Feeds.err.println( x.getMessage() ) ;
        	return null ;
//        	throw new FeedsException( x.getMessage() ) ;
        }
    }
    
    @SuppressWarnings("unchecked")
	synchronized public P data() throws FeedsException {
        try {
            if( ! dataDecoded ) {
                if( ! envelopeDecoded ) envelope() ;
                data = (P) packet.decoder().readObject() ;
                dataDecoded = true ;
            }
            return data ;
        } catch( Exception x ) {  
        	Feeds.err.println( x.getMessage() ) ;
        	return null ;
//        	throw new FeedsException( x.getMessage() ) ;
        }
    }
    
    synchronized Packet packet() throws IOException {
        if( packet == null ) {
            packet = new Packet() ;
            packet.encoder().writeObject( envelope ) ;
            packet.encoder().writeObject( data ) ;
            packet.close() ;
        }
        return packet ;
    }
    
    synchronized void writeTo( Encoder e ) throws IOException {
    		e.writeObject( envelope ) ;
            e.writeObject( data ) ;
    }
    
	private static final long serialVersionUID = 1L;
}
