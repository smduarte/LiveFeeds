package feeds.sys.backbone.containers;

import java.util.*;

import feeds.sys.core.*;

public interface BackboneNodes {

	public String urls();

	public Set<ID> nodes();
	
	public Set<ID> newNodes();

	public boolean isKnown( ID node) ;

	public Map<ID, Transport> transports();

	public ID rendezVous( ID x ) ;
	
	interface Updater {
		
		public Set<String> putAll( List<String> urls) ;
		
		public Set<ID> put( String url, boolean notifyImmediately) ;
		
		public Set<ID> putAll( String urls, boolean notifyImmediately) ;
			
	}
}
