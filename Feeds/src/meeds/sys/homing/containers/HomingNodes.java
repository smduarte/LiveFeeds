package meeds.sys.homing.containers;

import java.util.*;

import feeds.sys.core.*;

public interface HomingNodes {

	public Set<ID> nodes();
	
	public boolean isKnown( ID node) ;

	public Map<ID, Transport> transports();
	
	interface Updater {
			
		public void put( String url ) ;			
	}
}
