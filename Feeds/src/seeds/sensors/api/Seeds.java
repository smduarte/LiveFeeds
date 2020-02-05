package seeds.sensors.api;

import feeds.api.*;
import feeds.sys.core.*;

import meeds.sys.*;

public class Seeds extends meeds.api.Meeds {
		
	@SuppressWarnings("unchecked")
	public static <T> T lookup(String name, Object... extraArgs) throws FeedsException {
		return (T) MeedsNode.dir().lookup(name, extraArgs) ;
	}
	
	static {
		if( NodeContext.context == null ) 
			throw new Error("No Meeds context yet. Aborting...") ;
	}
}
