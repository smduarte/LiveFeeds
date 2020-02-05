package meeds.sys.proxying.containers;

import java.util.*;

import feeds.sys.core.*;

public interface ProxyClients {

	public Set<ID> nodes();
	
	public boolean isKnown( ID node) ;

	public Map<ID, Transport> transports();
	
	interface Updater {
			
		public void put( String url ) ;			
	}
}
