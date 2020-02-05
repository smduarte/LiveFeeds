package feeds.sys.templates.localloop;

import feeds.api.*;
import feeds.sys.core.*;
import feeds.sys.packets.*;
import feeds.sys.pipeline.*;

public class SynchronousLocalLoop<E, P, F, Q> extends BasicTemplate<E, P, F, Q> {
        
    public Object getStub( final String name, Object ... args ) throws FeedsException {
        return new ChannelStub<E, P, F, Q>( pipeline, name ) {

        	synchronized public Receipt publish( E envelope, P data ) throws FeedsException {
        		pPacket<E, P> p = new pPacket<E, P>( channel, source, envelope, data ) ;
        		notifier.pRoute( p ) ; 
        		return p ;
        	}

        	synchronized public Receipt feedback( Receipt r, F envelope, Q data ) throws FeedsException {
        		fPacket<F, Q> p = new fPacket<F, Q>( channel, source, (ID)r.source(), envelope, data ) ;
        		notifier.fRoute( p ); 
        		return p ;
        	}
        } ;
    }
}