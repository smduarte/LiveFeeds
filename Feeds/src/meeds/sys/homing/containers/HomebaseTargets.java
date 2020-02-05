package meeds.sys.homing.containers;

import java.util.* ;
import feeds.sys.core.*;

public interface HomebaseTargets {
    	
    public Collection<Transport> servers() ;

    public Map<ID, Transport> transports() ;

    interface Updater {
    	
    	public void put( String url ) ;
    	
    }

}