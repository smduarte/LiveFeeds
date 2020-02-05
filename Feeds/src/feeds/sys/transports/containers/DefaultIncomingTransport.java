package feeds.sys.transports.containers;

import feeds.sys.core.Transport;

public interface DefaultIncomingTransport {

    public String url() ;
    
    public Transport transport() ;

	interface Updater {
		
	}
}
