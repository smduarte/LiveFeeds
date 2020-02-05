package meeds.sys.proxying.containers;

import feeds.sys.core.*;
import meeds.sys.proxying.*;

public interface Proxy {

	public Transport closestProxy() ;
	    
    
	interface Updater {
	
	    public void put( ProxyBindingReply s ) ;

	}
}
