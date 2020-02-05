package feeds.sys.transports;

import feeds.sys.core.*;

public interface TransportFactory {
	public Transport open( String urlString, String mode ) ;
}
