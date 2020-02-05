package feeds.sys.core;

import feeds.api.FeedsException;
import feeds.sys.packets.cPacket;
import feeds.sys.transports.Transports;

public interface Transport {
	    
	    public ID dst() ;
	    
	    public String url() ;
	    
	    public String[] qos() ;
	    
	    public void dispose() ;
	    
	    public boolean isIncoming() ;
	    
	    public boolean isOutgoing() ;
	    
	    public boolean isUnicast() ;
	    
	    public boolean isMulticast() ;

	    public Transport open() throws FeedsException ;
	    
	    public void send( cPacket p ) throws FeedsException ;

	    void setDst( ID dst ) ;
	    
	    void setUrl( String url ) ;    
	    
	    //public void close() throws TransportCloseException ;
	    
	    void setFactory( Transports tf ) ;
	    
	    //void setTransportMonitor( TransportMonitor tm ) ;
	    
	    void setPacketDispatcher( Dispatcher d ) ;
}
