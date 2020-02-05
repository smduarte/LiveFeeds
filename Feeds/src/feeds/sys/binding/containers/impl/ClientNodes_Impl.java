package feeds.sys.binding.containers.impl;

import java.util.*;


import feeds.sys.*;
import feeds.sys.core.*;
import feeds.sys.util.*;
import feeds.sys.binding.containers.*;

public class ClientNodes_Impl extends Container<ClientNodes> implements ClientNodes, ClientNodes.Updater {

	public ClientNodes_Impl() {
		notifyUpdate() ;
	}

	synchronized public void put( String url ) {
		if (url != null) {
			Transport transport = FeedsNode.openTransport(url, "outgoing");
			if( transports.put( transport.dst(), transport) == null )
				super.notifyUpdateNow() ;
		}
	}
	
	public boolean isKnown(ID g) {
		return transports.containsKey(g);
	}
	
	public Map<ID, Transport> transports() {
		return Collections.unmodifiableMap( transports ) ;
	}

	public Set<ID> nodes() {
		return Collections.unmodifiableSet( transports.keySet() ) ;
	}

	
	private ExpirableMap<ID, Transport> transports = new ExpirableMap<ID, Transport>( 300.0, 10.0, new ExpirableMapListener<ID, Transport>() {
		public void keyExpired(Map<ID, Transport> m, ID key, Transport value) {
			notifyUpdate() ;
		}		
	} ) ; // Stored clients expire after 5 minutes
}