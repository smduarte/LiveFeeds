package feeds.sys.backbone.containers;

import java.util.*;

import feeds.sys.core.* ;
import feeds.sys.graphs.*;

public interface RoutingTables {

	public Map<ID, ID> unicastRTable() ;
	
	public Map<ID, List<ID>> bcastRTable() ;
	
	public interface Updater {
		public void update( SpanningTreeEncoding ste ) ;
	}
}
