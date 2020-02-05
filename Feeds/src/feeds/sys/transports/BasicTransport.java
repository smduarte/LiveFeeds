package feeds.sys.transports;

import feeds.api.*;
import feeds.sys.core.*;
import feeds.sys.packets.cPacket;

public abstract class BasicTransport implements Transport {

	protected BasicTransport(String url, String mode ) {		
		this.url = new Url( url ) ;
		this.dst = this.url.fid() ;
		isIncoming = mode.equals("incoming") ;
	}
	
	public ID dst() {
		return dst ;
	}

	public String url() {
		return url.toString() ;
	}
	
	public String[] qos() {
		return new String[0];
	}
	
	public boolean isIncoming() {
		return isIncoming ;
	}

	public boolean isOutgoing() {
		return ! isIncoming ;
	}
	
	public boolean isMulticast() {
		return isMulticast ;
	}

	public boolean isUnicast() {
		return ! isMulticast ;
	}

	public void setDst( ID dst ) {
		this.dst = dst ;
	}
	
	public void setUrl( String url ) {
		this.url = new Url( url ) ;
	}
	
	public void dispose() {
	}

	public Transport open() throws FeedsException {
		return this ;
	}

	public void setFactory(Transports tf) {
	}
	
	public void setPacketDispatcher( Dispatcher d ) {
		this.dispatcher = d ;
	}
	 	 
    public void send( cPacket p ) throws FeedsException {    	
    }

    public String toString() {
    	return url.toString() ;
    }
    
	protected ID dst ;
	protected Url url ;
	protected boolean isIncoming ;
	protected boolean isMulticast ;
	protected Dispatcher dispatcher ;
}
