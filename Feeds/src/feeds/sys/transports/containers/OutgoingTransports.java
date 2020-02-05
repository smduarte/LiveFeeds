package feeds.sys.transports.containers;

import java.util.* ;
import feeds.sys.core.*;


public interface OutgoingTransports {

    public Transport get( ID node ) ;

	public Map<ID, Transport> transports() ;
	
	interface Updater {
		
		   public void remove( ID node ) ;
		   
		   public Transport put( String url ) ;
		   
		   public Transport put( ID dst, String url ) ;
		   
	}
}
