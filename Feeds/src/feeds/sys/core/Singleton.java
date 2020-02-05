package feeds.sys.core;

import feeds.sys.* ;
/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 */

public class Singleton {
    static private final String REGISTRY_BASE = "/Singletons/" ;
    
    synchronized static public <T> T get( Class<T> cname ) {
        try {
        		String key = REGISTRY_BASE  + cname.getSimpleName() ;
        		SingletonHolder<T> sh = FeedsRegistry.get( key ) ;
                if( sh == null ) {
                	sh = new SingletonHolder<T>( cname.newInstance() ) ;
                    FeedsRegistry.put( key, sh ) ;
                }
                return sh.singleton ;
        } catch( Exception x ) {
            throw new RuntimeException( x.getMessage() ) ;
        }
    }
    
    static class SingletonHolder<T> {
    	T singleton ;
    	SingletonHolder( T singleton ) {
    		this.singleton = singleton ;
    	}
    }
}
