package meeds.sys.transports;

import feeds.api.* ;
import feeds.sys.core.* ;
import feeds.sys.transports.*;
import meeds.sys.transports.hoq.*;
import meeds.sys.transports.poq.*;

public class MeedsTransports extends feeds.sys.transports.Transports {

	public Transports init() throws FeedsException {
		super.init() ;

		factories.put("hoq", Singleton.get( HoqTransportFactory.class));
		factories.put("poq", Singleton.get( PoqTransportFactory.class));

		return this;
	}

}
