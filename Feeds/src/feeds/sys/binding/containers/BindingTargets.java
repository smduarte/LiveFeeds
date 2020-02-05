package feeds.sys.binding.containers;

import java.util.* ;
import feeds.sys.core.*;

public interface BindingTargets {
    	
    public Collection<Transport> servers() ;

    public Map<ID, Transport> transports() ;

    interface Updater {
    	
    	public void put( String url ) ;
    	
    }

}