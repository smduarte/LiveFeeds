package meeds.sys.transports.hoq;

import java.util.List;

import meeds.sys.homing.containers.Homebase;
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

public class HomebaseOutputQueue extends BasicTransport {
    
    public HomebaseOutputQueue(String url, String mode) {
        super( url,  mode ) ;
        Container.monitor( Homebase.class, new ContainerListener<Homebase>() {
			public void handleContainerUpdate(Homebase hb) {
		    	List<Transport> st = hb.sortedTransports() ;
		    	homebase = st.isEmpty() ? null : st.get(0) ;    	
			}
		}) ;
    }
    
    public ID dst() {
		return homebase != null ? homebase.dst() : new ID(0) ;
	}
    
    public void send( cPacket de) throws FeedsException {
        if( homebase != null ) 
        	homebase.send( de ) ;
    }
    
    public String toString() {
    	return String.format("%s  (%s)", url(), homebase == null ? "?" : homebase.url() ) ;
    }
    
    Transport homebase = null ;
}
