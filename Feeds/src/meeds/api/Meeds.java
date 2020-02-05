package meeds.api;

import feeds.api.*;
import feeds.sys.*;
import static feeds.sys.core.NodeContext.context;

import meeds.sys.core.*;

public class Meeds extends Feeds {

	
	public static <T> Directory directory() {
		init() ;
    	return FeedsNode.dir() ;
    }
	
	@SuppressWarnings("unchecked")
	public static <T> T clone(String template, String name, Object ... extra_args) throws FeedsException {
		return (T)directory().clone(template, name, extra_args ) ;
	}

	@SuppressWarnings("unchecked")
	public static <T> T lookup(String name, Object... extraArgs) throws FeedsException {
		return (T)directory().lookup(name, extraArgs) ;
	}

	static void init() {
		if( context == null ) {
			context = new MobileNodeContext() ;
			context.init();
		}
	}		
}
