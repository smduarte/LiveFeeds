package feeds.simsim.sys;

import java.util.*;


import feeds.api.*;
import feeds.sys.core.*;
import feeds.sys.packets.*;
import feeds.sys.transports.*;
import feeds.sys.binding.containers.*;

public class SS_SoqTransport  extends BasicTransport implements ContainerListener<BoundServers>{
	
	public SS_SoqTransport(String url, String mode) {
        super( url,  mode ) ;
        Container.monitor( BoundServers.class, this ) ;
    }
    
	public ID dst() {
		return server != null ? server.dst() : new ID(0) ;
	}
	
    public void send( cPacket p) throws FeedsException {
        if( server != null ) 
        	server.send( p ) ;
    }
    
    public void handleContainerUpdate( BoundServers bs ) {
    	List<Transport> st = bs.sortedTransports() ;
    	server = st.isEmpty() ? null : st.get(0) ;        	    	
    }
    
    public String toString() {
        return this.hashCode() + "   " + url() + "  (" + (server == null ? "null" : server.url()) + ')'  ;
    }
    
    Transport server = null ;
}
