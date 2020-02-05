package feeds.sys.transports.tcp.nat;

import feeds.sys.core.* ;
import feeds.sys.transports.*;

/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */
public class NatTransportFactory implements TransportFactory {
    
    public Transport open( String urlString, String mode ) {
        try {
            if( mode.equals("incoming") ) return new IncomingNatTransport( urlString ) ;
            else if( mode.equals("outgoing") ) return new OutgoingNatTransport( urlString ) ;
        }
        catch( Exception x ) {
            x.printStackTrace() ;
        }
        return null ;
    }
}

