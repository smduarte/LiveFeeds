package meeds.sys.homing.containers;

import java.util.*;

import feeds.sys.core.*;
import meeds.sys.homing.*;

public interface Homebase {

	public Map<ID, Transport> transports() ;
		
    public List<Transport> sortedTransports() ;
    
    
	interface Updater {
	
	    public void put( HomingReply s ) ;

	}
}
