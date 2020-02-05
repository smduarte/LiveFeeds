package feeds.simsim.sys;

import feeds.api.*;
import feeds.sys.core.*;
import feeds.sys.packets.*;
import feeds.sys.transports.* ;

class SS_IncomingTransport extends BasicTransport {
    
	SS_IncomingTransport( ID dst, String url, String mode ) {
        super( url,  mode ) ;
        setPacketDispatcher( SS_Node.db.get( dst ).context.plm.dispatcher() ) ;
    }
        
    public void send( cPacket p ) throws FeedsException {  
    	dispatcher.dispatch(p) ;
    }
}
