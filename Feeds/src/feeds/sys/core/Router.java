package feeds.sys.core;

import feeds.sys.packets.* ;

public interface Router<E, P, F, Q> {

    public void init() ;
    
    public ID channel() ;
        
    public void pRoute( pPacket<E,P> p ) throws Exception ;
    
    public void fRoute( fPacket<F,Q> p ) throws Exception ;
    
    public void cRoute( cPacket p ) throws Exception ;
}
