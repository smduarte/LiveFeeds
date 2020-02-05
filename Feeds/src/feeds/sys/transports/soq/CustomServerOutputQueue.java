package feeds.sys.transports.soq;

import java.util.* ;

import feeds.api.* ;
import feeds.sys.* ;
import feeds.sys.core.* ;
import feeds.sys.tasks.*;
import feeds.sys.packets.*;
import feeds.sys.transports.* ;
import feeds.sys.binding.containers.*;
/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 */
final public class CustomServerOutputQueue extends BasicTransport implements ContainerListener<BoundServers> {
    
    public CustomServerOutputQueue(String url, String mode) throws Exception {
        super( url,  mode ) ;
        channel = new ID( super.url.locator() ) ;
        Container.monitor( BoundServers.class, this ) ;
    }
    
    public void send( cPacket p) throws FeedsException {
        if( outgoing != null ) 
        	outgoing.send( p ) ;
    }
    
    public void handleContainerUpdate( BoundServers bs ) {
    	List<Transport> st = bs.sortedTransports() ;
    	outgoing = st.isEmpty() ? null : st.get(0) ;    	
    }
    
    public void handleContainerChanges(BoundServers bs ) {
    	List<Transport> st = bs.sortedTransports() ;    	
    	Transport t = st.isEmpty() ? null : st.get(0) ;
    	
        if( t != null && ! server.equals( t.dst() ) ) {
            outgoing = t ;
            server = t.dst() ;
            new Task(0) {
                public void run() {
                    try {                
                        String key = "/Local/Directory/Config/" + channel + "/OutgoingTransport" ;
                        String url = FeedsRegistry.get( key, true, 10);
                        System.out.println( channel + "/" + url ) ;
                        outgoing = FeedsNode.openTransport( url, "outgoing" ) ;
                    } catch( Exception x ) {
                        x.printStackTrace() ;
                    }
                }
            } ;
        }
    }
    
    public String toString() {
    	return String.format("%s  (%s)", url(), outgoing == null ? "?" : outgoing.url() ) ;
    }
    
    ID server ;
    ID channel ;
    Transport outgoing = null ;
}
