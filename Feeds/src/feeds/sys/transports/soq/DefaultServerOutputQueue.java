package feeds.sys.transports.soq;

import java.util.List;

import feeds.api.FeedsException;
import feeds.sys.binding.containers.BoundServers;
import feeds.sys.core.Container;
import feeds.sys.core.ContainerListener;
import feeds.sys.core.Transport;
import feeds.sys.packets.cPacket;
import feeds.sys.transports.BasicTransport;

/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 */

final public class DefaultServerOutputQueue extends BasicTransport implements ContainerListener<BoundServers> {
    
    public DefaultServerOutputQueue(String url, String mode) {
        super( url,  mode ) ;
        Container.monitor( BoundServers.class, this ) ;
    }
    
    public void send( cPacket de) throws FeedsException {
        if( server != null ) 
        	server.send( de ) ;
    }
    
    public void handleContainerUpdate( BoundServers bs ) {
    	List<Transport> st = bs.sortedTransports() ;
    	server = st.isEmpty() ? null : st.get(0) ;    	
    }
    
    public String toString() {
    	return String.format("%s  (%s)", url(), server == null ? "?" : server.url() ) ;
    }
    
    Transport server = null ;
}
