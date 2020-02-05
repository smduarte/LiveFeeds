package feeds.sys.transports.udp;

import feeds.sys.core.* ;
import feeds.sys.transports.*;

public class UdpTransportFactory implements TransportFactory {
    
   public Transport open( String urlString, String mode ) {
        try {
            if( mode.equals("incoming") ) return new IncomingUdpTransport( urlString ) ;
            else if( mode.equals("outgoing") ) return new OutgoingUdpTransport( urlString ) ;
        }
        catch( Exception x ) {
            x.printStackTrace() ;
        }
        return null ;
    }
}

