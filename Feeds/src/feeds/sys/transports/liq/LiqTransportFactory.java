package feeds.sys.transports.liq;

import feeds.api.FeedsException;
import feeds.sys.FeedsNode;
import feeds.sys.core.Transport;
import feeds.sys.transports.TransportFactory;
/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */
public class LiqTransportFactory implements TransportFactory {
    
	public LiqTransportFactory() {
		liq = new SyncLocalInputQueue("liq:/-/-", "outgoing") ;
		liq.setPacketDispatcher( FeedsNode.plm().dispatcher() ) ;
	}
    
    public Transport open( String url, String mode ) {
        if( mode.equals("outgoing") ) 
        	return liq ;
        else throw new FeedsException("Unexpected...liq") ;
    }
    
    Transport liq ;
}
