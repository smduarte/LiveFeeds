package feeds.sys.transports.soq;

import java.util.HashMap;
import java.util.Map;

import feeds.sys.core.Transport;
import feeds.sys.transports.TransportFactory;
import feeds.sys.transports.Url;
/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */
public class SoqTransportFactory implements TransportFactory {
    
	public SoqTransportFactory() {
        queues = new HashMap<String, Transport>() ;
        queues.put("-", new DefaultServerOutputQueue("soq://-/-/",  "outgoing") ) ;
	}
    
    public Transport open( String url, String mode ) {
        try {
            Url x = new Url( url ) ;
            Transport q = (Transport) queues.get( x.locator() ) ;
            if( q == null ) {
                q = new CustomServerOutputQueue( url, mode ) ;
                queues.put( x.locator(),  q ) ;
            }
            return q ;
        } catch( Exception x ) {
            x.printStackTrace() ;
        }
        return null ;
    }
    
    private Map<String, Transport> queues ;
}
