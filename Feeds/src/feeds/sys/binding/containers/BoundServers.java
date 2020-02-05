package feeds.sys.binding.containers;

import java.util.*;

import feeds.sys.core.*;
import feeds.sys.binding.*;

public interface BoundServers {

	public Map<ID, Transport> transports() ;
		
    public List<Transport> sortedTransports() ;
    
    
	interface Updater {
	
	    public void put( BindingReply s ) ;

	}
}
