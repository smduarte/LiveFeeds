package feeds.sys;

import feeds.api.* ;
import feeds.sys.core.* ;
import feeds.sys.registry.* ;
import static feeds.sys.core.NodeContext.context;

public class FeedsRegistry {
        
	public static final Location SOFTSTATE = Location.SOFTSTATE ;
	public static final Location HARDSTATE = Location.HARDSTATE ;
	
    public static RegistryItem getItem( String key ) {
        return context.reg.getItem( key ) ;
    }
    
    public static void putItem( RegistryItem ri, Location where ) throws IllegalArgumentException {
        context.reg.putItem( ri,  where ) ;
    }
    
    static public void put( String key, Object value ) throws IllegalArgumentException {
        context.reg.put( key, value, SOFTSTATE ) ;
    }
    
    public static void put( String key, Object value, Location where ) throws IllegalArgumentException {
    	context.reg.put( key, value, where ) ;
    }
    
    public static void put( String key, Object value, int scope, int duration, ID owner ) throws IllegalArgumentException {
    	context.reg.put( key, value, scope, duration, owner ) ;
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T remove( String key ) {
        return (T) context.reg.remove( key ) ;
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T get( String key ) {
        return (T) context.reg.get( key ) ;
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T get( String key, boolean invalidateCache, int timeout ) throws FeedsException {
        if( invalidateCache ) context.reg.remove( key, SOFTSTATE ) ;
        return (T)DistributedRegistry.get( key, timeout ) ; 
    }

    public static Iterable<RegistryItem> values( String preffix, Location source ) {
        return context.reg.values( preffix,  source ) ;
    }
    
    public static void init( String root ) {
    	//FeedsNode.reg().init( root ) ;
    }    
}