package feeds.api;

import static feeds.sys.core.NodeContext.context;

import java.io.* ;
import feeds.sys.* ;
import feeds.sys.core.*;

public class Feeds {
    
    public static PrintStream out, err ;

    public static void exit() {
    	FeedsNode.exit() ;
    }
    
    public static double time() {
    	return FeedsNode.time() ;
    }
    
    public static void sleep( double s ) {
    	FeedsNode.sleep( s ) ;
    }
    
	public static <T> Directory directory() {
		init() ;
    	return FeedsNode.dir() ;
    }
    
    public static Thread newThread( boolean daemon, Runnable r ) {
    	return FeedsNode.newThread( daemon, r ) ;
    }
     
	@SuppressWarnings("unchecked")
	public static <T> T clone(String template, String name, int lifeSpan, Object ... extra_args) throws FeedsException {
		return (T)directory().clone(template, name, lifeSpan, extra_args ) ;
	}

	@SuppressWarnings("unchecked")
	public static <T> T lookup(String name, Object... extraArgs) throws FeedsException {
		return (T)directory().lookup(name, extraArgs) ;
	}

    public static void todo() {
    	Thread.dumpStack() ;
    }

	static void init() {
		if( context == null ) {
			context = new ClientNodeContext() ;
			context.init();
		}
	}	
}
