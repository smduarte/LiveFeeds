package meeds.sys.proxying.containers;

import java.util.* ;

import feeds.sys.core.*;
import meeds.sys.proxying.*;

public interface ProxyTargets {
    	 
	public Transport closestProxy() ;
	
    public Map<ID, Transport> transports() ;

    interface Updater {
    	
    	public void put( ProxyInfo pi ) ;
    	
    }

}