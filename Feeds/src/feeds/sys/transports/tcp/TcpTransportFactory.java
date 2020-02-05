package feeds.sys.transports.tcp;

import feeds.sys.core.* ;
import feeds.sys.transports.*;

/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */
public class TcpTransportFactory implements TransportFactory {
    
    public Transport open( String urlString, String mode ) {
        try {
            if( mode.equals("incoming") ) return new IncomingTcpTransport( urlString ) ;
            else if( mode.equals("outgoing") ) return new OutgoingTcpTransport( urlString ) ;
        }
        catch( Exception x ) {
            x.printStackTrace() ;
        }
        return null ;
    }
}

