package meeds.sys.transports.hoq;

import java.util.* ;

import feeds.sys.core.* ;
import feeds.sys.transports.* ;
/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */
public class HoqTransportFactory implements TransportFactory {
    
	public HoqTransportFactory() {
        queues = new HashMap<String, Transport>() ;
        queues.put("-", new HomebaseOutputQueue("hoq://-/?",  "outgoing") ) ;
	}
    
    public Transport open( String url, String mode ) {
        try {
            Url x = new Url( url ) ;
            Transport q = (Transport) queues.get( x.locator() ) ;
            if( q == null ) {
                q = new HomebaseOutputQueue( url, mode ) ;
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
