package meeds.sys.transports.poq;



import java.util.List;

import meeds.sys.homing.containers.Homebase;
import meeds.sys.proxying.containers.Proxy;
import feeds.api.FeedsException;
import feeds.sys.core.Container;
import feeds.sys.core.ContainerListener;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;
import feeds.sys.packets.cPacket;
import feeds.sys.transports.BasicTransport;

/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 */

public class PoqOutputQueue extends BasicTransport {
    
    public PoqOutputQueue(String url, String mode) {
        super( url,  mode ) ;
                
        Container.monitor( Proxy.class, new ContainerListener<Proxy>(){
			public void handleContainerUpdate(Proxy p) {
				proxy = p.closestProxy() ;    	
			}
		}) ;
        Container.monitor( Homebase.class, new ContainerListener<Homebase>() {
			public void handleContainerUpdate(Homebase hb) {
		    	List<Transport> st = hb.sortedTransports() ;
		    	homebase = st.isEmpty() ? null : st.get(0) ;    
			}
		}) ;
    }
    
    public ID dst() {
    	return proxy != null ? proxy.dst() : (homebase != null ? homebase.dst() : new ID(0) ) ;
    }
    
    public void send( cPacket p) throws FeedsException {
    	//Feeds.err.printf("POQ: Trying to send:%s (%s/%s)\n", p, proxy, homebase ) ;

    	if( proxy != null ) 
        	proxy.send( p ) ;
    	else if( homebase != null )
    		homebase.send( p) ;
    }
        
    public String toString() {
    	return String.format("%s  (%s/%s)", url(), proxy, homebase ) ;
    }
    
    Transport proxy = null, homebase = null ;
}
