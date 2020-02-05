package feeds.sys.transports.liq;

import feeds.sys.packets.cPacket;
import feeds.sys.transports.BasicTransport;


/**
 *
 * @author  Sérgio Duarte
 * @version
 */
final public class SyncLocalInputQueue extends BasicTransport {
    
    public SyncLocalInputQueue(String urlString, String mode) {
        super("liq://-/-", mode ) ;
    }
        
    public void send( cPacket p) {
        dispatcher.dispatch(p) ;
    }
    
    public String getURL() {
        return "liq://?/-" ;
    }
}
