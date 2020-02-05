package feeds.sys.registry;

import feeds.api.*;
import feeds.sys.* ;

public class DistributedRegistry {

	static public Object get( String key, int timeout ) throws FeedsException {
        try {
            double delay = 0 ;
            double deadline = Feeds.time() + timeout ;
            do {
                Object o = FeedsRegistry.get( key ) ;
                if( o != null ) return o ;
                FeedsNode.rqc().publish( key, null ) ;
                delay = Math.max( 2 * delay, 1) ;
                Feeds.sleep( delay ) ;
            } while( Feeds.time() < deadline ) ;
        }
        catch( FeedsException x ) {
            x.printStackTrace() ;
        }
        if( timeout == 0 ) return null ;
        else throw new FeedsException("No answer after " + timeout + " seconds.") ;
    }
}