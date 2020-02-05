package feeds.sys.templates.localloop;

import feeds.sys.packets.* ;
import feeds.sys.pipeline.* ;

public class AsynchronousLocalLoop<E, P, F, Q> extends BasicTemplate<E, P, F, Q> {
        
    public void pRoute( pPacket<E, P> p) throws Exception {
        loq.send( p ) ;
    }
    
    public void fRoute( fPacket<F, Q> p) throws Exception {
        loq.send( p ) ;
    }
    
}