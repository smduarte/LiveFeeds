package feeds.sys.backbone.containers.impl;

import java.util.* ;

import feeds.api.*;
import feeds.sys.* ;
import feeds.sys.core.* ;
import feeds.sys.graphs.* ;
import feeds.sys.backbone.containers.*;

/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */
public class RoutingTables_Impl extends Container<RoutingTables> implements RoutingTables, RoutingTables.Updater{
    
    public RoutingTables_Impl() {
    	super.notifyUpdate() ;
    }

	public Map<ID, ID> unicastRTable() {
		return tables.getUnicastRTable() ;
	}

    public Map<ID, List<ID>> bcastRTable() {
    	return tables.getBroadcastRTable() ;
	}
	
    synchronized public void update( SpanningTreeEncoding ste ) {
        tables = new ST_RoutingTables( ste, FeedsNode.id() ) ;
        double now = Feeds.time() ;
        if( now - lastUpdate > 2.5 ) {
        	lastUpdate = now ;
        	super.notifyUpdate() ;
        }
    }
      
    private double lastUpdate = Feeds.time() ;
    private ST_RoutingTables tables = new ST_RoutingTables() ;
}